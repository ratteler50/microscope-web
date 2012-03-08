package support;

import db.DbException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import model.DatabaseReads;
import objects.Character;
import objects.Event;
import objects.Focus;
import objects.GameState;
import objects.Legacy;
import objects.Palette;
import objects.Period;
import objects.Player;
import objects.Scene;

public class CollectGameData {

  public static GameState populateGame(int gameID) throws DbException,
      SQLException {
    List<Player> gamePlayers = new ArrayList<Player>();
    List<Palette> gamePalette = new ArrayList<Palette>();
    List<Legacy> gameLegacies = new ArrayList<Legacy>();

    HashMap<Integer, Focus> gameFocuses = new HashMap<Integer, Focus>();
    HashMap<Integer, Period> gamePeriods = new HashMap<Integer, Period>();
    HashMap<Integer, Event> gameEvents = new HashMap<Integer, Event>();
    HashMap<Integer, Scene> gameScenes = new HashMap<Integer, Scene>();
    HashMap<Integer, Character> gameCharacters = new HashMap<Integer, Character>();

    // Fill in the initial Lists
    GameState retval = collectGameData(gameID, gamePlayers, gamePalette,
        gameLegacies, gameFocuses, gamePeriods, gameEvents, gameScenes,
        gameCharacters);

    // Nest periods/events/scenes/characters
    setupCards(gameID, gamePeriods, gameEvents, gameScenes, gameCharacters);

    // Finish setting up gamestate before returning it
    finishGameState(gamePlayers, gamePalette, gamePeriods, gameEvents,
        gameScenes, gameFocuses, gameLegacies, retval);
    return retval;

  }

  /**
   * This private method fills the Lists with the contents of the database
   */
  private static GameState collectGameData(int gameID,
      List<Player> gamePlayers, List<Palette> gamePalette,
      List<Legacy> gameLegacies, HashMap<Integer, Focus> gameFocuses,
      HashMap<Integer, Period> gamePeriods,
      HashMap<Integer, Event> gameEvents,
      HashMap<Integer, Scene> gameScenes,
      HashMap<Integer, Character> gameCharacters) throws DbException,
      SQLException {
    CachedRowSet rowSet;
    // Get Players
    rowSet = DatabaseReads.getPlayers(gameID);
    while (rowSet.next()) {
      gamePlayers.add(Player.fromRow(rowSet));
    }
    Collections.sort(gamePlayers);

    // Get Palette
    rowSet = DatabaseReads.getPalettes(gameID);
    while (rowSet.next()) {
      gamePalette.add(new Palette(rowSet));
    }

    // Get Focuses
    rowSet = DatabaseReads.getFocuses(gameID);
    while (rowSet.next()) {
      Focus working = Focus.fromRowSet(rowSet);
      gameFocuses.put(working.getRound(), working);
    }

    // Get Legacies
    rowSet = DatabaseReads.getLegacies(gameID);
    while (rowSet.next()) {
      Legacy working = Legacy.fromRow(rowSet);
      gameLegacies.add(working);
    }

    // Get Periods
    rowSet = DatabaseReads.getPeriods(gameID);
    while (rowSet.next()) {
      Period working = new Period(rowSet);
      gamePeriods.put(working.id, working);
    }

    // Get Events
    rowSet = DatabaseReads.getEvents(gameID);
    while (rowSet.next()) {
      Event working = new Event(rowSet);
      gameEvents.put(working.id, working);
    }

    // Get Scenes
    rowSet = DatabaseReads.getScenes(gameID);
    while (rowSet.next()) {
      Scene working = new Scene(rowSet);
      gameScenes.put(working.id, working);
    }

    // Get Characters
    rowSet = DatabaseReads.getCharacters(gameID);
    while (rowSet.next()) {
      Character working = Character.fromRow(rowSet);
      gameCharacters.put(working.getId(), working);
    }

    // Get Gamestate
    rowSet = DatabaseReads.getGameState(gameID);
    rowSet.next();
    return new GameState(rowSet);
  }

