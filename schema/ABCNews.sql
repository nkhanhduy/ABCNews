USE [master]
GO
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'ABCNews')
BEGIN
    CREATE DATABASE [ABCNews]
END
GO
USE [ABCNews]
GO
/****** Object:  Table [dbo].[OtpTokens]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OtpTokens](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [varchar](50) NOT NULL,
	[otp_code] [varchar](6) NOT NULL,
	[expiry_time] [datetime] NOT NULL,
	[created_at] [datetime] NULL,
	[is_used] [bit] NULL,
	[attempts] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[Id] [varchar](50) NOT NULL,
	[Password] [varchar](255) NOT NULL,
	[Fullname] [nvarchar](200) NULL,
	[Birthday] [date] NULL,
	[Gender] [bit] NULL,
	[Mobile] [varchar](20) NULL,
	[Email] [varchar](255) NULL,
	[Role] [bit] NOT NULL,
	[ImagePath] [varchar](255) NULL,
	[GoogleId] [varchar](255) NULL,
	[AuthProvider] [varchar](20) NULL,
	[Enabled] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[v_OtpTokens_Active]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[v_OtpTokens_Active] AS
SELECT 
    ot.id,
    ot.user_id,
    u.Fullname as user_name,
    u.Email as user_email,
    ot.otp_code,
    ot.expiry_time,
    ot.created_at,
    ot.is_used,
    ot.attempts,
    CASE 
        WHEN ot.is_used = 1 THEN 'Used'
        WHEN ot.expiry_time < GETDATE() THEN 'Expired'
        WHEN ot.attempts >= 3 THEN 'Locked'
        ELSE 'Active'
    END as status,
    DATEDIFF(SECOND, GETDATE(), ot.expiry_time) as seconds_remaining
FROM OtpTokens ot
INNER JOIN Users u ON ot.user_id = u.id;
GO
/****** Object:  Table [dbo].[ActivityLogs]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ActivityLogs](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [varchar](50) NOT NULL,
	[username] [nvarchar](100) NULL,
	[action_type] [varchar](20) NOT NULL,
	[entity_type] [varchar](50) NULL,
	[entity_id] [varchar](50) NULL,
	[description] [nvarchar](500) NULL,
	[old_data] [nvarchar](max) NULL,
	[new_data] [nvarchar](max) NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  View [dbo].[v_ActivityLogs_Recent]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[v_ActivityLogs_Recent] AS
SELECT TOP 100
    al.id,
    al.user_id,
    al.username,
    u.Email as user_email,
    al.action_type,
    al.entity_type,
    al.entity_id,
    al.description,
    al.ip_address,
    al.created_at,
    CASE 
        WHEN DATEDIFF(MINUTE, al.created_at, GETDATE()) < 1 THEN N'Vừa xong'
        WHEN DATEDIFF(MINUTE, al.created_at, GETDATE()) < 60 THEN CAST(DATEDIFF(MINUTE, al.created_at, GETDATE()) AS NVARCHAR) + N' phút trước'
        WHEN DATEDIFF(HOUR, al.created_at, GETDATE()) < 24 THEN CAST(DATEDIFF(HOUR, al.created_at, GETDATE()) AS NVARCHAR) + N' giờ trước'
        ELSE CAST(DATEDIFF(DAY, al.created_at, GETDATE()) AS NVARCHAR) + N' ngày trước'
    END as time_ago
FROM ActivityLogs al
LEFT JOIN Users u ON al.user_id = u.Id
ORDER BY al.created_at DESC;
GO
/****** Object:  Table [dbo].[Categories]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Categories](
	[Id] [varchar](50) NOT NULL,
	[Name] [nvarchar](200) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[News]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[News](
	[Id] [varchar](50) NOT NULL,
	[Title] [nvarchar](500) NOT NULL,
	[Summary] [nvarchar](1000) NULL,
	[Content] [nvarchar](max) NOT NULL,
	[Image] [nvarchar](500) NULL,
	[PostedDate] [datetime] NULL,
	[Author] [varchar](50) NULL,
	[ViewCount] [int] NULL,
	[CategoryId] [varchar](50) NULL,
	[Home] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Newsletters]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Newsletters](
	[Email] [varchar](255) NOT NULL,
	[Enabled] [bit] NULL,
	[SubscribedDate] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[Email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_ActivityLogs_ActionType]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [IX_ActivityLogs_ActionType] ON [dbo].[ActivityLogs]
(
	[action_type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [IX_ActivityLogs_CreatedAt]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [IX_ActivityLogs_CreatedAt] ON [dbo].[ActivityLogs]
(
	[created_at] DESC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_ActivityLogs_EntityType]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [IX_ActivityLogs_EntityType] ON [dbo].[ActivityLogs]
(
	[entity_type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_ActivityLogs_UserId]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [IX_ActivityLogs_UserId] ON [dbo].[ActivityLogs]
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_ActivityLogs_UserTime]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [IX_ActivityLogs_UserTime] ON [dbo].[ActivityLogs]
(
	[user_id] ASC,
	[created_at] DESC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [idx_otp_code]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [idx_otp_code] ON [dbo].[OtpTokens]
(
	[otp_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [idx_otp_expiry]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [idx_otp_expiry] ON [dbo].[OtpTokens]
(
	[expiry_time] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [idx_otp_user_id]    Script Date: 23/03/26 7:37:51 CH ******/
