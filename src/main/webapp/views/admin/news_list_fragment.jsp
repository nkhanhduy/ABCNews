<%-- 
  File: news_list_fragment.jsp
  Description: Fragment JSP để render danh sách bản tin (dùng cho AJAX)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="table-responsive">
    <table class="crud-table">
        <thead>
            <tr>
                <th>Tiêu đề</th>
                <th>Loại tin</th>
                <th>Ngày đăng</th>
                <th>Tác giả</th>
                <th>Lượt xem</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="news" items="${newsList}">
                <tr>
                    <td>
                        <a href="${pageContext.request.contextPath}/detail?id=${news.id}" 
                           class="news-title-link" 
                           target="_blank"
                           title="Xem chi tiết tin này">
                            ${news.title}
                        </a>
                    </td>
                    <td style="text-align: center;">
                        <c:set var="categoryName" value="" />
                        <c:forEach var="cat" items="${categoriesList}">
                            <c:if test="${cat.id == news.categoryId}">
                                <c:set var="categoryName" value="${cat.name}" />
                            </c:if>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${not empty categoryName}">
                                <span class="badge bg-info" style="display: inline-block;">${categoryName}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">-</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty news.author}">
                                ${news.author}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted" style="font-style: italic;">Tác giả đã bị xóa</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${news.viewCount}</td>
                    <td>
                        <c:if test="${sessionScope.user.role or sessionScope.user.id == news.author}">
                            <a href="${pageContext.request.contextPath}/admin/news?action=edit&id=${news.id}" 
                               class="btn btn-sm btn-update">Sửa</a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/news" style="display:inline; margin:0 3px;" onsubmit="return confirm('Xóa tin này?')">
                                <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${news.id}">
                                <button type="submit" class="btn btn-sm btn-delete" style="border:none; cursor:pointer;">Xóa</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty newsList}">
                <tr>
                    <td colspan="6">Không có bản tin nào.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
