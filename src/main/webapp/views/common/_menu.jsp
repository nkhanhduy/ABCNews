<%-- 
  File: _menu.jsp
  Description: Menu chuyên mục tin tức - phong cách thanh lịch, liền mạch với Header
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<nav class="site-nav navbar navbar-expand-lg navbar-dark">
    <div class="container">
        <button class="navbar-toggler py-1 px-2 border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-0">
                <li class="nav-item">
                    <a class="nav-link ${empty param.id ? 'active' : ''}" aria-current="page" href="${pageContext.request.contextPath}/home">
                        Trang chủ
                    </a>
                </li>
                <c:forEach var="cat" items="${categories}">
                    <li class="nav-item">
                        <a class="nav-link ${param.id eq cat.id ? 'active' : ''}" href="${pageContext.request.contextPath}/category?id=${cat.id}">
                            ${cat.name}
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</nav>