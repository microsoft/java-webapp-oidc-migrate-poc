<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<p>Login using a local account</p>
<form method = "POST">
<div class="loginForm">
	<div class="row">
		<div class="col-sm-3">Login</div>
		<div class="col-sm-9">
		<input class="form-control" type = "text" name="j_username" id="j_username"></div>
	</div>
	<div class="row">
		<div class="col-sm-3">Password</div>
		<div class="col-sm-9"><input class="form-control" type = "password" name="j_password" id="j_password"></div>
	</div>
	<div class="row">
		<div class="col-sm-12"><input class="btn btn-primary pull-right" type = "submit" value = "Login"></div>
	</div>
</div>
</form>
