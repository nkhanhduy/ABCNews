package poly.com.util.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.entity.Category;
import poly.com.entity.Newsletter;

/**
 * CSV Exporter - Export dữ liệu ra file CSV
 * Không cần thư viện bên ngoài, tự implement CSV writer
 */
public class CSVExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Export danh sách News ra CSV
     */
    public static void exportNews(List<News> newsList, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"news_export_" + System.currentTimeMillis() + ".csv\"");
        
        PrintWriter writer = response.getWriter();
        
        // BOM for Excel UTF-8 compatibility
        writer.write('\ufeff');
        
        // Header - KHÔNG dùng dấu nháy cho header
        writer.println("ID,Tiêu đề,Tóm tắt,Tác giả,Danh mục,Lượt xem,Ngày đăng,Trang nhất");
        
        // Data - Escape đúng cách
        for (News news : newsList) {
            writeCsvLine(writer,
                news.getId(),
                news.getTitle(),
                news.getSummary(),
                news.getAuthor(),
                news.getCategoryId(),
                String.valueOf(news.getViewCount()),
                news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "",
                news.isHome() ? "Có" : "Không"
            );
        }
        
        writer.flush();
    }
    
    /**
     * Export danh sách Users ra CSV
     */
    public static void exportUsers(List<User> users, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.CSV.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"users_export_" + System.currentTimeMillis() + ".csv\"");
        
        PrintWriter writer = response.getWriter();
        
        // Header with BOM
        writer.write('\ufeff');
        writer.println("ID,Fullname,Email,Mobile,Birthday,Gender,Role,Status");
        
        // Data
        for (User user : users) {
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                escapeCsv(user.getId()),
                escapeCsv(user.getFullname()),
                escapeCsv(user.getEmail()),
                escapeCsv(user.getMobile()),
                user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "",
                user.isGender() ? "Male" : "Female",
                user.isRole() ? "Admin" : "Reporter",
                user.isEnabled() ? "Active" : "Disabled"
            );
        }
        
        writer.flush();
    }
    
    /**
     * Export danh sách Categories ra CSV
     */
    public static void exportCategories(List<Category> categories, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.CSV.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"categories_export_" + System.currentTimeMillis() + ".csv\"");
        
        PrintWriter writer = response.getWriter();
        
        writer.write('\ufeff');
        writer.println("ID,Name");
        
        for (Category category : categories) {
            writer.printf("\"%s\",\"%s\"\n",
                escapeCsv(category.getId()),
                escapeCsv(category.getName())
            );
        }
        
        writer.flush();
    }
    
    /**
     * Export danh sách Newsletters ra CSV
     */
    public static void exportNewsletters(List<Newsletter> newsletters, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.CSV.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"newsletters_export_" + System.currentTimeMillis() + ".csv\"");
        
        PrintWriter writer = response.getWriter();
        
        writer.write('\ufeff');
        writer.println("Email,Status,Subscribed Date");
        
        for (Newsletter newsletter : newsletters) {
            writer.printf("\"%s\",\"%s\",\"%s\"\n",
                escapeCsv(newsletter.getEmail()),
                newsletter.isEnabled() ? "Active" : "Unsubscribed",
                newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : ""
            );
        }
        
        writer.flush();
    }
    
    /**
     * Export NHIỀU MODULE gộp vào 1 file CSV duy nhất
     */
    public static void exportMultiple(List<News> newsList, List<User> usersList, 
                                     List<Category> categoriesList, List<Newsletter> newslettersList,
                                     HttpServletResponse response) throws IOException {
        
        response.setContentType(ExportFormat.CSV.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"abcnews_export_" + System.currentTimeMillis() + ".csv\"");
        
        PrintWriter writer = response.getWriter();
        writer.write('\ufeff'); // UTF-8 BOM
        
        // Ghi tất cả modules vào 1 file, mỗi module là 1 section
        boolean firstSection = true;
        
        if (newsList != null && !newsList.isEmpty()) {
            if (!firstSection) writer.println(); // Empty line giữa các section
            writer.println("=== TIN TUC ===");
            writer.println("ID,Tieu de,Tom tat,Tac gia,Danh muc,Luot xem,Ngay dang,Trang nhat");
            for (News news : newsList) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    escapeCsv(news.getId()),
                    escapeCsv(news.getTitle()),
                    escapeCsv(news.getSummary()),
                    escapeCsv(news.getAuthor()),
                    escapeCsv(news.getCategoryId()),
                    String.valueOf(news.getViewCount()),
                    news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "",
                    news.isHome() ? "Co" : "Khong"
                );
            }
            firstSection = false;
        }
        
        if (usersList != null && !usersList.isEmpty()) {
            if (!firstSection) writer.println();
            writer.println("=== NGUOI DUNG ===");
            writer.println("ID,Ho ten,Email,Dien thoai,Ngay sinh,Gioi tinh,Vai tro,Trang thai");
            for (User user : usersList) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    escapeCsv(user.getId()),
                    escapeCsv(user.getFullname()),
                    escapeCsv(user.getEmail()),
                    escapeCsv(user.getMobile()),
                    user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "",
                    user.isGender() ? "Nam" : "Nu",
                    user.isRole() ? "Quan tri" : "Phong vien",
                    user.isEnabled() ? "Hoat dong" : "Bi khoa"
                );
            }
            firstSection = false;
        }
        
        if (categoriesList != null && !categoriesList.isEmpty()) {
            if (!firstSection) writer.println();
            writer.println("=== DANH MUC ===");
            writer.println("Ma,Ten danh muc");
            for (Category category : categoriesList) {
                writer.printf("\"%s\",\"%s\"\n",
                    escapeCsv(category.getId()),
                    escapeCsv(category.getName())
                );
            }
            firstSection = false;
        }
        
        if (newslettersList != null && !newslettersList.isEmpty()) {
            if (!firstSection) writer.println();
            writer.println("=== NEWSLETTER ===");
            writer.println("Email,Trang thai,Ngay dang ky");
            for (Newsletter newsletter : newslettersList) {
                writer.printf("\"%s\",\"%s\",\"%s\"\n",
                    escapeCsv(newsletter.getEmail()),
                    newsletter.isEnabled() ? "Hoat dong" : "Da huy",
                    newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : ""
                );
            }
        }
        
        writer.flush();
    }
    
    /**
     * Escape special characters trong CSV
     * Thay thế dấu nháy kép bằng hai dấu nháy kép
     */
    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
    
    /**
     * Ghi một dòng CSV với escape đúng cách
     * Tự động thêm dấu nháy nếu có dấu phẩy, xuống dòng, hoặc dấu nháy
     */
    private static void writeCsvLine(PrintWriter writer, String... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(",");
            }
            String value = values[i] != null ? values[i] : "";
            
            // Kiểm tra cần dấu nháy kép không
            boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
            
            if (needsQuotes) {
                line.append("\"");
                line.append(escapeCsv(value));
                line.append("\"");
            } else {
                line.append(value);
            }
        }
        writer.println(line.toString());
    }
}

