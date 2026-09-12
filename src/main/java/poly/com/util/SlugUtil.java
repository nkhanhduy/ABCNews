package poly.com.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Tiện ích chuyển đổi chuỗi tiếng Việt có dấu thành Slug URL thân thiện chuẩn SEO.
 * 
 * Quy chuẩn:
 * - Chuyển sang chữ thường (lowercase)
 * - Xóa khoảng trắng thừa (trim)
 * - Thay thế ký tự đ/Đ thành d
 * - Phân rã Unicode NFD và loại bỏ toàn bộ dấu thanh/dấu phụ tiếng Việt
 * - Chuyển đổi khoảng trắng, dấu gạch dưới và ký tự đặc biệt thành dấu gạch nối (-)
 * - Gom nhiều dấu gạch nối liên tiếp thành một dấu (-) duy nhất
 * - Xóa dấu gạch nối ở đầu và cuối chuỗi
 */
public final class SlugUtil {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");

    private SlugUtil() {
        // Utility class, private constructor
    }

    /**
     * Tạo slug từ chuỗi tiêu đề hoặc tên chuyên mục.
     * 
     * @param input Chuỗi văn bản đầu vào (ví dụ: "Công nghệ & AI")
     * @return Chuỗi slug chuẩn SEO (ví dụ: "cong-nghe-ai"), hoặc "" nếu không hợp lệ
     */
    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        String text = input.trim();
        if (text.isEmpty()) {
            return "";
        }

        // 1. Chuyển chữ 'đ', 'Đ' thành 'd' trước khi phân rã NFD (do NFD không tách ký tự đ)
        text = text.replace('đ', 'd').replace('Đ', 'd')
                   .replace('ð', 'd').replace('Ð', 'd');

        // 2. Chuyển sang chữ thường
        text = text.toLowerCase(Locale.ROOT);

        // 3. Chuẩn hóa Unicode Form NFD và loại bỏ các dấu phụ kết hợp
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS.matcher(normalized).replaceAll("");

        // 4. Thay thế mọi ký tự không phải chữ cái a-z hoặc chữ số 0-9 thành dấu gạch ngang '-'
        String hyphens = NON_ALPHANUMERIC.matcher(withoutDiacritics).replaceAll("-");

        // 5. Rút gọn nhiều dấu gạch ngang liên tiếp thành 1 dấu duy nhất
        String singleHyphen = MULTI_HYPHEN.matcher(hyphens).replaceAll("-");

        // 6. Cắt bỏ dấu gạch ngang ở đầu và cuối chuỗi
        String slug = singleHyphen.replaceAll("^-+|-+$", "");

        return slug;
    }

    /**
     * Kiểm tra xem một chuỗi có phải là slug hợp lệ hay không.
     * Slug hợp lệ chỉ chứa chữ thường a-z, chữ số 0-9 và dấu gạch ngang giữa các từ.
     * 
     * @param slug Chuỗi slug cần kiểm tra
     * @return true nếu hợp lệ, false nếu null, rỗng hoặc chứa ký tự trái phép
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty() || slug.length() > 200) {
            return false;
        }
        return SLUG_PATTERN.matcher(slug.trim()).matches();
    }
}
