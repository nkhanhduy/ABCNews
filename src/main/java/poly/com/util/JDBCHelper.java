package poly.com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp tiện ích (Helper Class) để quản lý kết nối và thao tác với cơ sở dữ liệu SQL Server
 * 
 * Lớp này cung cấp các phương thức static để:
 * - Tạo và quản lý kết nối đến database
 * - Thực thi các câu lệnh SQL (SELECT, INSERT, UPDATE, DELETE) với PreparedStatement
 * - Đóng các tài nguyên database (Connection, PreparedStatement, ResultSet)
 * 
 * Sử dụng PreparedStatement để tránh SQL Injection và tăng hiệu suất.
 * 
 * @author ABCNews Development Team
 * @version 1.0
 */
public class JDBCHelper {
    
    /** Tên database */
    private static final String DB_NAME = "ABCNews";
    
    /** Username để đăng nhập vào SQL Server */
    private static final String DB_USER = "sa";
    
    /** Password để đăng nhập vào SQL Server */
    private static final String DB_PASS = "123456";
    
    /** Địa chỉ host của SQL Server */
    private static final String DB_HOST = "localhost";
    
    /** Cổng kết nối của SQL Server (mặc định: 1433) */
    private static final String DB_PORT = "1433";

    /** Connection URL đầy đủ để kết nối đến database */
    private static String connectionUrl;
    
    /** Tên class của JDBC Driver cho SQL Server */
    private static String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * Khối static: Khởi tạo connection URL và nạp JDBC driver khi class được load lần đầu
     * 
     * Khối này sẽ chạy một lần duy nhất khi class được JVM load vào memory.
     * Nó thực hiện:
     * 1. Tạo connection URL từ các thông tin database
     * 2. Nạp JDBC driver class để đăng ký với DriverManager
     * 
     * Nếu không tìm thấy driver, sẽ throw RuntimeException để dừng ứng dụng.
     */
    static {
        // Tạo connection URL với format chuẩn của SQL Server JDBC
        connectionUrl = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true;",
                DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASS);
        
