package servlets;

import static model.DatabaseReads.getSceneStepsLeft;
import static model.DatabaseUpdates.gameNextRound;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.sceneSetAnswer;
import static model.DatabaseUpdates.sceneSetTone;
import static model.DatabaseUpdates.sceneStepDone;
import static support.GameLogic.PLAY_LEGACY_ES;
import static support.GameLogic.PLAY_NESTED_ES;
import static support.GameLogic.PLAY_PES;
import static support.GameLogic.SCENE_ANSWER;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;
import static support.GameLogic.getGameAction;
import static support.GameLogic.getTurnRound;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.GameLogic;
import support.Settings;

/** Servlet implementation class SetSceneAnswer */
public final class SetSceneAnswer extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public SetSceneAnswer() {}

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
      } else if (CPA[1] != PLAY_PES && CPA[1] != PLAY_NESTED_ES && CPA[1] != PLAY_LEGACY_ES) {
        System.err.println("It is not time to set the scene answer!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    String cardIDString = request.getParameter("cardID");
    int cardID = Integer.valueOf(cardIDString);

    // Only update the scene's answer if that is the correct step
    if (!Settings.DEBUG && getSceneStepsLeft(cardID) != SCENE_ANSWER) {
      System.err.println("It is not time to paly the answer!");
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      return;
    }

    int[] turnRound = getTurnRound(gameID);

    String toneString = request.getParameter("tone");
    boolean tone = Boolean.valueOf(toneString);

    int currAction = getGameAction(gameID);

    String answer = request.getParameter("answer");
    try {
      sceneSetAnswer(cardID, answer);
      sceneSetTone(cardID, tone);
      sceneStepDone(cardID);

      if (currAction == PLAY_LEGACY_ES) {
        int newLens = GameLogic.findCurrentPlayerID(gameID, 0, turnRound[1] + 1);
        gameNextRound(gameID, newLens);
      } else {
        gameNextTurn(gameID);
      }

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
