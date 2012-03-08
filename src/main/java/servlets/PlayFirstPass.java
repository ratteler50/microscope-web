package servlets;

import static model.DatabaseReads.actionAllDoneCheck;
import static model.DatabaseReads.actionDoneCheck;
import static model.DatabaseUpdates.eventAdd;
import static model.DatabaseUpdates.gameNextRound;
import static model.DatabaseUpdates.periodAdd;
import static model.DatabaseUpdates.playerSetActionDone;
import static support.GameLogic.EVENT;
import static support.GameLogic.FIRST_PASS_PE;
import static support.GameLogic.PERIOD;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.findCurrentPlayerID;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;
import static support.GameLogic.getTurnRound;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.Settings;

/**
 * Servlet implementation class PlayFirstPass
 */
public class PlayFirstPass extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlayFirstPass() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    int userID = getCurrUserID();
    int gameID = getCurrGameID(request);

    // VERIFY THE CORRECT PLAYER AND ACTION
    if (!Settings.DEBUG) {
      int[] CPA = currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (actionDoneCheck(userID, gameID)) {
        System.err.println("You have already played your first pass!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA[1] != FIRST_PASS_PE) {
        System.err.println("It is not time for first pass!");
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

    int[] turnRound = getTurnRound(gameID);
    int turn = turnRound[0];
    int round = turnRound[1];

    try {
      // Add a period
      if (pes == PERIOD)

      {
        newCardID = periodAdd(userID, parentID, position, turn, round,
            text, description, tone);
      } else if (pes == EVENT) {
        newCardID = eventAdd(userID, parentID, position, turn, round,
            text, description, tone);
      }
      // The player has played their card
      playerSetActionDone(userID, gameID, true);
      if (actionAllDoneCheck(gameID)) {
        int newLensID = findCurrentPlayerID(gameID, 0, 1);
        gameNextRound(gameID, newLensID);
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
