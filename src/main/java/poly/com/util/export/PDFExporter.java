package poly.com.util.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.servlet.http.HttpServletResponse;
import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.entity.Category;
import poly.com.entity.Newsletter;
import poly.com.util.FontHelper;

/**
 * PDF Exporter - Export dữ liệu ra file PDF
 * PHIÊN BẢN UTF-8 CHUẨN với Helvetica + Identity-H encoding
 */
public class PDFExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    // Colors
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(25, 25, 112); // Dark blue
    private static final DeviceRgb STRIPE_COLOR = new DeviceRgb(240, 248, 255); // Alice blue
    
    /**
     * Export danh sách News ra PDF với UTF-8
     */
    public static void exportNews(List<News> newsList, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.PDF.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"tin_tuc_export_" + System.currentTimeMillis() + ".pdf\"");
        
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font DejaVu Sans - Hỗ trợ tiếng Việt CHUẨN 100%
            PdfFont font = FontHelper.getRegularFont();
            PdfFont boldFont = FontHelper.getBoldFont();
            
            // Title
            addTitle(document, "ABC News - Báo Cáo Tin Tức", boldFont, font);
            
            // Table
            float[] columnWidths = {1f, 3f, 2.5f, 1.5f, 1.5f, 1f, 2f, 1.2f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Header
            String[] headers = {"ID", "Tiêu đề", "Tóm tắt", "Tác giả", "Danh mục", "Xem", "Ngày đăng", "Trang nhất"};
            addTableHeader(table, headers, boldFont);
            
            // Data
            int rowIndex = 0;
            for (News news : newsList) {
                DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
                
                addCell(table, news.getId(), bgColor, font);
                addCell(table, news.getTitle(), bgColor, font);
                addCell(table, news.getSummary(), bgColor, font);
                addCell(table, news.getAuthor(), bgColor, font);
                addCell(table, news.getCategoryId(), bgColor, font);
                addCell(table, String.valueOf(news.getViewCount()), bgColor, font);
                addCell(table, news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "-", bgColor, font);
                addCell(table, news.isHome() ? "Có" : "Không", bgColor, font);
                
                rowIndex++;
            }
            
            document.add(table);
            addFooter(document, "Tổng số: " + newsList.size() + " bản tin", boldFont);
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export danh sách Users ra PDF với UTF-8
     */
    public static void exportUsers(List<User> users, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.PDF.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"nguoi_dung_export_" + System.currentTimeMillis() + ".pdf\"");
        
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font DejaVu Sans - Tiếng Việt chuẩn
            PdfFont font = FontHelper.getRegularFont();
            PdfFont boldFont = FontHelper.getBoldFont();
            
            // Title
            addTitle(document, "ABC News - Báo Cáo Người Dùng", boldFont, font);
            
            // Table
            float[] columnWidths = {1.5f, 2.5f, 2.5f, 1.5f, 1.5f, 1.2f, 1.5f, 1.3f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Header
            String[] headers = {"ID", "Họ tên", "Email", "Điện thoại", "Ngày sinh", "Giới tính", "Vai trò", "Trạng thái"};
            addTableHeader(table, headers, boldFont);
            
            // Data
            int rowIndex = 0;
            for (User user : users) {
                DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
                
                addCell(table, user.getId(), bgColor, font);
                addCell(table, user.getFullname(), bgColor, font);
                addCell(table, user.getEmail(), bgColor, font);
                addCell(table, user.getMobile(), bgColor, font);
                addCell(table, user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "-", bgColor, font);
                addCell(table, user.isGender() ? "Nam" : "Nữ", bgColor, font);
                addCell(table, user.isRole() ? "Quản trị" : "Phóng viên", bgColor, font);
                addCell(table, user.isEnabled() ? "Hoạt động" : "Bị khóa", bgColor, font);
                
                rowIndex++;
            }
            
            document.add(table);
            addFooter(document, "Tổng số: " + users.size() + " người dùng", boldFont);
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export danh sách Categories ra PDF với UTF-8
     */
    public static void exportCategories(List<Category> categories, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.PDF.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"danh_muc_export_" + System.currentTimeMillis() + ".pdf\"");
        
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font DejaVu Sans - Tiếng Việt chuẩn
            PdfFont font = FontHelper.getRegularFont();
            PdfFont boldFont = FontHelper.getBoldFont();
            
            // Title
            addTitle(document, "ABC News - Báo Cáo Danh Mục", boldFont, font);
            
            // Table
            float[] columnWidths = {1, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(50));
            
            // Header
            String[] headers = {"Mã", "Tên danh mục"};
            addTableHeader(table, headers, boldFont);
            
            // Data
            int rowIndex = 0;
            for (Category category : categories) {
                DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
                addCell(table, category.getId(), bgColor, font);
                addCell(table, category.getName(), bgColor, font);
                rowIndex++;
            }
            
            document.add(table);
            addFooter(document, "Tổng số: " + categories.size() + " danh mục", boldFont);
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export danh sách Newsletters ra PDF với UTF-8
     */
    public static void exportNewsletters(List<Newsletter> newsletters, HttpServletResponse response) throws IOException {
        response.setContentType(ExportFormat.PDF.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"newsletter_export_" + System.currentTimeMillis() + ".pdf\"");
        
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font DejaVu Sans - Tiếng Việt chuẩn
            PdfFont font = FontHelper.getRegularFont();
            PdfFont boldFont = FontHelper.getBoldFont();
            
            // Title
            addTitle(document, "ABC News - Danh Sách Newsletter", boldFont, font);
            
            // Table
            float[] columnWidths = {3, 1.5f, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(70));
            
            // Header
            String[] headers = {"Email", "Trạng thái", "Ngày đăng ký"};
            addTableHeader(table, headers, boldFont);
            
            // Data
            int rowIndex = 0;
            for (Newsletter newsletter : newsletters) {
                DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
                addCell(table, newsletter.getEmail(), bgColor, font);
                addCell(table, newsletter.isEnabled() ? "Hoạt động" : "Đã hủy", bgColor, font);
                addCell(table, newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : "-", bgColor, font);
                rowIndex++;
            }
            
            document.add(table);
            addFooter(document, "Tổng số: " + newsletters.size() + " email", boldFont);
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export NHIỀU MODULE gộp vào 1 file PDF duy nhất
     */
    public static void exportMultiple(List<News> newsList, List<User> usersList, 
                                     List<Category> categoriesList, List<Newsletter> newslettersList,
                                     HttpServletResponse response) throws IOException {
        
        response.setContentType(ExportFormat.PDF.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"abcnews_export_" + System.currentTimeMillis() + ".pdf\"");
        
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font
            PdfFont font = FontHelper.getRegularFont();
            PdfFont boldFont = FontHelper.getBoldFont();
            
            // Title chung
            addTitle(document, "ABC News - Báo Cáo Tổng Hợp", boldFont, font);
            
            // Export từng module (mỗi module là 1 section)
            boolean firstSection = true;
            
            if (newsList != null && !newsList.isEmpty()) {
                if (!firstSection) addPageBreak(document);
                addNewsSection(document, newsList, font, boldFont);
                firstSection = false;
            }
            
            if (usersList != null && !usersList.isEmpty()) {
                if (!firstSection) addPageBreak(document);
                addUsersSection(document, usersList, font, boldFont);
                firstSection = false;
            }
            
            if (categoriesList != null && !categoriesList.isEmpty()) {
                if (!firstSection) addPageBreak(document);
                addCategoriesSection(document, categoriesList, font, boldFont);
                firstSection = false;
            }
            
            if (newslettersList != null && !newslettersList.isEmpty()) {
                if (!firstSection) addPageBreak(document);
                addNewslettersSection(document, newslettersList, font, boldFont);
            }
            
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
    
    // ============ HELPER METHODS ============
    
    /**
     * Thêm title cho PDF
     */
    private static void addTitle(Document document, String titleText, PdfFont boldFont, PdfFont regularFont) {
        Paragraph title = new Paragraph(titleText)
                .setFont(boldFont)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);
        
        Paragraph date = new Paragraph("Ngày xuất: " + DATE_FORMAT.format(new java.util.Date()))
                .setFont(regularFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(25);
        document.add(date);
    }
    
    /**
     * Thêm table header
     */
    private static void addTableHeader(Table table, String[] headers, PdfFont boldFont) {
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            table.addHeaderCell(cell);
        }
    }
    
    /**
     * Thêm cell với UTF-8 support
     */
    private static void addCell(Table table, String value, DeviceRgb bgColor, PdfFont font) {
        String displayValue = (value != null && !value.trim().isEmpty()) ? value : "-";
        Cell cell = new Cell()
                .add(new Paragraph(displayValue).setFont(font).setFontSize(11))
                .setPadding(6);
        
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
        table.addCell(cell);
    }
    
    /**
     * Thêm footer
     */
    private static void addFooter(Document document, String footerText, PdfFont boldFont) {
        Paragraph footer = new Paragraph("\n" + footerText)
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10);
        document.add(footer);
    }
    
    /**
     * Thêm page break
     */
    private static void addPageBreak(Document document) {
        document.add(new com.itextpdf.layout.element.AreaBreak(com.itextpdf.layout.properties.AreaBreakType.NEXT_PAGE));
    }
    
    /**
     * Thêm section header
     */
    private static void addSectionHeader(Document document, String sectionTitle, PdfFont boldFont) {
        Paragraph header = new Paragraph(sectionTitle)
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(new DeviceRgb(102, 126, 234))
                .setMarginTop(10)
                .setMarginBottom(15);
        document.add(header);
    }
    
    // ============ SECTION METHODS FOR MULTIPLE EXPORT ============
    
    /**
     * Thêm section Tin tức
     */
    private static void addNewsSection(Document document, List<News> newsList, PdfFont font, PdfFont boldFont) {
        addSectionHeader(document, "TIN TỨC", boldFont);
        
        float[] columnWidths = {1f, 3f, 2.5f, 1.5f, 1.5f, 1f, 2f, 1.2f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        String[] headers = {"ID", "Tiêu đề", "Tóm tắt", "Tác giả", "Danh mục", "Xem", "Ngày đăng", "Trang nhất"};
        addTableHeader(table, headers, boldFont);
        
        int rowIndex = 0;
        for (News news : newsList) {
            DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
            addCell(table, news.getId(), bgColor, font);
            addCell(table, news.getTitle(), bgColor, font);
            addCell(table, news.getSummary(), bgColor, font);
            addCell(table, news.getAuthor(), bgColor, font);
            addCell(table, news.getCategoryId(), bgColor, font);
            addCell(table, String.valueOf(news.getViewCount()), bgColor, font);
            addCell(table, news.getPostedDate() != null ? DATE_FORMAT.format(news.getPostedDate()) : "-", bgColor, font);
            addCell(table, news.isHome() ? "Có" : "Không", bgColor, font);
            rowIndex++;
        }
        
        document.add(table);
        addFooter(document, "Tổng số: " + newsList.size() + " bản tin", boldFont);
    }
    
    /**
     * Thêm section Người dùng
     */
    private static void addUsersSection(Document document, List<User> usersList, PdfFont font, PdfFont boldFont) {
        addSectionHeader(document, "NGƯỜI DÙNG", boldFont);
        
        float[] columnWidths = {1.5f, 2.5f, 2.5f, 1.5f, 1.5f, 1.2f, 1.5f, 1.3f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        String[] headers = {"ID", "Họ tên", "Email", "Điện thoại", "Ngày sinh", "Giới tính", "Vai trò", "Trạng thái"};
        addTableHeader(table, headers, boldFont);
        
        int rowIndex = 0;
        for (User user : usersList) {
            DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
            addCell(table, user.getId(), bgColor, font);
            addCell(table, user.getFullname(), bgColor, font);
            addCell(table, user.getEmail(), bgColor, font);
            addCell(table, user.getMobile(), bgColor, font);
            addCell(table, user.getBirthday() != null ? DATE_ONLY_FORMAT.format(user.getBirthday()) : "-", bgColor, font);
            addCell(table, user.isGender() ? "Nam" : "Nữ", bgColor, font);
            addCell(table, user.isRole() ? "Quản trị" : "Phóng viên", bgColor, font);
            addCell(table, user.isEnabled() ? "Hoạt động" : "Bị khóa", bgColor, font);
            rowIndex++;
        }
        
        document.add(table);
        addFooter(document, "Tổng số: " + usersList.size() + " người dùng", boldFont);
    }
    
    /**
     * Thêm section Danh mục
     */
    private static void addCategoriesSection(Document document, List<Category> categoriesList, PdfFont font, PdfFont boldFont) {
        addSectionHeader(document, "DANH MỤC", boldFont);
        
        float[] columnWidths = {1, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(50));
        
        String[] headers = {"Mã", "Tên danh mục"};
        addTableHeader(table, headers, boldFont);
        
        int rowIndex = 0;
        for (Category category : categoriesList) {
            DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
            addCell(table, category.getId(), bgColor, font);
            addCell(table, category.getName(), bgColor, font);
            rowIndex++;
        }
        
        document.add(table);
        addFooter(document, "Tổng số: " + categoriesList.size() + " danh mục", boldFont);
    }
    
    /**
     * Thêm section Newsletter
     */
    private static void addNewslettersSection(Document document, List<Newsletter> newslettersList, PdfFont font, PdfFont boldFont) {
        addSectionHeader(document, "NEWSLETTER", boldFont);
        
        float[] columnWidths = {3, 1.5f, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(70));
        
        String[] headers = {"Email", "Trạng thái", "Ngày đăng ký"};
        addTableHeader(table, headers, boldFont);
        
        int rowIndex = 0;
        for (Newsletter newsletter : newslettersList) {
            DeviceRgb bgColor = (rowIndex % 2 == 0) ? null : STRIPE_COLOR;
            addCell(table, newsletter.getEmail(), bgColor, font);
            addCell(table, newsletter.isEnabled() ? "Hoạt động" : "Đã hủy", bgColor, font);
            addCell(table, newsletter.getSubscribedDate() != null ? DATE_FORMAT.format(newsletter.getSubscribedDate()) : "-", bgColor, font);
            rowIndex++;
        }
        
        document.add(table);
        addFooter(document, "Tổng số: " + newslettersList.size() + " email", boldFont);
    }
}
