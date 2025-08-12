<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestionare Produse</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h1>Lista Produselor</h1>
    <table>
        <thead>
            <tr>
                <th>ID Produs</th>
                <th>Denumire</th>
                <th>Cantitate</th>
                <th>Acțiuni</th>
            </tr>
        </thead>
        <tbody>
        <%
    String tableContent = (String) request.getAttribute("tableContent");
    if (tableContent != null) {
        out.print(tableContent);
    }
       %>
</tbody>
        
    </table>
</body>
</html>

