<%@ include file="include.jsp" %>
<html>
<head>

</head>
<body>

<h2>Please Log in</h2>

<shiro:guest>
    <p>Here are the default logins and passwords</p>


    <style type="text/css">
        table.sample {
            border-width: 1px;
            border-style: outset;
            border-color: blue;
            border-collapse: separate;
            background-color: rgb(255, 255, 240);
        }

        table.sample th {
            border-width: 1px;
            padding: 1px;
            border-style: none;
            border-color: blue;
            background-color: rgb(255, 255, 240);
        }

        table.sample td {
            border-width: 1px;
            padding: 1px;
            border-style: none;
            border-color: blue;
            background-color: rgb(255, 255, 240);
        }
    </style>


    <table class="sample">
        <thead>
        <tr>
            <th>Username</th>
            <th>Password</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>David</td>
            <td>123123</td>
        </tr>
        <tr>
            <td>Ben</td>
            <td>123123</td>
        </tr>
        <tr>
            <td>Garrett</td>
            <td>123123</td>
        </tr>
        <tr>
            <td>Sam</td>
            <td>123123</td>
        </tr>
        <tr>
            <td>Sean</td>
            <td>123123</td>
        </tr>
        </tbody>
    </table>
    <br/>
    <br/>
</shiro:guest>

<shiro:user>
    <jsp:forward page="/PickGame"/>
</shiro:user>

<form name="loginform" action="" method="post">
    <table align="left" border="0" cellspacing="0" cellpadding="3">
        <tr>
            <td>Username:</td>
            <td><input type="text" name="username" maxlength="30"></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="password" maxlength="30"></td>
        </tr>
        <tr>
            <td colspan="2" align="left"><input type="hidden"
                                                name="rememberMe" value="true"></td>
        </tr>
        <tr>
            <td colspan="2" align="right"><input type="submit"
                                                 name="submit" value="Login"></td>
        </tr>
    </table>
</form>
<br clear="left"/>
<a href="/registration.jsp">Register as a new user</a>
</body>
</html>