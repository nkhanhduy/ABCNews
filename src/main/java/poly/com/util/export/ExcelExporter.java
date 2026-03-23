package poly.com.util.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.entity.Category;
import poly.com.entity.Newsletter;

/**
 * Excel Exporter - Export dữ liệu ra file Excel (.xlsx)
 * Sử dụng Apache POI để tạo file Excel với styling chuyên nghiệp
 */
public class ExcelExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Export danh sách News ra Excel
     */
    public static void exportNews(List<News> newsList, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.EXCEL.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"news_export_" + System.currentTimeMillis() + ".xlsx\"");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("News");
        
        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Title", "Summary", "Author", "Category", "View Count", "Posted Date", "Home"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        int rowNum = 1;
        for (News news : newsList) {
            Row row = sheet.createRow(rowNum++);
            
            createCell(row, 0, news.getId(), dataStyle);
            createCell(row, 1, news.getTitle(), dataStyle);
            createCell(row, 2, news.getSummary(), dataStyle);
            createCell(row, 3, news.getAuthor(), dataStyle);
            createCell(row, 4, news.getCategoryId(), dataStyle);
            createCell(row, 5, String.valueOf(news.getViewCount()), dataStyle);
            createCell(row, 6, news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "", dataStyle);
            createCell(row, 7, news.isHome() ? "Yes" : "No", dataStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * Export danh sách Users ra Excel
     */
    public static void exportUsers(List<User> users, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.EXCEL.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"users_export_" + System.currentTimeMillis() + ".xlsx\"");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Fullname", "Email", "Mobile", "Birthday", "Gender", "Role", "Status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            
            createCell(row, 0, user.getId(), dataStyle);
            createCell(row, 1, user.getFullname(), dataStyle);
            createCell(row, 2, user.getEmail(), dataStyle);
            createCell(row, 3, user.getMobile(), dataStyle);
            createCell(row, 4, user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "", dataStyle);
            createCell(row, 5, user.isGender() ? "Male" : "Female", dataStyle);
            createCell(row, 6, user.isRole() ? "Admin" : "Reporter", dataStyle);
            createCell(row, 7, user.isEnabled() ? "Active" : "Disabled", dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * Export danh sách Categories ra Excel
     */
    public static void exportCategories(List<Category> categories, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.EXCEL.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"categories_export_" + System.currentTimeMillis() + ".xlsx\"");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Categories");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Category category : categories) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, category.getId(), dataStyle);
            createCell(row, 1, category.getName(), dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * Export danh sách Newsletters ra Excel
     */
    public static void exportNewsletters(List<Newsletter> newsletters, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.EXCEL.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"newsletters_export_" + System.currentTimeMillis() + ".xlsx\"");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Newsletters");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Email", "Status", "Subscribed Date"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Newsletter newsletter : newsletters) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, newsletter.getEmail(), dataStyle);
            createCell(row, 1, newsletter.isEnabled() ? "Active" : "Unsubscribed", dataStyle);
            createCell(row, 2, newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : "", dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * Export NHIỀU MODULE gộp vào 1 file Excel duy nhất (mỗi module = 1 sheet)
     */
    public static void exportMultiple(List<News> newsList, List<User> usersList, 
                                     List<Category> categoriesList, List<Newsletter> newslettersList,
                                     HttpServletResponse response) throws IOException {
        
        response.setContentType(ExportFormat.EXCEL.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"abcnews_export_" + System.currentTimeMillis() + ".xlsx\"");
        
        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Tạo sheet cho từng module
        if (newsList != null && !newsList.isEmpty()) {
            createNewsSheet(workbook, newsList, headerStyle, dataStyle);
        }
        
        if (usersList != null && !usersList.isEmpty()) {
            createUsersSheet(workbook, usersList, headerStyle, dataStyle);
        }
        
        if (categoriesList != null && !categoriesList.isEmpty()) {
            createCategoriesSheet(workbook, categoriesList, headerStyle, dataStyle);
        }
        
        if (newslettersList != null && !newslettersList.isEmpty()) {
            createNewslettersSheet(workbook, newslettersList, headerStyle, dataStyle);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    // Helper methods để tạo từng sheet
    
    private static void createNewsSheet(Workbook workbook, List<News> newsList, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Tin tuc");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Tieu de", "Tom tat", "Tac gia", "Danh muc", "Luot xem", "Ngay dang", "Trang nhat"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (News news : newsList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, news.getId(), dataStyle);
            createCell(row, 1, news.getTitle(), dataStyle);
            createCell(row, 2, news.getSummary(), dataStyle);
            createCell(row, 3, news.getAuthor(), dataStyle);
            createCell(row, 4, news.getCategoryId(), dataStyle);
            createCell(row, 5, String.valueOf(news.getViewCount()), dataStyle);
            createCell(row, 6, news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "", dataStyle);
            createCell(row, 7, news.isHome() ? "Co" : "Khong", dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private static void createUsersSheet(Workbook workbook, List<User> usersList, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Nguoi dung");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Ho ten", "Email", "Dien thoai", "Ngay sinh", "Gioi tinh", "Vai tro", "Trang thai"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (User user : usersList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, user.getId(), dataStyle);
            createCell(row, 1, user.getFullname(), dataStyle);
            createCell(row, 2, user.getEmail(), dataStyle);
            createCell(row, 3, user.getMobile(), dataStyle);
            createCell(row, 4, user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "", dataStyle);
            createCell(row, 5, user.isGender() ? "Nam" : "Nu", dataStyle);
            createCell(row, 6, user.isRole() ? "Quan tri" : "Phong vien", dataStyle);
            createCell(row, 7, user.isEnabled() ? "Hoat dong" : "Bi khoa", dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private static void createCategoriesSheet(Workbook workbook, List<Category> categoriesList, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Danh muc");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Ma", "Ten danh muc"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Category category : categoriesList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, category.getId(), dataStyle);
            createCell(row, 1, category.getName(), dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private static void createNewslettersSheet(Workbook workbook, List<Newsletter> newslettersList, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Newsletter");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Email", "Trang thai", "Ngay dang ky"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Newsletter newsletter : newslettersList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, newsletter.getEmail(), dataStyle);
            createCell(row, 1, newsletter.isEnabled() ? "Hoat dong" : "Da huy", dataStyle);
            createCell(row, 2, newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : "", dataStyle);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create header cell style (bold, colored background)
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * Create data cell style (borders)
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * Helper method to create cell with value and style
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}

