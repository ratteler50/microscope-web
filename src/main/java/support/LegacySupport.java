package support;

import static model.DatabaseReads.getLegacies;
import static model.DatabaseUpdates.actionAllReset;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.legacyRemoveNull;
import static model.DatabaseUpdates.legacySwapNull;
import static model.DatabaseUpdates.playerSetActionDone;

import db.DbException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import objects.Legacy;

public class LegacySupport {

  /**
   * Takes all of the legacies from the last round, and returns a list of objects of them with round
   * changed to this round, to be added to the database if the user does not update their legacy
   *
   *
   * DOES NOT UPDATE THE DATABASE
   */
  public static List<Legacy> copyLegacies(int gameID, int round) {
    List<Legacy> legacies = getRoundsLegacies(gameID, round - 1);

    // Change the round to this round
    for (Legacy leg : legacies) {
      leg.id = -1;
      leg.round = round;
    }
    return legacies;
  }

  /**
   * Takes all of the legacies from the last round, and returns a list of objects of them with round
   * changed to this round with the exception of one.
   *
   * The given user's legacy is added instead with their legacy string, and their old legacy's user
   * is set to null.
   *
   * This is used when a user wants to update their legacy, and initiates an auction.
   *
   * DOES NOT UPDATE THE DATABASE
   */
  public static List<Legacy> copyAndAddLegacy(int gameID, int round,
      int userID, String legacy) {
    List<Legacy> legacies = getRoundsLegacies(gameID, round - 1);

    // Change the round to this round, and the user's old legacy's player to
    // null
    for (Legacy leg : legacies) {
      if (leg.playerID == userID) {
        leg.playerID = null;
      }
      leg.id = -1;
      leg.round = round;
    }

    legacies.add(new Legacy(-1, legacy, round, gameID, userID));
    return legacies;
  }

  /**
   * Returns all the legacies for the game for that round
   */

  public static List<Legacy> getRoundsLegacies(int gameID, int round) {
    List<Legacy> roundLegacies = new ArrayList<Legacy>();
    CachedRowSet rowSet;
    try {
      // Get Legacies for given round
      rowSet = getLegacies(gameID, round);
      while (rowSet.next()) {
        roundLegacies.add(new Legacy(rowSet));
      }
      Collections.sort(roundLegacies);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return roundLegacies;
  }

  /**
   * The given player swaps their legacy with the one being held by null.
   *
   * This resets all player action flags.
   *
   * THIS UPDATES THE DATABASE
   */
  public static void takeDiscarded(int userID, int gameID, int round)
      throws DbException {
    legacySwapNull(userID, gameID, round);
    actionAllReset(gameID);

    // The player who just took the extra card cannot swap back
    playerSetActionDone(userID, gameID, true);

  }

  /**
   * Returns TRUE if the game gameID is currently undergoing an auction
   */
  public static boolean isAuction(int gameID, int round) {
    List<Legacy> roundsLegacies = getRoundsLegacies(gameID, round);

		/*
     * There is an auction only if there are currently legacies in the
		 * database for this round, and one has player as null (which means one
		 * was discarded when a new one was placed, as opposed to the first one
		 * placed for that player)
		 */
    for (Legacy leg : roundsLegacies) {
      if (leg.playerID == null) {
        return true;
      }
    }

    return false;
  }

  public static void endAuction(int gameID, int round) throws DbException {
    legacyRemoveNull(gameID, round);
    gameNextTurn(gameID);
  }

  /**
   * Checks if any legacies exist for the current round
   */
  public boolean needsRoundLegacies(int gameID, int round) {
    return getRoundsLegacies(gameID, round).isEmpty();
  }

  /**
   * Checks if the user has a legacy currently. If so, that user may pass *
   */
  public boolean userCanPass(int gameID, int round, int user) {
    for (Legacy leg : getRoundsLegacies(gameID, round)) {
      if (leg.playerID == user) {
        return true;
      }
    }
    return false;
  }
}
