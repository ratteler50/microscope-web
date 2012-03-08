package objects;

import com.google.auto.value.AutoValue;
import java.sql.SQLException;
import java.util.OptionalInt;
import javax.sql.rowset.CachedRowSet;

@AutoValue
public abstract class Legacy implements Comparable<Legacy> {

  // Set during construction
  public abstract int getId();

  public abstract String getLegacy();

  public abstract int getRound();

  public abstract int getGameId();

  public abstract OptionalInt getPlayerId();

  public abstract Builder toBuilder();

  public static Legacy create(
      int newId, String newLegacy, int newRound, int newGameId, int newPlayerId) {
    return builder()
        .setId(newId)
        .setLegacy(newLegacy)
        .setRound(newRound)
        .setGameId(newGameId)
        .setPlayerId(newPlayerId)
        .build();
  }

  public int compareTo(Legacy otherLegacy) {
    return getRound() - otherLegacy.getRound();
  }

  public static Legacy fromRow(CachedRowSet row) throws SQLException {
    Legacy.Builder builder =
        Legacy.builder()
            .setId(row.getInt("id"))
            .setLegacy(row.getString("legacy"))
            .setRound(row.getInt("round"))
            .setGameId(row.getInt("game"));
    int playerId = row.getInt("player");
    // If row was NULL, getInt returns 0.  We want to differentiate here.
    if (!row.wasNull()) {
      builder.setPlayerId(playerId);
    }
    return builder.build();
  }

  public static Builder builder() {
    return new AutoValue_Legacy.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setId(int newId);

    public abstract Builder setLegacy(String newLegacy);

    public abstract Builder setRound(int newRound);

    public abstract Builder setGameId(int newGameId);

    public abstract Builder setPlayerId(int newPlayerId);

    public abstract Builder setPlayerId(OptionalInt newPlayerId);

    public abstract Legacy build();
  }
}
