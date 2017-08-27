package model;

import static model.DatabaseReads.actionAnyDoneCheck;

import db.DbConnect;
import db.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import objects.Legacy;

public class DatabaseUpdates {

  // Connect to the database
  protected static Connection connect() throws DbException {
    return DbConnect.connect();
  }

  public static int actionAllReset(int gameID) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      // Reset the action boolean every turn
      String updatePlayer = "UPDATE PLAYERS SET action_done = ? WHERE game = ?";
      PreparedStatement pstmt = conn.prepareStatement(updatePlayer);
      pstmt.setBoolean(1, false);
      setInt(pstmt, 2, gameID);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Adds a new game with no description to the database
   */
  public static int gameCreate(int userID) throws DbException {
    int gameID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String insert = "INSERT INTO GAMES(active, round, turn, lens) "
          + "VALUES(TRUE,?,?,?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      setInt(pstmt, 1, 0);
      setInt(pstmt, 2, 0);
      setInt(pstmt, 3, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      gameID = rs.getInt("id");
      rs.close();
      pstmt.close();

      String insert2 = "INSERT INTO PLAYERS(game, userid, position, palette_num, action_done) "
          + "VALUES(?,?,?,?,FALSE)";
      pstmt = conn.prepareStatement(insert2);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      setInt(pstmt, 3, 1);
      setInt(pstmt, 4, 0);
      pstmt.executeUpdate();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return gameID;
  }

  /**
   * Adds a new game with no description to the database (and a full list of players)
   */
  public static int gameCreate(int[] userIDs) throws DbException {
    int gameID;
    try {
      Date sysTime = new Date();
      String tempName = "New Game: " + sysTime.toString();
      Connection conn = connect();
      conn.setAutoCommit(false);

      String insert = "INSERT INTO GAMES(active, round, turn, lens, big_picture) "
          + "VALUES(TRUE,?,?,?,?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      setInt(pstmt, 1, 0);
      setInt(pstmt, 2, 1);
      setInt(pstmt, 3, userIDs[0]);
      pstmt.setString(4, tempName);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      gameID = rs.getInt("id");
      rs.close();
      pstmt.close();

      int position = 1;
      for (int userID : userIDs) {
        String insert2 = "INSERT INTO PLAYERS(game, userid, position, palette_num, action_done) "
            + "VALUES(?,?,?,?,FALSE)";
        pstmt = conn.prepareStatement(insert2);
        setInt(pstmt, 1, gameID);
        setInt(pstmt, 2, userID);
        setInt(pstmt, 3, position);
        setInt(pstmt, 4, 0);
        pstmt.executeUpdate();
        position++;
      }
      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return gameID;
  }

  /**
   * Sets the big picture for the given game to be bp
   */
  public static int gameBigPictureAdd(int gameID, String bp)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String update = "UPDATE GAMES SET big_picture = ? WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(update);
      pstmt.setString(1, bp);
      setInt(pstmt, 2, gameID);
      numUpdated = pstmt.executeUpdate();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Moves the game to the next round
   *
   * @param lensID - The lens for the next round
   */
  public static int gameNextRound(int gameID, int lensID) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String nextRound = "UPDATE GAMES SET turn = ?, round = round+1, lens = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(nextRound);
      setInt(pstmt, 1, 0);
      setInt(pstmt, 2, lensID);
      setInt(pstmt, 3, gameID);
      numUpdated = pstmt.executeUpdate();

      // Reset the action boolean every turn
      String updatePlayer = "UPDATE PLAYERS SET action_done = ? "
          + "WHERE game = ?";
      pstmt = conn.prepareStatement(updatePlayer);
      pstmt.setBoolean(1, false);
      setInt(pstmt, 2, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Moves the game to the next turn
   */
  public static int gameNextTurn(int gameID) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String nextTurn = "UPDATE GAMES SET turn = turn + 1 WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(nextTurn);
      setInt(pstmt, 1, gameID);
      numUpdated = pstmt.executeUpdate();

      // Reset the action boolean every turn
      String updatePlayer = "UPDATE PLAYERS SET action_done = ? "
          + "WHERE game = ?";
      pstmt = conn.prepareStatement(updatePlayer);
      pstmt.setBoolean(1, false);
      setInt(pstmt, 2, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Manually set the game's Round and Turn
   */
  public static int gameSetRoundTurn(int gameID, int round, int turn)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String nextTurn = "UPDATE GAMES SET round = ?, turn = ? WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(nextTurn);
      setInt(pstmt, 1, round);
      setInt(pstmt, 2, turn);
      setInt(pstmt, 3, gameID);
      numUpdated = pstmt.executeUpdate();

      // Reset the action boolean every turn
      String updatePlayer = "UPDATE PLAYERS SET action_done = ? "
          + "WHERE game = ?";
      pstmt = conn.prepareStatement(updatePlayer);
      pstmt.setBoolean(1, false);
      setInt(pstmt, 2, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Adds a new focus for the current lens to the database
   */
  public static int focusSet(int gameID, String focus) throws DbException {
    int currRound;
    int currLens;
    int focusID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String getRound = "SELECT round, lens FROM GAMES WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(getRound);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      currRound = rs.getInt(1);
      currLens = rs.getInt(2);
      rs.close();
      pstmt.close();

      String insert = "INSERT INTO FOCUSES(focus, round, game, player) "
          + "VALUES(?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, focus);
      setInt(pstmt, 2, currRound);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, currLens);
      rs = pstmt.executeQuery();
      rs.next();
      focusID = rs.getInt(1);

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return focusID;
  }

  /**
   * Adds a new focus for a given lens to the database
   */
  public static int focusSet(int lensID, int gameID, String focus)
      throws DbException {
    int currRound;
    int focusID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String getRound = "SELECT round, FROM GAMES WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(getRound);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      currRound = rs.getInt(1);
      rs.close();
      pstmt.close();

      String insert = "INSERT INTO FOCUSES(focus, round, game, player) "
          + "VALUES(?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, focus);
      setInt(pstmt, 2, currRound);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, lensID);
      rs = pstmt.executeQuery();
      rs.next();
      focusID = rs.getInt(1);

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return focusID;
  }

  /**
   * Adds a new focus for a given lens and round to the database
   */
  public static int focusSet(int lensID, int gameID, int roundNum,
      String focus) throws DbException {
    int focusID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String insert = "INSERT INTO FOCUSES(focus, round, game, player) "
          + "VALUES(?, ?, ?, ?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, focus);
      setInt(pstmt, 2, roundNum);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, lensID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      focusID = rs.getInt(1);

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return focusID;
  }

  /**
   * Batch adds a set of legacies to the database (once per round)
   *
   * @param legacies The legacies to be added
   * @return The number of legacies added
   */
  public static int legaciesAdd(List<Legacy> legacies) throws DbException {
    int count = 0;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);
      PreparedStatement pstmt;

      String insert = "INSERT INTO LEGACIES (legacy, round, game, player) "
          + "VALUES(?, ?, ?, ?)";
      pstmt = conn.prepareStatement(insert);
      for (Legacy leg : legacies) {
        pstmt.setString(1, leg.getLegacy());
        setInt(pstmt, 2, leg.getRound());
        setInt(pstmt, 3, leg.getGameId());
        setInt(pstmt, 4, leg.getPlayerId().orElseThrow(() ->
            new IllegalArgumentException("TOOD(dlorant): handle this case.")));
        count += pstmt.executeUpdate();
      }
      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return count;
  }

  /**
   * Swaps the current legacy for the given game/round/player with the one current held by null for
   * that game/round.
   *
   * The logic assumes that null has exactly one, and the player has exactly one
   *
   * @return the ID of the newly discarded legacy
   */
  public static int legacySwapNull(int userID, int gameID, int round)
      throws DbException {
    int newNullLegacy;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String setNull = "UPDATE LEGACIES SET player = NULL "
          + "WHERE player = ? AND game = ? AND round = ?"
          + "RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(setNull);
      setInt(pstmt, 1, userID);
      setInt(pstmt, 2, gameID);
      setInt(pstmt, 3, round);

      ResultSet rs = pstmt.executeQuery();
      rs.next();
      newNullLegacy = rs.getInt(1);

      String takeNull = "UPDATE LEGACIES SET player = ? "
          + "WHERE player IS NULL AND game = ? AND round = ? AND NOT id = ?";
      pstmt = conn.prepareStatement(takeNull);
      setInt(pstmt, 1, userID);
      setInt(pstmt, 2, gameID);
      setInt(pstmt, 3, round);
      setInt(pstmt, 4, newNullLegacy);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return newNullLegacy;
  }

  /**
   * Removes all of the null player legacies for the given game/round
   *
   * You do this when you are done auctioning for the round and nobody wants the discarded legacy
   *
   * @return the ID of the newly discarded legacy
   */
  public static int legacyRemoveNull(int gameID, int round) throws DbException {
    int numRemoved;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String removeNull = "DELETE FROM legacies WHERE game = ? AND round = ? AND player IS NULL";
      PreparedStatement pstmt = conn.prepareStatement(removeNull);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, round);

      numRemoved = pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numRemoved;
  }

  /**
   * Adds a new legacy for a given player to the database
   */
  public static int legacySet(Integer userID, int gameID, String legacy)
      throws DbException {
    int currRound;
    int legacyID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String getRound = "SELECT round FROM GAMES WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(getRound);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      currRound = rs.getInt(1);
      rs.close();
      pstmt.close();

      String insert = "INSERT INTO LEGACIES (legacy, round, game, player) "
          + "VALUES(?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, legacy);
      setInt(pstmt, 2, currRound);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, userID);
      rs = pstmt.executeQuery();
      rs.next();
      legacyID = rs.getInt(1);

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return legacyID;
  }

