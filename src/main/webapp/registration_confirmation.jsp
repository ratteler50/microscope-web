<%@page import="org.apache.shiro.authc.credential.DefaultPasswordService" %>
<%@ page import="org.apache.shiro.authc.credential.PasswordService" %>

<%
    PasswordService svc = new DefaultPasswordService();
    String username = (String) session.getAttribute("username");
    String pw_plain = (String) session.getAttribute("password");
    String email = (String) session.getAttribute("email");
    String pw_hash = svc.encryptPassword(pw_plain);
    String role = "Player";

    int userID = model.RegistrationModel.userAdd(username, pw_hash, email);
%>
<html>
<head>
    <title>Registration Confirmation</title>
    <meta http-equiv="refresh" content="1; url=index.jsp">
</head>
<body>
You have successfully registered!
</body>
</html>