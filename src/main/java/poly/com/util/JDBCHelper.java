package poly.com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Lớp tiện ích quản lý kết nối và thao tác với cơ sở dữ liệu SQL Server
 * 
 * Nâng cấp kiến trúc hiệu năng cao:
 * - Sử dụng HikariCP Connection Pool (Connection pool nhanh và nhẹ nhất cho Java)
 * - Tái sử dụng các kết nối đã mở thay vì tạo mới liên tục, giảm thiểu độ trễ kết nối
 * - Cơ chế Fallback an toàn sang DriverManager nếu HikariCP gặp sự cố cấu hình
 * 
 * @author ABCNews Development Team
 * @version 2.0 (HikariCP Integrated)
 */
public class JDBCHelper {
    
    /** Tên database */
    private static final String DB_NAME = ConfigHelper.get("db.name", "ABCNews");
    
    /** Username để đăng nhập vào SQL Server */
    private static final String DB_USER = ConfigHelper.get("db.user", "sa");
    
    /** Password để đăng nhập vào SQL Server */
    private static final String DB_PASS = ConfigHelper.get("db.password", "123456");
    
    /** Địa chỉ host của SQL Server */
    private static final String DB_HOST = ConfigHelper.get("db.host", "localhost");
    
    /** Cổng kết nối của SQL Server (mặc định: 1433) */
    private static final String DB_PORT = ConfigHelper.get("db.port", "1433");

    /** Connection URL đầy đủ để kết nối đến database */
    private static String connectionUrl;
    
    /** Tên class của JDBC Driver cho SQL Server */
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /** HikariCP DataSource singleton */
    private static HikariDataSource dataSource;

    static {
        // Ưu tiên đọc cấu hình db.url nếu có
        String configuredUrl = ConfigHelper.get("db.url");
        if (configuredUrl != null && !configuredUrl.trim().isEmpty()) {
            connectionUrl = configuredUrl.trim();
        } else {
            connectionUrl = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true;sendStringParametersAsUnicode=true;characterEncoding=UTF-8;",
                    DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASS);
        }
        
        try {
            // Đăng ký JDBC Driver
            Class.forName(DRIVER_CLASS);
            
            // Khởi tạo HikariCP Connection Pool
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(DRIVER_CLASS);
            config.setJdbcUrl(connectionUrl);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASS);
            
            // Tối ưu hóa cấu hình Pool
            int maxPoolSize = ConfigHelper.getInt("db.pool.maxSize", 10);
            int minIdle = ConfigHelper.getInt("db.pool.minIdle", 2);
            int timeout = ConfigHelper.getInt("db.pool.connectionTimeout", 10000);
            
            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);
            config.setConnectionTimeout(timeout);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000); // 30 phút
            config.setPoolName("ABCNews-HikariCP-Pool");
            
            dataSource = new HikariDataSource(config);
            System.out.println("[INFO] Khởi tạo HikariCP Connection Pool thành công cho database: " + DB_NAME);
            
        } catch (Throwable e) {
            System.err.println("[CẢNH BÁO] Không thể khởi tạo HikariCP Connection Pool (" + e.getMessage() + "). Chuyển sang chế độ DriverManager dự phòng.");
            dataSource = null;
        }
    }

    /**
     * Mở kết nối đến cơ sở dữ liệu SQL Server từ HikariCP Pool
     * 
     * @return Đối tượng Connection
     * @throws SQLException nếu không thể lấy kết nối
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            return dataSource.getConnection();
        }
        // Dự phòng: Mở kết nối trực tiếp qua DriverManager nếu pool chưa khởi tạo
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
     * ĐÃ FIX: Connection và PreparedStatement được đóng đúng cách trong mọi trường hợp
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
     * [DEPRECATED] - KHONG NEN SU DUNG!
     * 
     * Phương thức này có vấn đề nghiêm trọng về quản lý tài nguyên:
     * - Tạo Connection và PreparedStatement nhưng không đóng chúng
     * - Connection được tạo bên trong getPreparedStatement(), không thể truy cập từ bên ngoài
     * - Gây memory leak và connection pool exhaustion
     * 
     * [GIAI PHAP]: Sử dụng pattern trong các DAO:
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
     *         [LUU Y]: Connection và PreparedStatement KHÔNG được đóng tự động!
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

    /**
     * Đóng HikariCP connection pool một cách an toàn
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println(">> Đã đóng HikariDataSource connection pool an toàn.");
        }
    }
}