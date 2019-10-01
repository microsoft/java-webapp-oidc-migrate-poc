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
			<div class="col-sm-12">
			<h4>Usage</h4>
			<ul>
				<li>Local users are listed in the [User] table in the database. Log in using a name and password to continue
				working using a local account. (Note: if you have deployed this from scratch, you will need to populate this table manually.)</li>
				<li>Click "Azure Active Directory" to login using an Azure AD work account. On first login, a consent form will display
				ensuring the user can authorize the app for their own tenant.</li>
				<li>After authenticating to Azure AD, the user is returned to the web app. The app will notice that the user hasn't linked
				their Azure AD account to a local account and will prompt them to do so.</li>
				<li>The user will login a final time using their legacy, local credentials.</li>
				<li>This app will update the local database record with the unique ID from Azure AD. It will then scramble the password on the
				local account so the user can't login there again.</li>
				<li>Going forward, the user will authenticate to Azure AD but still have all the relevant information available in their session
				from their local user account.</li>
			</ul>
			</div>
	</div>
</div>

<%@ include file = "/_footer.jsp" %>