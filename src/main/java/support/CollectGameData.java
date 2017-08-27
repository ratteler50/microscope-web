package support;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import db.DbException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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

public final class CollectGameData {

  public static GameState populateGame(int gameID) throws DbException, SQLException {
    List<Player> gamePlayers = new ArrayList<>();
    List<Palette> gamePalette = new ArrayList<>();
    List<Legacy> gameLegacies = new ArrayList<>();

    HashMap<Integer, Focus> gameFocuses = new HashMap<>();
    HashMap<Integer, Period> gamePeriods = new HashMap<>();
    HashMap<Integer, Event> gameEvents = new HashMap<>();
    HashMap<Integer, Scene> gameScenes = new HashMap<>();
    HashMap<Integer, Character> gameCharacters = new HashMap<>();

    // Fill in the initial Lists
    GameState retval =
        collectGameData(
            gameID,
            gamePlayers,
            gamePalette,
            gameLegacies,
            gameFocuses,
            gamePeriods,
            gameEvents,
            gameScenes,
            gameCharacters);

    // Nest periods/events/scenes/characters
    setupCards(gameID, gamePeriods, gameEvents, gameScenes, gameCharacters);

    // Finish setting up gamestate before returning it
    finishGameState(
        gamePlayers,
        gamePalette,
        gamePeriods,
        gameEvents,
        gameScenes,
        gameFocuses,
        gameLegacies,
        retval);
    return retval;
  }

  /** This private method fills the Lists with the contents of the database */
  private static GameState collectGameData(
      int gameID,
      List<Player> gamePlayers,
      List<Palette> gamePalette,
      List<Legacy> gameLegacies,
      HashMap<Integer, Focus> gameFocuses,
      HashMap<Integer, Period> gamePeriods,
      HashMap<Integer, Event> gameEvents,
      HashMap<Integer, Scene> gameScenes,
      HashMap<Integer, Character> gameCharacters)
      throws DbException, SQLException {
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
      gamePalette.add(Palette.fromRow(rowSet));
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

  /** Takes Lists of periods, events and scenes */
  private static void setupCards(
      int gameID,
      HashMap<Integer, Period> gamePeriods,
      HashMap<Integer, Event> gameEvents,
      HashMap<Integer, Scene> gameScenes,
      HashMap<Integer, Character> gameCharacters)
      throws DbException, SQLException {

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
      OptionalInt banned1Id = scene.banned1Id;
      if (banned1Id.isPresent()) {
        scene.banned1 = Optional.of(gameCharacters.get(banned1Id.getAsInt()));
      }
      OptionalInt banned2Id = scene.banned2Id;
      if (banned2Id.isPresent()) {
        scene.banned2 = Optional.of(gameCharacters.get(banned2Id.getAsInt()));
      }
      OptionalInt required1Id = scene.required1Id;
      if (required1Id.isPresent()) {
        scene.required1 = Optional.of(gameCharacters.get(required1Id.getAsInt()));
      }
      OptionalInt required2Id = scene.required2Id;
      if (required2Id.isPresent()) {
        scene.required2 = Optional.of(gameCharacters.get(required2Id.getAsInt()));
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

  /** Sets up the game state object with object version of contained information */
  private static void finishGameState(
      List<Player> gamePlayersMutable,
      List<Palette> gamePaletteMutable,
      HashMap<Integer, Period> gamePeriodsMutable,
      HashMap<Integer, Event> gameEventsMutable,
      HashMap<Integer, Scene> gameScenesMutable,
      HashMap<Integer, Focus> gameFocusesMutable,
      List<Legacy> gameLegaciesMutable,
      GameState game) {
    ImmutableList<Player> gamePlayers =
        gamePlayersMutable.stream().sorted().collect(toImmutableList());

    ImmutableList<Palette> gamePalette = ImmutableList.copyOf(gamePaletteMutable);
    ImmutableList<Legacy> gameLegacies =
        gameLegaciesMutable.stream().sorted().collect(toImmutableList());
    ImmutableMap<Integer, Period> gamePeriods = ImmutableMap.copyOf(gamePeriodsMutable);
    ImmutableMap<Integer, Event> gameEvents = ImmutableMap.copyOf(gameEventsMutable);
    ImmutableMap<Integer, Scene> gameScenes = ImmutableMap.copyOf(gameScenesMutable);
    ImmutableMap<Integer, Focus> gameFocuses = ImmutableMap.copyOf(gameFocusesMutable);
    ImmutableList<Period> periodList =
        gamePeriods.values().stream().sorted().collect(toImmutableList());

    // Setup Current Lens
    for (Player player : gamePlayers) {
      if (player.getUserId() == game.lensID) {
        game.lens = player;
      }
    }

    if (game.last_pes.isPresent()) {
      switch (game.last_pes.get()) {
        case PERIOD:
          game.lastCardID.ifPresent(id -> game.lastCard = gamePeriods.get(id));
          break;
        case EVENT:
          game.lastCardID.ifPresent(id -> game.lastCard = gameEvents.get(id));
          break;
        case SCENE:
          game.lastCardID.ifPresent(id -> game.lastCard = gameScenes.get(id));
      }
    }

    // Attach the list of periods
    game.periods = periodList;

    // Attach the current focus
    game.focus = gameFocuses.get(game.round);

    // Attach the list of players
    game.players = gamePlayers;

    // Attach all legacies
    game.legacies = gameLegacies;

    // Attaches the palettes
    List<Palette> banned = new ArrayList<>();
    List<Palette> recommended = new ArrayList<>();
    for (Palette item : gamePalette) {
      if (item.isInGame()) {
        recommended.add(item);
      } else {
        banned.add(item);
      }
    }
    game.palette_banned = banned;
    game.palette_recommended = recommended;
  }
}
