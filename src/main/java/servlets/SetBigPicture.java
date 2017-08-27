package servlets;

import static model.DatabaseUpdates.gameBigPictureAdd;
import static model.DatabaseUpdates.gameNextTurn;
import static support.GameLogic.BIG_PICTURE;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.Settings;

/** Servlet implementation class SetBigPicture */
public final class SetBigPicture extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public SetBigPicture() {}

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {}

  /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    int gameID = getCurrGameID(request);

    // VERIFY THE CORRECT PLAYER AND ACTION
    if (!Settings.DEBUG) {
      int[] CPA = currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA[0] != -1) {
        System.err.println("Something is wrong. " + "It should not be anyone's turn");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA[1] != BIG_PICTURE) {
        System.err.println("It is not time to set the big picture!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    String bigPictureText = request.getParameter("bigPicture");

    try {
      gameBigPictureAdd(gameID, bigPictureText);
      gameNextTurn(gameID);

    } catch (Exception e) {
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      e.printStackTrace();
    }

    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.print("{\"success\" : true}");
    writer.flush();
  }
}
