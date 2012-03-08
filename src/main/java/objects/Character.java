package objects;

import com.google.auto.value.AutoValue;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

@AutoValue
public abstract class Character {

  public abstract int getId();

  public abstract String getName();

  public abstract String getDescription();

  public static Character fromRow(CachedRowSet row) throws SQLException {
    int id = row.getInt("id");
    String name = row.getString("name");
    String description = row.getString("description");
    return new AutoValue_Character(id, name, description);
  }
}
