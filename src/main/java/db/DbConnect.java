package db;

import static support.Settings.HEROKU;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class DbConnect {

  public static DataSource getDataSource() {
    // Default to localhost settings
    String user = "postgres";
    String password = "password";
    String serverName = "localhost";
    String databaseName = "microscope";
    int portNumber = 5432;
    PGSimpleDataSource source = new PGSimpleDataSource();
    if (HEROKU) {
      source.setSsl(true);
      source.setSslfactory("org.postgresql.ssl.NonValidatingFactory");
      URI dbUri = null;
      try {
        dbUri = new URI(System.getenv("DATABASE_URL"));
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }

      user = dbUri.getUserInfo().split(":")[0];
      password = dbUri.getUserInfo().split(":")[1];
      serverName = dbUri.getHost();
      databaseName = dbUri.getPath().substring(1).trim();
      portNumber = dbUri.getPort();
    }

    source.setServerName(serverName);
    source.setDatabaseName(databaseName);
    source.setPortNumber(portNumber);
    source.setUser(user);
    source.setPassword(password);
    return source;
  }

  // Connect to the database
  public static Connection connect() throws DbException {
    try {
      return getDataSource().getConnection();
    } catch (SQLException ex) {
      System.err.println("Couldn't connect to database");
      ex.printStackTrace();
      throw new DbException(ex);
    }
  }
}
