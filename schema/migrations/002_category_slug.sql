-- Migration: 002_category_slug.sql
-- Description: Add Slug column to Categories table, backfill with deterministic sanitization,
--              validate regex ^[a-z0-9]+(?:-[a-z0-9]+)*$ and uniqueness, set NOT NULL, and create UNIQUE INDEX.

USE [ABCNews];
GO

-- 1. Thêm cột Slug vào bảng Categories nếu chưa có (tạm thời NULL để backfill)
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'[dbo].[Categories]') AND name = 'Slug'
)
BEGIN
    ALTER TABLE [dbo].[Categories] 
    ADD [Slug] [varchar](200) NULL;
END
GO

-- 2. Backfill dữ liệu slug chuẩn cho các chuyên mục mặc định của hệ thống
UPDATE [dbo].[Categories] 
SET [Slug] = 'cong-nghe-ai' 
WHERE [Id] = 'TECH' 
  AND ([Slug] IS NULL OR [Slug] = '' OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2);

UPDATE [dbo].[Categories] 
SET [Slug] = 'kinh-te-tai-chinh' 
WHERE ([Id] = 'ECONOMY' OR [Id] = 'ECON') 
  AND ([Slug] IS NULL OR [Slug] = '' OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2);

UPDATE [dbo].[Categories] 
SET [Slug] = 'the-thao-quoc-te' 
WHERE [Id] = 'SPORT' 
  AND ([Slug] IS NULL OR [Slug] = '' OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2);

UPDATE [dbo].[Categories] 
SET [Slug] = 'doi-song-khoa-hoc' 
WHERE [Id] = 'LIFE' 
  AND ([Slug] IS NULL OR [Slug] = '' OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2);

UPDATE [dbo].[Categories] 
SET [Slug] = 'giao-duc-ky-nang' 
WHERE ([Id] = 'EDUCATION' OR [Id] = 'EDU') 
  AND ([Slug] IS NULL OR [Slug] = '' OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2);
GO

-- 3. Backfill an toàn cho các bản ghi còn lại (hoặc bản ghi có slug không hợp lệ):
-- Thuật toán sanitize từ Id: chuyển lowercase, thay thế mọi ký tự ngoài [a-z0-9] thành dấu '-',
-- gộp liên tiếp '--' thành '-', cắt bỏ dấu '-' ở đầu và cuối chuỗi.
-- Nếu Id không chứa bất kỳ ký tự [a-z0-9] nào (ví dụ: '___'), dùng fallback an toàn 'category-{n}'.
DECLARE @catId NVARCHAR(50);
DECLARE @catCursor CURSOR;
SET @catCursor = CURSOR FOR 
    SELECT [Id] FROM [dbo].[Categories]
    WHERE [Slug] IS NULL 
       OR LTRIM(RTRIM([Slug])) = ''
       OR [Slug] LIKE '-%'
       OR [Slug] LIKE '%-'
       OR [Slug] LIKE '%--%'
       OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2
       OR [Slug] LIKE '%[A-Z]%' COLLATE Latin1_General_BIN2;

OPEN @catCursor;
FETCH NEXT FROM @catCursor INTO @catId;

DECLARE @rowSeq INT = 1;
WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @raw VARCHAR(200) = LOWER(LTRIM(RTRIM(@catId)));
    DECLARE @sanitized VARCHAR(200) = '';
    DECLARE @idx INT = 1;
    DECLARE @rawLen INT = LEN(@raw);
    DECLARE @curChar CHAR(1);
    DECLARE @prevChar CHAR(1) = '-';

    WHILE @idx <= @rawLen
    BEGIN
        SET @curChar = SUBSTRING(@raw, @idx, 1);
        IF @curChar LIKE '[a-z0-9]'
        BEGIN
            SET @sanitized = @sanitized + @curChar;
            SET @prevChar = @curChar;
        END
        ELSE
        BEGIN
            -- Chuyển ký tự đặc biệt thành dấu gạch nối (tránh trùng lặp liên tiếp)
            IF @prevChar <> '-' AND LEN(@sanitized) > 0
            BEGIN
                SET @sanitized = @sanitized + '-';
                SET @prevChar = '-';
            END
        END
        SET @idx = @idx + 1;
    END

    -- Cắt dấu gạch nối ở cuối chuỗi nếu có
    IF RIGHT(@sanitized, 1) = '-'
        SET @sanitized = LEFT(@sanitized, LEN(@sanitized) - 1);

    -- Deterministic safe fallback nếu ID hoàn toàn là ký tự đặc biệt
    IF LEN(@sanitized) = 0
        SET @sanitized = 'category-' + CAST(@rowSeq AS VARCHAR(10));

    UPDATE [dbo].[Categories] 
    SET [Slug] = @sanitized 
    WHERE [Id] = @catId;

    SET @rowSeq = @rowSeq + 1;
    FETCH NEXT FROM @catCursor INTO @catId;
