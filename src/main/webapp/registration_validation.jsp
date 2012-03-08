<%
    int error = 0; // no error currently found
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String password_confirmation = request
            .getParameter("password_confirmation");
    String email = request.getParameter("email");

    boolean uniqueUN = model.RegistrationModel.isUserUnique(username);
    boolean uniqueEmail = model.RegistrationModel.isEmailUnique(email);

    if (username.isEmpty()) {
        error = 1; // if user name is missing
    } else if (password.isEmpty()) {
        error = 2; // if password is missing
    } else if (email.isEmpty()) {
        error = 3; // if email is missing
    } else if (!password.equals(password_confirmation)) {
        error = 4; // if password and retyped password don't match
    }
/* 	else if (password.length() < 6)
		error = 5; // if password too short */
    else if (!uniqueUN) {
        error = 6; // if username already taken
    } else if (!uniqueEmail) {
        error = 7; // if email already taken
    } else

    {
        // no error was found, store information for use on next page
        session.setAttribute("username", username);
        session.setAttribute("password", password);
        session.setAttribute("email", email);
    }
%>
<html>
<head>
    <title>Registration Validation</title>
    <%
        if (error == 0) {
    %>
    <meta http-equiv="refresh"
          content="1; url=registration_confirmation.jsp">
    <title>Applicant Registration</title>
    <%
    } else {
    %>
    <meta http-equiv="refresh"
          content="1; url=registration.jsp?error=<%=error%>">
    <%
        }
    %>
</head>
</html>