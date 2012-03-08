package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;


public class Focus {

  // Set during construction
  public int id;
  public String focus;
  public int round;
  public int gameID;
  public int playerID;
  public Focus(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.focus = row.getString("focus");
    this.round = row.getInt("round");
    this.gameID = row.getInt("game");
    this.playerID = row.getInt("player");
  }

  @Override
  public String toString() {
    return "Focus [id=" + id + ", "
        + (focus != null ? "focus=" + focus + ", " : "") + "round="
        + round + ", gameID=" + gameID + ", playerID=" + playerID + "]";
  }
}
