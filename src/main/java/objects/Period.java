package objects;

import static objects.IndexCard.Tone.DARK;
import static objects.IndexCard.Tone.LIGHT;

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
  }}