END

CLOSE @catCursor;
DEALLOCATE @catCursor;
GO

-- 4. Xử lý trùng lặp slug (nếu có) trước khi tạo unique index
-- Đảm bảo lặp cho tới khi không còn bất kỳ slug nào bị trùng
WHILE EXISTS (
    SELECT 1 FROM [dbo].[Categories] 
    GROUP BY [Slug] 
    HAVING COUNT(*) > 1
)
BEGIN
    ;WITH DupCTE AS (
        SELECT [Id], [Slug],
               ROW_NUMBER() OVER (PARTITION BY [Slug] ORDER BY [Id]) AS RowNum
        FROM [dbo].[Categories]
    )
    UPDATE c
    SET c.[Slug] = c.[Slug] + '-' + CAST(cte.RowNum AS VARCHAR(10))
    FROM [dbo].[Categories] c
    INNER JOIN DupCTE cte ON c.[Id] = cte.[Id]
    WHERE cte.RowNum > 1;
END
GO

-- 5. Kiểm tra nghiêm ngặt tính toàn vẹn dữ liệu trước khi khóa ràng buộc
-- Slug bắt buộc: không NULL, không rỗng, không chứa ký tự ngoài [a-z0-9-], không bắt đầu/kết thúc bằng '-', không có '--'
DECLARE @invalidCount INT;
SELECT @invalidCount = COUNT(*) 
FROM [dbo].[Categories]
WHERE [Slug] IS NULL 
   OR LTRIM(RTRIM([Slug])) = ''
   OR [Slug] LIKE '-%'
   OR [Slug] LIKE '%-'
   OR [Slug] LIKE '%--%'
   OR [Slug] LIKE '%[^a-z0-9-]%' COLLATE Latin1_General_BIN2
   OR [Slug] LIKE '%[A-Z]%' COLLATE Latin1_General_BIN2;

IF @invalidCount > 0
BEGIN
    RAISERROR(N'Lỗi Migration 002: Vẫn còn %d chuyên mục có slug không thỏa regex ^[a-z0-9]+(?:-[a-z0-9]+)*$!', 16, 1, @invalidCount);
    RETURN;
END

DECLARE @dupCount INT;
SELECT @dupCount = COUNT(*)
FROM (
    SELECT [Slug] FROM [dbo].[Categories] GROUP BY [Slug] HAVING COUNT(*) > 1
) d;

IF @dupCount > 0
BEGIN
    RAISERROR(N'Lỗi Migration 002: Vẫn còn %d nhóm slug bị trùng lặp!', 16, 1, @dupCount);
    RETURN;
END
GO

-- 6. Đặt thuộc tính NOT NULL cho cột Slug sau khi đã bảo đảm 100% dữ liệu hợp lệ
ALTER TABLE [dbo].[Categories] 
ALTER COLUMN [Slug] [varchar](200) NOT NULL;
GO

-- 7. Tạo Unique Index cho cột Slug (idempotent: kiểm tra sys.indexes)
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes 
    WHERE object_id = OBJECT_ID(N'[dbo].[Categories]') AND name = N'UX_Categories_Slug'
)
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX [UX_Categories_Slug] 
    ON [dbo].[Categories]([Slug]);
END
GO