  /**
   * Adds a new legacy for a given player and round to the database
   */
  public static int legacySet(Integer userID, int gameID, int round,
      String legacy) throws DbException {
    int legacyID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String insert = "INSERT INTO LEGACIES (legacy, round, game, player) "
          + "VALUES(?, ?, ?, ?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, legacy);
      setInt(pstmt, 2, round);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      legacyID = rs.getInt(1);

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return legacyID;
  }

  /**
   * These functions relate to creating and manipulating the palette
   */

  public static int paletteAdd(int userID, int gameID, boolean in_game,
      String palette) throws DbException {
    int paletteID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String newpalette = "INSERT INTO PALETTES (player, description, in_game, game)"
          + " VALUES (?, ?, ?, ?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(newpalette);
      setInt(pstmt, 1, userID);
      pstmt.setString(2, palette);
      pstmt.setBoolean(3, in_game);
      setInt(pstmt, 4, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      paletteID = rs.getInt(1);
      pstmt.close();

      String updatePlayer = "UPDATE PLAYERS SET palette_num = palette_num + 1 "
          + "WHERE game = ? AND userid = ?";

      // If any players declared themselves done, then this is this
      // player's last add
      if (actionAnyDoneCheck(gameID)) {
        updatePlayer = "UPDATE PLAYERS SET palette_num = palette_num + 1, action_done = TRUE "
            + "WHERE game = ? AND userid = ?";
      }
      pstmt = conn.prepareStatement(updatePlayer);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return paletteID;
  }

  /**
   * Sets the user userID (and the infered users) as a done action for
   */
  public static int paletteSetDone(int userID, int gameID) throws DbException {
    int numUpdated = -1;
    try {
      Connection conn = connect();
      PreparedStatement pstmt;

      String setDone = "UPDATE PLAYERS SET action_done = TRUE WHERE "
          + "game = ? AND (userid = ? OR palette_num > "
          + "(SELECT palette_num FROM PLAYERS WHERE game = ? AND userid = ?))";

      pstmt = conn.prepareStatement(setDone);

      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      setInt(pstmt, 3, gameID);
      setInt(pstmt, 4, userID);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int characterAdd(String name, String description)
      throws DbException {
    int characterID = characterAdd(name);
    characterSetDescription(characterID, description);
    return characterID;

  }

  public static int characterAdd(String name) throws DbException {
    int characterID;
    try {
      Connection conn = connect();

      String insert = "INSERT INTO CHARACTERS(name, description) "
          + "VALUES(?, ?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, name);
      pstmt.setString(2, "");
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      characterID = rs.getInt("id");
      rs.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return characterID;
  }

  public static int characterSetDescription(int character, String description)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE CHARACTERS SET description = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, description);
      setInt(pstmt, 2, character);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

	/*
   * ******************************************************* These functions
	 * involve mapping characters to scenes, be them played, banned, or
	 * background *******************************************************
	 */

  /**
   * This is the normal version of characterInScene used for player controlled characters
   */
  public static int characterInScene(int characterID, int sceneID, int userID)
      throws DbException {
    int insceneID;
    try {
      Connection conn = connect();

      String insert = "INSERT INTO INSCENE(role, scene, player, banned) "
          + "VALUES(?,?, ?, FALSE) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      setInt(pstmt, 1, characterID);
      setInt(pstmt, 2, sceneID);
      setInt(pstmt, 3, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      insceneID = rs.getInt("id");
      rs.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return insceneID;
  }

  /**
   * This is the alternate version of characterInScene used for both extra characters and banned
   * characters
   */
  public static int characterInScene(int characterID, int sceneID,
      boolean banned) throws DbException {
    int insceneID;
    try {
      Connection conn = connect();

      String insert = "INSERT INTO INSCENE(role, scene, banned) "
          + "VALUES(?, ?, ?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      setInt(pstmt, 1, characterID);
      setInt(pstmt, 2, sceneID);
      pstmt.setBoolean(3, banned);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      insceneID = rs.getInt("id");
      rs.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return insceneID;
  }

  // *******************************************************

  /**
   * Add a new period into the database in a game
   */
  public static int periodAdd(int userID, int gameID, int position, int turn,
      int round, String periodText, String description, Boolean tone)
      throws DbException {

    int periodID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String makeWay = "UPDATE PERIODS SET position = position + 1 "
          + "WHERE game = ? AND position  >= ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, position);
      pstmt.executeUpdate();
      pstmt.close();

      String newPeriod =
          "INSERT INTO PERIODS (period, tone, position, game, player, turn, round, description)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(newPeriod);
      pstmt.setString(1, periodText);
      pstmt.setBoolean(2, tone);
      setInt(pstmt, 3, position);
      setInt(pstmt, 4, gameID);
      setInt(pstmt, 5, userID);
      setInt(pstmt, 6, turn);
      setInt(pstmt, 7, round);
      pstmt.setString(8, description);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      periodID = rs.getInt(1);
      rs.close();

      String setLastPlayed = "UPDATE GAMES SET last_pes = ?, last_card = ? WHERE id = ?";
      pstmt = conn.prepareStatement(setLastPlayed);
      setInt(pstmt, 1, 0);
      setInt(pstmt, 2, periodID);
      setInt(pstmt, 3, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return periodID;
  }

  public static int periodSetDescription(int period, String description)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE PERIODS SET description = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, description);
      setInt(pstmt, 2, period);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int periodSetTone(int period, boolean tone) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE PERIODS SET tone = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setBoolean(1, tone);
      setInt(pstmt, 2, period);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Adds a player to the game
   */
  public static int playerAdd(int userID, int gameID) throws DbException {
    int numPlayers;
    int numAdded;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String count = "SELECT COUNT(*) FROM PLAYERS WHERE game = ?";
      PreparedStatement pstmt = conn.prepareStatement(count);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      numPlayers = rs.getInt(1);
      rs.close();
      pstmt.close();

      String insert2 = "INSERT INTO PLAYERS(game, userid, position, palette_num, action_done) "
          + "VALUES(?,?,?,?, FALSE)";
      pstmt = conn.prepareStatement(insert2);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      setInt(pstmt, 3, numPlayers + 1);
      setInt(pstmt, 4, 0);
      numAdded = pstmt.executeUpdate();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numAdded;
  }

  /**
   * @return the number of players that were shifted
   */
  public static int playerRemove(int userID, int gameID) throws DbException {
    int numPlayersRemoved;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String condensePlayers = "UPDATE PLAYERS SET position = position - 1 "
          + "WHERE position  > "
          + "(SELECT position FROM PLAYERS WHERE game = ? AND userid = ?)";
      PreparedStatement pstmt = conn.prepareStatement(condensePlayers);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      numPlayersRemoved = pstmt.executeUpdate();
      pstmt.close();

      String insert2 = "DELETE FROM PLAYERS WHERE game = ? AND userid = ?";
      pstmt = conn.prepareStatement(insert2);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numPlayersRemoved;
  }

  public static int playerSetActionDone(int userID, int gameID,
      boolean isDone) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String updatePlayer = "UPDATE PLAYERS SET action_done = ? "
          + "WHERE game = ? AND userid = ?";
      PreparedStatement pstmt = conn.prepareStatement(updatePlayer);
      setInt(pstmt, 2, gameID);
      setInt(pstmt, 3, userID);
      pstmt.setBoolean(1, isDone);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  /**
   * Add a new event to the database
   */
  public static int eventAdd(int userID, int periodID, int position, int turn,
      int round, String eventText, String description, Boolean tone)
      throws DbException {

    int eventID;
    int gameID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String makeWay = "UPDATE EVENTS SET position = position + 1 "
          + "WHERE period = ? AND position  >= ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, periodID);
      setInt(pstmt, 2, position);
      pstmt.executeUpdate();
      pstmt.close();

      String newPeriod =
          "INSERT INTO EVENTS (event, tone, position, period, turn, round, description, player)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(newPeriod);
      pstmt.setString(1, eventText);
      pstmt.setBoolean(2, tone);
      setInt(pstmt, 3, position);
      setInt(pstmt, 4, periodID);
      setInt(pstmt, 5, turn);
      setInt(pstmt, 6, round);
      pstmt.setString(7, description);
      setInt(pstmt, 8, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      eventID = rs.getInt(1);
      rs.close();

      String getGame = "SELECT game FROM PERIODS WHERE id = ?";
      pstmt = conn.prepareStatement(getGame);
      setInt(pstmt, 1, periodID);
      rs = pstmt.executeQuery();
      rs.next();
      gameID = rs.getInt(1);
      rs.close();

      String setLastPlayed = "UPDATE GAMES SET last_pes = ?, last_card = ? WHERE id = ?";
      pstmt = conn.prepareStatement(setLastPlayed);
      setInt(pstmt, 1, 1);
      setInt(pstmt, 2, eventID);
      setInt(pstmt, 3, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return eventID;
  }

  public static int eventSetDescription(int event, String description)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE EVENTS SET description = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, description);
      setInt(pstmt, 2, event);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int eventSetTone(int event, boolean tone) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE EVENTS SET tone = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setBoolean(1, tone);
      setInt(pstmt, 2, event);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneAdd(int userID, int eventID, int position, int turn,
      int round, String question, String setting, String answer,
      String description, boolean dictated) throws DbException {

    int sceneID;
    int gameID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String makeWay = "UPDATE SCENES SET position = position + 1 "
          + "WHERE event = ? AND position  >= ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, eventID);
      setInt(pstmt, 2, position);
      pstmt.executeUpdate();
      pstmt.close();

      String newScene = "INSERT INTO SCENES "
          + "(tone, dictated, position, event, player, question, description, setting, answer, steps_left, turn, round)"
          + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
      pstmt = conn.prepareStatement(newScene);
      pstmt.setBoolean(1, false);
      pstmt.setBoolean(2, dictated);
      setInt(pstmt, 3, position);
      setInt(pstmt, 4, eventID);
      setInt(pstmt, 5, userID);
      pstmt.setString(6, question);
      pstmt.setString(7, description);
      pstmt.setString(8, setting);
      pstmt.setString(9, answer);
      if (dictated) {
        setInt(pstmt, 10, 1); // answer and done
      } else {
        setInt(pstmt, 10, 3); // banned/required, characterChoice,
      }
      // answer, done
      setInt(pstmt, 11, turn);
      setInt(pstmt, 12, round);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      sceneID = rs.getInt(1);
      rs.close();

      String getGame = "SELECT game FROM "
          + "PERIODS JOIN EVENTS ON (EVENTS.period = PERIODS.id)  "
          + "WHERE EVENTS.id = ?";
      pstmt = conn.prepareStatement(getGame);
      setInt(pstmt, 1, eventID);
      rs = pstmt.executeQuery();
      rs.next();
      gameID = rs.getInt(1);
      rs.close();

      String setLastPlayed = "UPDATE GAMES SET last_pes = ?, last_card = ? WHERE id = ?";
      pstmt = conn.prepareStatement(setLastPlayed);
      setInt(pstmt, 1, 2);
      setInt(pstmt, 2, sceneID);
      setInt(pstmt, 3, gameID);
      pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return sceneID;
  }

  /**
   * Moves the game to the next turn
   */
  public static int sceneStepDone(int sceneID) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String nextTurn = "UPDATE SCENES SET steps_left = steps_left - 1 WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(nextTurn);
      setInt(pstmt, 1, sceneID);
      numUpdated = pstmt.executeUpdate();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();

    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetDescription(int scene, String description)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET description = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, description);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetQuestion(int scene, String question)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET question = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, question);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetSetting(int scene, String setting)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET setting = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, setting);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetAnswer(int scene, String answer)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET answer = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setString(1, answer);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetBanned(int scene, int banned) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET banned1 = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, banned);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetBanned(int scene, int banned1, int banned2)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET banned1 = ?, banned2 = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, banned1);
      setInt(pstmt, 2, banned2);
      setInt(pstmt, 3, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetRequired(int scene, int required)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET required1 = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, required);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetRequired(int scene, int required1, int required2)
      throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET required1 = ?, required2 = ? "
          + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      setInt(pstmt, 1, required1);
      setInt(pstmt, 2, required2);
      setInt(pstmt, 3, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static int sceneSetTone(int scene, boolean tone) throws DbException {
    int numUpdated;
    try {
      Connection conn = connect();

      String makeWay = "UPDATE SCENES SET tone = ? " + "WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(makeWay);
      pstmt.setBoolean(1, tone);
      setInt(pstmt, 2, scene);
      numUpdated = pstmt.executeUpdate();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return numUpdated;
  }

  public static boolean paletteAutoDone(int userID, int gameID)
      throws DbException {
    if (!actionAnyDoneCheck(gameID)) {
      return false;
    }
    boolean autoset = false;
    try {
      Connection conn = connect();
      PreparedStatement pstmt;

      String autoCheck = "SELECT COUNT(*) FROM PLAYERS WHERE "
          + "game = ? AND userid = ? AND action_done = FALSE AND "
          + "palette_num > (SELECT MIN(palette_num) FROM PLAYERS "
          + "WHERE game = ? AND action_done = TRUE)";
      pstmt = conn.prepareStatement(autoCheck);

      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      setInt(pstmt, 3, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      // If I will never be able to play another card, set me to done
      autoset = (rs.getInt(1) != 0);
      rs.close();
      pstmt.close();

      if (autoset) {
        playerSetActionDone(userID, gameID, true);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return autoset;
  }

  // Does the equivalient of stmt.setInt(col, i) but preserves nullness.
  protected static void setInt(PreparedStatement stmt, int col, @Nullable Integer i)
      throws SQLException {
    if (i == null) {
      stmt.setNull(col, java.sql.Types.INTEGER);
    } else {
      stmt.setInt(col, i);
    }
  }
}