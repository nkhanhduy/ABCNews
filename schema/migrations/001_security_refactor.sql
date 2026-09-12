-- Migration: 001_security_refactor.sql
-- Description: Add IsSuperAdmin column to Users table and create RememberTokens table for safe Remember Me

USE [ABCNews];
GO

-- 1. Thêm cột IsSuperAdmin vào bảng Users nếu chưa có
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'[dbo].[Users]') AND name = 'IsSuperAdmin'
)
BEGIN
    ALTER TABLE [dbo].[Users] 
    ADD [IsSuperAdmin] [bit] NOT NULL CONSTRAINT [DF_Users_IsSuperAdmin] DEFAULT ((0));
END
GO

-- 2. Migration 1 lần: Gán quyền SuperAdmin cho tài khoản khởi tạo chính (admin001) hoặc các tài khoản admin khởi nguồn
UPDATE [dbo].[Users] 
SET [IsSuperAdmin] = 1 
WHERE [Id] = 'admin001' OR [Email] = 'admin@abcnews.com' OR LOWER([Id]) LIKE 'super%';
GO

-- 3. Tạo bảng RememberTokens lưu SHA-256 hash của token thay vì plaintext / Base64(userId)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE object_id = OBJECT_ID(N'[dbo].[RememberTokens]'))
BEGIN
    CREATE TABLE [dbo].[RememberTokens](
        [id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] [varchar](50) NOT NULL,
        [token_hash] [varchar](64) NOT NULL,
        [created_at] [datetime] NOT NULL CONSTRAINT [DF_RememberTokens_created_at] DEFAULT (getdate()),
        [expires_at] [datetime] NOT NULL,
        [revoked] [bit] NOT NULL CONSTRAINT [DF_RememberTokens_revoked] DEFAULT ((0)),
        CONSTRAINT [FK_RememberTokens_Users] FOREIGN KEY([user_id]) 
            REFERENCES [dbo].[Users] ([Id]) ON DELETE CASCADE
    );

    CREATE NONCLUSTERED INDEX [IX_RememberTokens_token_hash] 
    ON [dbo].[RememberTokens]([token_hash], [revoked], [expires_at]);

    CREATE NONCLUSTERED INDEX [IX_RememberTokens_user_id] 
    ON [dbo].[RememberTokens]([user_id]);
END
GO
