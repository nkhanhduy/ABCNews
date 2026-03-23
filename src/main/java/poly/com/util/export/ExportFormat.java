package poly.com.util.export;

/**
 * Enum định nghĩa các định dạng export được hỗ trợ
 * Mỗi format có content type và extension riêng để phục vụ HTTP response
 */
public enum ExportFormat {
    CSV("text/csv", ".csv"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    PDF("application/pdf", ".pdf");

    private final String contentType;
    private final String extension;

    ExportFormat(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }
    
    /**
     * Parse string thành ExportFormat
     * @param format String format ("csv", "excel", "pdf")
     * @return ExportFormat tương ứng
     * @throws IllegalArgumentException nếu format không hợp lệ
     */
    public static ExportFormat fromString(String format) {
        if (format == null || format.trim().isEmpty()) {
            return CSV; // Default
        }
        try {
            return ExportFormat.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid export format: " + format);
        }
    }
}

