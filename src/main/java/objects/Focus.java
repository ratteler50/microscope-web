package objects;

import com.google.auto.value.AutoValue;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;


@AutoValue
public abstract class Focus {

  public abstract int getId();

  public abstract String getFocus();

  public abstract int getRound();

  public abstract int getGameId();

  public abstract int getPlayerId();

  public static Focus fromRowSet(CachedRowSet row) throws SQLException {
    int id = row.getInt("id");
    String focus = row.getString("focus");
    int round = row.getInt("round");
    int gameId = row.getInt("game");
    int playerId = row.getInt("player");
    return new AutoValue_Focus(id, focus, round, gameId, playerId);
  }
}
