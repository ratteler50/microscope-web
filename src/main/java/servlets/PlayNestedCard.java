package servlets;

import static model.DatabaseUpdates.eventAdd;
import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.sceneAdd;
import static model.DatabaseUpdates.sceneSetSetting;
import static support.GameLogic.EVENT;
import static support.GameLogic.PLAY_NESTED_ES;
import static support.GameLogic.SCENE;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;

import db.DbException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import model.DatabaseReads;
import support.GameLogic;
import support.Settings;

/** Servlet implementation class PlayNestedCard */
public class PlayNestedCard extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public PlayNestedCard() {}

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
      } else if (CPA[1] != PLAY_NESTED_ES) {
        System.err.println("It is not time to paly a nested E/S!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    int newCardID = -1;
    int parentPES = -1;
    int parentID = -1;

    int[] turnRound = GameLogic.getTurnRound(gameID);
    int turn = turnRound[0];
    int round = turnRound[1];

    String passString = request.getParameter("pass");
    boolean pass = Boolean.valueOf(passString);

    if (pass) {
      try {
        gameNextTurn(gameID);
      } catch (DbException e) {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : true}");
      writer.flush();

      return;
    }

    /*
     * Logic for playing nested card below
     */
    CachedRowSet rowSet;

    try {
      rowSet = DatabaseReads.getGameState(gameID);
      rowSet.next();
      parentPES = rowSet.getInt("last_pes");
      parentID = rowSet.getInt("last_card");

    } catch (Exception e) {
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      return;
      // e.printStackTrace();
    }

    // You are playing a nested card
    int pes = parentPES + 1;

    int position = 0;

    String toneString = request.getParameter("tone");
    boolean tone = Boolean.valueOf(toneString);

    String text = request.getParameter("text");
    String description = request.getParameter("description");

    try {
      if (pes == EVENT) {
        newCardID = eventAdd(userID, parentID, position, turn, round, text, description, tone);

        gameNextTurn(gameID);
      } else if (pes == SCENE) {
        String dictatedString = request.getParameter("dictated");
        boolean dictated = Boolean.valueOf(dictatedString);

        String scene = request.getParameter("scene");
        newCardID =
            sceneAdd(userID, parentID, position, turn, round, text, scene, "", "", dictated);
        // Add the setting for the scene
        sceneSetSetting(newCardID, scene);
      }

    } catch (Exception e) {
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      return;
      // e.printStackTrace();
    }

    System.err.println(newCardID);
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.print("{\"success\" : true, \"id\" : " + newCardID + "}");
    writer.flush();
  }
}
