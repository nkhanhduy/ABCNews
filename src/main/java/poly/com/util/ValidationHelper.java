package poly.com.util;

/**
 * Lớp tiện ích (Helper Class) để xử lý các validation và kiểm tra dữ liệu chung
 * 
 * Lớp này cung cấp các phương thức static để:
 * - Kiểm tra exception có phải là lỗi duplicate key không
 * - Kiểm tra string có null, empty, hoặc blank không
 * 
 * Các phương thức này giúp giảm code trùng lặp và tăng tính nhất quán trong validation.
 * 
 * @author Nguyen Duy Khanh
 * @version 1.0
 */
public class ValidationHelper {
    
    /**
     * Kiểm tra xem exception có phải là lỗi duplicate key (trùng khóa chính) không
     * 
     * Phương thức này phân tích exception (kể cả exception lồng nhau qua getCause())
     * để xác định xem có phải là lỗi vi phạm ràng buộc PRIMARY KEY hoặc UNIQUE constraint không:
     * - SQL Server Error Code: 2627 (Violation of UNIQUE KEY constraint), 2601 (Cannot insert duplicate key row with unique index)
     * - SQLState: "23000" (Integrity constraint violation)
     * - Các thông báo ngoại lệ liên quan đến duplicate/unique key
     * 
     * @param e Exception hoặc Throwable cần kiểm tra
     * @return true nếu là lỗi vi phạm duplicate key / unique constraint, false nếu không phải
     */
    public static boolean isDuplicateKeyException(Throwable e) {
        if (e == null) {
            return false;
        }
        Throwable current = e;
        while (current != null) {
            if (current instanceof poly.com.exception.DuplicateSlugException) {
                return true;
            }
            if (current instanceof java.sql.SQLException sqlEx) {
                int errorCode = sqlEx.getErrorCode();
                String sqlState = sqlEx.getSQLState();
                if (errorCode == 2627 || errorCode == 2601 || "23000".equals(sqlState)) {
                    return true;
                }
            }
            String msg = current.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("primary key") || 
                    lower.contains("duplicate key") || 
                    lower.contains("unique") ||
                    lower.contains("ux_categories_slug")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * Kiểm tra xem ngoại lệ có phải do vi phạm tính duy nhất của đường dẫn thân thiện (Slug)
     * của bảng Categories (UX_Categories_Slug) hay không.
     * 
     * @param e Exception hoặc Throwable cần kiểm tra
     * @return true nếu vi phạm unique slug, false nếu không phải
     */
    public static boolean isDuplicateSlugException(Throwable e) {
        if (e == null) {
            return false;
        }
        Throwable current = e;
        while (current != null) {
            if (current instanceof poly.com.exception.DuplicateSlugException) {
                return true;
            }
            String msg = current.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("ux_categories_slug") || 
                   (lower.contains("slug") && (lower.contains("duplicate") || lower.contains("unique")))) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * Kiểm tra string có null hoặc empty (sau khi trim) không
     * 
     * Phương thức này kiểm tra:
     * - String có null không
     * - String có rỗng không (sau khi loại bỏ khoảng trắng đầu cuối)
     * 
     * Thường được dùng để validate input từ form hoặc parameter.
     * 
     * @param str String cần kiểm tra (có thể null)
     * @return true nếu string là null hoặc empty (sau khi trim), false nếu có nội dung
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra string có null, empty hoặc chỉ chứa khoảng trắng không
     * 
     * Phương thức này tương tự isNullOrEmpty(), nhưng tên gọi rõ ràng hơn
     * để chỉ ra rằng nó cũng kiểm tra string chỉ chứa khoảng trắng (blank).
     * 
     * @param str String cần kiểm tra (có thể null)
     * @return true nếu string là null, empty, hoặc chỉ chứa khoảng trắng, false nếu có nội dung
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
