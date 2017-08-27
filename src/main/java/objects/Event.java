package objects;

import static objects.IndexCard.Tone.DARK;
import static objects.IndexCard.Tone.LIGHT;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

public class Event extends IndexCard {

  public int periodID;
  // Not set during construction
  public List<Scene> scenes;

  public Event(CachedRowSet row) throws SQLException {
    super(row, "event");
    this.periodID = row.getInt("period");
    this.scenes = new ArrayList<>();
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getRound() {
    return round;
  }

  @Override
  public int getTurn() {
    return turn;
  }

  @Override
  public Tone getTone() {
    return tone ? LIGHT : DARK;
  }

  @Override
  public int getPosition() {
    return position;
  }

  @Override
  public int getPlayerID() {
    return playerID;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return super.toString()
        + "periodID="
        + periodID
        + ", "
        + (scenes != null ? "scenes=" + scenes : "")
        + "]";
  }
}
