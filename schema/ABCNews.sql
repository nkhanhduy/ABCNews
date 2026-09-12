USE [master]
GO
/****** Object:  Database [ABCNews]    Script Date: 23/03/26 7:37:50 CH ******/
CREATE DATABASE [ABCNews]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'ABCNews', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\ABCNews.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'ABCNews_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\ABCNews_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [ABCNews] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [ABCNews].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [ABCNews] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [ABCNews] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [ABCNews] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [ABCNews] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [ABCNews] SET ARITHABORT OFF 
GO
ALTER DATABASE [ABCNews] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [ABCNews] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [ABCNews] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [ABCNews] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [ABCNews] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [ABCNews] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [ABCNews] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [ABCNews] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [ABCNews] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [ABCNews] SET  ENABLE_BROKER 
GO
ALTER DATABASE [ABCNews] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [ABCNews] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [ABCNews] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [ABCNews] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [ABCNews] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [ABCNews] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [ABCNews] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [ABCNews] SET RECOVERY FULL 
GO
ALTER DATABASE [ABCNews] SET  MULTI_USER 
GO
ALTER DATABASE [ABCNews] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [ABCNews] SET DB_CHAINING OFF 
GO
ALTER DATABASE [ABCNews] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [ABCNews] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [ABCNews] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [ABCNews] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'ABCNews', N'ON'
GO
ALTER DATABASE [ABCNews] SET QUERY_STORE = ON
GO
ALTER DATABASE [ABCNews] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
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
USE [master]
GO
ALTER DATABASE [ABCNews] SET  READ_WRITE 
GO
