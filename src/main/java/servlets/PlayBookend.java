package servlets;

import static model.DatabaseReads.getNumPeriods;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.periodAdd;
import static support.GameLogic.BOOKEND_PERIODS;
import static support.GameLogic.PERIOD;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;
import static support.GameLogic.getGamePlayers;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.GameLogic;
import support.Settings;

/** Servlet implementation class PlayBookend */
public final class PlayBookend extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public PlayBookend() {}

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {}

  /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    int userID = getCurrUserID();
    int gameID = getCurrGameID(request);
    int gameOwner = getGamePlayers(gameID)[0];
    int periodsPlayed = getNumPeriods(gameID);

    // VERIFY THE CORRECT PLAYER AND ACTION
    if (!Settings.DEBUG) {
      int[] CPA = currPlayerAndAction(gameID);
      System.err.println("owner: " + gameOwner + "; userID:" + userID);
      // If the current player and action are not valid, exit
      if (gameOwner != userID) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA[1] != BOOKEND_PERIODS) {
        System.err.println("It is not time to play bookends!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (periodsPlayed > 1) {
        System.err.println("The bookends have already both been played!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    int newCardID = -1;

    String pesString = request.getParameter("pes");
    int pes = Integer.valueOf(pesString);

    String parentIDString = request.getParameter("parentID");
    int parentID = Integer.valueOf(parentIDString);

    String positionString = request.getParameter("position");
    int position = Integer.valueOf(positionString);

    String toneString = request.getParameter("tone");
    boolean tone = Boolean.valueOf(toneString);

    String text = request.getParameter("text");
    String description = request.getParameter("description");

    int[] turnRound = GameLogic.getTurnRound(gameID);
    int turn = turnRound[0];
    int round = turnRound[1];

    try {
      if (pes == PERIOD) {
        newCardID = periodAdd(userID, parentID, position, turn, round, text, description, tone);
        if (periodsPlayed == 1) {
          gameNextTurn(gameID);
        }
      }

    } catch (Exception e) {
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      e.printStackTrace();
    }

    System.err.println(newCardID);
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.print("{\"success\" : true, \"id\" : " + newCardID + "}");
    writer.flush();
  }
}
