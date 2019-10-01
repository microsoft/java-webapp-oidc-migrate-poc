<%@ page import = "com.microsoft.aad.oidcpoc.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Auth Test</title>
	<%
	//forcing SSL here to work around Azure app service java hosting
	//https://stackoverflow.com/questions/30371122/azure-web-app-not-passing-https-scheme-to-java-web-application
	Boolean requireSsl = Boolean.valueOf(request.getSession().getServletContext().getInitParameter("require_ssl"));
	if (requireSsl) { %>
		<script type="text/javascript">
		if (location.protocol == "http:")
			location.href = "https://" + location.host + location.pathname + location.search + location.hash;
		</script>
	<% } %>

    <link rel="shortcut icon" href="https://appservice.azureedge.net/images/favicon.ico" type="image/x-icon">
	<link rel="stylesheet" href="//ajax.aspnetcdn.com/ajax/bootstrap/3.3.7/css/bootstrap.css"></link>
	<link rel="stylesheet" href="//ajax.aspnetcdn.com/ajax/bootstrap/3.3.7/css/bootstrap-theme.css"></link>
	<link rel="stylesheet" href="/resources/site.css"></link>
	<script type="text/javascript" src="//ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.js"></script>
	<script type="text/javascript" src="//ajax.aspnetcdn.com/ajax/bootstrap/3.3.7/bootstrap.js"></script>
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">AAD Auth Test</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                	<li><a href="/about">About</a>
                	<li><a href="/contact">Contact</a>
					<% if (AuthHelper.isAuthenticated(request)) { %>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Admin <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                        	<li><a href="/secure/stuff">My Stuff</a></li>
                        </ul>
                    </li>
                    <% } %>
                </ul>
                <%@ include file = "_login.jsp" %>
            </div>
        </div>
    </div>

    <div class="container body-content">    
    
