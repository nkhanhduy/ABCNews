package poly.com.util;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

/**
 * FontHelper - Quản lý font cho PDF Export
 * 
 * Sử dụng DejaVu Sans font (TTF) để hỗ trợ TIẾNG VIỆT 100%
 * 
 * Font files cần có trong: src/main/resources/fonts/
 * - DejaVuSans.ttf (Regular)
 * - DejaVuSans-Bold.ttf (Bold)
 * 
 * @author Nguyen Duy Khanh
 */
public class FontHelper {
    
    private static final String FONT_REGULAR = "/fonts/DejaVuSans.ttf";
    private static final String FONT_BOLD = "/fonts/DejaVuSans-Bold.ttf";
    
    private static final Logger LOGGER = Logger.getLogger(FontHelper.class.getName());
    
    // Cache font DATA (byte array), KHÔNG cache PdfFont object
    private static byte[] regularFontData = null;
    private static byte[] boldFontData = null;
    
    /**
     * Lấy font Regular (DejaVu Sans)
     * TẠO MỚI mỗi lần để tránh lỗi "belongs to other PDF document"
     * 
     * @return PdfFont Regular hỗ trợ tiếng Việt
     * @throws RuntimeException nếu không tìm thấy font file
     */
    public static PdfFont getRegularFont() {
        try {
            // Load font data (chỉ load 1 lần)
            if (regularFontData == null) {
                InputStream fontStream = FontHelper.class.getResourceAsStream(FONT_REGULAR);
                
                if (fontStream == null) {
                    throw new RuntimeException("Không tìm thấy font file: " + FONT_REGULAR + 
                        "\nVui lòng copy DejaVuSans.ttf vào folder: src/main/resources/fonts/");
                }
                
                regularFontData = fontStream.readAllBytes();
                fontStream.close();
            }
            
            // Tạo PdfFont MỚI mỗi lần (quan trọng!)
            return PdfFontFactory.createFont(regularFontData, PdfEncodings.IDENTITY_H, 
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể tải DejaVuSans font, chuyển sang Helvetica: {0}", e.getMessage());
            // Fallback về Helvetica
            try {
                return PdfFontFactory.createFont("Helvetica", PdfEncodings.IDENTITY_H);
            } catch (Exception ex) {
                throw new RuntimeException("Không thể tạo font!", ex);
            }
        }
    }
    
    /**
     * Lấy font Bold (DejaVu Sans Bold)
     * TẠO MỚI mỗi lần để tránh lỗi "belongs to other PDF document"
     * 
     * @return PdfFont Bold hỗ trợ tiếng Việt
     * @throws RuntimeException nếu không tìm thấy font file
     */
    public static PdfFont getBoldFont() {
        try {
            // Load font data (chỉ load 1 lần)
            if (boldFontData == null) {
                InputStream fontStream = FontHelper.class.getResourceAsStream(FONT_BOLD);
                
                if (fontStream == null) {
                    throw new RuntimeException("Không tìm thấy font file: " + FONT_BOLD + 
                        "\nVui lòng copy DejaVuSans-Bold.ttf vào folder: src/main/resources/fonts/");
                }
                
                boldFontData = fontStream.readAllBytes();
                fontStream.close();
            }
            
            // Tạo PdfFont MỚI mỗi lần (quan trọng!)
            return PdfFontFactory.createFont(boldFontData, PdfEncodings.IDENTITY_H, 
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể tải DejaVuSans-Bold font, chuyển sang Helvetica-Bold: {0}", e.getMessage());
            // Fallback về Helvetica-Bold
            try {
                return PdfFontFactory.createFont("Helvetica-Bold", PdfEncodings.IDENTITY_H);
            } catch (Exception ex) {
                throw new RuntimeException("Không thể tạo font bold!", ex);
            }
        }
    }
    
    /**
     * Reset font data cache (dùng khi cần reload font mới)
     */
    public static void resetFonts() {
        regularFontData = null;
        boldFontData = null;
    }
    
    /**
     * Kiểm tra font files có tồn tại không
     * 
     * @return true nếu cả 2 font files đều có
     */
    public static boolean checkFontsExist() {
        InputStream regular = FontHelper.class.getResourceAsStream(FONT_REGULAR);
        InputStream bold = FontHelper.class.getResourceAsStream(FONT_BOLD);
        
        boolean exists = (regular != null && bold != null);
        
        try {
            if (regular != null) regular.close();
            if (bold != null) bold.close();
        } catch (Exception e) {
            // Ignore
        }
        
        return exists;
    }
}

