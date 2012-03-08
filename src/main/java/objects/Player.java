package objects;

import com.google.auto.value.AutoValue;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

@AutoValue
public abstract class Player implements Comparable<Player> {

  public abstract String getUsername();

  public abstract int getGameId();

  public abstract int getUserId();

  public abstract int getPosition();

  public abstract int getPaletteNum();

  public abstract boolean getActionDone();

  public static Player fromRow(CachedRowSet row) throws SQLException {
    String username = row.getString("user_name");
    int gameId = row.getInt("game");
    int userId = row.getInt("userid");
    int position = row.getInt("position");
    int paletteNum = row.getInt("palette_num");
    boolean actionDone = row.getBoolean("action_done");

    return new AutoValue_Player(username, gameId, userId, position, paletteNum,
        actionDone);
  }

  public int compareTo(Player otherPlayer) {
    return getPosition() - otherPlayer.getPosition();
  }
}
