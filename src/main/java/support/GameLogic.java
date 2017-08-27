package support;

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

  // ENUMS for possible game states
  public static final int ADD_PLAYERS = 0;
  public static final int BIG_PICTURE = 1;
  public static final int BOOKEND_PERIODS = 2;
  public static final int PALETTE = 3;
  public static final int FIRST_PASS_PE = 4;
  public static final int PICK_FOCUS = 5;
  public static final int PLAY_PES = 6;
  public static final int PLAY_NESTED_ES = 7;
  public static final int PICK_LEGACY = 8;
  public static final int PLAY_LEGACY_ES = 9;

  // ENUMS for Scene Creation
  public static final int SCENE_DONE = 0;
  public static final int SCENE_ANSWER = 1;
  public static final int PLAYERS_CHOOSE_CHARACTER = 2;
  public static final int SCENE_BAN_REQUIRE = 3;

  // ENUMS for PES
  public static final int PERIOD = 0;
  public static final int EVENT = 1;
  public static final int SCENE = 2;

  public static void main(String args[]) {
    System.err.println(findCurrentPlayerID(1));
    System.err.println(getGameAction(1));
  }

  /**
   * Returns a 2 valued array containing the active player's userID and the ENUM for the game
   * action
   */
  public static int[] currPlayerAndAction(int gameID) {
    int[] retval = new int[2];
    int[] gamePlayers = getGamePlayers(gameID);
    int[] turnRound = getTurnRound(gameID);

    retval[0] = findCurrentPlayerID(gamePlayers, turnRound[0], turnRound[1]);
    retval[1] = getGameAction(turnRound[1], turnRound[0],
        gamePlayers.length);
    return retval;
  }

  /**
   * Given a complete game state, find the current game action
   *
   * @param state The game's current total state
   * @return the enum for the current game action
   */
  public static int getGameAction(GameState state) {
    return getGameAction(state.round, state.turn, state.players.size());
  }

  /**
   * Given a gameID, find the current game action
   *
   * @param state The game's current total state
   * @return the enum for the current game action
   */
  public static int getGameAction(int gameID) {
    int[] gamePlayers = getGamePlayers(gameID);
    int[] turnRound = getTurnRound(gameID);

    return getGameAction(turnRound[1], turnRound[0], gamePlayers.length);
  }

  /**
   * Given the number of players in the game, along with the current round and turn, find out what
   * game state the game is current in (what action is it?)
   *
   * @param state The game's current total state
   * @return the enum for the current game action
   */
  public static int getGameAction(int round, int turn, int numPlayers) {
    if (round == 0) {
      return getGameSetupAction(turn);
    }

    // Special logic for a 2 person game
    if (numPlayers == 2) {
      assert (turn < 10);
      switch (turn) {
        case 0:
          return PICK_FOCUS;
        case 1:
          return PLAY_PES;
        case 2:
          return PLAY_NESTED_ES;
        case 3:
        case 4:
        case 5:
        case 6:
          return PLAY_PES;
        case 7:
          return PLAY_NESTED_ES;
        case 8:
          return PICK_LEGACY;
        case 9:
          return PLAY_LEGACY_ES;
        default:
          return -1;
      }
    }

    // Regular game logic for n players
    assert (turn <= numPlayers + 5);
    // On these turns the lens goes (slightly special logic)
    if (turn == 0) {
      return PICK_FOCUS;
    } else if (turn == 2 || turn == (numPlayers + 3)) {
      return PLAY_NESTED_ES;
    } else if (turn == numPlayers + 4) {
      return PICK_LEGACY;
    } else if (turn == numPlayers + 5) {
      return PLAY_LEGACY_ES;
    } else {
      return PLAY_PES;
    }

  }

  /**
   * Returns the user IDs of the players in the game as an array sorted by player position in that
   * game.
   */
  public static int[] getGamePlayers(int gameID) {
    CachedRowSet rs;
    List<Player> players = new ArrayList<Player>();
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
   * current player
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

  /**
   * Given a game id and a player id (user id), tells if the player is done with the action.
   */
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
   * @param state The game's current total state
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   * current player
   */
  public static int findCurrentPlayerID(int gameID) {
    int[] turnRound = getTurnRound(gameID);
    return findCurrentPlayerID(gameID, turnRound[0], turnRound[1]);
  }

  /**
   * Given a gameID, round, and turn, find the user ID for the active player
   *
   * @param state The game's current total state
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   * current player
   */
  public static int findCurrentPlayerID(int gameID, int turn, int round) {
    int[] playerIDs = getGamePlayers(gameID);
    return findCurrentPlayerID(playerIDs, turn, round);
  }

  /**
   * Given a list of userIDs, the round, and the turn, find the user ID for the active player.
   *
   * NOTE: Players for game logic are 1 indexed!!! Turns and Rounds are 0 indexed
   *
   * @param allPlayerIDs An array where index i is the user-id for player i+1
   * @param turn The round's current turn
   * @param round The game's current round
   * @return -1 if there is no "current player" (game setup). Otherwise return the userID of the
   * current player
   */
  public static int findCurrentPlayerID(int[] allPlayerIDs, int turn,
      int round) {
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
      if (turn == 0 || turn == 1 || turn == 2 || turn == numPlayers + 2
          || turn == numPlayers + 3) {
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
  private static int getGameSetupAction(int turn) {
    switch (turn) {
      case (0):
        return ADD_PLAYERS;
      case (1):
        return BIG_PICTURE;
      case (2):
        return BOOKEND_PERIODS;
      case (3):
        return PALETTE;
      case (4):
        return FIRST_PASS_PE;
      default:
        return -1;
    }
  }

  /**
   * // First check the request, then the session for the userID public static int
   * getCurrUserID(HttpServletRequest request) { String userIDString =
   * request.getParameter("userID"); int userID;
   *
   * // If ID is not in the request, see if there's a value in the session try { userID =
   * Integer.valueOf(userIDString); } catch (Exception e) { userID = (Integer)
   * request.getSession().getAttribute("userID"); }
   *
   * return userID; }
   **/

  // Get the UserID from the principal
  public static int getCurrUserID() {
    return SecurityUtils.getSubject().getPrincipals()
        .oneByType(Integer.class);
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

  /**
   * Returns a 2 entry array containing the given games turn and round
   */
  public static int[] getTurnRound(int gameID) {
    CachedRowSet rs;
    int[] turnRound = new int[2];
    try {
      rs = DatabaseReads.getGameState(gameID);

      if (rs.next()) {
        turnRound[1] = rs.getInt("round");
        turnRound[0] = rs.getInt("turn");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return turnRound;
  }

}
