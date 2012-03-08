package testing;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import org.sqlite.SQLiteDataSource;

public class DBConnect {

  /**
   * Connect to a sample database
   */
  public static void connect() {
    SQLiteDataSource source = new SQLiteDataSource();
    source.setUrl("jdbc:sqlite:" + Paths.get("microscospe.db").toAbsolutePath().toString());
    Connection conn = null;
    try {
      conn = checkNotNull(source.getConnection(), "Could not connect to DB");
//            conn = DriverManager.getConnection("jdbc:sqlite:" + source.getUrl());
      System.err.println(conn.isValid(400) ? "valid" : "invalid");
      conn.getMetaData();

      System.out.println("Connection to SQLite has been established.");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    connect();
  }
}
