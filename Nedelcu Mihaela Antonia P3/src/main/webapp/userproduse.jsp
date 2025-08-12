<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Produse" %>  <!-- Importă clasa Produse din pachetul model -->
<!DOCTYPE html>
<html>
<head>
    <title>Lista Produse</title>
</head>
<body>
    <h2>Lista Produse</h2>
    
    <table border="1">
        <tr><th>ID</th><th>Denumire</th><th>Cantitate</th></tr>
        <%
            // Preluăm lista de produse din request
            List<Produse> produse = (List<Produse>) request.getAttribute("produse");

            // Verificăm dacă lista nu este goală
            if (produse != null && !produse.isEmpty()) {
                for (Produse p : produse) { 
        %>
            <tr>
                <td><%= p.getId() %></td>
                <td><%= p.getDenumire() %></td>
                <td><%= p.getCantitate() %></td>
            </tr>
        <%  } 
            } else { 
        %>
            <tr>
                <td colspan="3">Nu există produse disponibile.</td>
            </tr>
        <% 
            } 
        %>
    </table>
</body>
</html>
