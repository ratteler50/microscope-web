package servlets;

import static support.GameLogic.getCurrUserID;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import objects.GameOverview;

/**
 * Servlet that gets a list of games a given user is in
 */
public class GetUserGames extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public GetUserGames() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int userID = getCurrUserID();

    List<GameOverview> gameOverviews = null;
    try {
      gameOverviews = support.CollectGameOverviewData
          .getGameOverview(userID);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    request.setAttribute("gameOverviews", gameOverviews);

    RequestDispatcher dispatch = request
        .getRequestDispatcher("pickGame.jsp");
    dispatch.forward(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

}
