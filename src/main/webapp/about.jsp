<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file = "/_header.jsp" %>

<div class="panel panel-primary" style="margin-top:20px;">
    <div class="panel-title panel-heading">
        About
    </div>
    <div class="panel-body">
			<div class="col-sm-12">
			<p>This proof of concept builds on the OIDC Java sample hosted at <a target="_blank" href="https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect">https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect</a>.
			The purpose is to demonstrate a pattern (using the adal4j library) for integrating Azure AD authentication with a 
			legacy authentication system, and illustrating one approach to migrating users from the legacy auth system over to
			Azure AD.</p>
			<p>IMPORTANT: the "legacy auth system" articulated here is in NO WAY meant to model a best practice for legacy
			authentication. The database table, hosted in an Azure SQL database, includes a CLEAR TEXT password. Surprisingly,
			there are still systems running today using such a process but it is unwise in the extreme and should be considered
			an anti-pattern.</p>
			</div>
	</div>
</div>

<%@ include file = "/_footer.jsp" %>