<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file = "_header.jsp" %>

<div class="panel panel-primary" style="margin-top:20px;">
    <div class="panel-title panel-heading">
        Login
    </div>
    <div class="panel-body">
			<div class="col-sm-6">
			<div class="alert alert-warning" style="display:none">${error}</div>
			<%@ include file = "_loginForm.jsp" %>
			</div>
			<div class="col-sm-6" style="text-align:center;margin-top:30px;">
				<p>Login using a work account</p>
				<button class="btn btn-primary" id="btnAAD">Azure Active Directory</button>
			</div>
	</div>
</div>
<script type="text/javascript">
$(function() {
	$("#btnAAD").on("click", function() {
		location.href="/secure/aad";
	});
	if ($("div.alert.alert-warning").html().length>0) {
		$("div.alert.alert-warning").css({"display":"block"});
	}
	$("#j_username").focus();	
});
</script>

<%@ include file = "_footer.jsp" %>
