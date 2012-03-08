package model;

import db.DbConnect;
import db.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationModel {

  // Connect to the database
  protected static Connection connect() throws DbException {
    return DbConnect.connect();
  }

  /**
   * Checks if a given username is already in use
   */
  public static boolean isUserUnique(String username) {
    boolean userUnique = false;
    ;
    try {
      Connection conn = connect();

      String usernameCheck = "SELECT * FROM USERS WHERE user_name = ?";
      PreparedStatement pstmt = conn.prepareStatement(usernameCheck);
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();
      if (!rs.next()) {
        userUnique = true;
      }
      rs.close();
      pstmt.close();

      conn.close();
    } catch (Exception ex) {
      try {
        throw new DbException(ex);
      } catch (DbException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return userUnique;
  }

  /**
   * Checks if a given email is already in use
   */
  public static boolean isEmailUnique(String email) {
    boolean emailUnique = false;
    try {
      Connection conn = connect();

      String emailCheck = "SELECT * FROM USERS WHERE email = ?";
      PreparedStatement pstmt = conn.prepareStatement(emailCheck);
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (!rs.next()) {
        emailUnique = true;
      }
      rs.close();
      pstmt.close();

      conn.close();
    } catch (Exception ex) {
      try {
        throw new DbException(ex);
      } catch (DbException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return emailUnique;
  }

  /**
   * Adds a user to the database with a given email, username and password
   */
  public static int userAdd(String username, String password, String email)
      throws DbException {
    int userID;
    try {
      Connection conn = connect();
      conn.setAutoCommit(false);

      String insert = "INSERT INTO USERS(email, user_name, password) "
          + "values(?,?,?) RETURNING id";
      PreparedStatement pstmt = conn.prepareStatement(insert);
      pstmt.setString(1, email);
      pstmt.setString(2, username);
      pstmt.setString(3, password);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      userID = rs.getInt("id");
      rs.close();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
      conn.close();
    } catch (SQLException ex) {
      throw new DbException(ex);
    }
    return userID;
  }

  // /**
  // * Adds a user, along with their role, to the user-role table
  // *
  // * @param userID
  // * @param username
  // * @param role
  // * @return
  // * @throws DbException
  // */
  // public static int userRoleAdd(int userID, String username, String role)
  // throws DbException
  // {
  // int roleID;
  // try
  // {
  // Connection conn = connect();
  // conn.setAutoCommit(false);
  //
  // String insert = "INSERT INTO USER_ROLES (user_ref, user_name, role) "
  // + "values(?,?,?) RETURNING id";
  // PreparedStatement pstmt = conn.prepareStatement(insert);
  // pstmt.setInt(1, userID);
  // pstmt.setString(2, username);
  // pstmt.setString(3, role);
  // ResultSet rs = pstmt.executeQuery();
  // rs.next();
  // roleID = rs.getInt("id");
  // rs.close();
  // pstmt.close();
  //
  // conn.commit();
  // conn.setAutoCommit(true);
  // conn.close();
  // } catch (SQLException ex)
  // {
  // throw new DbException(ex);
  // }
  // return roleID;
  // }
}