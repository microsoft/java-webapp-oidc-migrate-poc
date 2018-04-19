<%@ include file = "/_header.jsp" %>

	<h3>Current user</h3>
	<table>
		<tr>
			<td>uniqueId:</td>
			<td>${userInfo.uniqueId}</td>
		</tr>
		<tr>
			<td>displayableId:</td>
			<td>${userInfo.displayableId}</td>
		</tr>
		<tr>
			<td>givenName:</td>
			<td>${userInfo.givenName}</td>
		</tr>
		<tr>
			<td>familyName:</td>
			<td>${userInfo.familyName}</td>
		</tr>
		<tr>
			<td>identityProvider:</td>
			<td>${userInfo.identityProvider}</td>
		</tr>
	</table>
	<br>
	<ul>
		<li><a href="<%=request.getContextPath()%>/secure/aad?cc=1">Get
				new Access Token via Client Credentials</a></li>
	</ul>
	<ul>
		<li><a href="<%=request.getContextPath()%>/secure/aad?refresh=1">Get
				new Access Token via Refresh Token</a></li>
	</ul>
	<ul>
		<li><a href="<%=request.getContextPath()%>/index.jsp">Go Home</a></li>
	</ul>
<%@ include file = "/_footer.jsp" %>
