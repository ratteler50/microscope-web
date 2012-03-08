package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public class Legacy implements Comparable<Legacy> {

  // Set during construction
  public int id;
  public String legacy;
  public int round;
  public int gameID;
  public Integer playerID;
  /**
   * @param id
   * @param legacy
   * @param round
   * @param gameID
   * @param playerID
   */
  public Legacy(int id, String legacy, int round, int gameID, Integer playerID) {
    super();
    this.id = id;
    this.legacy = legacy;
    this.round = round;
    this.gameID = gameID;
    this.playerID = playerID;
  }
  public Legacy(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.legacy = row.getString("legacy");
    this.round = row.getInt("round");
    this.gameID = row.getInt("game");
    this.playerID = row.getInt("player");
    if (row.wasNull()) {
      this.playerID = null;
    }
  }

  public int compareTo(Legacy otherLegacy) {
    return round - otherLegacy.round;
  }

  @Override
  public String toString() {
    return "Legacy [id=" + id + ", "
        + (legacy != null ? "legacy=" + legacy + ", " : "") + "round="
        + round + ", gameID=" + gameID + ", playerID=" + playerID + "]";
  }
}
