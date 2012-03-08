package objects;

import java.sql.SQLException;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

/**
 * The basic information about a game. Enough to display on a list of games a player is in
 *
 * @author David Lorant
 */
public class GameOverview {

  // Set during construction
  public int id;
  public boolean active;
  public String bigPicture;
  // Not set during construction
  public List<Player> players;

  public GameOverview(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.active = row.getBoolean("active");
    this.bigPicture = row.getString("big_picture");
  }

  @Override
  public String toString() {
    return "GameOverview [id=" + id + ", active=" + active + ", "
        + (bigPicture != null ? "bigPicture=" + bigPicture + ", " : "")
        + (players != null ? "players=" + players : "") + "]";
  }


}
