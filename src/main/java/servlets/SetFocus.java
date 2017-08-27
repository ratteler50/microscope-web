package servlets;

import static model.DatabaseUpdates.focusSet;
import static model.DatabaseUpdates.gameNextTurn;
import static support.GameLogic.PICK_FOCUS;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.Settings;

/** Servlet implementation class SetFocus */
public final class SetFocus extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public SetFocus() {}

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {}

  /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    int userID = getCurrUserID();
    int gameID = getCurrGameID(request);

    // VERIFY THE CORRECT PLAYER AND ACTION
    if (!Settings.DEBUG) {
      int[] CPA = currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA[0] != userID) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA[1] != PICK_FOCUS) {
        System.err.println("It is not time to pick the focus!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    int newFocusID = -1;

    String focusText = request.getParameter("focus");

    try {
      newFocusID = focusSet(gameID, focusText);
      gameNextTurn(gameID);

    } catch (Exception e) {
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      e.printStackTrace();
    }

    System.err.println(newFocusID);
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.print("{\"success\" : true, \"id\" : " + newFocusID + "}");
    writer.flush();
  }
}
