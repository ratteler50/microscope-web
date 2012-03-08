<%@ include file="include.jsp" %>
<%@page
        import="java.util.List, objects.GameOverview" %>

<html>
<head>
    <title>Which Game?</title>
</head>
<body>
<shiro:user>
    Hello, <shiro:principal/>, (User ID: <shiro:principal type="java.lang.Integer"/>).
    <br>Please select a game you are in, or create a new game.<br>
</shiro:user>
<%
    List<GameOverview> overviews = (List<GameOverview>) (request.getAttribute("gameOverviews"));
    for (int i = 0; i < overviews.size(); i++) {
        String bigPicture = overviews.get(i).bigPicture;
        int gameID = overviews.get(i).id;
%>
<a href="PlayGame?gameID=<%=gameID%>"><%=bigPicture%> </br> </a>
<%
    }
%>
<br>
<a href="NewGame">Start A New Game</a>
</body>
</html>
