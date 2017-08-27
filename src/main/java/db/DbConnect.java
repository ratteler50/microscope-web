package db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.sqlite.SQLiteDataSource;

public final class DbConnect {

  static DataSource getDataSource() {
    SQLiteDataSource source = new SQLiteDataSource();
    source.setUrl("jdbc:sqlite:" + Paths.get("microscope.db").toAbsolutePath().toString());
    return source;
  }

  // Connect to the database
  public static Connection connect() throws DbException {
    try {
      return checkNotNull(getDataSource().getConnection());
    } catch (SQLException ex) {
      System.err.println("Couldn't connect to database");
      ex.printStackTrace();
      throw new DbException(ex);
    }
  }
}
