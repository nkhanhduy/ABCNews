<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="table-responsive">
    <table class="table table-hover table-striped mb-0">
        <thead style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
            <tr>
                <th style="width: 50px;">#</th>
                <th style="width: 150px;">Người dùng</th>
                <th style="width: 120px;">Hành động</th>
                <th style="width: 100px;">Đối tượng</th>
                <th>Mô tả</th>
                <th style="width: 180px;">Thời gian</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty logs}">
                    <tr>
                        <td colspan="6" class="text-center py-4 text-muted">
                            <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                            <p class="mb-0">Không có dữ liệu</p>
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="log" items="${logs}" varStatus="status">
                        <tr class="log-row">
                            <td class="text-center fw-bold">${(currentPage - 1) * pageSize + status.index + 1}</td>
                            <td>
                                <div>
                                    <div class="fw-semibold">${log.username}</div>
                                    <small class="text-muted">${log.userId}</small>
                                </div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${log.actionType == 'CREATE'}">
                                        <span class="badge bg-success">
                                            <i class="fas fa-plus-circle me-1"></i>Tạo mới
                                        </span>
                                    </c:when>
                                    <c:when test="${log.actionType == 'UPDATE'}">
                                        <span class="badge bg-warning">
                                            <i class="fas fa-edit me-1"></i>Cập nhật
                                        </span>
                                    </c:when>
                                    <c:when test="${log.actionType == 'DELETE'}">
                                        <span class="badge bg-danger">
                                            <i class="fas fa-trash me-1"></i>Xóa
                                        </span>
                                    </c:when>
                                    <c:when test="${log.actionType == 'LOGIN'}">
                                        <span class="badge bg-info">
                                            <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                                        </span>
                                    </c:when>
                                    <c:when test="${log.actionType == 'LOGOUT'}">
                                        <span class="badge bg-secondary">
                                            <i class="fas fa-sign-out-alt me-1"></i>Đăng xuất
                                        </span>
                                    </c:when>
                                    <c:when test="${log.actionType == 'EXPORT'}">
                                        <span class="badge bg-primary">
                                            <i class="fas fa-download me-1"></i>Xuất dữ liệu
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-dark">${log.actionType}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${not empty log.entityType}">
                                    <span class="badge bg-light text-dark border">
                                        <c:choose>
                                            <c:when test="${log.entityType == 'NEWS'}">
                                                <i class="fas fa-newspaper me-1"></i>Tin tức
                                            </c:when>
                                            <c:when test="${log.entityType == 'USER'}">
                                                <i class="fas fa-user me-1"></i>User
                                            </c:when>
                                            <c:when test="${log.entityType == 'CATEGORY'}">
                                                <i class="fas fa-tags me-1"></i>Danh mục
                                            </c:when>
                                            <c:when test="${log.entityType == 'NEWSLETTER'}">
                                                <i class="fas fa-envelope me-1"></i>Newsletter
                                            </c:when>
                                            <c:otherwise>
                                                ${log.entityType}
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </c:if>
                            </td>
                            <td>
                                <div class="text-truncate" style="max-width: 400px;" title="${log.description}">
                                    ${log.description}
                                </div>
                            </td>
                            <td>
                                <div>
                                    <fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                                <small class="text-muted">${log.timeAgo}</small>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- Pagination -->
<c:if test="${totalPages > 1}">
    <div class="mt-3">
        <nav>
            <ul class="pagination pagination-sm mb-0 justify-content-center">
                <!-- Previous -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-page="${currentPage - 1}">
                        <i class="fas fa-chevron-left"></i>
                    </a>
                </li>
                
                <!-- Pages -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="#" data-page="${i}">
                                ${i}
                            </a>
                        </li>
                    </c:if>
                </c:forEach>
                
                <!-- Next -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-page="${currentPage + 1}">
                        <i class="fas fa-chevron-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
        
        <div class="text-center text-muted mt-2">
            <small>
                Trang ${currentPage} / ${totalPages} 
                (Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize} / ${totalRecords})
            </small>
        </div>
    </div>
</c:if>

