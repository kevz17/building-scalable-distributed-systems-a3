import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Template from https://www.baeldung.com/hikaricp
 */
public class DataSource {

  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource dataSource;
  private static final String DATABASE = "GiantTigle";
    private static final String HOST_NAME = System.getProperty("MYSQL_IP_ADDRESS");
    private static final String PORT = System.getProperty("MYSQL_PORT");
    private static final String USERNAME = System.getProperty("DB_USERNAME");
    private static final String PASSWORD = System.getProperty("DB_PASSWORD");

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    String url = String
        .format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
    config.setJdbcUrl(url);
    config.setUsername(USERNAME);
    config.setPassword(PASSWORD);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    config.addDataSourceProperty("useLocalSessionState", "true");
    config.addDataSourceProperty("rewriteBatchedStatements", "true");
    config.addDataSourceProperty("cacheResultSetMetadata", "true");
    config.addDataSourceProperty("cacheServerConfiguration", "true");
    config.addDataSourceProperty("elideSetAutoCommits", "true");
    config.addDataSourceProperty("maintainTimeStats", "true");

    dataSource = new HikariDataSource(config);
  }

  public DataSource() {
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
