<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <h2>Autentificare</h2>
    <form action="LoginServlet" method="post">
        <label>Username:</label>
        <input type="text" name="username" required>
        <br>
        <label>Parola:</label>
        <input type="password" name="parola" required>
        <br>
        <input type="submit" value="Login">
    </form>
</body>
</html>
