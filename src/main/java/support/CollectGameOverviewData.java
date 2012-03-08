package support;

import db.DbException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import model.DatabaseReads;
import objects.GameOverview;
import objects.Player;

public class CollectGameOverviewData {

  /**
   * Get the game overviews for all games a given user is in
   */
  public static List<GameOverview> getGameOverview(int userID)
      throws DbException, SQLException {
    List<Integer> gameIDs = getGameIDs(userID);
    List<GameOverview> overviewList = new ArrayList<>();
    for (Integer gameID : gameIDs) {
      overviewList.add(collectOverviewData(gameID));
    }

    return overviewList;

  }

  /**
   * Get the game IDs for all games a given user is in
   */
  private static List<Integer> getGameIDs(int userID) throws DbException,
      SQLException {
    List<Integer> gameID = new ArrayList<>();
    CachedRowSet rowSet;
    rowSet = DatabaseReads.getGameList(userID);
    while (rowSet.next()) {
      gameID.add(rowSet.getInt(1));
    }
    return gameID;
  }

  private static GameOverview collectOverviewData(Integer gameID)
      throws DbException, SQLException {
    CachedRowSet rowSet;
    // Get the players in the given game
    List<Player> gamePlayers = new ArrayList<>();
    rowSet = DatabaseReads.getPlayers(gameID);
    while (rowSet.next()) {
      gamePlayers.add(Player.fromRow(rowSet));
    }
    Collections.sort(gamePlayers);

    // Get the rest of the game info
    rowSet = DatabaseReads.getGameState(gameID);
    rowSet.next();
    GameOverview game = new GameOverview(rowSet);
    game.players = gamePlayers;

    return game;

  }
}
