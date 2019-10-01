<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import = "com.microsoft.aad.oidcpoc.*"%>

<% if (AuthHelper.isAuthenticated(request)) { 
String userName = AuthHelper.getPrincipalName(request);
if (userName == null) userName = "N/A";
%>
    <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><%= userName %> <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a href="/secure/profile">View Profile</a></li>
            </ul>
        </li>
        <li>
        	<a href="/logout">Sign out</a>
        </li>
    </ul>
<% } else { %>
    <ul class="nav navbar-nav navbar-right">
    	<li><a href="/login">Sign In</a></li>
    </ul>
<% } %>