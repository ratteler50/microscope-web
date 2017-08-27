package objects;

import java.sql.SQLException;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

public class GameState {

  // Set during construction
  public int id;
  public int userID;
  public boolean active;
  public String bigPicture;
  public int round;
  public int turn;
  public int lensID;
  public Integer last_pes; // is the last card a period event or scene
  public Integer lastCardID;
  //Not set during construction
  public Player lens;
  public IndexCard lastCard;
  public Focus focus;
  public List<Period> periods;
  public List<Player> players;
  public List<Legacy> legacies;
  public List<Palette> palette_banned;
  public List<Palette> palette_recommended;
  public GameState(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.active = row.getBoolean("active");
    this.bigPicture = row.getString("big_picture");
    this.round = row.getInt("round");
    this.turn = row.getInt("turn");
    this.lensID = row.getInt("lens");
    this.last_pes = row.getInt("last_pes");
    if (row.wasNull()) {
      this.last_pes = null;
    }
    this.lastCardID = row.getInt("last_card");
    if (row.wasNull()) {
      this.lastCardID = null;
    }
  }

  @Override
  public String toString() {
    return "ActiveGameState [id=" + id + ", active=" + active + ", bigPicture="
        + bigPicture + ", round=" + round + ", turn=" + turn
        + ", lensID=" + lensID + ", last_pes=" + last_pes
        + ", lastCardID=" + lastCardID + ", lens=" + lens
        + ", lastCard=" + lastCard + ", focus=" + focus + ", periods="
        + periods + ", players=" + players + ", legacies="
        + legacies + "palette_banned=" + palette_banned
        + ", palette_recommended=" + palette_recommended + "]";
  }


}
