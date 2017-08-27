package support;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import model.DatabaseReads;
import objects.GameState;
import objects.Player;
import org.apache.shiro.SecurityUtils;

/**
 * This class contains the main chunk of game rule logic.
 *
 * @author David Lorant
 */
public final class GameLogic {

  public enum ActiveGameState {
    ADD_PLAYERS,
    BIG_PICTURE,
    BOOKEND_PERIODS,
    PALETTE,
    FIRST_PASS_PE,
    PICK_FOCUS,
    PLAY_PES,
    PLAY_NESTED_ES,
    PICK_LEGACY,
    PLAY_LEGACY_ES;
  }

  @SuppressWarnings("unused")
  public enum SceneCreationState {
    SCENE_DONE(0),
    SCENE_ANSWER(1),
    PLAYERS_CHOOSE_CHARACTER(2),
    SCENE_BAN_REQUIRE(3);

    private final int number;

    SceneCreationState(int i) {
      this.number = i;
    }

    public int getNumber() {
      return number;
    }
  }

  public enum PES {
    PERIOD(0),
    EVENT(1),
    SCENE(2);

    private final int number;

    PES(int i) {
      this.number = i;
    }

    public int getNumber() {
      return number;
    }
  }

  public static void main(String args[]) {
    System.err.println(findCurrentPlayerID(1));
    System.err.println(getGameAction(1));
  }

  @AutoValue
  public abstract static class PlayerAndAction {
    public abstract int getPlayer();

    public abstract ActiveGameState getAction();

    public static PlayerAndAction create(int newPlayer, ActiveGameState newActiom) {
      return new AutoValue_GameLogic_PlayerAndAction(newPlayer, newActiom);
    }
  }
  /**
   * Returns a 2 valued array containing the active player's userID and the ENUM for the game action
   */
  public static PlayerAndAction currPlayerAndAction(int gameId) {
    int[] gamePlayers = getGamePlayers(gameId);
    TurnRound turnRound = getTurnRound(gameId);

    int currentPlayerID = findCurrentPlayerID(gamePlayers, turnRound);
    ActiveGameState gameAction = getGameAction(turnRound, gamePlayers.length);
    return PlayerAndAction.create(currentPlayerID, gameAction);
  }

  /**
   * Given a complete game state, find the current game action
   *
   * @param state The game's current total state
   * @return the enum for the current game action
   */
  public static ActiveGameState getGameAction(GameState state) {
    return getGameAction(state.round, state.turn, state.players.size());
  }

  /**
   * Given a gameID, find the current game action
   *
   * @return the enum for the current game action
   */
  public static ActiveGameState getGameAction(int gameID) {
    int[] gamePlayers = getGamePlayers(gameID);
    TurnRound turnRound = getTurnRound(gameID);

    return getGameAction(turnRound, gamePlayers.length);
  }

  /**
   * Given the number of players in the game, along with the current round and turn, find out what
   * game state the game is current in (what action is it?)
   *
   * @return the enum for the current game action
   */
  private static ActiveGameState getGameAction(TurnRound turnRound, int numPlayers) {
    return getGameAction(turnRound.getRound(), turnRound.getTurn(), numPlayers);
  }

