package servlets;

import static model.DatabaseUpdates.eventAdd;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.periodAdd;
import static model.DatabaseUpdates.sceneAdd;
import static support.GameLogic.EVENT;
import static support.GameLogic.PERIOD;
import static support.GameLogic.SCENE;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.GameLogic;
import support.Settings;

/**
 * Servlet implementation class PlayNewCard
 */
public class PlayNewCard extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlayNewCard() {
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
      int[] CPA = GameLogic.currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA[0] != userID) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA[1] != GameLogic.PLAY_PES) {
        System.err.println("It is not time to paly a normal P/E/S!");
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
      // Add a period
      if (pes == PERIOD) {
        newCardID = periodAdd(userID, parentID, position, turn, round, text,
            description, tone);

        gameNextTurn(gameID);
      } else if (pes == EVENT) {
        newCardID = eventAdd(userID, parentID, position, turn, round, text,
            description, tone);

        gameNextTurn(gameID);
      } else if (pes == SCENE) {
        String dictatedString = request.getParameter("dictated");
        boolean dictated = Boolean.valueOf(dictatedString);

        String scene = request.getParameter("scene");
        newCardID = sceneAdd(userID, parentID, position, turn, round, text, scene, "", "",
            dictated);
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
