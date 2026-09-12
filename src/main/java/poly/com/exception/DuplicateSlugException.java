package poly.com.exception;

/**
 * Ngoại lệ ném ra khi có xung đột trùng lặp đường dẫn thân thiện (Slug) của chuyên mục.
 * Được chuyển đổi từ SQLException / Unique Constraint Violation (UX_Categories_Slug)
 * trong tầng DAO/JDBC mà không phụ thuộc vào Spring Framework.
 * 
 * @author Nguyen Duy Khanh
 */
public class DuplicateSlugException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String slug;

    public DuplicateSlugException(String message) {
        super(message);
        this.slug = null;
    }

    public DuplicateSlugException(String message, String slug) {
        super(message);
        this.slug = slug;
    }

    public DuplicateSlugException(String message, Throwable cause, String slug) {
        super(message, cause);
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }
}
