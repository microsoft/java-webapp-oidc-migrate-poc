<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file = "/_header.jsp" %>

<div class="panel panel-warning" style="margin-top:20px;">
    <div class="panel-title panel-heading">
        NOTICE: Link Accounts
    </div>
    <div class="panel-body">
			<div class="col-sm-4">
			<div class="alert alert-warning" style="display:none">${error}</div>
			<%@ include file = "/_loginForm.jsp" %>
			</div>
			<div class="col-sm-4">
			<p>You have successfully authenticated to your work account for the first time. We now need you to log in one more time
			using your existing account so that the accounts will be linked. After this is completed, you will no longer be able to
			log in using your old account.</p>
			<p>Please login on the left.</p>
			</div>
	</div>
</div>
<script type="text/javascript">
$(function() {
	if ($("div.alert.alert-warning").html().length>0) {
		$("div.alert.alert-warning").css({"display":"block"});
	}
	$("#j_username").focus();	
});
</script>

<%@ include file = "/_footer.jsp" %>
