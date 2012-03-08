package objects;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public abstract class IndexCard implements Comparable<IndexCard> {

  public final int id;
  public final int round;
  public final int turn;
  public final boolean tone;
  public final int position;
  public final int playerID;
  public final String text;
  public final String description;


  public IndexCard(CachedRowSet row, String cardTextColumn) throws SQLException {
    this.id = row.getInt("id");
    this.tone = row.getBoolean("tone");
    this.turn = row.getInt("turn");
    this.round = row.getInt("round");
    this.playerID = row.getInt("player");
    this.position = row.getInt("position");
    this.description = row.getString("description");
    this.text = row.getString(cardTextColumn);
  }

  /**
   * This compare method returns negative is this card comes before otherCard and positive is this
   * card comes after.
   */
  public int compareTo(IndexCard otherCard) {
    return position - otherCard.position;
  }


  @Override
  public String toString() {
    return "IndexCard [id=" + id + ", round=" + round + ", turn=" + turn
        + ", tone=" + tone + ", position=" + position + ", playerID="
        + playerID + ", text=" + text + ", description=" + description
        + "]";
  }


}
