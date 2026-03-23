package poly.com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import poly.com.entity.News;

/**
 * Tạo HTML template cho email newsletter
 */
public class EmailTemplate {
    /**
     * Tạo nội dung HTML cho email newsletter
     * 
     * @param news Đối tượng News chứa thông tin tin tức
     * @param baseUrl Base URL đầy đủ của ứng dụng (ví dụ: http://localhost:8080/ABCNews) để tạo absolute URL
     * @param subscriberEmail Email của subscriber (để tạo link unsubscribe)
     * @param servletContextPath Đường dẫn thực tế của thư mục /uploads trên server (từ ServletContext.getRealPath("/uploads"))
     * @return Chuỗi HTML đầy đủ cho email
     */
    public static String createNewsletterEmail(News news, String baseUrl, String subscriberEmail, String servletContextPath) {
        String imageHtml = "";
        if (news.getImage() != null && !news.getImage().isEmpty()) {
            String imageUrl = news.getImage();
            String imageDataUri = null;
            
            // Nếu đã là absolute URL (http/https), giữ nguyên
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                // Đã là absolute URL, không cần xử lý
                imageHtml = "<img src=\"" + imageUrl + "\" alt=\"" + escapeHtml(news.getTitle()) + "\" style=\"max-width: 100%; height: auto; border-radius: 8px; margin: 15px 0;\">";
            } else {
                // Loại bỏ contextPath nếu có trong imageUrl (tránh trùng lặp)
                // Ví dụ: /ABCNews/uploads/image.jpg -> /uploads/image.jpg
                String normalizedPath = imageUrl;
                
                // Tìm vị trí của /uploads/ trong đường dẫn
                int uploadsIndex = normalizedPath.indexOf("/uploads/");
                String fileName = null;
                if (uploadsIndex >= 0) {
                    // Có /uploads/ trong đường dẫn
                    fileName = normalizedPath.substring(uploadsIndex + "/uploads/".length());
                } else {
                    // Kiểm tra nếu có /uploads (không có dấu / cuối)
                    uploadsIndex = normalizedPath.indexOf("/uploads");
                    if (uploadsIndex >= 0 && uploadsIndex + 7 < normalizedPath.length()) {
                        String afterUploads = normalizedPath.substring(uploadsIndex + "/uploads".length());
                        if (afterUploads.startsWith("/")) {
                            fileName = afterUploads.substring(1);
                        } else {
                            fileName = afterUploads;
                        }
                    } else {
                        // Không tìm thấy /uploads, có thể là tên file trực tiếp
                        if (normalizedPath.startsWith("/")) {
                            fileName = normalizedPath.substring(1);
                        } else {
                            fileName = normalizedPath;
                        }
                    }
                }
                
                // Thử embed ảnh dưới dạng base64 (fallback nếu CID không hoạt động)
                if (servletContextPath != null && fileName != null) {
                    File imageFile = new File(servletContextPath, fileName);
                    if (imageFile.exists() && imageFile.isFile()) {
                        try {
                            imageDataUri = convertImageToBase64(imageFile);
                        } catch (IOException e) {
                            System.err.println("Lỗi đọc file ảnh để embed: " + e.getMessage());
                            // Fallback về URL nếu không đọc được file
                        }
                    }
                }
                
                // Nếu không embed được, dùng absolute URL
                if (imageDataUri == null) {
                    // Đảm bảo normalizedPath bắt đầu bằng /
                    if (!normalizedPath.startsWith("/")) {
                        normalizedPath = "/" + normalizedPath;
                    }
                    // Tạo absolute URL bằng cách ghép baseUrl + normalizedPath
                    imageUrl = baseUrl + normalizedPath;
                    imageHtml = "<img src=\"" + imageUrl + "\" alt=\"" + escapeHtml(news.getTitle()) + "\" style=\"max-width: 100%; height: auto; border-radius: 8px; margin: 15px 0;\">";
                } else {
                    // Sử dụng base64 embedded image
                    imageHtml = "<img src=\"" + imageDataUri + "\" alt=\"" + escapeHtml(news.getTitle()) + "\" style=\"max-width: 100%; height: auto; border-radius: 8px; margin: 15px 0;\">";
                }
            }
        }
        
