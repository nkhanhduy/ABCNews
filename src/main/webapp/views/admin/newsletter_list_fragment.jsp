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
                <th>Email</th>
                <th>Trạng thái</th>
                <th>Ngày đăng ký</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="n" items="${newsletterList}">
                <tr>
                    <td>${n.email}</td>
                    <td style="text-align: center;">
                        <span class="badge ${n.enabled ? 'bg-success' : 'bg-secondary'}" style="min-width: 120px; text-align: center; display: inline-block;">
                            ${n.enabled ? 'Đang hoạt động' : 'Đã hủy'}
                        </span>
                    </td>
                    <td><fmt:formatDate value="${n.subscribedDate}" pattern="dd/MM/yyyy"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/newsletters?action=delete&email=${n.email}" 
                           class="btn btn-sm btn-delete" 
                           onclick="return confirm('Xóa email này?')">Xóa</a>
                        <a href="${pageContext.request.contextPath}/admin/newsletters?action=toggle&email=${n.email}" 
                           class="btn btn-sm btn-update">
                           ${n.enabled ? 'Hủy' : 'Kích hoạt'}
                        </a>
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
