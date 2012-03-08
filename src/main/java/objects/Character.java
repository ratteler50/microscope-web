package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public class Character {

  // Set during construction
  public int id;
  public String name;
  public String description;
  public Character(CachedRowSet row) throws SQLException {
    this.id = row.getInt("id");
    this.name = row.getString("name");
    this.description = row.getString("description");
  }

  @Override
  public String toString() {
    return "Character [id=" + id + ", "
        + (name != null ? "name=" + name + ", " : "")
        + (description != null ? "description=" + description : "")
        + "]";
  }

}
