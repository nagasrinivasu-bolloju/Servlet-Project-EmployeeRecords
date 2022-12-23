<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>login form</title>
<style>
	body{
	background-color:rgba(0,150,150,0.4);}
	
	.container{
	margin-top:70px;
	margin-left:470px;
	}
	
	.login-button{
	background-color:orange;
	border:0px;
	border-radius:3px;
	font-weight:400;
	font-size:15px;
	width:80%;
	margin-left:10px;
	margin-top:10px;
	height:30px;
	box-shadow:0px 4px 40px 0 rgba(0,0,0,0.2),0px 6px 10px 0 rgba(0,0,0,0.19);}
	
	.login-button:hover{
background-color:rgba(0,0,0,0.7);
color:orange;
box-shadow:0px 4px 80px 0 rgba(0,0,0,0.2),0px 6px 40px 0 rgba(0,0,0,0.19);
}
</style>
</head>
<body>
<div class="container">
<form action="login" method="post">
	<table>
	<tr>
	<td><label>Admin name:</label></td><td><input type="text" name="admin"></td><br>
	</tr>
	<tr>
	<td><label>Password:</label></td><td><input type="password" name="pass"></td><br>
	</tr>
	<tr>
	<td></td>
	<td><input type="submit" value="Log in" class="login-button"></td>
	</tr>
	</table>
</form>
</div>
</body>
</html>