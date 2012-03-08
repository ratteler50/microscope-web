package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

/**
 * A single element of the palette
 *
 * @author dlorant
 */
public class Palette {

  // Set during construction
  public int id;
  public int userID;
  public String description;
  public boolean inGame; // AKA not banned
  public int gameID;
  public Palette(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.userID = row.getInt("player");
    this.description = row.getString("description");
    this.inGame = row.getBoolean("in_game");
    this.gameID = row.getInt("game");
  }

  @Override
  public String toString() {
    return "Palette [id="
        + id
        + ", userID="
        + userID
        + ", "
        + (description != null ? "description=" + description + ", "
        : "") + "inGame=" + inGame + ", gameID=" + gameID + "]";
  }


}