        try {
            // Nạp và đăng ký JDBC driver
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver JDBC: " + driverClass);
            throw new RuntimeException("Lỗi nạp driver JDBC", e);
        }
    }

    /**
     * Mở kết nối mới đến cơ sở dữ liệu SQL Server
     * 
     * Phương thức này sử dụng DriverManager để tạo một Connection mới.
     * Connection này cần được đóng sau khi sử dụng xong để tránh memory leak.
     * 
     * @return Đối tượng Connection đã kết nối đến database
     * @throws SQLException nếu không thể kết nối đến database (sai thông tin, database không tồn tại, v.v.)
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    /**
     * Tạo đối tượng PreparedStatement với các tham số đã được gán sẵn
     * 
     * Phương thức này:
     * 1. Tạo một Connection mới
     * 2. Tạo PreparedStatement từ Connection đó
     * 3. Gán các tham số vào PreparedStatement (thay thế các dấu ? trong SQL)
     * 4. Trả về PreparedStatement đã sẵn sàng để thực thi
     * 
     * Lưu ý: Connection được tạo bên trong method này, cần được đóng sau khi dùng xong.
     * 
     * @param sql Câu lệnh SQL có chứa các placeholder (dấu ?) để thay thế bằng tham số
     *            Ví dụ: "SELECT * FROM Users WHERE Id = ? AND Email = ?"
     * @param args Danh sách các giá trị để thay thế vào các placeholder trong SQL
     *             Thứ tự phải khớp với thứ tự các dấu ? trong SQL
     * @return PreparedStatement đã được gán tham số, sẵn sàng để thực thi
     * @throws SQLException nếu có lỗi khi tạo PreparedStatement hoặc gán tham số
     */
    public static PreparedStatement getPreparedStatement(String sql, Object... args) throws SQLException {
        // Tạo Connection mới
        Connection conn = getConnection();
        PreparedStatement pstmt;
        
        // Nếu là câu lệnh INSERT, yêu cầu trả về generated keys (auto-increment ID)
        if (sql.trim().toLowerCase().startsWith("insert")) {
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            // Các câu lệnh khác (SELECT, UPDATE, DELETE) không cần generated keys
            pstmt = conn.prepareStatement(sql);
        }

        // Gán các tham số vào PreparedStatement
        // Tham số đầu tiên có index = 1 (không phải 0)
        for (int i = 0; i < args.length; i++) {
            pstmt.setObject(i + 1, args[i]);
        }
        return pstmt;
    }

    /**
     * Thực thi câu lệnh SQL để cập nhật dữ liệu (INSERT, UPDATE, DELETE)
     * 
     * Phương thức này:
     * 1. Tạo Connection và PreparedStatement từ SQL và các tham số
     * 2. Thực thi câu lệnh bằng executeUpdate()
     * 3. Tự động đóng PreparedStatement và Connection trong finally block
     * 4. Trả về số hàng bị ảnh hưởng
     * 
     * ✅ ĐÃ FIX: Connection và PreparedStatement được đóng đúng cách trong mọi trường hợp
     * 
     * @param sql Câu lệnh SQL có chứa các placeholder (dấu ?)
     *            Ví dụ: "UPDATE Users SET Fullname = ? WHERE Id = ?"
     * @param args Danh sách các giá trị để thay thế vào các placeholder
     * @return Số hàng (rows) bị ảnh hưởng bởi câu lệnh SQL
     *         - INSERT: thường là 1 (nếu thành công)
     *         - UPDATE: số hàng được cập nhật
     *         - DELETE: số hàng bị xóa
     * @throws RuntimeException nếu có lỗi SQL khi thực thi
     */
    public static int executeUpdate(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Tạo Connection và PreparedStatement
            pstmt = getPreparedStatement(sql, args);
            // Lấy Connection từ PreparedStatement để đóng sau
            conn = pstmt.getConnection();
            // Thực thi câu lệnh
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thực thi executeUpdate", e);
        } finally {
            // Đảm bảo đóng PreparedStatement và Connection trong mọi trường hợp
            // (kể cả khi có exception)
            close(pstmt, conn);
        }
    }

    /**
     * Thực thi câu lệnh SQL để truy vấn dữ liệu (SELECT) và trả về ResultSet
     * 
     * ⚠️ DEPRECATED - KHÔNG NÊN SỬ DỤNG!
     * 
     * Phương thức này có vấn đề nghiêm trọng về quản lý tài nguyên:
     * - Tạo Connection và PreparedStatement nhưng không đóng chúng
     * - Connection được tạo bên trong getPreparedStatement(), không thể truy cập từ bên ngoài
     * - Gây memory leak và connection pool exhaustion
     * 
     * ✅ GIẢI PHÁP: Sử dụng pattern trong các DAO:
     * ```java
     * Connection conn = null;
     * PreparedStatement pstmt = null;
     * ResultSet rs = null;
     * try {
     *     pstmt = JDBCHelper.getPreparedStatement(sql, args);
     *     conn = pstmt.getConnection();
     *     rs = pstmt.executeQuery();
     *     // ... xử lý ResultSet ...
     * } finally {
     *     JDBCHelper.close(rs, pstmt, conn);
     * }
     * ```
     * 
     * @param sql Câu lệnh SQL SELECT có chứa các placeholder (dấu ?)
     *            Ví dụ: "SELECT * FROM Users WHERE Email = ?"
     * @param args Danh sách các giá trị để thay thế vào các placeholder
     * @return Đối tượng ResultSet chứa kết quả truy vấn
     *         ⚠️ LƯU Ý: Connection và PreparedStatement KHÔNG được đóng tự động!
     * @throws RuntimeException nếu có lỗi SQL khi thực thi
     * @deprecated Phương thức này có bug memory leak. 
     *             Sử dụng pattern tự quản lý Connection/PreparedStatement trong DAO thay thế.
     */
    @Deprecated
    public static ResultSet executeQuery(String sql, Object... args) {
        try {
            PreparedStatement pstmt = getPreparedStatement(sql, args);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thực thi executeQuery", e);
        }
    }
    
    /**
     * Đóng các tài nguyên database (ResultSet, PreparedStatement, Connection)
     * 
     * Phương thức này đóng các tài nguyên theo thứ tự:
     * 1. ResultSet (nếu có)
     * 2. PreparedStatement/Statement (nếu có)
     * 3. Connection (nếu có)
     * 
     * Mỗi tài nguyên được đóng trong một try-catch riêng để đảm bảo các tài nguyên khác
     * vẫn được đóng ngay cả khi một tài nguyên gặp lỗi khi đóng.
     * 
     * @param rs ResultSet cần đóng (có thể null)
     * @param stmt PreparedStatement hoặc Statement cần đóng (có thể null)
     * @param conn Connection cần đóng (có thể null)
     */
    public static void close(ResultSet rs, PreparedStatement stmt, Connection conn) {
        // Đóng ResultSet
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Đóng PreparedStatement/Statement
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Đóng Connection
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đóng các tài nguyên database khi không có ResultSet
     * 
     * Phương thức này là overload của close() method, dùng khi không có ResultSet.
     * Nó sẽ gọi close(null, stmt, conn) để đóng Statement và Connection.
     * 
     * @param stmt PreparedStatement hoặc Statement cần đóng (có thể null)
     * @param conn Connection cần đóng (có thể null)
     */
    public static void close(PreparedStatement stmt, Connection conn) {
        close(null, stmt, conn);
    }
}