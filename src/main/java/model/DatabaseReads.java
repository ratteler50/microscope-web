package model;

import com.sun.rowset.CachedRowSetImpl;
import db.DbConnect;
import db.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;

public class DatabaseReads {

  // Connect to the database
  protected static Connection connect() throws DbException {
    return DbConnect.connect();
  }

  /**
   * Given a unique username, return the userID for that user
   *
   * @return userid for username, or -1 on error
   */
  public static int getUserID(String userName) throws DbException {
    int userID = -1; // will return -1 on error
    try {
      Connection conn = connect();

      // Create the statement
      String getID = "SELECT id FROM USERS WHERE user_name = ?";
      PreparedStatement pStmt = conn.prepareStatement(getID);
      pStmt.setString(1, userName);

      ResultSet id = pStmt.executeQuery();
      if (id.next()) {
        userID = id.getInt(1);
      }
      id.close();
      pStmt.close();
      conn.close();

      return userID;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get the number of players in a given game */
  public static int getNumPlayers(int gameID) {
    int numPlayers = -1;
    try {
      Connection conn = connect();

      // Create the statement
      String getplayers = "SELECT COUNT(*) FROM PLAYERS WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getplayers);
      setInt(pStmt, 1, gameID);

      ResultSet countRS = pStmt.executeQuery();
      if (countRS.next()) {
        numPlayers = countRS.getInt(1);
      }
      countRS.close();
      pStmt.close();
      conn.close();

      return numPlayers;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return numPlayers;
  }

  /**
   * Returns the numbers of periods that are in the DB for the current game. This is used when
   * playing the bookends both to make sure that the plays is valid, and to find out when you have
   * played the last bookend.
   */
  public static int getNumPeriods(int gameID) {
    int numPeriods = -1;
    try {
      Connection conn = connect();

      // Create the statement
      String getplayers = "SELECT COUNT(*) FROM PERIODS WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getplayers);
      setInt(pStmt, 1, gameID);

      ResultSet countRS = pStmt.executeQuery();
      if (countRS.next()) {
        numPeriods = countRS.getInt(1);
      }
      countRS.close();
      pStmt.close();
      conn.close();

      return numPeriods;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return numPeriods;
  }

  /**
   * ******************************************************** The following functions are mostly
   * used for a given player to see info about the games they are currently in
   *
   * <p>********************************************************
   */
  /** Returns a list of gameIDs for games a given user is in */
  public static CachedRowSet getGameList(int userID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getGames = "SELECT game FROM PLAYERS WHERE userid = ?";
      PreparedStatement pStmt = conn.prepareStatement(getGames);
      setInt(pStmt, 1, userID);

      ResultSet games = pStmt.executeQuery();

      CachedRowSet crsGameList = new CachedRowSetImpl();
      crsGameList.populate(games);

      games.close();
      pStmt.close();
      conn.close();

      return crsGameList;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /**
   * ******************************************************** The following functions are mostly
   * used to populate a new connection with current game information
   *
   * <p>********************************************************
   */

  /** Get all Periods for a given game */
  public static CachedRowSet getPeriods(int gameID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getPeriods = "SELECT * FROM PERIODS WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getPeriods);
      setInt(pStmt, 1, gameID);

      ResultSet periods = pStmt.executeQuery();

      CachedRowSet crsPeriods = new CachedRowSetImpl();
      crsPeriods.populate(periods);

      periods.close();
      pStmt.close();
      conn.close();

      return crsPeriods;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /**
   * ******************************************************** The following functions are mostly
   * used to populate a new connection with current game information
   *
   * <p>********************************************************
   */

  /** Get all events for a given game */
  public static CachedRowSet getEvents(int gameID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getevents =
          "SELECT EVENTS.* FROM EVENTS, PERIODS " + "WHERE game = ? AND EVENTS.period = PERIODS.id";
      PreparedStatement pStmt = conn.prepareStatement(getevents);
      setInt(pStmt, 1, gameID);

      ResultSet events = pStmt.executeQuery();

      CachedRowSet crsevents = new CachedRowSetImpl();
      crsevents.populate(events);

      events.close();
      pStmt.close();
      conn.close();

      return crsevents;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all scenes for a given game */
  public static CachedRowSet getScenes(int gameID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getscenes =
          "SELECT SCENES.* FROM SCENES, EVENTS, PERIODS"
              + " WHERE game = ? AND SCENES.event = EVENTS.id AND "
              + "EVENTS.period = PERIODS.id";
      PreparedStatement pStmt = conn.prepareStatement(getscenes);
      setInt(pStmt, 1, gameID);

      ResultSet scenes = pStmt.executeQuery();

      CachedRowSet crsscenes = new CachedRowSetImpl();
      crsscenes.populate(scenes);

      scenes.close();
      pStmt.close();
      conn.close();

      return crsscenes;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all characters for a given game */
  public static CachedRowSet getCharacters(int gameID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getcharacters =
          "SELECT * FROM "
              + "CHARACTERS,INSCENE, SCENES, EVENTS, PERIODS "
              + "WHERE INSCENE.role = CHARACTERS.id AND "
              + "INSCENE.scene = SCENES.id AND "
              + "SCENES.event = EVENTS.id AND "
              + "EVENTS.period = PERIODS.id AND "
              + "PERIODS.game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getcharacters);
      setInt(pStmt, 1, gameID);

      ResultSet characters = pStmt.executeQuery();

      CachedRowSet crscharacters = new CachedRowSetImpl();
      crscharacters.populate(characters);

      characters.close();
      pStmt.close();
      conn.close();

      return crscharacters;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all palettes for a given game */
  public static CachedRowSet getPalettes(int gameID) throws DbException {

    try {
      Connection conn = connect();

      // Create the statement
      String getpalettes = "SELECT * FROM PALETTES WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getpalettes);
      setInt(pStmt, 1, gameID);

      ResultSet palettes = pStmt.executeQuery();

      CachedRowSet crspalettes = new CachedRowSetImpl();
      crspalettes.populate(palettes);

      palettes.close();
      pStmt.close();
      conn.close();

      return crspalettes;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all players for a given game */
  public static CachedRowSet getPlayers(int gameID) throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getplayers =
          "SELECT PLAYERS.*, USERS.user_name "
              + "FROM PLAYERS, USERS "
              + "WHERE USERS.id = PLAYERS.userid AND game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getplayers);
      setInt(pStmt, 1, gameID);

      ResultSet players = pStmt.executeQuery();

      CachedRowSet crsplayers = new CachedRowSetImpl();
      crsplayers.populate(players);

      players.close();
      pStmt.close();
      conn.close();

      return crsplayers;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all focuses for a given game */
  public static CachedRowSet getFocuses(int gameID) throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getFocuses = "SELECT * FROM FOCUSES WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getFocuses);
      setInt(pStmt, 1, gameID);

      ResultSet focuses = pStmt.executeQuery();

      CachedRowSet crsFocuses = new CachedRowSetImpl();
      crsFocuses.populate(focuses);

      focuses.close();
      pStmt.close();
      conn.close();

      return crsFocuses;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all legacies for a given game */
  public static CachedRowSet getLegacies(int gameID) throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getLegacies = "SELECT * FROM LEGACIES WHERE game = ?";
      PreparedStatement pStmt = conn.prepareStatement(getLegacies);
      setInt(pStmt, 1, gameID);

      ResultSet legacy = pStmt.executeQuery();

      CachedRowSet crsLegacies = new CachedRowSetImpl();
      crsLegacies.populate(legacy);

      legacy.close();
      pStmt.close();
      conn.close();

      return crsLegacies;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get all legacies for a given game and round */
  public static CachedRowSet getLegacies(int gameID, int round) throws DbException {
    CachedRowSet crsLegacies;
    try (Connection conn = connect()) {
      // Create the statement
      String getLegacies = "SELECT * FROM LEGACIES WHERE game = ? AND round = ?";
      try (PreparedStatement pStmt = conn.prepareStatement(getLegacies)) {
        setInt(pStmt, 1, gameID);
        setInt(pStmt, 2, round);

        try (ResultSet legacy = pStmt.executeQuery()) {
          crsLegacies = new CachedRowSetImpl();
          crsLegacies.populate(legacy);
          return crsLegacies;
        }
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /** Get all character/scene/players for played out scenes */
  public static CachedRowSet getInScene(int gameID) throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getInScene =
          "SELECT role, scene, players.position "
              + "FROM CHARACTERS,INSCENE, SCENES, EVENTS, PERIODS,PLAYERS"
              + " WHERE INSCENE.player IS NOT NULL AND "
              + "INSCENE.role = CHARACTERS.id AND INSCENE.scene = SCENES.id "
              + "AND SCENES.event = EVENTS.id AND"
              + " EVENTS.period = PERIODS.id AND"
              + " INSCENE.player = PLAYERS.userid AND PERIODS.game = ?;";
      PreparedStatement pStmt = conn.prepareStatement(getInScene);
      setInt(pStmt, 1, gameID);

      ResultSet inScene = pStmt.executeQuery();

      CachedRowSet crsInScene = new CachedRowSetImpl();
      crsInScene.populate(inScene);

      inScene.close();
      pStmt.close();
      conn.close();

      return crsInScene;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get the Game State for the database for a given game ID */
  public static CachedRowSet getGameState(int gameID) throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getGame = "SELECT * FROM GAMES WHERE id = ?";
      PreparedStatement pStmt = conn.prepareStatement(getGame);
      setInt(pStmt, 1, gameID);

      ResultSet game = pStmt.executeQuery();

      CachedRowSet crsGame = new CachedRowSetImpl();
      crsGame.populate(game);

      game.close();
      pStmt.close();
      conn.close();

      return crsGame;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Get the Game State for the database for a given game ID */
  public static CachedRowSet getAllUsers() throws DbException {
    try {
      Connection conn = connect();

      // Create the statement
      String getGame = "SELECT id, user_name FROM USERS";
      PreparedStatement pStmt = conn.prepareStatement(getGame);

      ResultSet users = pStmt.executeQuery();

      CachedRowSet crsUsers = new CachedRowSetImpl();
      crsUsers.populate(users);

      users.close();
      pStmt.close();
      conn.close();

      return crsUsers;
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
  }

  /** Checks if all players have completed their action */
  public static boolean actionAllDoneCheck(int gameID) throws DbException {
    boolean allDone;
    try {
      Connection conn = connect();

      String doneCount = "SELECT COUNT(*) FROM PLAYERS WHERE game = ? " + "AND action_done = FALSE";
      PreparedStatement pstmt = conn.prepareStatement(doneCount);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      allDone = (rs.getInt(1) == 0);
      rs.close();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return allDone;
  }

  /**
   * Checks to see if the given player have completed their action
   *
   * @param gameID The game to check
   * @return True if any players have finished their action, else false
   */
  public static boolean actionDoneCheck(int userID, int gameID) {
    boolean actionDone = false;
    try {
      Connection conn = connect();

      String actionDoneCheck = "SELECT action_done FROM PLAYERS " + "WHERE game = ? AND userid = ?";
      PreparedStatement pstmt = conn.prepareStatement(actionDoneCheck);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      actionDone = rs.getBoolean(1);
      rs.close();
      pstmt.close();

      conn.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return actionDone;
  }

  /**
   * Checks to see if any players have declared themselves done with their action
   *
   * @param gameID The game to check
   * @return True if any players have finished their action, else false
   */
  public static boolean actionAnyDoneCheck(int gameID) throws DbException {
    boolean anyDone;
    try {
      Connection conn = connect();

      String doneCount = "SELECT COUNT(*) FROM PLAYERS WHERE game = ? " + "AND action_done = TRUE";
      PreparedStatement pstmt = conn.prepareStatement(doneCount);
      setInt(pstmt, 1, gameID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      anyDone = (rs.getInt(1) != 0);
      rs.close();
      pstmt.close();

      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return anyDone;
  }

  public static boolean paletteLegalCheck(int userID, int gameID) {
    boolean notDone = false;
    boolean atMin = false;
    try {
      Connection conn = connect();

      // You can only add if you did not say your are done
      String paletteDone = "SELECT action_done FROM PLAYERS WHERE game = ? AND userid = ?";
      PreparedStatement pstmt = conn.prepareStatement(paletteDone);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      notDone = !rs.getBoolean(1);
      rs.close();
      pstmt.close();

      // You cannot add if you have added more than anyone else currently
      String minCheck =
          "SELECT COUNT(*) FROM PLAYERS WHERE game = ? "
              + "AND userid = ? AND palette_num = "
              + "(SELECT MIN(palette_num) FROM PLAYERS WHERE game = ?)";
      pstmt = conn.prepareStatement(minCheck);
      setInt(pstmt, 1, gameID);
      setInt(pstmt, 2, userID);
      setInt(pstmt, 3, gameID);
      rs = pstmt.executeQuery();
      rs.next();
      atMin = (rs.getInt(1) == 1);
      rs.close();
      pstmt.close();

      conn.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return notDone && atMin;
  }

  /** Get all scenes for a given game */
  public static int getSceneStepsLeft(int sceneID) {
    int retval = -1;
    try {
      Connection conn = connect();

      // Create the statement
      String getscenes = "SELECT steps_left FROM SCENES" + " WHERE id = ?";
      PreparedStatement pStmt = conn.prepareStatement(getscenes);
      setInt(pStmt, 1, sceneID);

      ResultSet sceneStep = pStmt.executeQuery();

      if (sceneStep.next()) {
        retval = sceneStep.getInt(1);
      }

      sceneStep.close();
      pStmt.close();
      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return retval;
  }

  /** Does the equivalent of stmt.setInt(col, i) but preserves nullness. */
  protected static void setInt(PreparedStatement stmt, int col, Integer i) throws SQLException {
    if (i == null) {
      stmt.setNull(col, java.sql.Types.INTEGER);
    } else {
      stmt.setInt(col, i);
    }
  }
}
