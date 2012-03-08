package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public class Scene extends IndexCard {

  // Set during construction
  public boolean dictated;
  public int eventID;
  public String setting;
  public String answer;
  public Integer banned1ID;
  public Integer banned2ID;
  public Integer required1ID;
  public Integer required2ID;
  public int stepsLeft;
  // Not set during construction
  public Character banned1 = null;
  public Character banned2 = null;
  public Character required1 = null;
  public Character required2 = null;
  // I am assuming for now no games have more than 10 characters in them
  public Character[] inScene = new Character[10];

  public Scene(CachedRowSet row) throws SQLException {
    super(row, "question");
    this.dictated = row.getBoolean("dictated");
    this.eventID = row.getInt("event");
    this.setting = row.getString("setting");
    this.answer = row.getString("answer");
    this.banned1ID = row.getInt("banned1");
    if (row.wasNull()) {
      this.banned1ID = null;
    }
    this.banned2ID = row.getInt("banned2");
    if (row.wasNull()) {
      this.banned2ID = null;
    }
    this.required1ID = row.getInt("required1");
    if (row.wasNull()) {
      this.required1ID = null;
    }
    this.required2ID = row.getInt("required2");
    if (row.wasNull()) {
      this.required2ID = null;
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
        : "") + "banned1ID=" + banned1ID + ", banned2ID="
        + banned2ID + ", required1ID=" + required1ID + ", required2ID="
        + required2ID + ", eventID=" + eventID + ", "
        + (banned1 != null ? "banned1=" + banned1 + ", " : "")
        + (banned2 != null ? "banned2=" + banned2 + ", " : "")
        + (required1 != null ? "required1=" + required1 + ", " : "")
        + (required2 != null ? "required2=" + required2 + ", " : "")
        + (inScene != null ? "inScene=" + inScene : "") + "]";
  }

}