  /**
   * Given the number of players in the game, along with the current round and turn, find out what
   * game state the game is current in (what action is it?)
   *
   * @return the enum for the current game action
   */
  public static ActiveGameState getGameAction(int round, int turn, int numPlayers) {
    if (round == 0) {
      return getGameSetupAction(turn);
    }

    // Special logic for a 2 person game
    if (numPlayers == 2) {
      Preconditions.checkArgument(turn < 10, "There are only 9 turns");
      switch (turn) {
        case 0:
          return ActiveGameState.PICK_FOCUS;
        case 1:
          return ActiveGameState.PLAY_PES;
        case 2:
          return ActiveGameState.PLAY_NESTED_ES;
        case 3:
        case 4:
        case 5:
        case 6:
          return ActiveGameState.PLAY_PES;
        case 7:
          return ActiveGameState.PLAY_NESTED_ES;
        case 8:
          return ActiveGameState.PICK_LEGACY;
        case 9:
          return ActiveGameState.PLAY_LEGACY_ES;
        default:
          throw new IllegalArgumentException();
      }
    }

    // Regular game logic for n players
    Preconditions.checkArgument(
        turn <= numPlayers + 5,
        "A %s player game should have at most %s turns per round.  Tried to calculate turn %s",
        numPlayers,
        numPlayers + 5,
        turn);
    // On these turns the lens goes (slightly special logic)
    if (turn == 0) {
      return ActiveGameState.PICK_FOCUS;
    } else if (turn == 2 || turn == (numPlayers + 3)) {
      return ActiveGameState.PLAY_NESTED_ES;
    } else if (turn == numPlayers + 4) {
      return ActiveGameState.PICK_LEGACY;
    } else if (turn == numPlayers + 5) {
      return ActiveGameState.PLAY_LEGACY_ES;
    } else {
      return ActiveGameState.PLAY_PES;
    }
  }