        String detailUrl = baseUrl + "/detail?id=" + news.getId();
        String unsubscribeUrl;
        try {
            unsubscribeUrl = baseUrl + "/newsletter/unsubscribe?email=" + URLEncoder.encode(subscriberEmail, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            // Fallback nếu có lỗi encoding
            unsubscribeUrl = baseUrl + "/newsletter/unsubscribe?email=" + subscriberEmail;
        }
        
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
               "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
               "<h1 style='margin: 0;'>ABC News Leu Leu</h1>" +
               "</div>" +
               "<div style='background: #f8f9fa; padding: 25px; border-radius: 0 0 8px 8px;'>" +
               "<h2 style='color: #667eea; margin-top: 0;'>" + escapeHtml(news.getTitle()) + "</h2>" +
               imageHtml +
               "<p style='font-size: 16px; color: #555;'>" + escapeHtml(news.getSummary() != null ? news.getSummary() : "") + "</p>" +
               "<a href=\"" + detailUrl + "\" style='display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold;'>Đọc thêm →</a>" +
               "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
               "<p style='font-size: 12px; color: #999; text-align: center;'>" +
               "Bạn nhận được email này vì đã đăng ký nhận bản tin từ ABC News Leu Leu.<br>" +
               "<a href=\"" + unsubscribeUrl + "\" style='color: #667eea;'>Hủy đăng ký</a>" +
               "</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Tạo nội dung HTML cho email newsletter sử dụng CID (Content-ID) để reference ảnh đã attach
     * 
     * @param news Đối tượng News chứa thông tin tin tức
     * @param baseUrl Base URL đầy đủ của ứng dụng
     * @param subscriberEmail Email của subscriber
     * @return Chuỗi HTML đầy đủ cho email
     */
    public static String createNewsletterEmailWithCID(News news, String baseUrl, String subscriberEmail) {
        String imageHtml = "";
        if (news.getImage() != null && !news.getImage().isEmpty()) {
            // Sử dụng CID để reference ảnh đã attach
            imageHtml = "<img src=\"cid:news-image\" alt=\"" + escapeHtml(news.getTitle()) + "\" style=\"max-width: 100%; height: auto; border-radius: 8px; margin: 15px 0;\">";
        }
        
        String detailUrl = baseUrl + "/detail?id=" + news.getId();
        String unsubscribeUrl;
        try {
            unsubscribeUrl = baseUrl + "/newsletter/unsubscribe?email=" + URLEncoder.encode(subscriberEmail, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            unsubscribeUrl = baseUrl + "/newsletter/unsubscribe?email=" + subscriberEmail;
        }
        
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
               "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
               "<h1 style='margin: 0;'>ABC News Leu Leu</h1>" +
               "</div>" +
               "<div style='background: #f8f9fa; padding: 25px; border-radius: 0 0 8px 8px;'>" +
               "<h2 style='color: #667eea; margin-top: 0;'>" + escapeHtml(news.getTitle()) + "</h2>" +
               imageHtml +
               "<p style='font-size: 16px; color: #555;'>" + escapeHtml(news.getSummary() != null ? news.getSummary() : "") + "</p>" +
               "<a href=\"" + detailUrl + "\" style='display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold;'>Đọc thêm →</a>" +
               "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
               "<p style='font-size: 12px; color: #999; text-align: center;'>" +
               "Bạn nhận được email này vì đã đăng ký nhận bản tin từ ABC News Leu Leu.<br>" +
               "<a href=\"" + unsubscribeUrl + "\" style='color: #667eea;'>Hủy đăng ký</a>" +
               "</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Convert file ảnh sang base64 data URI để embed vào email
     * 
     * @param imageFile File ảnh cần convert
     * @return Data URI string (ví dụ: "data:image/jpeg;base64,/9j/4AAQSkZJRg...")
     * @throws IOException Nếu có lỗi khi đọc file
     */
    private static String convertImageToBase64(File imageFile) throws IOException {
        // Xác định MIME type từ extension
        String fileName = imageFile.getName().toLowerCase();
        String mimeType = "image/jpeg"; // Mặc định
        if (fileName.endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (fileName.endsWith(".webp")) {
            mimeType = "image/webp";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        }
        
        // Đọc file và convert sang base64
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            byte[] imageBytes = fis.readAllBytes();
            String base64String = Base64.getEncoder().encodeToString(imageBytes);
            return "data:" + mimeType + ";base64," + base64String;
        }
    }
    
    /**
     * Tạo nội dung HTML cho email gửi mã OTP
     * 
     * @param otpCode Mã OTP 6 số
     * @param fullname Tên đầy đủ của người dùng
     * @return Chuỗi HTML đầy đủ cho email OTP
     */
    public static String createOtpEmail(String otpCode, String fullname) {
        return "<!DOCTYPE html>" +
                "<html lang=\"vi\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Mã OTP Đặt Lại Mật Khẩu</title>" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;\">" +
                "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f4f4f4; padding: 20px;\">" +
                "        <tr>" +
                "            <td align=\"center\">" +
                "                <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">" +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;\">" +
                "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px;\">🔐 Đặt Lại Mật Khẩu</h1>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Content -->" +
                "                    <tr>" +
                "                        <td style=\"padding: 40px 30px;\">" +
                "                            <p style=\"color: #333333; font-size: 16px; line-height: 1.6; margin-top: 0;\">Xin chào <strong>" + escapeHtml(fullname) + "</strong>,</p>" +
                "                            " +
                "                            <p style=\"color: #666666; font-size: 15px; line-height: 1.6;\">Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản ABC News. Sử dụng mã OTP bên dưới để tiếp tục:</p>" +
                "                            " +
                "                            <!-- OTP Box -->" +
                "                            <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 30px 0;\">" +
                "                                <tr>" +
                "                                    <td align=\"center\">" +
                "                                        <div style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 10px; padding: 25px; display: inline-block;\">" +
                "                                            <p style=\"color: #ffffff; font-size: 14px; margin: 0 0 10px 0; text-transform: uppercase; letter-spacing: 1px;\">Mã OTP của bạn</p>" +
                "                                            <p style=\"color: #ffffff; font-size: 36px; font-weight: bold; margin: 0; letter-spacing: 8px; font-family: 'Courier New', monospace;\">" + otpCode + "</p>" +
                "                                        </div>" +
                "                                    </td>" +
                "                                </tr>" +
                "                            </table>" +
                "                            " +
                "                            <!-- Warning Box -->" +
                "                            <div style=\"background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px;\">" +
                "                                <p style=\"color: #856404; font-size: 14px; margin: 0; line-height: 1.5;\">" +
                "                                    <strong>⚠️ Lưu ý quan trọng:</strong><br>" +
                "                                    • Mã OTP này có hiệu lực trong <strong>5 phút</strong><br>" +
                "                                    • Chỉ sử dụng được <strong>1 lần</strong><br>" +
                "                                    • Không chia sẻ mã này với bất kỳ ai" +
                "                                </p>" +
                "                            </div>" +
                "                            " +
                "                            <p style=\"color: #666666; font-size: 15px; line-height: 1.6;\">Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "                            " +
                "                            <p style=\"color: #999999; font-size: 13px; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;\">Cảm ơn bạn đã sử dụng ABC News!</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style=\"background-color: #f8f9fa; padding: 20px; text-align: center;\">" +
                "                            <p style=\"color: #999999; font-size: 12px; margin: 0;\">© 2025 ABC News. All rights reserved.</p>" +
                "                            <p style=\"color: #999999; font-size: 12px; margin: 10px 0 0 0;\">Email được gửi tự động, vui lòng không trả lời.</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }
    
    /**
     * Escape HTML để tránh XSS và hiển thị đúng ký tự đặc biệt
     * 
     * @param text Chuỗi cần escape
     * @return Chuỗi đã được escape HTML
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
