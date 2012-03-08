package objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

public class Period extends IndexCard {

  // Set during construction
  public int gameID;
  // Not set during construction
  public List<Character> characters;
  public List<Event> events;
  public Period(CachedRowSet row) throws SQLException {
    super(row, "period");
    this.gameID = row.getInt("game");
    this.events = new ArrayList<Event>();
  }

  @Override
  public String toString() {
    return super.toString()
        + (characters != null ? "characters=" + characters + ", " : "")
        + (events != null ? "events=" + events + ", " : "") + "gameID="
        + gameID + "]";
  }

}
