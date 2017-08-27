package objects;

import com.google.auto.value.AutoValue;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

/**
 * A single element of the palette
 *
 * @author dlorant
 */
@AutoValue
public abstract class Palette {

  public abstract int getId();

  public abstract int getUserID();

  public abstract String getDescription();

  public abstract boolean isInGame(); // AKA not banned

  public abstract int getGameID();

  public static Palette fromRow(CachedRowSet row) throws SQLException {
    int id = row.getInt("id");
    int userID = row.getInt("player");
    String description = row.getString("description");
    boolean inGame = row.getBoolean("in_game");
    int gameID = row.getInt("game");
    return new AutoValue_Palette(id, userID, description, inGame, gameID);
  }
}
