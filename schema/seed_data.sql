USE [ABCNews];
GO

SET NOCOUNT ON;

-- 1. DỌN DẸP DỮ LIỆU CŨ ĐỂ NẠP BỘ DỮ LIỆU MẪU MỚI CHUẨN UNICODE UTF-8
DELETE FROM [dbo].[Comments];
DELETE FROM [dbo].[ActivityLogs];
DELETE FROM [dbo].[OtpTokens];
DELETE FROM [dbo].[News];
DELETE FROM [dbo].[Categories];
DELETE FROM [dbo].[Users];
DELETE FROM [dbo].[Newsletters];
GO

-- 2. TÀI KHOẢN NGƯỜI DÙNG (USERS)
-- Mật khẩu mặc định: 123456 (Hệ thống tự động nâng cấp mã hóa BCrypt khi đăng nhập)
INSERT INTO [dbo].[Users] ([Id], [Password], [Fullname], [Birthday], [Gender], [Mobile], [Email], [Role], [AuthProvider], [Enabled], [ImagePath])
VALUES 
('admin001', '123456', N'Tổng Biên Tập - Nguyễn Khánh Duy', '2004-05-15', 1, '0912345678', 'admin@abcnews.com', 1, 'local', 1, '/uploads/avatars/a0abfecd-d31a-41bd-b209-258fee975d1c.png'),
('rep001', '123456', N'Nhà Báo - Trần Khánh Duy', '1998-08-20', 1, '0987654321', 'reporter1@abcnews.com', 0, 'local', 1, NULL),
('rep002', '123456', N'Biên Tập Viên - Lê Minh Tú', '1999-11-05', 0, '0908123456', 'reporter2@abcnews.com', 0, 'local', 1, NULL);
GO

-- 3. CHUYÊN MỤC TIN TỨC (CATEGORIES)
INSERT INTO [dbo].[Categories] ([Id], [Name], [Slug])
VALUES 
('TECH', N'Công nghệ & AI', 'cong-nghe-ai'),
('ECONOMY', N'Kinh tế & Tài chính', 'kinh-te-tai-chinh'),
('SPORT', N'Thể thao Quốc tế', 'the-thao-quoc-te'),
('LIFE', N'Đời sống & Khoa học', 'doi-song-khoa-hoc'),
('EDUCATION', N'Giáo dục & Kỹ năng', 'giao-duc-ky-nang');
GO

