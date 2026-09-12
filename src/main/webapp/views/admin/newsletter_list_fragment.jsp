<%-- 
  File: newsletter_list_fragment.jsp
  Description: Fragment JSP để render danh sách newsletter (dùng cho AJAX)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="table-responsive">
    <table class="crud-table">
        <thead>
            <tr>
                <th style="width: 38%; text-align: left; padding-left: 20px;">Email</th>
                <th style="width: 22%; text-align: center;">Trạng thái</th>
                <th style="width: 20%; text-align: center;">Ngày đăng ký</th>
                <th style="width: 20%; text-align: center;">Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="n" items="${newsletterList}">
                <tr>
                    <td style="text-align: left; padding-left: 20px;">${n.email}</td>
                    <td style="text-align: center;">
                        <span class="badge ${n.enabled ? 'bg-success' : 'bg-secondary'}" style="min-width: 120px; text-align: center; display: inline-block;">
                            ${n.enabled ? 'Đang hoạt động' : 'Đã hủy'}
                        </span>
                    </td>
                    <td style="text-align: center;"><fmt:formatDate value="${n.subscribedDate}" pattern="dd/MM/yyyy"/></td>
                    <td style="text-align: center;">
                        <form method="post" action="${pageContext.request.contextPath}/admin/newsletters" style="display:inline; margin:0 3px;" onsubmit="return confirm('Xóa email này?')">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="email" value="${n.email}">
                            <button type="submit" class="btn btn-sm btn-delete" style="border:none; cursor:pointer;">Xóa</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/newsletters" style="display:inline; margin:0 3px;">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="email" value="${n.email}">
                            <button type="submit" class="btn btn-sm btn-update" style="border:none; cursor:pointer;">
                                ${n.enabled ? 'Hủy' : 'Kích hoạt'}
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty newsletterList}">
                <tr>
                    <td colspan="4" style="text-align: center; padding: 40px; color: #6c757d;">
                        <i class="fas fa-envelope" style="font-size: 2rem; margin-bottom: 10px; display: block; opacity: 0.5;"></i>
                        <p style="margin: 0; font-size: 1rem;">Không tìm thấy email nào.</p>
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
