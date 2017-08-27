package objects;

import static objects.IndexCard.Tone.DARK;
import static objects.IndexCard.Tone.LIGHT;

import java.sql.SQLException;
import java.util.Optional;
import java.util.OptionalInt;
import javax.sql.rowset.CachedRowSet;

public class Scene extends IndexCard {

  // Set during construction
  public boolean dictated;
  public int eventID;
  public String setting;
  public String answer;
  public OptionalInt banned1Id;
  public OptionalInt banned2Id;
  public OptionalInt required1Id;
  public OptionalInt required2Id;
  public int stepsLeft;
  // Not set during construction
  public Optional<Character> banned1 = Optional.empty();
  public Optional<Character> banned2 = Optional.empty();
  public Optional<Character> required1 = Optional.empty();
  public Optional<Character> required2 = Optional.empty();
  // I am assuming for now no games have more than 10 characters in them
  public Character[] inScene = new Character[10];

  public Scene(CachedRowSet row) throws SQLException {
    super(row, "question");
    this.dictated = row.getBoolean("dictated");
    this.eventID = row.getInt("event");
    this.setting = row.getString("setting");
    this.answer = row.getString("answer");
    this.banned1Id = OptionalInt.of(row.getInt("banned1"));
    if (row.wasNull()) {
      this.banned1Id = OptionalInt.empty();
    }
    this.banned2Id = OptionalInt.of(row.getInt("banned2"));
    if (row.wasNull()) {
      this.banned2Id = OptionalInt.empty();
    }
    this.required1Id = OptionalInt.of(row.getInt("required1"));
    if (row.wasNull()) {
      this.required1Id = OptionalInt.empty();
    }
    this.required2Id = OptionalInt.of(row.getInt("required2"));
    if (row.wasNull()) {
      this.required2Id = OptionalInt.empty();
    }
    this.stepsLeft = row.getInt("steps_left");
  }

  @Override
  public String toString() {
    return super.toString()
        + "dictated="
        + dictated
        + ", position="
        + position
        + ", "
        + (setting != null ? "setting=" + setting + ", " : "")
        + (answer != null ? "answer=" + answer + ", " : "")
        + (description != null ? "description=" + description + ", "
        : "") + "banned1Id=" + banned1Id + ", banned2Id="
        + banned2Id + ", required1Id=" + required1Id + ", required2Id="
        + required2Id + ", eventID=" + eventID + ", "
        + (banned1 != null ? "banned1=" + banned1 + ", " : "")
        + (banned2 != null ? "banned2=" + banned2 + ", " : "")
        + (required1 != null ? "required1=" + required1 + ", " : "")
        + (required2 != null ? "required2=" + required2 + ", " : "")
        + (inScene != null ? "inScene=" + inScene : "") + "]";
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

}
