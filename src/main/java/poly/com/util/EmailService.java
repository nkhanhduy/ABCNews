package poly.com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import poly.com.entity.News;

/**
 * Service đơn giản để gửi email newsletter
 * Sử dụng Gmail SMTP (phù hợp cho project nhỏ)
 * 
 * Lưu ý: Cần cấu hình SMTP_USER và SMTP_PASSWORD với Gmail App Password
 */
public class EmailService {
    // Cấu hình SMTP nạp an toàn từ ConfigHelper (hỗ trợ biến môi trường hoặc file app.properties)
    private static final String SMTP_HOST = ConfigHelper.get("mail.smtp.host", "smtp.gmail.com");
    private static final int SMTP_PORT = ConfigHelper.getInt("mail.smtp.port", 587);
    private static final String SMTP_USER = ConfigHelper.get("mail.smtp.user", "");
    private static final String SMTP_PASSWORD = ConfigHelper.get("mail.smtp.password", "");
    
    /**
     * Gửi email newsletter cho một subscriber
     * 
     * @param toEmail Email người nhận
     * @param news Đối tượng News chứa thông tin tin tức
     * @param baseUrl Base URL đầy đủ của ứng dụng (ví dụ: http://localhost:8080/ABCNews) để tạo absolute URL cho ảnh
     * @param servletContextPath Đường dẫn thực tế của thư mục /uploads trên server (từ ServletContext.getRealPath("/uploads"))
     * @throws MessagingException Nếu có lỗi khi gửi email
     */
    public void sendNewsletterEmail(String toEmail, News news, String baseUrl, String servletContextPath) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });
        
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SMTP_USER, "ABC News"));
        } catch (java.io.UnsupportedEncodingException e) {
            message.setFrom(new InternetAddress(SMTP_USER));
        }
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("Tin mới: " + news.getTitle());
        
        // Tạo multipart message để có thể attach ảnh
        MimeMultipart multipart = new MimeMultipart("related");
        
        // Phần HTML body
        MimeBodyPart htmlPart = new MimeBodyPart();
        String htmlContent = EmailTemplate.createNewsletterEmail(news, baseUrl, toEmail, servletContextPath);
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);
        
        // Attach ảnh nếu có
        if (news.getImage() != null && !news.getImage().isEmpty() && servletContextPath != null) {
            String imageUrl = news.getImage();
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                // Extract fileName từ imageUrl
                String fileName = extractFileName(imageUrl);
                if (fileName != null) {
                    File imageFile = new File(servletContextPath, fileName);
                    if (imageFile.exists() && imageFile.isFile()) {
                        try {
                            MimeBodyPart imagePart = new MimeBodyPart();
                            DataSource source = new FileDataSource(imageFile);
                            imagePart.setDataHandler(new DataHandler(source));
                            imagePart.setHeader("Content-ID", "<news-image>");
                            imagePart.setDisposition(MimeBodyPart.INLINE);
                            multipart.addBodyPart(imagePart);
                            
                            // Cập nhật HTML để dùng CID thay vì base64
                            htmlContent = EmailTemplate.createNewsletterEmailWithCID(news, baseUrl, toEmail);
                            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
                        } catch (Exception e) {
                            System.err.println("Lỗi attach ảnh: " + e.getMessage());
                            // Fallback về base64 nếu attach lỗi
                        }
                    }
                }
            }
        }
        
        message.setContent(multipart);
        Transport.send(message);
    }
    
    /**
     * Gửi email chứa mã OTP cho chức năng quên mật khẩu
     * 
     * @param toEmail Email người nhận
     * @param otpCode Mã OTP 6 số
     * @param fullname Tên đầy đủ của người dùng
     * @throws MessagingException Nếu có lỗi khi gửi email
     */
    public void sendOtpEmail(String toEmail, String otpCode, String fullname) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SMTP_USER, "ABC News - Đặt lại mật khẩu"));
        } catch (java.io.UnsupportedEncodingException e) {
            message.setFrom(new InternetAddress(SMTP_USER));
        }
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("Mã OTP đặt lại mật khẩu - ABC News");

        String htmlContent = EmailTemplate.createOtpEmail(otpCode, fullname);
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }
    
    /**
     * Gửi email cho nhiều subscribers (đồng bộ, đơn giản)
     * 
     * @param emails Danh sách email người nhận
     * @param news Đối tượng News chứa thông tin tin tức
     * @param baseUrl Base URL đầy đủ của ứng dụng (ví dụ: http://localhost:8080/ABCNews)
     * @param servletContextPath Đường dẫn thực tế của thư mục /uploads trên server
     * @return Số lượng email gửi thành công
     */
    public int sendBulkNewsletter(java.util.List<String> emails, News news, String baseUrl, String servletContextPath) {
        int successCount = 0;
        int failCount = 0;
        
        for (String email : emails) {
            try {
                sendNewsletterEmail(email, news, baseUrl, servletContextPath);
                successCount++;
                // Delay nhỏ để tránh rate limit của Gmail
                Thread.sleep(100);
            } catch (Exception e) {
                failCount++;
                System.err.println("Lỗi gửi email đến " + email + ": " + e.getMessage());
            }
        }
        
        System.out.println("Đã gửi: " + successCount + " thành công, " + failCount + " thất bại");
        return successCount;
    }
    
    /**
     * Extract tên file từ image URL path
     * 
     * @param imageUrl Đường dẫn ảnh (ví dụ: /ABCNews/uploads/dv1.jpg)
     * @return Tên file (ví dụ: dv1.jpg) hoặc null nếu không extract được
     */
    private String extractFileName(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Tìm vị trí của /uploads/ trong đường dẫn
        int uploadsIndex = imageUrl.indexOf("/uploads/");
        if (uploadsIndex >= 0) {
            return imageUrl.substring(uploadsIndex + "/uploads/".length());
        }
        
        // Kiểm tra nếu có /uploads (không có dấu / cuối)
        uploadsIndex = imageUrl.indexOf("/uploads");
        if (uploadsIndex >= 0 && uploadsIndex + 7 < imageUrl.length()) {
            String afterUploads = imageUrl.substring(uploadsIndex + "/uploads".length());
            if (afterUploads.startsWith("/")) {
                return afterUploads.substring(1);
            }
            return afterUploads;
        }
        
        // Nếu không tìm thấy /uploads, lấy phần cuối cùng sau dấu /
        int lastSlash = imageUrl.lastIndexOf("/");
        if (lastSlash >= 0 && lastSlash + 1 < imageUrl.length()) {
            return imageUrl.substring(lastSlash + 1);
        }
        
        return imageUrl; // Trả về toàn bộ nếu không có dấu /
    }
}
