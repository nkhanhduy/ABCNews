<%-- Đây là file hiển thị thông báo lỗi --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lỗi</title>
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <%-- CSS chung --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <%-- Và layout chung --%>
    <jsp:include page="/views/common/_header.jsp" />
    <jsp:include page="/views/common/_menu.jsp" />
    
    <div class="wrapper container">
        <main class="main-content">
            <h1>Đã có lỗi xảy ra!</h1>
            <p>Hệ thống vừa gặp một lỗi không mong muốn. Vui lòng thử lại sau.</p>
            <p><a href="${pageContext.request.contextPath}/home">Quay về trang chủ</a></p>
            
            <%-- 
              Phần này để debug, bạn có thể bật lên để xem lỗi
            <hr>
            <h3>Chi tiết lỗi (dành cho lập trình viên):</h3>
            <pre><% 
                if (exception != null) {
                    exception.printStackTrace(new java.io.PrintWriter(out));
                } else {
                    out.println("Không có thông tin lỗi chi tiết.");
                }
            %></pre>
            --%>
        </main>
    </div>

    <jsp:include page="/views/common/_footer.jsp" />
    
    <%-- Bootstrap 5 JS Bundle (cần cho dropdown hoạt động) --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
</body>
</html>