CREATE NONCLUSTERED INDEX [idx_otp_user_id] ON [dbo].[OtpTokens]
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[ActivityLogs] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[News] ADD  DEFAULT (getdate()) FOR [PostedDate]
GO
ALTER TABLE [dbo].[News] ADD  DEFAULT ((0)) FOR [ViewCount]
GO
ALTER TABLE [dbo].[News] ADD  DEFAULT ((0)) FOR [Home]
GO
ALTER TABLE [dbo].[Newsletters] ADD  DEFAULT ((1)) FOR [Enabled]
GO
ALTER TABLE [dbo].[Newsletters] ADD  DEFAULT (getdate()) FOR [SubscribedDate]
GO
ALTER TABLE [dbo].[OtpTokens] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[OtpTokens] ADD  DEFAULT ((0)) FOR [is_used]
GO
ALTER TABLE [dbo].[OtpTokens] ADD  DEFAULT ((0)) FOR [attempts]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT ('local') FOR [AuthProvider]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT ((1)) FOR [Enabled]
GO
ALTER TABLE [dbo].[ActivityLogs]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([Id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[News]  WITH CHECK ADD  CONSTRAINT [FK_News_Author] FOREIGN KEY([Author])
REFERENCES [dbo].[Users] ([Id])
GO
ALTER TABLE [dbo].[News] CHECK CONSTRAINT [FK_News_Author]
GO
ALTER TABLE [dbo].[News]  WITH CHECK ADD  CONSTRAINT [FK_News_Category] FOREIGN KEY([CategoryId])
REFERENCES [dbo].[Categories] ([Id])
GO
ALTER TABLE [dbo].[News] CHECK CONSTRAINT [FK_News_Category]
GO
ALTER TABLE [dbo].[OtpTokens]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([Id])
ON DELETE CASCADE
GO
/****** Object:  StoredProcedure [dbo].[sp_GetActivityLogs]    Script Date: 23/03/26 7:37:51 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_GetActivityLogs]
    @UserId VARCHAR(50) = NULL,
    @ActionType VARCHAR(20) = NULL,
    @EntityType VARCHAR(50) = NULL,
    @FromDate DATETIME = NULL,
    @ToDate DATETIME = NULL,
    @PageNumber INT = 1,
    @PageSize INT = 20
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    SELECT 
        al.id,
        al.user_id,
        al.username,
        u.Email as user_email,
        al.action_type,
        al.entity_type,
        al.entity_id,
        al.description,
        al.old_data,
        al.new_data,
        al.ip_address,
        al.user_agent,
        al.created_at
    FROM ActivityLogs al
    LEFT JOIN Users u ON al.user_id = u.Id
    WHERE 
        (@UserId IS NULL OR al.user_id = @UserId)
        AND (@ActionType IS NULL OR al.action_type = @ActionType)
        AND (@EntityType IS NULL OR al.entity_type = @EntityType)
        AND (@FromDate IS NULL OR al.created_at >= @FromDate)
        AND (@ToDate IS NULL OR al.created_at <= @ToDate)
    ORDER BY al.created_at DESC
    OFFSET @Offset ROWS
    FETCH NEXT @PageSize ROWS ONLY;
    
    -- Trả về tổng số records
    SELECT COUNT(*) as TotalRecords
    FROM ActivityLogs al
    WHERE 
        (@UserId IS NULL OR al.user_id = @UserId)
        AND (@ActionType IS NULL OR al.action_type = @ActionType)
        AND (@EntityType IS NULL OR al.entity_type = @EntityType)
        AND (@FromDate IS NULL OR al.created_at >= @FromDate)
        AND (@ToDate IS NULL OR al.created_at <= @ToDate);
END;
GO
USE [ABCNews]
GO

-- Nạp dữ liệu mẫu ban đầu (Sample Data) chuẩn UTF-8
IF NOT EXISTS (SELECT * FROM Users WHERE Id = 'admin001')
BEGIN
    INSERT INTO Users (Id, Password, Fullname, Birthday, Gender, Mobile, Email, Role, AuthProvider, Enabled)
    VALUES ('admin001', '123456', N'Tổng Biên Tập - Nguyễn Khánh Duy', '2004-05-15', 1, '0912345678', 'admin@abcnews.com', 1, 'local', 1);
END;

IF NOT EXISTS (SELECT * FROM Users WHERE Id = 'rep001')
BEGIN
    INSERT INTO Users (Id, Password, Fullname, Birthday, Gender, Mobile, Email, Role, AuthProvider, Enabled)
    VALUES ('rep001', '123456', N'Nhà Báo - Trần Khánh Duy', '1998-08-20', 1, '0987654321', 'reporter1@abcnews.com', 0, 'local', 1);
END;

IF NOT EXISTS (SELECT * FROM Users WHERE Id = 'rep002')
BEGIN
    INSERT INTO Users (Id, Password, Fullname, Birthday, Gender, Mobile, Email, Role, AuthProvider, Enabled)
    VALUES ('rep002', '123456', N'Biên Tập Viên - Lê Minh Tú', '1999-11-05', 0, '0908123456', 'reporter2@abcnews.com', 0, 'local', 1);
END;

IF NOT EXISTS (SELECT * FROM Categories WHERE Id = 'TECH')
    INSERT INTO Categories (Id, Name) VALUES ('TECH', N'Công nghệ & AI');
IF NOT EXISTS (SELECT * FROM Categories WHERE Id = 'ECONOMY')
    INSERT INTO Categories (Id, Name) VALUES ('ECONOMY', N'Kinh tế & Tài chính');
IF NOT EXISTS (SELECT * FROM Categories WHERE Id = 'SPORT')
    INSERT INTO Categories (Id, Name) VALUES ('SPORT', N'Thể thao Quốc tế');
IF NOT EXISTS (SELECT * FROM Categories WHERE Id = 'LIFE')
    INSERT INTO Categories (Id, Name) VALUES ('LIFE', N'Đời sống & Khoa học');
IF NOT EXISTS (SELECT * FROM Categories WHERE Id = 'EDUCATION')
    INSERT INTO Categories (Id, Name) VALUES ('EDUCATION', N'Giáo dục & Kỹ năng');

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS001')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS001', 
            N'Kỷ Nguyên Agentic AI: Bước Chuyển Mình Vượt Bậc Của Trí Tuệ Nhân Tạo Năm 2026', 
            N'Không dừng lại ở mô hình ngôn ngữ lớn (LLM) phản hồi thụ động, thế giới công nghệ năm 2026 chứng kiến làn sóng bùng nổ của Agentic AI - hệ thống tác tử thông minh có khả năng tự lập kế hoạch, phối hợp công cụ và giải quyết bài toán phức tạp độc lập.',
            N'<p class="lead">Năm 2026 đánh dấu cột mốc lịch sử khi trí tuệ nhân tạo chính thức chuyển mình từ các mô hình hội thoại thụ động sang kỷ nguyên <strong>Agentic AI</strong>.</p><h3>1. Sự Khác Biệt Giữa Generative AI Và Agentic AI</h3><p>Agentic AI sở hữu ba năng lực cốt lõi: Tự hoạch định mục tiêu (Goal Planning), Sử dụng công cụ linh hoạt (Tool Use) và Bộ nhớ dài hạn liên tục.</p><blockquote class="border-start border-4 border-success ps-3 my-3 fst-italic">"Agentic AI biến mỗi kỹ sư phần mềm trở thành một kiến trúc sư trưởng chỉ huy cả một đội ngũ tác tử AI chuyên trách."</blockquote>', 
            'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(HOUR, -3, GETDATE()), 'admin001', 8420, 'TECH', 1);
END;

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS002')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS002', 
            N'Tối Ưu Hóa Hạ Tầng Dữ Liệu Doanh Nghiệp Với HikariCP & Clean Architecture', 
            N'Việc áp dụng giải pháp Connection Pool hiện đại như HikariCP kết hợp chuẩn thiết kế 3 tầng (3-Tier Architecture) giúp các hệ thống báo điện tử và thương mại điện tử duy trì thời gian phản hồi dưới 50ms ngay cả trong các khung giờ cao điểm.',
            N'<p class="lead">Trong các hệ thống báo điện tử quy mô lớn, việc nghẽn cổ chai tại tầng truy xuất dữ liệu luôn là thách thức hàng đầu.</p><h3>1. Hiệu năng vượt trội của HikariCP</h3><p>FastList thay thế cho ArrayList cùng thuật toán lock-free giúp giảm tối đa overhead của Java Reflection.</p>', 
            'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(HOUR, -8, GETDATE()), 'rep001', 5690, 'TECH', 1);
END;

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS003')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS003', 
            N'Chiến Lược Bảo Mật Zero-Trust: Phòng Thủ Toàn Diện Trước Các Cuộc Tấn Công Số 2026', 
            N'Trước các hiểm họa mã độc tống tiền và tấn công mạng sử dụng AI ngày càng tinh vi, mô hình bảo mật Zero-Trust không còn là một lựa chọn xa xỉ mà đã trở thành tiêu chuẩn bắt buộc cho mọi nền tảng số.',
            N'<p class="lead">Triết lý căn bản của Zero-Trust: <em>"Never Trust, Always Verify"</em>. Mọi thực thể đều phải trải qua quy trình xác thực định danh và phân quyền nghiêm ngặt.</p>', 
            'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(DAY, -1, GETDATE()), 'admin001', 4210, 'TECH', 1);
END;

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS004')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS004', 
            N'Kinh Tế Số Việt Nam 2026: Động Lực Tăng Trưởng Đột Phá Đóng Góp Lớn Cho GDP Quốc Gia', 
            N'Báo cáo kinh tế quý 1/2026 cho thấy lĩnh vực thương mại điện tử, thanh toán không dùng tiền mặt và công nghệ số tiếp tục duy trì mức tăng trưởng ấn tượng trên 22%/năm, khẳng định vị thế trung tâm đổi mới sáng tạo khu vực.',
            N'<p class="lead">Nền kinh tế số Việt Nam đang bước vào giai đoạn tăng tốc mạnh mẽ với quy mô dự kiến vượt mốc 50 tỷ USD trong năm nay.</p>', 
            'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(HOUR, -5, GETDATE()), 'rep002', 6850, 'ECONOMY', 1);
END;

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS005')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS005', 
            N'Thị Trường Vốn Toàn Cầu Dịch Chuyển Mạnh Sang Các Dự Án Năng Lượng Xanh & Net Zero', 
            N'Các quỹ đầu tư mạo hiểm và ngân hàng thương mại quốc tế đang ưu tiên rót vốn vào các doanh nghiệp tuân thủ nghiêm ngặt tiêu chuẩn ESG, mở ra làn sóng phát hành trái phiếu xanh kỷ lục.',
            N'<p class="lead">Tính bền vững và trách nhiệm môi trường đã trở thành thước đo hàng đầu trong việc định giá doanh nghiệp.</p>', 
            'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(DAY, -2, GETDATE()), 'rep001', 3120, 'ECONOMY', 1);
END;

IF NOT EXISTS (SELECT * FROM News WHERE Id = 'NEWS006')
BEGIN
    INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home)
    VALUES ('NEWS006', 
            N'Đêm Chung Kết UEFA Champions League 2026: Đại Chiến Đỉnh Cao Và Cơn Mưa Bàn Thắng', 
            N'Trận chung kết Cúp C1 châu Âu đã cống hiến cho hàng trăm triệu khán giả toàn cầu 90 phút thi đấu kịch tính với chất lượng chuyên môn đỉnh cao, khẳng định sức hấp dẫn số một của bóng đá đương đại.',
            N'<p class="lead">Sân vận động chật kín hơn 75.000 khán giả đã được chứng kiến một trong những trận cầu kinh điển nhất lịch sử bóng đá hiện đại.</p>', 
            'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80', 
            DATEADD(HOUR, -12, GETDATE()), 'rep001', 7890, 'SPORT', 1);
END;
GO
USE [master]
GO
ALTER DATABASE [ABCNews] SET READ_WRITE 
GO
