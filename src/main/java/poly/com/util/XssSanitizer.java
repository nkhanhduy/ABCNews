package poly.com.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Tiện ích làm sạch dữ liệu đầu vào và chống tấn công Cross-Site Scripting (XSS).
 * Áp dụng nguyên lý Defense-in-Depth để bảo vệ toàn diện nội dung bài viết CKEditor.
 * 
 * @author ABCNews Development Team
 */
public class XssSanitizer {

    /**
     * Safelist được cấu hình riêng cho trình soạn thảo báo chí CKEditor 5:
     * Cho phép các thẻ định dạng chuẩn (p, h1-h6, strong, em, u, img, table, blockquote, a...)
     * nhưng lọc sạch triệt để các thẻ nguy hiểm (<script>, <iframe>, <object>, <embed>)
     * và các thuộc tính thực thi mã (onclick, onerror, onload, javascript:).
     */
    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            .addTags("figure", "figcaption", "article", "section")
            .addAttributes("figure", "class")
            .addAttributes("figcaption", "class")
            .addAttributes("img", "class", "style", "loading", "data-src")
            .addAttributes("p", "class", "style")
            .addAttributes("span", "class", "style")
            .addAttributes("div", "class", "style")
            .addAttributes("table", "class", "style", "border")
            .addAttributes("td", "class", "style", "colspan", "rowspan")
            .addAttributes("th", "class", "style", "colspan", "rowspan")
            .addProtocols("a", "href", "http", "https", "mailto", "tel")
            .addProtocols("img", "src", "http", "https", "data");

    /**
     * Làm sạch mã HTML từ CKEditor, loại bỏ XSS nhưng giữ trọn định dạng bài viết
     * @param rawHtml Nội dung HTML thô người dùng gửi lên
     * @return HTML đã được làm sạch và an toàn để lưu vào DB và hiển thị
     */
    public static String sanitize(String rawHtml) {
        if (rawHtml == null || rawHtml.trim().isEmpty()) {
            return "";
        }
        return Jsoup.clean(rawHtml.trim(), RICH_TEXT_SAFELIST);
    }

    /**
     * Loại bỏ hoàn toàn mọi thẻ HTML (dùng cho Tiêu đề, Tóm tắt, Nhãn, v.v.)
     * @param rawText Chuỗi văn bản thô
     * @return Chuỗi văn bản thuần túy không chứa thẻ HTML
     */
    public static String stripHtml(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return "";
        }
        return Jsoup.clean(rawText.trim(), Safelist.none());
    }
}
