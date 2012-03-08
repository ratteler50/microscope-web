package testing;

import static model.DatabaseReads.actionAllDoneCheck;
import static support.Settings.BEN_LOC;
import static support.Settings.IS_BEN;
import static support.Settings.LORANT_DESK;
import static support.Settings.LORANT_OSX;
import static support.Settings.ON_DESKTOP;

import db.DbException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameImport extends model.DatabaseUpdates {

  private static final String CREATE_TABLES = "CreateTables.sql";
  private static final String POPULATE_DB = "populate_database.sql";

  static String bigPicture = "The Moons of Khalidor Struggle for Supremacy";

  /**
   * @param args
   * @throws InterruptedException
   * @throws SQLException
   * @throws DbException
   */
  public static void main(String[] args) throws InterruptedException,
      SQLException, DbException {
    Scanner in = new Scanner(System.in);

//		System.err.println("main");
    String scriptFolder = getScriptFolder();
//		System.err.println(scriptFolder);

    System.out.println("Would you like to reset the DB first? Y/N");
    if (in.nextLine().toLowerCase().equals("y")) {
      resetDB();
      runScript(scriptFolder + CREATE_TABLES);
      runScript(scriptFolder + POPULATE_DB);
    }

    int[] gameAndPlayerIDs = createGame(); // gameID, and player n is
    // gameAndPlayer[n]

    gameUpdate(gameAndPlayerIDs, in);

    in.close();

  }

  private static void gameUpdate(int[] gameAndPlayerIDs, Scanner in)
      throws DbException {
    int gameID = gameAndPlayerIDs[0];
    int userID = gameAndPlayerIDs[1];
    int periodPosition = 1;

    // Set the current round and turn
    System.out.println("What round is it?");
    int round = in.nextInt();
    in.nextLine();
    System.out.println("What turn is it?");
    int turn = in.nextInt();
    in.nextLine();
    gameSetRoundTurn(gameID, round, turn);

    // Set the current legacies
    for (int i = 1; i < gameAndPlayerIDs.length; i++) {
      System.out.println("What is player " + i + "'s legacy?");
      String legacy = in.nextLine();
      System.out.println("Is '" + legacy + "' correct?");
      if (in.nextLine().toLowerCase().equals("y")) {
        legacySet(gameAndPlayerIDs[i], gameID, legacy);
      } else {
        i--;
      }
    }

    // Add the cards (periods call events which call scenes)
    System.out.println("Would you like to add a period?");
    while (in.nextLine().toLowerCase().equals("y")) {
      addPeriod(gameID, userID, periodPosition, round, turn, in);
      periodPosition++;
      System.out.println("Would you like to add another period?");
    }

  }

  private static void addPeriod(int gameID, int userID, int periodPosition,
      int round, int turn, Scanner in) throws DbException {
    int eventPosition = 1;
    String description = "";
    String periodText;
    boolean tone;
    System.out.println("What is the period text?");
    periodText = in.nextLine();
    System.out.println("Is '" + periodText + "' correct?");
    while (!in.nextLine().toLowerCase().equals("y")) {
      System.out.println("What is the period text?");
      periodText = in.nextLine();
      System.out.println("Is '" + periodText + "' correct?");
    }
    System.out.println("Is the tone of the period dark?");
    tone = in.nextLine().toLowerCase().equals("y");

    int periodID = periodAdd(userID, gameID, periodPosition, turn, round,
        periodText, description, tone);

    System.out.println("Would you like to add an event to this period?");
    while (in.nextLine().toLowerCase().equals("y")) {
      addEvent(periodID, userID, eventPosition, round, turn, in);
      eventPosition++;
      System.out
          .println("Would you like to add another event to this period?");
    }
  }

  private static void addEvent(int periodID, int userID, int eventPosition,
      int round, int turn, Scanner in) throws DbException {
    int scenePosition = 1;
    String description = "";
    String eventText;
    boolean tone;
    System.out.println("What is the event text?");
    eventText = in.nextLine();
    System.out.println("Is '" + eventText + "' correct?");
    while (!in.nextLine().toLowerCase().equals("y")) {
      System.out.println("What is the event text?");
      eventText = in.nextLine();
      System.out.println("Is '" + eventText + "' correct?");
    }
    System.out.println("Is the tone of the event dark?");
    tone = in.nextLine().toLowerCase().equals("y");

    int eventID = eventAdd(userID, periodID, eventPosition, turn, round,
        eventText, description, tone);

    System.out.println("Would you like to add a scene to this event?");
    while (in.nextLine().toLowerCase().equals("y")) {
      addScene(eventID, userID, scenePosition, round, turn, in);
      scenePosition++;
      System.out
          .println("Would you like to add another scene to this event?");
    }
  }

  private static void addScene(int eventID, int userID, int scenePosition,
      int round, int turn, Scanner in) throws DbException {
    boolean dictated = true;
    String description = "";
    boolean tone;
    String question = "";
    String setting = "";
    String answer = "";
    String fact;

    // Question
    System.out.println("What is the question?");
    question = in.nextLine();
    System.out.println("Is '" + question + "' correct?");
    while (!in.nextLine().toLowerCase().equals("y")) {
      System.out.println("What is the question?");
      question = in.nextLine();
      System.out.println("Is '" + question + "' correct?");
    }

    // Information Gathered
    System.out.println("Did we learn anything interesting?");
    while (in.nextLine().toLowerCase().equals("y")) {
      System.out.println("What did we learn?");
      fact = "- " + in.nextLine() + '\n';
      System.out.println("Is '" + fact + "' correct?");
      while (!in.nextLine().toLowerCase().equals("y")) {
        System.out.println("What did we learn?");
        fact = "- " + in.nextLine();
        System.out.println("Is '" + setting + "' correct?");
      }
      setting += fact;
      System.out.println("Did we learn anything else interesting?");
    }
    // Answer
    System.out.println("What is the answer?");
    answer = in.nextLine();
    System.out.println("Is '" + answer + "' correct?");
    while (!in.nextLine().toLowerCase().equals("y")) {
      System.out.println("What is the answer?");
      answer = in.nextLine();
      System.out.println("Is '" + answer + "' correct?");
    }

    int sceneID = sceneAdd(userID, eventID, scenePosition, turn, round,
        question, setting, answer, description, dictated);

    sceneStepDone(sceneID);
    System.out.println("Is the tone of the scene dark?");
    tone = in.nextLine().toLowerCase().equals("y");
    sceneSetTone(sceneID, tone);
  }

  private static String getScriptFolder() {
    if (IS_BEN) {
      return BEN_LOC;
    } else {
      if (ON_DESKTOP) {
        return LORANT_DESK;
      } else {
        return LORANT_OSX;
      }
    }

  }

  private static int[] createGame() throws DbException, InterruptedException {
    int[] retval = new int[5];

    // Create users for players
    int david = 1;
    int ben = 2;
    int sam = 4;
    int sean = 5;

    // Create game (and player 1)
    int game = gameCreate(david);
    retval[0] = game;
    retval[1] = david;

    // Create players
    playerAdd(sean, game);
    retval[2] = sean;
    playerAdd(sam, game);
    retval[3] = sam;
    playerAdd(ben, game);
    retval[4] = ben;

    gameNextTurn(game);

    // Big Picture
    gameBigPictureAdd(game, bigPicture);

    gameNextTurn(game);

    // Palette, including autocheck thread
    List<Integer> players = new ArrayList<Integer>();
    players.add(david);
    players.add(ben);
    players.add(sean);
    players.add(sam);

    Thread check = new Thread(new autoDone(players, game));
    check.start();
    paletteAdd(david, game, true, "MULTIPLE RACES");
    paletteAdd(ben, game, true, "MULTIPLE DIFFERENT MAGIC SYSTEMS");
    paletteAdd(sean, game, true, "TABOOED MAGIC");
    paletteAdd(sam, game, true, "DRAGONS");
    paletteAdd(david, game, false, "TELEPORTATION");
    paletteAdd(ben, game, false, "BORING RELIGIONS");
    paletteAdd(sean, game, false, "RESSURECTION");
    paletteAdd(sam, game, false, "SACRIFICE MAGIC");
    paletteAdd(david, game, false, "OMNIPOTENT GODS");
    playerSetActionDone(sean, game, true);
    playerSetActionDone(sam, game, true);
    paletteAdd(ben, game, false, "OMNIPRESENCE");

    check.join();

    gameNextTurn(game);

    System.err.println("Round Zero Played");
    return retval;

  }

  private static void resetDB() throws DbException {
    // Connect to the database
    try {
      Class.forName("org.postgresql.Driver");
      Connection conn = DriverManager
          .getConnection("jdbc:postgresql://localhost/postgres?"
              + "user=postgres&password=password");

      String dropDB = "DROP DATABASE microscope";
      String newDB = "CREATE DATABASE microscope "
          + "WITH OWNER = postgres " + "ENCODING = 'UTF8' "
          + "TABLESPACE = pg_default "
          + "LC_COLLATE = 'English, United States' "
          + "LC_CTYPE = 'English, United States' "
          + "CONNECTION LIMIT = -1";

      String newDBOSX = "CREATE DATABASE microscope "
          + "WITH OWNER = postgres " + "ENCODING = 'UTF8' "
          + "TABLESPACE = pg_default " + "LC_COLLATE = 'C' "
          + " LC_CTYPE = 'C' " + "CONNECTION LIMIT = -1";

      PreparedStatement pstmt = conn.prepareStatement(dropDB);
      pstmt.executeUpdate();
      if (ON_DESKTOP) {
        pstmt = conn.prepareStatement(newDB);
      } else {
        pstmt = conn.prepareStatement(newDBOSX);
      }
      pstmt.executeUpdate();
      conn.close();

      System.err.println("Database reset");

    } catch (SQLException ex) {
      throw new DbException(ex);
    } catch (ClassNotFoundException e) {
      throw new DbException(e);
    }
  }

  private static void runScript(String sqlScript) throws DbException {
    try {
      Class.forName("org.postgresql.Driver");
      Connection conn = DriverManager
          .getConnection("jdbc:postgresql://localhost/microscope?"
              + "user=postgres&password=password");

      ScriptRunner populate = new ScriptRunner(conn, false, true);
      FileReader inFile = new FileReader(sqlScript);
      populate.runScript(inFile);
      inFile.close();
      conn.close();
      System.err.println("Done running " + sqlScript);

    } catch (SQLException ex) {
      throw new DbException(ex);
    } catch (ClassNotFoundException e) {
      throw new DbException(e);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

class autoDone extends model.DatabaseUpdates implements Runnable {

  List<Integer> players;
  int game;

  public autoDone(List<Integer> players, int game) {
    this.players = players;
    this.game = game;
  }

  public void run() {
    try {
      while (!actionAllDoneCheck(game)) {
        System.err.println("Checking to autoDone");
        for (Integer player : players) {
          paletteAutoDone(player, game);
        }
        Thread.sleep(1000);

      }
      System.err.println("No more to do");
    } catch (DbException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}