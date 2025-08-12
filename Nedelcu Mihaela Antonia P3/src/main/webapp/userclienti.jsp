<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Clienti" %>  <!-- Importă corect clasa Clienti -->

<!DOCTYPE html>
<html>
<head>
    <title>Lista Clienti</title>
</head>
<body>
    <h2>Lista Clienti</h2>
    
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Nume</th>
            <th>Prenume</th>
            <th>Adresa</th>
            <th>Email</th>
        </tr>
        <%
            // Preluăm lista de clienți din request
            List<Clienti> clienti = (List<Clienti>) request.getAttribute("clienti");

            // Verificăm dacă lista nu este goală
            if (clienti != null && !clienti.isEmpty()) {
                for (Clienti c : clienti) { 
        %>
            <tr>
                <td><%= c.getClientID() %></td>
                <td><%= c.getNume() %></td>
                <td><%= c.getPrenume() %></td>
                <td><%= c.getAdresa() %></td>
                <td><%= c.getEmail() %></td>
            </tr>
        <% 
                } 
            } else { 
        %>
            <tr>
                <td colspan="5">Nu există clienți disponibili.</td>
            </tr>
        <% 
            } 
        %>
    </table>
</body>
</html>
