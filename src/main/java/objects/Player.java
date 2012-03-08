package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public class Player implements Comparable<Player> {

  // Set during construction
  public String username;
  public int gameID;
  public int userID;
  public int position;
  public int paletteNum;
  public boolean actionDone;
  public Player(CachedRowSet row) throws SQLException {
    this.gameID = row.getInt("game");
    this.userID = row.getInt("userid");
    this.position = row.getInt("position");
    this.paletteNum = row.getInt("palette_num");
    this.actionDone = row.getBoolean("action_done");
    this.username = row.getString("user_name");
  }

  public int compareTo(Player otherPlayer) {
    return position - otherPlayer.position;
  }


  @Override
  public String toString() {
    return "Player ["
        + (username != null ? "username=" + username + ", " : "")
        + "gameID=" + gameID + ", userID=" + userID + ", position="
        + position + ", paletteNum=" + paletteNum + ", actionDone="
        + actionDone + "]";
  }


}
