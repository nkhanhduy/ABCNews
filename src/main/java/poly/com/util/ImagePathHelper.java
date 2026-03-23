package poly.com.util;

import java.util.List;

import poly.com.entity.News;
import poly.com.entity.User;

/**
 * Lớp tiện ích (Helper Class) để chuẩn hóa đường dẫn ảnh
 * 
 * Lớp này đảm bảo đường dẫn ảnh luôn có contextPath đúng để hiển thị trong HTML.
 * 
 * Vấn đề: Đường dẫn ảnh trong database có thể:
 * - Có contextPath cũ (khi deploy với context path khác)
 * - Không có contextPath (đường dẫn tương đối)
 * - Chỉ là tên file
 * 
 * Giải pháp: Chuẩn hóa tất cả về format: {contextPath}/uploads/{filename}
 * 
 * Hỗ trợ cả:
 * - News entity (field: Image)
 * - User entity (field: ImagePath)
 * 
 * @author ABCNews Development Team
 * @version 1.0
 */
public class ImagePathHelper {
    
    /**
     * Chuẩn hóa đường dẫn ảnh cho một News object
     * @param news Đối tượng News cần chuẩn hóa
     * @param contextPath Context path của ứng dụng (ví dụ: "/ABCNews")
     */
    public static void normalizeImagePath(News news, String contextPath) {
        if (news == null || news.getImage() == null || news.getImage().isEmpty()) {
            return;
        }
        
        String imagePath = news.getImage();
        String normalizedPath = normalizeImagePath(imagePath, contextPath);
        news.setImage(normalizedPath);
    }
    
    /**
     * Chuẩn hóa đường dẫn ảnh cho một danh sách News
     * @param newsList Danh sách News cần chuẩn hóa
     * @param contextPath Context path của ứng dụng
     */
    public static void normalizeImagePaths(List<News> newsList, String contextPath) {
        if (newsList == null || newsList.isEmpty()) {
            return;
        }
        
        for (News news : newsList) {
            normalizeImagePath(news, contextPath);
        }
    }
    
    /**
     * Chuẩn hóa đường dẫn ảnh cho một User object (ImagePath)
     * @param user Đối tượng User cần chuẩn hóa
     * @param contextPath Context path của ứng dụng (ví dụ: "/ABCNews")
     */
    public static void normalizeUserImagePath(User user, String contextPath) {
        if (user == null || user.getImagePath() == null || user.getImagePath().isEmpty()) {
            return;
        }
        
        String imagePath = user.getImagePath();
        String normalizedPath = normalizeImagePath(imagePath, contextPath);
        user.setImagePath(normalizedPath);
    }
    
    /**
     * Chuẩn hóa đường dẫn ảnh cho một danh sách User
     * @param userList Danh sách User cần chuẩn hóa
     * @param contextPath Context path của ứng dụng
     */
    public static void normalizeUserImagePaths(List<User> userList, String contextPath) {
        if (userList == null || userList.isEmpty()) {
            return;
        }
        
        for (User user : userList) {
            normalizeUserImagePath(user, contextPath);
        }
    }
    
    /**
     * Chuẩn hóa một đường dẫn ảnh
     * @param imagePath Đường dẫn ảnh từ database (có thể có hoặc không có contextPath)
     * @param contextPath Context path của ứng dụng (ví dụ: "/ABCNews" hoặc "")
     * @return Đường dẫn đã được chuẩn hóa với contextPath đúng
     */
    public static String normalizeImagePath(String imagePath, String contextPath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // Nếu đã là URL đầy đủ (http/https), giữ nguyên
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        
        // Tìm vị trí của /uploads/ trong đường dẫn
        int uploadsIndex = imagePath.indexOf("/uploads/");
        boolean hasTrailingSlash = uploadsIndex >= 0;
        
        if (!hasTrailingSlash) {
            // Nếu không tìm thấy /uploads/, thử tìm /uploads (không có dấu / cuối)
            uploadsIndex = imagePath.indexOf("/uploads");
        }
        
        // Nếu có chứa /uploads hoặc /uploads/
        if (uploadsIndex >= 0) {
            // Lấy phần sau /uploads/ hoặc /uploads (tên file)
            String fileName;
            if (hasTrailingSlash) {
                // Có dấu / sau uploads: /uploads/filename.jpg
                fileName = imagePath.substring(uploadsIndex + "/uploads/".length());
            } else {
                // Không có dấu / sau uploads: /uploadsfilename.jpg (ít xảy ra)
                // Hoặc /uploads ở cuối
                if (uploadsIndex + 8 >= imagePath.length()) {
                    // /uploads ở cuối, không có tên file
                    return imagePath; // Giữ nguyên
                }
                // Lấy từ sau /uploads
                fileName = imagePath.substring(uploadsIndex + "/uploads".length());
                // Nếu bắt đầu bằng /, bỏ đi
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
            }
            
            // Tạo đường dẫn mới với contextPath hiện tại
            // Xử lý contextPath rỗng (ROOT context)
            if (contextPath == null || contextPath.isEmpty() || contextPath.equals("/")) {
                return "/uploads/" + fileName;
            } else {
                // Đảm bảo contextPath bắt đầu bằng /
                String normalizedContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
                return normalizedContextPath + "/uploads/" + fileName;
            }
        }
        
        // Nếu không có /uploads, có thể là đường dẫn tương đối hoặc chỉ tên file
        // Nếu bắt đầu bằng /, có thể là đường dẫn tuyệt đối khác
        if (imagePath.startsWith("/")) {
            // Nếu đã có contextPath đúng ở đầu, giữ nguyên
            if (contextPath != null && !contextPath.isEmpty() && imagePath.startsWith(contextPath + "/")) {
                return imagePath;
            }
            // Nếu không, thêm contextPath vào đầu (nếu có)
            if (contextPath != null && !contextPath.isEmpty() && !contextPath.equals("/")) {
                String normalizedContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
                return normalizedContextPath + imagePath;
            }
            // Nếu contextPath là ROOT, giữ nguyên
            return imagePath;
        }
        
        // Nếu chỉ là tên file (không có /), thêm contextPath + /uploads/
        if (contextPath == null || contextPath.isEmpty() || contextPath.equals("/")) {
            return "/uploads/" + imagePath;
        } else {
            String normalizedContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
            return normalizedContextPath + "/uploads/" + imagePath;
        }
    }
}
