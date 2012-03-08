package servlets;

import static support.GameLogic.getCurrGameID;
import static support.GameLogic.getCurrUserID;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import objects.GameState;

/**
 * Servlet implementation class GetFreshGameState
 */
public class GetFreshGameState extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public GetFreshGameState() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    int gameID = getCurrGameID(request);

    // Add the gameID to the session
    request.getSession().setAttribute("gameID", gameID);
    GameState state = null;
    try {
      state = support.CollectGameData.populateGame(gameID);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    request.setAttribute("gameState", state);

    RequestDispatcher dispatch = request.getRequestDispatcher("game.jsp");
    dispatch.forward(request, response);

  }


  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int gameID = getCurrGameID(request);
    GameState state = null;
    try {
      state = support.CollectGameData.populateGame(gameID);
      state.userID = getCurrUserID();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Gson gson = new Gson();

    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    String jsonText = gson.toJson(state);
    writer.print(jsonText);
    writer.flush();
    return;
  }

}
