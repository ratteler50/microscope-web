package servlets;

import static model.DatabaseUpdates.gameNextTurn;
import static model.DatabaseUpdates.legaciesAdd;
import static model.DatabaseUpdates.playerSetActionDone;
import static support.GameLogic.PICK_LEGACY;
import static support.GameLogic.currPlayerAndAction;
import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;
import static support.GameLogic.isPlayerActionDone;
import static support.LegacySupport.isAuction;

import db.DbException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.DatabaseReads;
import model.DatabaseUpdates;
import objects.Legacy;
import support.GameLogic;
import support.LegacySupport;
import support.Settings;

//TODO: Check Auction is correctly done

/**
 * Servlet implementation class SetLegacy
 */
public class SetLegacy extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public SetLegacy() {
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

    int[] turnRound = GameLogic.getTurnRound(gameID);
    int round = turnRound[1];
    boolean isAuction = isAuction(gameID, round);

    // VERIFY THE CORRECT PLAYER AND ACTION
    //TODO: Fix check for auction!
    if (!Settings.DEBUG) {
      int[] CPA = currPlayerAndAction(gameID);
      // If the current player and action are not valid, exit
      if (CPA[1] != PICK_LEGACY) {
        System.err.println("It is not time to set the legacy!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
      if (CPA[0] != userID && (!isAuction || isPlayerActionDone(gameID, userID))) {
        System.err.println("It is not your turn!");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"success\" : false}");
        writer.flush();
        return;
      }
    }

    String passString = request.getParameter("pass");
    boolean pass = Boolean.valueOf(passString);

    // Do this if it is not currently an auction (play new legacy or pass)
    if (!isAuction(gameID, round)) {
      if (pass) {
        try {
          List<Legacy> newLegacies = LegacySupport.copyLegacies(
              gameID, round);
          legaciesAdd(newLegacies);
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
       * Logic for adding a new legacy
			 */
      String legacy = request.getParameter("legacy");
      try {
        List<Legacy> newLegacies = LegacySupport.copyAndAddLegacy(
            gameID, round, userID, legacy);
        legaciesAdd(newLegacies);

        // This player cannot try to take from null
        playerSetActionDone(userID, gameID, true);

        // If noting was discarded, there is no auction and you can
        // just move to the next turn
        if (!isAuction(gameID, round)) {
          gameNextTurn(gameID);
        }

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
    } else {
      // You do not want the card up for grabs
      if (pass) {
        try {
          // Set the "action" flag to true for this player
          DatabaseUpdates.playerSetActionDone(userID, gameID, true);

          // If everyone has passed, end the auction (which moves on
          // to the next turn)
          if (DatabaseReads.actionAllDoneCheck(gameID)) {
            LegacySupport.endAuction(gameID, round);
          }

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
       * You want the card up for grabs. Replace it with your own
			 */
      try {
        LegacySupport.takeDiscarded(userID, gameID, round);

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
  }
}
