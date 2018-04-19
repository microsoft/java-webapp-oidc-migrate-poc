<%@ page import = "com.microsoft.aad.oidcpoc.*"%>
<%@ page import = "com.microsoft.aad.adal4j.*"%>
<%@ page import = "java.util.*"%>
<%@ include file = "/_header.jsp" %>

<div class="col-sm-4">
<h4>User Info</h4>
<% 
DBUser user = AuthHelper.GetSessionProfile(request);
if (user==null){ %>
	Your account has not been linked to the system yet.
<% } else { %>
<table class="table">
<tr>
	<td>UserID</td>
	<td><%= user.UserId %></td>
</tr>
<tr>
	<td>Email</td>
	<td><%= user.Email %></td>
</tr>
<tr>
	<td>UniqueId</td>
	<td><%= user.UniqueId %></td>
</tr>
<tr>
	<td>FName</td>
	<td><%= user.FName %></td>
</tr>
<tr>
	<td>LName</td>
	<td><%= user.LName %></td>
</tr>
</table>

<% } %>
</div>
<div class="col-sm-4">
<h4>Claims Info</h4>
<% 
AuthenticationResult res = AuthHelper.getAuthSessionObject(request);
if (res.getAccessTokenType() != AuthHelper.ACCESS_TOKEN_LOCAL) {
	Map<String, Object> claims = AuthHelper.GetClaims(res.getIdToken()); %>
<table class="table">
<tr>
	<th>Claim Name</th>
	<th>Claim Value</th>
</tr>
<% for (Map.Entry<String, Object> claim : claims.entrySet()) { %>
<tr>
	<td><%= claim.getKey() %></td>
	<td><%= claim.getValue() %></td>
</tr>
<% } %>

</table>
	
<% } else { %>
	You were authenticated locally and don't have any claims to display.

<% } %>
</div>


<%@ include file = "/_footer.jsp" %>
	