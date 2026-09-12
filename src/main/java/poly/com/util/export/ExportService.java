package poly.com.util.export;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.entity.Category;
import poly.com.entity.Newsletter;

/**
 * ExportService - Class trung tâm quản lý tất cả export operations
 * 
 * Sử dụng Factory Pattern để xử lý export theo format
 * Giúp code sạch hơn, dễ maintain và mở rộng
 * 
 * Usage:
 * <pre>
 * ExportService.exportNews(newsList, ExportFormat.EXCEL, response);
 * ExportService.exportUsers(usersList, ExportFormat.PDF, response);
 * </pre>
 * 
 * @author Nguyen Duy Khanh
 * @version 1.0
 */
public class ExportService {
    
    /**
     * Export danh sách News theo format chỉ định
     * 
     * @param newsList Danh sách tin tức cần export
     * @param format Format export (CSV, EXCEL, PDF)
     * @param response HttpServletResponse để ghi file
     * @throws IOException Nếu có lỗi khi ghi file
     * @throws IllegalArgumentException Nếu format không hợp lệ
     */
    public static void exportNews(List<News> newsList, ExportFormat format, HttpServletResponse response) 
            throws IOException {
        
        if (newsList == null) {
            throw new IllegalArgumentException("News list cannot be null");
        }
        
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        switch (format) {
            case CSV:
                CSVExporter.exportNews(newsList, response);
                break;
            case EXCEL:
                ExcelExporter.exportNews(newsList, response);
                break;
            case PDF:
                PDFExporter.exportNews(newsList, response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }
    
    /**
     * Export danh sách Users theo format chỉ định
     * 
     * @param users Danh sách người dùng cần export
     * @param format Format export (CSV, EXCEL, PDF)
     * @param response HttpServletResponse để ghi file
     * @throws IOException Nếu có lỗi khi ghi file
     * @throws IllegalArgumentException Nếu format không hợp lệ
     */
    public static void exportUsers(List<User> users, ExportFormat format, HttpServletResponse response) 
            throws IOException {
        
        if (users == null) {
            throw new IllegalArgumentException("Users list cannot be null");
        }
        
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        switch (format) {
            case CSV:
                CSVExporter.exportUsers(users, response);
                break;
            case EXCEL:
                ExcelExporter.exportUsers(users, response);
                break;
            case PDF:
                PDFExporter.exportUsers(users, response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }
    
    /**
     * Export danh sách Categories theo format chỉ định
     * 
     * @param categories Danh sách loại tin cần export
     * @param format Format export (CSV, EXCEL, PDF)
     * @param response HttpServletResponse để ghi file
     * @throws IOException Nếu có lỗi khi ghi file
     * @throws IllegalArgumentException Nếu format không hợp lệ
     */
    public static void exportCategories(List<Category> categories, ExportFormat format, HttpServletResponse response) 
            throws IOException {
        
        if (categories == null) {
            throw new IllegalArgumentException("Categories list cannot be null");
        }
        
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        switch (format) {
            case CSV:
                CSVExporter.exportCategories(categories, response);
                break;
            case EXCEL:
                ExcelExporter.exportCategories(categories, response);
                break;
            case PDF:
                PDFExporter.exportCategories(categories, response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }
    
    /**
     * Export danh sách Newsletters theo format chỉ định
     * 
     * @param newsletters Danh sách newsletter cần export
     * @param format Format export (CSV, EXCEL, PDF)
     * @param response HttpServletResponse để ghi file
     * @throws IOException Nếu có lỗi khi ghi file
     * @throws IllegalArgumentException Nếu format không hợp lệ
     */
    public static void exportNewsletters(List<Newsletter> newsletters, ExportFormat format, HttpServletResponse response) 
            throws IOException {
        
        if (newsletters == null) {
            throw new IllegalArgumentException("Newsletters list cannot be null");
        }
        
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        switch (format) {
            case CSV:
                CSVExporter.exportNewsletters(newsletters, response);
                break;
            case EXCEL:
                ExcelExporter.exportNewsletters(newsletters, response);
                break;
            case PDF:
                PDFExporter.exportNewsletters(newsletters, response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }
    
    /**
     * Export nhiều module gộp vào 1 file duy nhất
     * 
     * @param newsList Danh sách tin tức (có thể null nếu không chọn)
     * @param usersList Danh sách người dùng (có thể null nếu không chọn)
     * @param categoriesList Danh sách danh mục (có thể null nếu không chọn)
     * @param newslettersList Danh sách newsletter (có thể null nếu không chọn)
     * @param format Format export (CSV, EXCEL, PDF)
     * @param response HttpServletResponse để ghi file
     * @throws IOException Nếu có lỗi khi ghi file
     */
    public static void exportMultiple(List<News> newsList, List<User> usersList, 
                                     List<Category> categoriesList, List<Newsletter> newslettersList,
                                     ExportFormat format, HttpServletResponse response) throws IOException {
        
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        switch (format) {
            case CSV:
                CSVExporter.exportMultiple(newsList, usersList, categoriesList, newslettersList, response);
                break;
            case EXCEL:
                ExcelExporter.exportMultiple(newsList, usersList, categoriesList, newslettersList, response);
                break;
            case PDF:
                PDFExporter.exportMultiple(newsList, usersList, categoriesList, newslettersList, response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }
    
    /**
     * Kiểm tra xem format có được hỗ trợ hay không
     * 
     * @param format Format cần kiểm tra
     * @return true nếu format được hỗ trợ
     */
    public static boolean isSupportedFormat(ExportFormat format) {
        return format == ExportFormat.CSV 
            || format == ExportFormat.EXCEL 
            || format == ExportFormat.PDF;
    }
    
    /**
     * Lấy danh sách tất cả format được hỗ trợ
     * 
     * @return Mảng các format được hỗ trợ
     */
    public static ExportFormat[] getSupportedFormats() {
        return ExportFormat.values();
    }
    
    /**
     * Lấy MIME type cho format
     * 
     * @param format Format cần lấy MIME type
     * @return MIME type string
     */
    public static String getContentType(ExportFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        return format.getContentType();
    }
    
    /**
     * Lấy file extension cho format
     * 
     * @param format Format cần lấy extension
     * @return File extension (bao gồm dấu chấm)
     */
    public static String getFileExtension(ExportFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        return format.getExtension();
    }
}

