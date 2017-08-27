package servlets;

import static model.DatabaseReads.getSceneStepsLeft;
import static model.DatabaseUpdates.gameNextRound;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.sceneSetAnswer;
import static model.DatabaseUpdates.sceneSetTone;
import static model.DatabaseUpdates.sceneStepDone;
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
import support.GameLogic.ActiveGameState;
import support.GameLogic.PlayerAndAction;
import support.GameLogic.SceneCreationState;
import support.GameLogic.TurnRound;
import support.Settings;

/** Servlet implementation class SetSceneAnswer */
@SuppressWarnings("serial")
public final class SetSceneBannedRequired extends HttpServlet {

  @Inject
  public SetSceneBannedRequired() {}

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
      PlayerAndAction CPA = currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA.getPlayer() != userID) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      } else if (CPA.getAction() != ActiveGameState.PLAY_PES
          && CPA.getAction() != ActiveGameState.PLAY_NESTED_ES) {
        System.err.println("It is not time to pick who is banned/required from a scene!");
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
    if (!Settings.DEBUG
        && getSceneStepsLeft(cardID) != SceneCreationState.SCENE_ANSWER.getNumber()) {
      System.err.println("It is not time to paly the answer!");
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      return;
    }

    TurnRound turnRound = getTurnRound(gameID);

    String toneString = request.getParameter("tone");
    boolean tone = Boolean.valueOf(toneString);

    ActiveGameState currAction = getGameAction(gameID);

    String answer = request.getParameter("answer");
    try {
      sceneSetAnswer(cardID, answer);
      sceneSetTone(cardID, tone);
      sceneStepDone(cardID);

      if (currAction == ActiveGameState.PLAY_LEGACY_ES) {
        int newLens = GameLogic.findCurrentPlayerID(gameID, 0, turnRound.getRound() + 1);
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
