<%
    session.invalidate();
    response.sendRedirect(application.getContextPath());
%>
