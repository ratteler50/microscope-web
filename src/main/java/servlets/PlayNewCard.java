package servlets;

import static model.DatabaseUpdates.eventAdd;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.periodAdd;
import static model.DatabaseUpdates.sceneAdd;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.GameLogic;
import support.GameLogic.ActiveGameState;
import support.GameLogic.PES;
import support.GameLogic.PlayerAndAction;
import support.GameLogic.TurnRound;
import support.Settings;

/** Servlet implementation class PlayNewCard */
public final class PlayNewCard extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public PlayNewCard() {}

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
      PlayerAndAction CPA = GameLogic.currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA.getPlayer() != userID) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA.getAction() != ActiveGameState.PLAY_PES) {
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

    TurnRound turnRound = GameLogic.getTurnRound(gameID);

    try {
      // Add a period
      if (pes == PES.PERIOD.getNumber()) {
        newCardID =
            periodAdd(
                userID,
                parentID,
                position,
                turnRound.getTurn(),
                turnRound.getRound(),
                text,
                description,
                tone);

        gameNextTurn(gameID);
      } else if (pes == PES.EVENT.getNumber()) {
        newCardID =
            eventAdd(
                userID,
                parentID,
                position,
                turnRound.getTurn(),
                turnRound.getRound(),
                text,
                description,
                tone);

        gameNextTurn(gameID);
      } else if (pes == PES.SCENE.getNumber()) {
        String dictatedString = request.getParameter("dictated");
        boolean dictated = Boolean.valueOf(dictatedString);

        String scene = request.getParameter("scene");
        newCardID =
            sceneAdd(
                userID,
                parentID,
                position,
                turnRound.getTurn(),
                turnRound.getRound(),
                text,
                scene,
                "",
                "",
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
