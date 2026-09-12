package poly.com.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

/**
 * Lớp tiện ích (Helper Class) để xử lý upload và quản lý file hình ảnh
 * 
 * Lớp này cung cấp các phương thức static để:
 * - Lưu file hình ảnh được upload từ form vào thư mục /uploads trên server
 * - Xử lý logic cập nhật ảnh: dùng ảnh mới nếu có, giữ ảnh cũ nếu không
 * - Chuẩn hóa đường dẫn ảnh với contextPath đúng
 * 
 * Tất cả file ảnh được lưu vào thư mục /uploads trong webapp directory.
 * 
 * @author ABCNews Development Team
 * @version 1.0
 */
public class FileUploadHelper {
    
    /**
     * Lưu file hình ảnh được upload từ form vào thư mục /uploads trên server
     * 
     * Phương thức này:
     * 1. Lấy file từ request theo tên part (thường là input type="file" trong form)
     * 2. Kiểm tra file có tồn tại và có kích thước > 0 không
     * 3. Tạo thư mục /uploads nếu chưa tồn tại
     * 4. Lưu file vào thư mục /uploads với tên file gốc
     * 5. Trả về đường dẫn URL đã chuẩn hóa (có contextPath) để hiển thị
     * 
     * @param request HttpServletRequest chứa file upload (phải có @MultipartConfig)
     * @param partName Tên của input file trong form HTML (ví dụ: "imageFile", "userImage")
     *                 Nếu null hoặc empty, sẽ dùng "imageFile" làm mặc định
     * @return Đường dẫn URL của file đã lưu (luôn có contextPath), 
     *         hoặc null nếu không có file được upload hoặc file rỗng
     * @throws IOException nếu có lỗi khi ghi file vào disk
     * @throws ServletException nếu có lỗi khi xử lý multipart request
     */
    public static String saveImage(HttpServletRequest request, String partName) throws IOException, ServletException {
        // Sử dụng tên mặc định nếu không chỉ định
        if (partName == null || partName.isEmpty()) {
            partName = "imageFile";
        }
        
        // Lấy Part chứa file từ request
        Part filePart = request.getPart(partName);
        
        // Kiểm tra file có tồn tại, có kích thước > 0, và có tên file không
        if (filePart != null && filePart.getSize() > 0 
            && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().trim().isEmpty()) {
            
            // Lấy extension an toàn
            String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String ext = "";
            int dotIndex = submitted.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = submitted.substring(dotIndex + 1).toLowerCase(java.util.Locale.ROOT);
            }
            
            // Kiểm tra phần mở rộng file được phép
            if (!java.util.Arrays.asList("jpg", "jpeg", "png", "webp", "gif").contains(ext)) {
                return null;
            }
            
            // Kiểm tra MIME type hợp lệ
            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.toLowerCase(java.util.Locale.ROOT).startsWith("image/")) {
                return null;
            }
            
            // Sinh tên file ngẫu nhiên bằng UUID để chống path traversal và ghi đè
            String safeFileName = java.util.UUID.randomUUID().toString() + "." + ext;
            
            // Lấy đường dẫn thực tế của thư mục /uploads trên server
            String uploadDir = request.getServletContext().getRealPath("/uploads");
            File dir = new File(uploadDir);
            
            // Tạo thư mục nếu chưa tồn tại
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Đường dẫn đầy đủ để lưu file an toàn trên server
            String filePath = uploadDir + File.separator + safeFileName;
            
            // Ghi file vào disk
            filePart.write(filePath);
            
            // Trả về đường dẫn URL đã chuẩn hóa (có contextPath) để hiển thị trong HTML
            String contextPath = request.getContextPath();
            return ImagePathHelper.normalizeImagePath(contextPath + "/uploads/" + safeFileName, contextPath);
        }
        return null;
    }

    /**
     * Lưu file hình ảnh với tên input mặc định "imageFile"
     * 
     * Đây là overload method để tiện sử dụng khi form chỉ có một input file
     * với tên mặc định "imageFile".
     * 
     * @param request HttpServletRequest chứa file upload
     * @return Đường dẫn URL của file đã lưu, hoặc null nếu không có file
     * @throws IOException nếu có lỗi khi ghi file
     * @throws ServletException nếu có lỗi khi xử lý request
     */
    public static String saveImage(HttpServletRequest request) throws IOException, ServletException {
        return saveImage(request, "imageFile");
    }

    /**
     * Xử lý logic cập nhật ảnh: dùng ảnh mới nếu có upload, giữ ảnh cũ nếu không
     * 
     * Phương thức này được dùng khi cập nhật thông tin (ví dụ: edit user, edit news).
     * Logic:
     * 1. Thử lưu ảnh mới từ form
     * 2. Nếu có ảnh mới → trả về đường dẫn ảnh mới
     * 3. Nếu không có ảnh mới → giữ lại ảnh cũ (chuẩn hóa đường dẫn)
     * 4. Nếu không có cả ảnh mới và ảnh cũ → trả về null
     * 
     * @param request HttpServletRequest chứa file upload
     * @param existingImagePath Đường dẫn ảnh cũ từ database (có thể null nếu chưa có ảnh)
     * @param partName Tên của input file trong form
     * @return Đường dẫn ảnh cuối cùng (mới hoặc cũ đã chuẩn hóa), hoặc null nếu không có ảnh
     * @throws IOException nếu có lỗi khi ghi file
     * @throws ServletException nếu có lỗi khi xử lý request
     */
    public static String handleImageUpdate(HttpServletRequest request, String existingImagePath, String partName) 
            throws IOException, ServletException {
        // Thử lưu ảnh mới từ form
        String newImagePath = saveImage(request, partName);
        
        if (newImagePath != null) {
            // Có ảnh mới được upload → dùng ảnh mới
            return newImagePath;
        } else {
            // Không có ảnh mới → giữ lại ảnh cũ nếu có
            if (existingImagePath != null && !existingImagePath.isEmpty()) {
                String contextPath = request.getContextPath();
                // Chuẩn hóa đường dẫn ảnh cũ để đảm bảo có contextPath đúng
                return ImagePathHelper.normalizeImagePath(existingImagePath, contextPath);
            }
        }
        return null;
    }

    /**
     * Xử lý cập nhật ảnh với tên input mặc định "imageFile"
     * 
     * Đây là overload method để tiện sử dụng khi form chỉ có một input file
     * với tên mặc định "imageFile".
     * 
     * @param request HttpServletRequest chứa file upload
     * @param existingImagePath Đường dẫn ảnh cũ từ database
     * @return Đường dẫn ảnh cuối cùng (mới hoặc cũ), hoặc null nếu không có ảnh
     * @throws IOException nếu có lỗi khi ghi file
     * @throws ServletException nếu có lỗi khi xử lý request
     */
    public static String handleImageUpdate(HttpServletRequest request, String existingImagePath) 
            throws IOException, ServletException {
        return handleImageUpdate(request, existingImagePath, "imageFile");
    }
}
