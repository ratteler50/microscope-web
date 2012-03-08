package servlets;

import static model.DatabaseUpdates.gameCreate;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SetFocus
 */
public class CreateGame extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public CreateGame() {
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
    // Convert the string list to an int list
    String[] usersInGameString = request.getParameterValues("players");
    int[] usersInGame = new int[usersInGameString.length];
    for (int i = 0; i < usersInGameString.length; i++) {
      usersInGame[i] = Integer.parseInt(usersInGameString[i]);
    }

    int newGameID = -1;

    try {
      newGameID = gameCreate(usersInGame);

    } catch (Exception e) {
      PrintWriter writer = response.getWriter();
      writer.print("{\"success\" : false}");
      writer.flush();
      e.printStackTrace();
      // TODO: Something better if this is the case. Maybe back to the previous page?
      //       Or back to picking a game?
    }

    System.err.println(newGameID);
//		response.sendRedirect("PlayGame?gameID=" + newGameID);
    RequestDispatcher dispatch = request
        .getRequestDispatcher("/PickGame");
    dispatch.forward(request, response);

  }

}
