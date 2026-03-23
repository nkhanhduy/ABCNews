<%-- 
  File: index.jsp
  Description: Trang này tự động chuyển hướng đến HomeController.
  Chúng ta sẽ tạo servlet "HomeController" ở bước sau, nó sẽ được map với "/home"
--%>
<%
    response.sendRedirect(request.getContextPath() + "/home");
%>