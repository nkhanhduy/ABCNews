package poly.com.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

/**
 * Tiện ích lưu trữ hình ảnh an toàn theo kiến trúc phân vùng thư mục thông minh
 * (Tham khảo chuẩn bảo mật SafeImageStorage từ dự án DATN)
 * 
 * Các tính năng an toàn:
 * 1. Phân vùng thư mục chuyên dụng: avatars, news, categories, misc.
 * 2. Xác thực kích thước file (tối đa 5MB cho ảnh đại diện).
 * 3. Kiểm tra MIME type hợp lệ (JPEG, PNG, WEBP, GIF).
 * 4. Xác thực dữ liệu nhị phân của ảnh qua ImageIO (chống webshell và mã độc nhúng trong metadata).
 * 5. Tên file được sinh ngẫu nhiên bằng UUID (tránh trùng tên, path traversal, unicode mojibake).
 * 6. Tự động dọn dẹp file ảnh cũ an toàn khi người dùng cập nhật ảnh mới.
 */
public class SafeImageStorage {

    /** Dung lượng tối đa cho file ảnh avatar: 5MB */
    public static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;

    /** Dung lượng tối đa cho file ảnh bài viết: 10MB */
    public static final long MAX_NEWS_IMAGE_SIZE = 10L * 1024 * 1024;

    /** Danh sách thư mục được phép lưu trữ */
    private static final Set<String> ALLOWED_FOLDERS = new HashSet<>(Arrays.asList(
        "avatars", "news", "categories", "misc"
    ));

    /** Danh sách định dạng mở rộng cho phép */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "webp", "gif"
    ));

    /** Danh sách MIME types được chấp nhận */
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/png", "image/webp", "image/gif", "image/pjpeg"
    ));

    /**
     * Chuẩn hóa tên thư mục lưu trữ hợp lệ
     * @param folder Tên thư mục yêu cầu
     * @return Tên thư mục đã chuẩn hóa
     */
    public static String normalizeFolder(String folder) {
        if (folder == null || folder.trim().isEmpty()) {
            return "misc";
        }
        String normalized = folder.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_FOLDERS.contains(normalized)) {
            throw new IllegalArgumentException("Thư mục tải lên không hợp lệ: " + folder);
        }
        return normalized;
    }

    /**
     * Lưu ảnh đại diện người dùng vào thư mục /uploads/avatars/
     * 
     * @param request HttpServletRequest chứa file upload
     * @param partName Tên trường input trong form (ví dụ: "avatarFile")
     * @param oldImagePath Đường dẫn ảnh cũ của user để tự động xóa nếu có
     * @return Đường dẫn tương đối của ảnh mới (/uploads/avatars/{UUID}.{ext}) hoặc null nếu không có file
     * @throws Exception nếu file không hợp lệ hoặc lỗi lưu trữ
     */
    public static String storeAvatar(HttpServletRequest request, String partName, String oldImagePath) throws Exception {
        if (partName == null || partName.trim().isEmpty()) {
            partName = "avatarFile";
        }

        Part filePart = request.getPart(partName);
        if (filePart == null || filePart.getSize() <= 0) {
            return null;
        }

        // Kiểm tra dung lượng
        if (filePart.getSize() > MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException("Kích thước ảnh vượt quá giới hạn cho phép (tối đa 5 MB).");
        }

        // Kiểm tra MIME Content-Type
        String contentType = filePart.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Định dạng tệp không được hỗ trợ. Vui lòng chọn ảnh JPG, PNG, WEBP hoặc GIF.");
        }

        // Trích xuất đuôi file gốc
        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String extension = getExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            extension = "png"; // Fallback an toàn
        }
        if ("jpeg".equals(extension)) {
            extension = "jpg";
        }

        // Xác thực nội dung nhị phân xem có phải là ảnh hợp lệ không (chống webshell)
        try (InputStream is = filePart.getInputStream()) {
            BufferedImage bimg = ImageIO.read(is);
            if (bimg == null) {
                throw new IllegalArgumentException("Tệp tải lên không phải là định dạng hình ảnh hợp lệ hoặc đã bị lỗi.");
            }
        }

        // Lấy thư mục upload thực tế trên server
        String uploadBasePath = request.getServletContext().getRealPath("/uploads/avatars");
        if (uploadBasePath == null) {
            // Fallback khi chạy embedded server
            uploadBasePath = System.getProperty("java.io.tmpdir") + File.separator + "abcnews_uploads" + File.separator + "avatars";
        }

        Path targetDir = Paths.get(uploadBasePath).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // Tạo tên file UUID an toàn
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        Path targetFile = targetDir.resolve(uniqueFileName).normalize();

        // Chống path traversal
        if (!targetFile.startsWith(targetDir)) {
            throw new SecurityException("Phát hiện đường dẫn tệp không an toàn.");
        }

        // Ghi file
        try (InputStream is = filePart.getInputStream()) {
            Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Xóa ảnh đại diện cũ nếu là ảnh nằm trong thư mục /uploads/avatars/
        deleteOldAvatar(request, oldImagePath);

        // Trả về đường dẫn chuẩn hóa để lưu vào DB: /uploads/avatars/{uuid}.ext
        return "/uploads/avatars/" + uniqueFileName;
    }

    /**
     * Xóa ảnh đại diện cũ an toàn
     * @param request HttpServletRequest
     * @param oldImagePath Đường dẫn ảnh cũ từ database
     */
    public static void deleteOldAvatar(HttpServletRequest request, String oldImagePath) {
        if (oldImagePath == null || oldImagePath.trim().isEmpty()) {
            return;
        }

        // Chỉ xóa nếu ảnh cũ nằm trong /uploads/avatars/ (tránh xóa nhầm ảnh hệ thống)
        String normalized = oldImagePath.replace('\\', '/');
        if (!normalized.contains("/uploads/avatars/")) {
            return;
        }

        try {
            int index = normalized.indexOf("/uploads/avatars/");
            String relativePart = normalized.substring(index + "/uploads/avatars/".length());
            // Chỉ lấy tên file, không cho phép ký tự phân cấp ../
            String fileName = Paths.get(relativePart).getFileName().toString();

            String uploadBasePath = request.getServletContext().getRealPath("/uploads/avatars");
            if (uploadBasePath != null) {
                Path targetDir = Paths.get(uploadBasePath).toAbsolutePath().normalize();
                Path targetFile = targetDir.resolve(fileName).normalize();
                if (targetFile.startsWith(targetDir) && Files.exists(targetFile)) {
                    Files.deleteIfExists(targetFile);
                }
            }
        } catch (Exception e) {
            // Log nhưng không làm gián đoạn luồng chính
            System.err.println("[SafeImageStorage] Lỗi khi dọn dẹp ảnh cũ: " + e.getMessage());
        }
    }

    /**
     * Lấy phần mở rộng (extension) của file
     * @param filename Tên file
     * @return Chuỗi extension viết thường (không bao gồm dấu chấm)
     */
    private static String getExtension(String filename) {
        if (filename == null) return null;
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot >= filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