-- 4. BẢN TIN CHI TIẾT (NEWS) - 11 BÀI BÁO CHUYÊN SÂU CHUẨN PHONG CÁCH TÒA SOẠN SỐ
-- BÀI 1: Công nghệ & AI (Tin tiêu điểm - Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('4594fcf6-c827-4ace-bc20-9bff52d424d8', 
N'Kỷ Nguyên Agentic AI: Bước Chuyển Mình Vượt Bậc Của Trí Tuệ Nhân Tạo Năm 2025',
N'Không dừng lại ở mô hình ngôn ngữ lớn (LLM) phản hồi thụ động, thế giới công nghệ năm 2025 chứng kiến làn sóng bùng nổ của Agentic AI - hệ thống tác tử thông minh có khả năng tự lập kế hoạch, phối hợp công cụ và giải quyết bài toán phức tạp độc lập.',
N'<p class="lead">Năm 2025 đánh dấu cột mốc lịch sử khi trí tuệ nhân tạo chính thức chuyển mình từ các mô hình hội thoại thụ động (Chatbot) sang kỷ nguyên <strong>Agentic AI (Tác tử Trí tuệ Nhân tạo)</strong>. Thay vì chỉ đưa ra câu trả lời dựa trên gợi ý từ người dùng, các hệ thống AI tác tử ngày nay đã có thể tự động lập trình kế hoạch hành động, phân rã công việc phức tạp thành các chuỗi hành động con và tương tác trực tiếp với các API, cơ sở dữ liệu để đạt được mục tiêu kinh doanh.</p>

<h3>1. Sự Khác Biệt Giữa Generative AI Truyền Thống Và Agentic AI</h3>
<p>Nếu như thế hệ Generative AI đầu tiên (2022 - 2024) tập trung chủ yếu vào việc sáng tạo văn bản và hình ảnh dựa trên câu lệnh đơn lẻ, thì Agentic AI sở hữu ba năng lực cốt lõi vượt trội:</p>
<ul>
    <li><strong>Khả năng Tự hoạch định (Goal Planning):</strong> AI tự phân tích mục tiêu lớn, định vị các rào cản và chia nhỏ lộ trình giải quyết thành từng bước logic.</li>
    <li><strong>Sử dụng Công cụ Linh hoạt (Tool Use & Function Calling):</strong> Khả năng tra cứu cơ sở dữ liệu nội bộ, gọi REST API bên ngoài, thực thi mã code và tự kiểm tra lỗi (Self-healing).</li>
    <li><strong>Bộ nhớ Dài hạn & Tự Học hỏi (Persistent Memory):</strong> Lưu trữ ngữ cảnh dự án liên tục qua vector database, không bị giới hạn bởi độ dài cửa sổ ngữ cảnh tạm thời.</li>
</ul>

<blockquote class="border-start border-4 border-success ps-3 my-4 fst-italic bg-light p-3 rounded">
    "Agentic AI không sinh ra để thay thế lập trình viên, mà nó biến mỗi kỹ sư phần mềm trở thành một kiến trúc sư trưởng chỉ huy cả một đội ngũ tác tử AI chuyên trách."
</blockquote>

<h3>2. Ứng Dụng Thực Tiễn Trong Doanh Nghiệp Hiện Đại</h3>
<p>Tại các tập đoàn công nghệ lớn, quy trình phát triển phần mềm (SDLC) đã được tái cấu trúc hoàn toàn. Các tác tử AI đảm nhận việc quét mã tự động, đề xuất các bản vá bảo mật theo chuẩn OWASP, tối ưu hóa câu truy vấn SQL và tạo tài liệu kiến trúc kỹ thuật đồng bộ. Điều này giúp rút ngắn chu kỳ phát hành sản phẩm từ hàng tháng xuống chỉ còn vài ngày làm việc.</p>

<p>Đặc biệt trong lĩnh vực tài chính và báo chí số, Agentic AI hỗ trợ phân tích xu hướng thị trường từ hàng triệu nguồn tin thời gian thực, cảnh báo rủi ro thanh khoản và hỗ trợ các nhà báo tổng hợp dữ liệu kiểm chứng với độ chính xác cao.</p>',
'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=80',
DATEADD(HOUR, -3, GETDATE()), 'admin001', 8420, 'TECH', 1);

-- BÀI 2: Công nghệ & Tối ưu Database (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('b3cc6eb9-74ff-49a3-8875-ba00e050a146', 
N'Tối Ưu Hóa Hạ Tầng Dữ Liệu Doanh Nghiệp Với HikariCP & Clean Architecture',
N'Việc áp dụng giải pháp Connection Pool hiện đại như HikariCP kết hợp chuẩn thiết kế 3 tầng (3-Tier Architecture) giúp các hệ thống báo điện tử và thương mại điện tử duy trì thời gian phản hồi dưới 50ms ngay cả trong các khung giờ cao điểm.',
N'<p class="lead">Trong các hệ thống báo điện tử và dịch vụ trực tuyến quy mô lớn, việc nghẽn cổ chai tại tầng truy xuất dữ liệu (Data Access Layer) luôn là cơn ác mộng của các kỹ sư hạ tầng. Bài viết này phân tích sâu cách thức ABCNews triển khai kiến trúc hồ kết nối HikariCP cùng Hibernate JPA để đạt thông lượng hàng chục nghìn yêu cầu mỗi giây.</p>

<h3>1. Tại Sao HikariCP Được Mệnh Danh Là Connection Pool Nhanh Nhất?</h3>
<p>HikariCP đạt được hiệu năng vượt trội so với các đối thủ lâu đời như Apache DBCP hay C3P0 nhờ vào hàng loạt tinh hoa tối ưu hóa cấp độ bytecode:</p>
<ul>
    <li><strong>FastList thay thế cho ArrayList:</strong> Loại bỏ các bước kiểm tra biên không cần thiết khi giải phóng Statement và ResultSet.</li>
    <li><strong>Sử dụng Javassist để sinh mã trực tiếp:</strong> Giảm thiểu tối đa overhead của Java Reflection trong lúc khởi tạo kết nối vật lý.</li>
    <li><strong>Thuật toán lock-free thông minh:</strong> Tối đa hóa khả năng xử lý đa luồng đồng thời của CPU hiện đại mà không gặp tình trạng tranh chấp tài nguyên (thread contention).</li>
</ul>

<h3>2. Mô Hình Phân Tầng Clean Architecture Tại ABCNews</h3>
<p>Toàn bộ mã nguồn backend được tổ chức chặt chẽ theo nguyên lý Clean Architecture:</p>
<ol>
    <li><strong>Presentation Layer:</strong> Jakarta Servlets và Bộ lọc mã hóa EncodingFilter xử lý routing, bảo vệ phân quyền AuthFilter.</li>
    <li><strong>Service & Business Layer:</strong> Đảm bảo toàn vẹn nghiệp vụ, tích hợp hashing BCrypt chuẩn OWASP và gửi thông báo OTP bất đồng bộ.</li>
    <li><strong>Data Persistence Layer:</strong> Sự kết hợp linh hoạt giữa JDBC Template thuần cho các báo cáo thống kê phức tạp và JPA Hibernate ORM cho các tác vụ CRUD an toàn.</li>
</ol>

<p>Nhờ cấu trúc này, thời gian xử lý trung bình cho mỗi lượt đọc tin tức chỉ dao động từ 12ms đến 25ms, mang lại trải nghiệm mượt mà tuyệt đối cho bạn đọc toàn cầu.</p>',
'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=1200&q=80',
DATEADD(HOUR, -8, GETDATE()), 'rep001', 5690, 'TECH', 1);

-- BÀI 3: Công nghệ & An ninh mạng (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('efcc2da5-e983-4aa0-9218-0248aa48bb08', 
N'Chiến Lược Bảo Mật Zero-Trust: Phòng Thủ Toàn Diện Trước Các Cuộc Tấn Công Số 2025',
N'Trước các hiểm họa mã độc tống tiền và tấn công mạng sử dụng AI ngày càng tinh vi, mô hình bảo mật Zero-Trust không còn là một lựa chọn xa xỉ mà đã trở thành tiêu chuẩn bắt buộc cho mọi nền tảng số.',
N'<p class="lead">Triết lý căn bản của Zero-Trust rất ngắn gọn nhưng kỷ luật: <em>"Không bao giờ tin tưởng, luôn luôn xác minh" (Never Trust, Always Verify)</em>. Mọi thực thể, dù ở trong hay ngoài mạng nội bộ, đều phải trải qua quy trình xác thực định danh và phân quyền nghiêm ngặt trước khi truy cập tài nguyên dữ liệu.</p>

<h3>1. Các Trụ Cột Chính Của Kiến Trúc Zero-Trust</h3>
<p>Để xây dựng một lá chắn số vững chắc cho hệ thống tòa soạn điện tử, các chuyên gia an ninh mạng khuyến nghị triển khai đồng bộ 4 giải pháp:</p>
<ul>
    <li><strong>Xác thực đa yếu tố thích ứng (Adaptive MFA):</strong> Kết hợp mật khẩu mạnh, mã OTP qua email thời gian thực và sinh trắc học thiết bị.</li>
    <li><strong>Nguyên tắc đặc quyền tối thiểu (Least Privilege Access):</strong> Chỉ cấp quyền hạn vừa đủ cho từng vai trò người dùng (Admin, Editor, Reporter, Reader) và tự động hết hạn phiên truy cập (Session Expiration).</li>
    <li><strong>Mã hóa dữ liệu toàn trình (End-to-End Encryption):</strong> Áp dụng chuẩn TLS 1.3 cho đường truyền và mã hóa AES-256 đối với các dữ liệu nhạy cảm lưu trong cơ sở dữ liệu.</li>
    <li><strong>Nhật ký kiểm toán liên tục (Continuous Audit Logging):</strong> Ghi lại chi tiết mọi hành vi đăng nhập, thêm, sửa, xóa dữ liệu thông qua bảng ActivityLogs để phục vụ phân tích điều tra số.</li>
</ul>

<p>Việc đầu tư bài bản vào kiến trúc an toàn thông tin ngay từ những dòng code đầu tiên chính là lời cam kết uy tín nhất của doanh nghiệp công nghệ đối với khách hàng và đối tác.</p>',
'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -1, GETDATE()), 'admin001', 4210, 'TECH', 1);

-- BÀI 4: Kinh tế & Tài chính số (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('b3cd69a0-1662-4ab4-98dc-f36eb0fbd99d', 
N'Kinh Tế Số Việt Nam 2025: Động Lực Tăng Trưởng Đột Phá Đóng Góp Lớn Cho GDP Quốc Gia',
N'Báo cáo kinh tế quý 1/2025 cho thấy lĩnh vực thương mại điện tử, thanh toán không dùng tiền mặt và công nghệ số tiếp tục duy trì mức tăng trưởng ấn tượng trên 22%/năm, khẳng định vị thế trung tâm đổi mới sáng tạo khu vực.',
N'<p class="lead">Nền kinh tế số Việt Nam đang bước vào giai đoạn tăng tốc mạnh mẽ với quy mô dự kiến vượt mốc 50 tỷ USD trong năm nay. Sự hội tụ giữa cơ sở hạ tầng mạng 5G phủ sóng toàn quốc, tỷ lệ dân số sở hữu smartphone trên 85% và hệ sinh thái thanh toán số đa dạng đã tạo bệ phóng vững chắc cho hàng nghìn doanh nghiệp khởi nghiệp.</p>

<h3>1. Bức Tranh Tăng Trưởng Của Các Ngành Tiên Phong</h3>
<p>Thương mại điện tử xuyên biên giới và công nghệ tài chính (Fintech) tiếp tục dẫn đầu làn sóng chuyển dịch. Các giải pháp ngân hàng mở (Open Banking) và API tài chính nhúng (Embedded Finance) cho phép người dân tiếp cận các dịch vụ vay vốn, bảo hiểm và đầu tư chỉ với vài chạm trên ứng dụng di động.</p>

<h3>2. Dòng Vốn FDI Công Nghệ Cao Đổ Bộ</h3>
<p>Việt Nam tiếp tục là điểm đến chiến lược của các tập đoàn bán dẫn và trung tâm dữ liệu (Data Center) toàn cầu. Nhiều dự án phòng nghiên cứu R&D quy mô lớn đã được khánh thành tại Hà Nội, Đà Nẵng và TP. Hồ Chí Minh, tạo ra hàng vạn việc làm kỹ thuật chất lượng cao cho thế hệ trẻ.</p>

<blockquote class="border-start border-4 border-success ps-3 my-4 fst-italic bg-light p-3 rounded">
    "Chuyển đổi số không chỉ là mục tiêu công nghệ đơn thuần, mà là chìa khóa mở ra cánh cửa tăng năng suất lao động và phát triển kinh tế bền vững cho đất nước."
</blockquote>',
'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80',
DATEADD(HOUR, -5, GETDATE()), 'rep002', 6850, 'ECONOMY', 1);

-- BÀI 5: Kinh tế & Đầu tư ESG (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('b2016873-a0a1-4148-bb54-cfe3d489fd6a', 
N'Thị Trường Vốn Toàn Cầu Dịch Chuyển Mạnh Sang Các Dự Án Năng Lượng Xanh & Net Zero',
N'Các quỹ đầu tư mạo hiểm và ngân hàng thương mại quốc tế đang ưu tiên rót vốn vào các doanh nghiệp tuân thủ nghiêm ngặt tiêu chuẩn ESG, mở ra làn sóng phát hành trái phiếu xanh kỷ lục.',
N'<p class="lead">Tính bền vững và trách nhiệm môi trường đã trở thành thước đo hàng đầu trong việc định giá doanh nghiệp. Trong năm 2025, lượng phát hành trái phiếu xanh (Green Bonds) trên toàn cầu đã tăng trưởng 35%, thu hút sự quan tâm đặc biệt từ các nhà đầu tư tổ chức.</p>

<h3>1. Tiêu Chuẩn ESG Không Còn Là Khẩu Hiệu</h3>
<p>Các doanh nghiệp áp dụng công nghệ chuyển đổi năng lượng mặt trời áp mái, tối ưu hóa chuỗi cung ứng giảm thiểu phát thải carbon đang được hưởng ưu đãi thuế suất và lãi suất vay vốn đặc biệt từ các định chế tài chính quốc tế.</p>

<h3>2. Cơ Hội Cho Doanh Nghiệp Trẻ Việt Nam</h3>
<p>Các công ty sản xuất nông nghiệp công nghệ cao và logistics xanh tại Việt Nam đang tận dụng tốt thời cơ này để mở rộng thị trường xuất khẩu sang các thị trường khó tính như Liên minh Châu Âu (EU) và Bắc Mỹ, nơi cơ chế điều chỉnh biên giới carbon (CBAM) bắt đầu có hiệu lực toàn diện.</p>',
'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -2, GETDATE()), 'rep001', 3120, 'ECONOMY', 1);

-- BÀI 6: Thể thao Quốc tế (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('de4aaf76-a857-4b7b-a31f-81d7cd1fa18b', 
N'Đêm Chung Kết UEFA Champions League 2025: Đại Chiến Đỉnh Cao Và Cơn Mưa Bàn Thắng',
N'Trận chung kết Cúp C1 châu Âu đã cống hiến cho hàng trăm triệu khán giả toàn cầu 90 phút thi đấu kịch tính với chất lượng chuyên môn đỉnh cao, khẳng định sức hấp dẫn số một của bóng đá đương đại.',
N'<p class="lead">Sân vận động chật kín hơn 75.000 khán giả đã được chứng kiến một trong những trận cầu kinh điển nhất lịch sử bóng đá hiện đại. Cả hai đội bóng hàng đầu châu Âu đã cống hiến lối chơi tấn công rực lửa với tốc độ luân chuyển bóng chóng mặt.</p>

<h3>1. Cuộc Đấu Trí Chiến Thuật Cân Não</h3>
<p>Cả hai huấn luyện viên đã mang đến những biến thể chiến thuật táo bạo. Việc sử dụng tiền vệ con thoi (Box-to-box) linh hoạt kết hợp với bẫy pressing tầm cao đã khiến hiệp một diễn ra với tốc độ nghẹt thở. Những bàn thắng liên tiếp được ghi trong sự phấn khích tột độ của người hâm mộ.</p>

<h3>2. Ngôi Sao Trẻ Tỏa Sáng Định Đoạt Trận Đấu</h3>
<p>Khoảnh khắc lóe sáng ở phút thứ 88 với pha solo qua ba hậu vệ đã ấn định chiến thắng chung cuộc 3-2. Chiến thắng không chỉ mang về chiếc cúp bạc danh giá mà còn khẳng định một kỷ nguyên mới của những tài năng trẻ trong làng túc cầu thế giới.</p>',
'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80',
DATEADD(HOUR, -12, GETDATE()), 'rep001', 7890, 'SPORT', 1);

-- BÀI 7: Thể thao & Khoa học Dữ liệu (Trang Thể thao)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('865ac7a4-483e-43ea-a6df-7fef4a7b6a60', 
N'Ứng Dụng Phân Tích Dữ Liệu Lớn & AI Trong Huấn Luyện Thể Thao Đỉnh Cao',
N'Từ thiết bị đeo theo dõi tải trọng cơ bắp đến camera nhận diện chiến thuật thời gian thực, khoa học dữ liệu đang trở thành vũ khí bí mật giúp các vận động viên phá vỡ mọi kỷ lục thế giới.',
N'<p class="lead">Thể thao hiện đại ngày nay không chỉ dựa vào thể lực và ý chí thi đấu của vận động viên mà còn là cuộc đua công nghệ phân tích dữ liệu giữa các trung tâm huấn luyện thể thao hàng đầu.</p>

<h3>1. Định Lượng Hóa Từng Bước Chạy</h3>
<p>Các thiết bị cảm biến gia tốc và cảm biến nhịp tim siêu nhỏ gắn trên trang phục thi đấu cung cấp hàng nghìn điểm dữ liệu mỗi giây. Đội ngũ y tế và phân tích hiệu suất có thể phát hiện sớm nguy cơ quá tải cơ bắp trước 48 giờ, giảm thiểu tối đa các chấn thương dây chằng nghiêm trọng.</p>

<h3>2. Trực Quan Hóa Chiến Thuật Bằng AI</h3>
<p>Hệ thống camera thị giác máy tính tự động vẽ bản đồ nhiệt (Heatmap), tính toán xác suất thành công của các đường chuyền và đề xuất phương án di chuyển tối ưu cho toàn đội hình trong giờ nghỉ giải lao.</p>',
'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -3, GETDATE()), 'rep002', 2450, 'SPORT', 0);

-- BÀI 8: Đời sống & Sức khỏe (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('c354b4da-8d60-4072-90c9-279978ee36ae', 
N'Cân Bằng Công Việc & Cuộc Sống: Cẩm Nang Chăm Sóc Sức Khỏe Cho Kỹ Sư Công Nghệ',
N'Đối mặt với áp lực deadline và nhịp sống số hối hả, hội chứng kiệt sức (burnout) đang trở thành thử thách lớn của giới trẻ. Các chuyên gia y tế chia sẻ giải pháp thiết lập ranh giới làm việc lành mạnh.',
N'<p class="lead">Làm việc linh hoạt (Remote / Hybrid Work) mang lại nhiều tiện ích nhưng cũng dễ xóa nhòa ranh giới giữa giờ làm việc và thời gian nghỉ ngơi cá nhân. Việc ngồi liên tục trước màn hình máy tính từ 10 đến 12 tiếng mỗi ngày gây ảnh hưởng tiêu cực đến cột sống, thị lực và giấc ngủ.</p>

<h3>1. Quy Tắc 20-20-20 Và Vận Động Gián Đoạn</h3>
<p>Cứ sau mỗi 20 phút tập trung vào màn hình, hãy nhìn xa 20 feet (khoảng 6 mét) trong 20 giây để thư giãn cơ mắt. Đồng thời, kết hợp các động tác kéo giãn cơ tại chỗ giúp tuần hoàn máu lưu thông tốt hơn.</p>

<h3>2. Thiết Lập Ranh Giới Số (Digital Detox)</h3>
<p>Tắt toàn bộ thông báo công việc sau 20h00 tối và dành thời gian cho các hoạt động ngoài trời, đọc sách hoặc nấu ăn cùng gia đình. Một cơ thể khỏe mạnh và một tinh thần sảng khoái chính là nền tảng vững chắc nhất cho sự sáng tạo bền bỉ.</p>',
'https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -1, GETDATE()), 'rep002', 5230, 'LIFE', 1);

-- BÀI 9: Đời sống & Không gian Vũ trụ (Trang Đời sống)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('f9ac2d87-1803-464a-b9bf-ddbac175ad91', 
N'Kính Viễn Vọng Không Gian Thế Hệ Mới Khám Phá Thêm Dấu Vết Nước Trên Hành Tinh Mới',
N'Cơ quan Hàng không Vũ trụ Quốc tế vừa công bố những hình ảnh quang phổ sắc nét chưa từng có về bầu khí quyển của ngoại hành tinh cách Trái Đất 120 năm ánh sáng.',
N'<p class="lead">Sử dụng công nghệ cảm biến hồng ngoại tối tân, các nhà thiên văn học đã phát hiện thấy dấu hiệu rõ ràng của hơi nước, khí methane và carbon dioxide trong khí quyển của hành tinh K2-18b, một thiên thể nằm trong vùng có thể duy trì sự sống của ngôi sao chủ.</p>

<h3>1. Bước Tiến Lớn Của Khoa Học Thiên Văn</h3>
<p>Dữ liệu quang phổ truyền về được phân tích thông qua các thuật toán học sâu giúp lọc bỏ nhiễu từ ánh sáng sao mẹ, mở ra hy vọng mới cho việc tìm kiếm các dấu hiệu sinh học ngoài Trái Đất trong thập kỷ tới.</p>',
'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -4, GETDATE()), 'admin001', 1980, 'LIFE', 0);

-- BÀI 10: Giáo dục & Đào tạo (Home)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('65bcca03-cf7b-4cd6-8945-c7f88faa6395', 
N'Chuyển Đổi Số Giáo Dục Đại Học: Mô Hình Học Tập Thực Chiến Liên Kết Doanh Nghiệp',
N'Các trường đại học công nghệ hàng đầu đang tái cấu trúc chương trình đào tạo theo hướng thực chiến, tích hợp dự án thực tế giúp sinh viên vững vàng kỹ năng chuyên môn trước khi tốt nghiệp.',
N'<p class="lead">Phương pháp học vẹt lý thuyết hàn lâm đang nhường chỗ cho mô hình đào tạo theo dự án thực tế (Project-Based Learning). Sinh viên được trực tiếp tham gia xây dựng các hệ thống phần mềm hoàn chỉnh, làm quen với quy trình Git workflow, CI/CD và kiến trúc container ngay từ năm thứ hai.</p>

<h3>1. Doanh Nghiệp Đồng Hành Cùng Nhà Trường</h3>
<p>Các doanh nghiệp công nghệ đóng vai trò là cố vấn chuyên môn (Mentor), đưa ra các bài toán thực tế của thị trường để sinh viên tìm giải pháp. Cách tiếp cận này giúp rút ngắn khoảng cách giữa giảng đường và thực tế tuyển dụng.</p>

<h3>2. Trang Bị Tư Duy Giải Quyết Vấn Đề</h3>
<p>Bên cạnh kiến thức lập trình chuyên sâu, các kỹ năng mềm như làm việc nhóm, giao tiếp thuyết trình và tư duy phản biện được lồng ghép chặt chẽ vào từng học phần, tạo nên chân dung những kỹ sư công nghệ toàn diện.</p>',
'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -2, GETDATE()), 'rep001', 3870, 'EDUCATION', 1);

-- BÀI 11: Giáo dục & Kỹ năng (Trang Giáo dục)
INSERT INTO [dbo].[News] ([Id], [Title], [Summary], [Content], [Image], [PostedDate], [Author], [ViewCount], [CategoryId], [Home])
VALUES 
('f98c028a-b6ea-454a-a7a5-df120ca29ed2', 
N'Kỹ Năng Thế Kỷ 21: Năng Lực Học Hỏi Trọn Đời (Lifelong Learning) Trong Kỷ Nguyên AI',
N'Khi công nghệ liên tục thay đổi với tốc độ cấp số nhân, khả năng quan trọng nhất của mỗi cá nhân không phải là những gì đã biết, mà là tốc độ tiếp thu kiến thức mới và từ bỏ những thói quen cũ.',
N'<p class="lead">Trí tuệ nhân tạo có thể viết code, vẽ tranh và phân tích dữ liệu, nhưng năng lực đặt câu hỏi đúng, đồng cảm với người dùng và tư duy liên ngành vẫn là đặc quyền riêng có của con người. Để không bị tụt lại phía sau, mỗi người lao động cần chủ động xây dựng lộ trình học tập trọn đời.</p>

<h3>1. Xây Dựng Thói Quen Đọc Sâu Và Đào Sâu Bản Chất</h3>
<p>Thay vì chỉ lướt qua các đoạn tóm tắt bề nổi trên mạng xã hội, hãy rèn luyện thói quen đọc các bài phân tích chuyên sâu, tài liệu kỹ thuật gốc (Official Documentation) và tự tay thực hành các dự án thực tế.</p>',
'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80',
DATEADD(DAY, -5, GETDATE()), 'admin001', 2940, 'EDUCATION', 0);
GO

-- 5. DANH SÁCH NEWSLETTER ĐĂNG KÝ MẪU
INSERT INTO [dbo].[Newsletters] ([Email], [Enabled], [SubscribedDate])
VALUES 
('contact@vietnamtech.org', 1, DATEADD(DAY, -10, GETDATE())),
('lead.developer@fpt.edu.vn', 1, DATEADD(DAY, -7, GETDATE())),
('recruiter.talent@vng.com.vn', 1, DATEADD(DAY, -3, GETDATE())),
('subscriber.digest@abcnews.com', 1, DATEADD(DAY, -1, GETDATE()));
GO

-- 6. NHẬT KÝ HOẠT ĐỘNG QUẢN TRỊ MẪU (ACTIVITY LOGS)
INSERT INTO [dbo].[ActivityLogs] ([user_id], [username], [action_type], [entity_type], [entity_id], [description], [created_at])
VALUES 
('admin001', N'Tổng Biên Tập - Nguyễn Khánh Duy', 'LOGIN', 'Auth', 'admin001', N'Đăng nhập hệ thống quản trị thành công', DATEADD(MINUTE, -120, GETDATE())),
('admin001', N'Tổng Biên Tập - Nguyễn Khánh Duy', 'CREATE', 'News', '4594fcf6-c827-4ace-bc20-9bff52d424d8', N'Xuất bản bài viết tiêu điểm: Kỷ Nguyên Agentic AI 2025', DATEADD(MINUTE, -115, GETDATE())),
('rep001', N'Nhà Báo - Trần Khánh Duy', 'CREATE', 'News', 'b3cc6eb9-74ff-49a3-8875-ba00e050a146', N'Tạo mới bài viết: Tối Ưu Hóa Hạ Tầng Dữ Liệu HikariCP', DATEADD(MINUTE, -90, GETDATE())),
('rep002', N'Biên Tập Viên - Lê Minh Tú', 'CREATE', 'News', 'b3cd69a0-1662-4ab4-98dc-f36eb0fbd99d', N'Xuất bản bài viết: Kinh Tế Số Việt Nam 2025', DATEADD(MINUTE, -60, GETDATE())),
('admin001', N'Tổng Biên Tập - Nguyễn Khánh Duy', 'UPDATE', 'Category', 'TECH', N'Cập nhật tên danh mục thành Công nghệ & AI', DATEADD(MINUTE, -30, GETDATE()));
GO

-- 7. BÌNH LUẬN ĐỘC GIẢ MẪU (COMMENTS)
-- Status: 0 (Chờ duyệt), 1 (Đã duyệt), 2 (Từ chối)
INSERT INTO [dbo].[Comments] ([NewsId], [AuthorName], [AuthorEmail], [Content], [CreatedDate], [Status])
VALUES 
('4594fcf6-c827-4ace-bc20-9bff52d424d8', N'Lê Hoàng Nam', 'hoangnam.dev@gmail.com', N'Bài viết rất sâu sắc và đón đầu xu hướng công nghệ Agentic AI năm 2025. Cảm ơn tòa soạn!', DATEADD(HOUR, -2, GETDATE()), 1),
('4594fcf6-c827-4ace-bc20-9bff52d424d8', N'Nguyễn Thu Thảo', 'thuthao.tech@outlook.com', N'Hệ thống tác tử thông minh thực sự sẽ giải phóng sức lao động cho các lập trình viên.', DATEADD(MINUTE, -45, GETDATE()), 1),
('4594fcf6-c827-4ace-bc20-9bff52d424d8', N'Đặng Quốc Bảo', 'baodang@yahoo.com', N'Liệu trong tương lai Agentic AI có khả năng tự sửa lỗi production mà không cần con người review không?', DATEADD(MINUTE, -10, GETDATE()), 0),
('b3cd69a0-1662-4ab4-98dc-f36eb0fbd99d', N'Phạm Quang Huy', 'quanghuy@fpt.com', N'Số liệu thống kê về kinh tế số rất chi tiết và đáng tin cậy. Chúc tòa soạn phát triển mạnh mẽ!', DATEADD(MINUTE, -5, GETDATE()), 0);
GO
