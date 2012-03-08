package servlets;

import static model.DatabaseReads.actionAllDoneCheck;
import static model.DatabaseReads.paletteLegalCheck;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.paletteAdd;
import static model.DatabaseUpdates.paletteSetDone;
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
 * Servlet implementation class PlayPalette
 */
public class PlayPalette extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlayPalette() {
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
      if (CPA[1] != GameLogic.PALETTE) {
        System.err.println("It is not time to change the palette!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (!paletteLegalCheck(userID, gameID)) {
        System.err.println("You are not allowed to do that right now");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    int newPaletteID = -1;

    String skipString = request.getParameter("skip");
    boolean skip = Boolean.valueOf(skipString);

    String inGameString = request.getParameter("in_game");
    boolean inGame = Boolean.valueOf(inGameString);

    String text = request.getParameter("text");

    try {
      // The player passed
      if (skip) {
        paletteSetDone(userID, gameID);
      }
      // The player added to the palette
      else {
        newPaletteID = paletteAdd(userID, gameID, inGame, text);
      }

      if (actionAllDoneCheck(gameID)) {
        gameNextTurn(gameID);
      }

    } catch (Exception e) {
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      e.printStackTrace();
    }

    System.err.println(newPaletteID);
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.print("{\"success\" : true, \"id\" : " + newPaletteID + "}");
    writer.flush();
  }
}