  /**
   * Returns the user IDs of the players in the game as an array sorted by player position in that
   * game.
   */
  public static int[] getGamePlayers(int gameID) {
    CachedRowSet rs;
    List<Player> players = new ArrayList<>();
    int[] retval = null;
    try {
      rs = DatabaseReads.getPlayers(gameID);

      while (rs.next()) {
        players.add(Player.fromRow(rs));
      }
      Collections.sort(players);
      retval = new int[players.size()];

      // Add each player's ID into the returned array
      for (int i = 0; i < retval.length; i++) {
        retval[i] = players.get(i).getUserId();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return retval;
  }

  /**
   * Given a complete game state, find the user ID for the active player
   *
   * @param state The game's current total state
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  public static int findCurrentPlayerID(GameState state) {
    int round = state.round;
    int turn = state.turn;
    int[] playerIDs = new int[state.players.size()];
    for (int i = 0; i < playerIDs.length; i++) {
      playerIDs[i] = state.players.get(i).getUserId();
    }
    return findCurrentPlayerID(playerIDs, turn, round);
  }

  /** Given a game id and a player id (user id), tells if the player is done with the action. */
  public static boolean isPlayerActionDone(int gameID, int playerID) {
    CachedRowSet rs;
    try {
      rs = DatabaseReads.getPlayers(gameID);

      while (rs.next()) {
        Player player = Player.fromRow(rs);
        if (player.getUserId() == playerID) {
          return player.getActionDone();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Given a gameID find the user ID for the active player this turn/round
   *
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  public static int findCurrentPlayerID(int gameID) {
    TurnRound turnRound = getTurnRound(gameID);
    return findCurrentPlayerID(gameID, turnRound);
  }

  /**
   * Given a gameID, round, and turn, find the user ID for the active player
   *
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  public static int findCurrentPlayerID(int gameID, TurnRound turnround) {
    int[] playerIDs = getGamePlayers(gameID);
    return findCurrentPlayerID(playerIDs, turnround.getTurn(), turnround.getRound());
  }

  /**
   * Given a gameID, round, and turn, find the user ID for the active player
   *
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  public static int findCurrentPlayerID(int gameID, int turn, int round) {
    int[] playerIDs = getGamePlayers(gameID);
    return findCurrentPlayerID(playerIDs, turn, round);
  }

  /**
   * Given a gameID, round, and turn, find the user ID for the active player
   *
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  private static int findCurrentPlayerID(int[] allPlayerIds, TurnRound turnRound) {
    return findCurrentPlayerID(allPlayerIds, turnRound.getTurn(), turnRound.getRound());
  }

  /**
   * Given a list of userIDs, the round, and the turn, find the user ID for the active player.
   *
   * <p>NOTE: Players for game logic are 1 indexed!!! Turns and Rounds are 0 indexed
   *
   * @param allPlayerIDs An array where index i is the user-id for player i+1
   * @param turn The round's current turn
   * @param round The game's current round
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   *     current player
   */
  public static int findCurrentPlayerID(int[] allPlayerIDs, int turn, int round) {
    assert (turn >= 0);
    int numPlayers = allPlayerIDs.length;
    int playerNum;

    // No active players in round 0
    if (round == 0) {
      return -1;
    }

    // Special logic for a 2 person game
    if (numPlayers == 2) {
      assert (turn < 10);
      switch (turn) {
        case 3:
        case 5:
        case 8:
        case 9:
          playerNum = ((round + 1) % 2);
          break;
        default:
          playerNum = round % 2;
          break;
      }
    }

    // A general game for n>2 players
    else {
      assert (turn <= numPlayers + 5);
      // On these turns the lens goes (slightly special logic)
      if (turn == 0 || turn == 1 || turn == 2 || turn == numPlayers + 2 || turn == numPlayers + 3) {
        playerNum = round % numPlayers;
      }

      // These turns are the legacy
      else if (turn == numPlayers + 4 || turn == numPlayers + 5) {
        playerNum = (round - 1) % numPlayers;
      }

      // The general formula otherwise
      else {
        playerNum = (round + turn - 2) % numPlayers;
      }
    }

    // Player 0 is really player N (modular arithmetic)
    if (playerNum == 0) {
      playerNum = numPlayers;
    }

    // Players are 1 indexed
    return allPlayerIDs[playerNum - 1];
  }

  /**
   * Returns the action for the given turn in round 0
   *
   * @param turn The turn number for the current turn
   */
  private static ActiveGameState getGameSetupAction(int turn) {
    switch (turn) {
      case (0):
        return ActiveGameState.ADD_PLAYERS;
      case (1):
        return ActiveGameState.BIG_PICTURE;
      case (2):
        return ActiveGameState.BOOKEND_PERIODS;
      case (3):
        return ActiveGameState.PALETTE;
      case (4):
        return ActiveGameState.FIRST_PASS_PE;
      default:
        throw new IllegalArgumentException("Game setup only has five steps!");
    }
  }

  /**
   * // First check the request, then the session for the userID public static int
   * getCurrUserID(HttpServletRequest request) { String userIDString =
   * request.getParameter("userID"); int userID;
   *
   * <p>// If ID is not in the request, see if there's a value in the session try { userID =
   * Integer.valueOf(userIDString); } catch (Exception e) { userID = (Integer)
   * request.getSession().getAttribute("userID"); }
   *
   * <p>return userID; }
   */

  // Get the UserID from the principal
  public static int getCurrUserID() {
    return SecurityUtils.getSubject().getPrincipals().oneByType(Integer.class);
  }

  public static int getCurrGameID(HttpServletRequest request) {
    String gameIDString = request.getParameter("gameID");
    int gameID;

    // If ID is not in the request, see if there's a value in the session
    try {
      gameID = Integer.valueOf(gameIDString);
    } catch (Exception e) {
      gameID = (Integer) request.getSession().getAttribute("gameID");
    }
    return gameID;
  }

  /** Returns a 2 entry array containing the given games turn and round */
  public static TurnRound getTurnRound(int gameID) {
    CachedRowSet rs;
    try {
      rs = DatabaseReads.getGameState(gameID);
      if (rs.next()) {
        return TurnRound.builder().setRound(rs.getInt("round")).setTurn(rs.getInt("turn")).build();
      }
      throw new RuntimeException("Couldn't find current state for this game");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AutoValue
  public abstract static class TurnRound {
    public abstract int getTurn();

    public abstract int getRound();

    public static Builder builder() {
      return new AutoValue_GameLogic_TurnRound.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

      public abstract Builder setTurn(int newTurn);

      public abstract Builder setRound(int newRound);

      public abstract TurnRound build();
    }
  }
}
