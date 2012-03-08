<%@ include file="include.jsp" %>
<%@page
        import="javax.sql.RowSet, support.GameLogic" %>

<html>
<head>
    <title>Who Is Playing?</title>
</head>
<body>
<shiro:user>
    Hello, <shiro:principal/>, (User ID: <shiro:principal
        type="java.lang.Integer"/>).
    <br>Please select the players you would like in your game.<br>
    <br>
</shiro:user>

<form action="PickBigPicture" method="POST">
    <%
        //Game Creator's UserID
        int myID = GameLogic.getCurrUserID();
    %>
    <!-- Game creator should be first lens -->
    <input type="hidden" name="players" value="<%=myID%>"/>

    <%
        RowSet users = (RowSet) (request.getAttribute("allUsers"));
        while (users.next()) {
            String username = users.getString("user_name");
            int
                    userID = users.getInt("id");
            if (userID != myID) // Other Users
            {
    %>
    <input type="checkbox" name="players" value="<%=userID%>"><%=username%><br>
    <%
    } else {
    %>
    <!-- visual dummy for the creating user -->
    <input type="checkbox" name="players" value="<%=userID%>" checked
           disabled><%=username%><br>
    <%
            }
        }
    %>
    <input type="submit" value="Pick Big Picture"/>
</form>
</body>
</html>
