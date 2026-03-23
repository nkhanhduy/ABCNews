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
 * @author ABCNews Development Team
 * @version 1.0
 */
public class ValidationHelper {
    
    /**
     * Kiểm tra xem exception có phải là lỗi duplicate key (trùng khóa chính) không
     * 
     * Phương thức này phân tích message của exception để xác định xem có phải là lỗi
     * vi phạm ràng buộc PRIMARY KEY hoặc UNIQUE constraint không.
     * 
     * Thường được dùng để hiển thị thông báo lỗi thân thiện cho người dùng khi
     * họ cố gắng tạo ID hoặc email đã tồn tại.
     * 
     * @param e Exception cần kiểm tra (có thể là SQLException hoặc bất kỳ Exception nào)
     * @return true nếu exception message chứa các từ khóa liên quan đến duplicate key,
     *         false nếu không phải hoặc exception/null
     */
    public static boolean isDuplicateKeyException(Exception e) {
        if (e == null || e.getMessage() == null) {
            return false;
        }
        // Chuyển message sang chữ thường để so sánh không phân biệt hoa thường
        String errorMessage = e.getMessage().toLowerCase();
        // Kiểm tra các từ khóa thường gặp trong lỗi duplicate key
        return errorMessage.contains("primary key") || 
               errorMessage.contains("duplicate key") || 
               errorMessage.contains("unique");
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
