package objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

public class Event extends IndexCard {

  public int periodID;
  // Not set during construction
  public List<Scene> scenes;
  public boolean processed = false;

  public Event(CachedRowSet row) throws SQLException {
    super(row, "event");
    this.periodID = row.getInt("period");
    this.scenes = new ArrayList<Scene>();
  }

  @Override
  public String toString() {
    return super.toString() + "periodID=" + periodID + ", "
        + (scenes != null ? "scenes=" + scenes : "") + "]";
  }
}
