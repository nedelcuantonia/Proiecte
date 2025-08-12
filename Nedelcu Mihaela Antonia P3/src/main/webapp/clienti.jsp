
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html>
<head>
    <title>Gestionare Clienți</title>
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
    <h1>Lista Clienților</h1>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nume</th>
                <th>Prenume</th>
                <th>Adresa</th>
                 <th>Email</th>
            </tr>
        </thead>
        <tbody>
            <%
    String tableContent = (String) request.getAttribute("tableContent");
    if (tableContent != null) {
        out.print(tableContent);
    }
             %>
                <tr>
                    <td>${client.clientID}</td>
                    <td>${client.nume}</td>
                    <td>${client.prenume}</td>
                    <td>${client.adresa}</td>
                    <td>${client.email}</td>
                    <td>
                        <!-- Formular pentru ștergere -->
                        <form action="clienti" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="clientID" value="${client.clientID}">
                            <button type="submit">Șterge</button>
                        </form>
                    </td>
                </tr>
        </tbody>
    </table>

    <h2>Adaugă un Client</h2>
    <!-- Formular pentru adăugare -->
    <form action="clienti" method="post">
        <input type="hidden" name="action" value="add">
        <label for="nume">Nume:</label>
        <input type="text" id="nume" name="nume" required>
        <br>
        <label for="prenume">Prenume:</label>
        <input type="text" id="prenume" name="prenume" required>
        <br>
        <label for="adresa">Adresa:</label>
        <input type="text" id="adresa" name="adresa" required>
        <br>
        <label for="email">Email:</label>
        <input type="text" id="email" name="email" required>
        <br>
        <button type="submit">Adaugă</button>
    </form>
</body>
</html>
