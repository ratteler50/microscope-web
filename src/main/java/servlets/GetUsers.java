package servlets;

import db.DbException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import model.DatabaseReads;

/** Servlet that gets a list of all users */
public final class GetUsers extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Inject
  public GetUsers() {}

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    CachedRowSet crsUsers = null;
    try {
      crsUsers = DatabaseReads.getAllUsers();
    } catch (DbException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    request.setAttribute("allUsers", crsUsers);

    RequestDispatcher dispatch = request.getRequestDispatcher("pickPlayers.jsp");
    dispatch.forward(request, response);
  }

  /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
