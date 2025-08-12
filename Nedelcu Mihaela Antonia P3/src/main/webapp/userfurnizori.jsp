<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Furnizori" %>  <!-- Importă corect clasa Furnizori -->

<!DOCTYPE html>
<html>
<head>
    <title>Lista Furnizori</title>
</head>
<body>
    <h2>Lista Furnizori</h2>
    
    <table border="1">
        <tr>
            <th>ID Furnizor</th>
            <th>Nume Furnizor</th>
            <th>Adresa</th>
        </tr>
        <%
            // Preluăm lista de furnizori din request
            List<Furnizori> furnizori = (List<Furnizori>) request.getAttribute("furnizori");

            // Verificăm dacă lista nu este goală
            if (furnizori != null && !furnizori.isEmpty()) {
                for (Furnizori f : furnizori) { 
        %>
            <tr>
                <td><%= f.getFurnizorID() %></td>
                <td><%= f.getNumeFurnizor() %></td>
                <td><%= f.getAdresa() %></td>
            </tr>
        <% 
                } 
            } else { 
        %>
            <tr>
                <td colspan="3">Nu există furnizori disponibili.</td>
            </tr>
        <% 
            } 
        %>
    </table>
</body>
</html>