  /**
   * Takes Lists of periods, events and scenes
   */
  private static void setupCards(int gameID,
      HashMap<Integer, Period> gamePeriods,
      HashMap<Integer, Event> gameEvents,
      HashMap<Integer, Scene> gameScenes,
      HashMap<Integer, Character> gameCharacters) throws DbException,
      SQLException {

    // Setup the list of player controlled characters for each scene
    CachedRowSet inScene = DatabaseReads.getInScene(gameID);
    while (inScene.next()) {
      int characterID = inScene.getInt(1);
      int sceneID = inScene.getInt(2);
      int playerNum = inScene.getInt(3);

      Scene scene = gameScenes.get(sceneID);
      Character role = gameCharacters.get(characterID);
      scene.inScene[playerNum - 1] = role;
    }

    // Setup parent event and children characters for scene cards
    for (Scene scene : gameScenes.values()) {
      // Add banned and required characters to the scene
      Integer banned1ID = scene.banned1ID;
      if (banned1ID != null) {
        scene.banned1 = gameCharacters.get(banned1ID);
      }
      Integer banned2ID = scene.banned2ID;
      if (banned2ID != null) {
        scene.banned2 = gameCharacters.get(banned2ID);
      }
      Integer required1ID = scene.required1ID;
      if (required1ID != null) {
        scene.required1 = gameCharacters.get(required1ID);
      }
      Integer required2ID = scene.required2ID;
      if (required2ID != null) {
        scene.required2 = gameCharacters.get(required2ID);
      }

      // Add the scene as a child to the correct event
      int eventID = scene.eventID;
      Event parent = gameEvents.get(eventID);
      parent.scenes.add(scene);
      Collections.sort(parent.scenes);

    }

    // Now add the Events to the Periods
    for (Event event : gameEvents.values()) {
      int periodID = event.periodID;
      Period parent = gamePeriods.get(periodID);
      parent.events.add(event);
      Collections.sort(parent.events);
    }
  }

  /**
   * Sets up the game state object with object version of contained information
   */
  private static void finishGameState(List<Player> gamePlayers,
      List<Palette> gamePalette, HashMap<Integer, Period> gamePeriods,
      HashMap<Integer, Event> gameEvents,
      HashMap<Integer, Scene> gameScenes,
      HashMap<Integer, Focus> gameFocuses, List<Legacy> gameLegacies,
      GameState game) {

    // Setup Current Lens
    for (Player player : gamePlayers) {
      if (player.getUserId() == game.lensID) {
        game.lens = player;
      }
    }

    if (game.last_pes != null) {
      // Setup Last Card
      if (game.last_pes == 0) {
        game.lastCard = gamePeriods.get(game.lastCardID);
      } else if (game.last_pes == 1) {
        game.lastCard = gameEvents.get(game.lastCardID);
      } else if (game.last_pes == 2) {
        game.lastCard = gameScenes.get(game.lastCardID);
      }
    }

    // Attach the list of periods
    List<Period> periodList = new ArrayList<Period>(gamePeriods.values());
    Collections.sort(periodList);
    game.periods = periodList;

    // Attach the current focus
    game.focus = gameFocuses.get(game.round);

    // Attach the list of players
    Collections.sort(gamePlayers);
    game.players = gamePlayers;

    // Attach all legacies
    Collections.sort(gameLegacies);
    game.legacies = gameLegacies;

    // Attaches the palettes
    List<Palette> banned = new ArrayList<Palette>();
    List<Palette> recommended = new ArrayList<Palette>();
    for (Palette item : gamePalette) {
      if (item.inGame) {
        recommended.add(item);
      } else {
        banned.add(item);
      }
    }
    game.palette_banned = banned;
    game.palette_recommended = recommended;
  }
}
