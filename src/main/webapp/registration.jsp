<html>
<head><title>New Account</title></head>
<body>
<%
    // if user name is missing
    if ("1".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "Username required." %>
</font>
<% } // if password is missing
else if ("2".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "Password required." %>
</font>
<% } // if email is missing
else if ("3".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "Email required." %>
</font>
<% } // if password and retyped password don't match
else if ("4".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "Entered passwords do not match." %>
</font>
<% }
// if password too short
else if ("5".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "Password needs to be at least 6 characters." %>
</font>
<% } // if username already taken
else if ("6".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "This username is already taken." %>
</font>
<% } // if email already taken
else if ("7".equals(request.getParameter("error"))) { %>
<font COLOR="FF0000"><%= "This email has already been used to register." %>
</font>
<% }
%>

<form method="POST" action="registration_validation.jsp">
    <table>
        <tr>
            <td>Username</td>
            <td>
                <input maxlength="20" size="12" name="username" type="text"/>
            </td>
        </tr>
        <tr>
            <td>Password</td>
            <td>
                <input maxlength="20" size="12" name="password" type="password"/>
            </td>
        </tr>
        <tr>
            <td>Retype Password</td>
            <td>
                <input maxlength="20" size="12" name="password_confirmation" type="password"/>
            </td>
        </tr>
        <tr>
            <td>E-Mail</td>
            <td>
                <input maxlength="40" size="24" name="email" type="text"/>
            </td>
        </tr>
    </table>
    <input type="submit" value="Register"/>
</form>
</body>
</html>