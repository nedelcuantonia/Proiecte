package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import db.FurnizoriClientiDAO;
import model.FurnizoriClienti;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


//@WebServlet("/furnizoriClienti")
public class FurnizoriclientiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FurnizoriClientiDAO dao = new FurnizoriClientiDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            List<FurnizoriClienti> lista = dao.getAll();
            
            // Pagina HTML
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Lista Furnizori-Clienti</title></head><body>");
            out.println("<h1>Legături Furnizori-Clienti</h1>");
            out.println("<table border='1'><tr><th>FurnizorID</th><th>ClientID</th><th>Nume Furnizor</th><th>Nume Client</th><th>Acțiuni</th></tr>");

            for (FurnizoriClienti fc : lista) {
                out.println("<tr>");
                out.println("<td>" + fc.getFurnizorID() + "</td>");
                out.println("<td>" + fc.getClientID() + "</td>");
                out.println("<td>" + fc.getNumefurnizor() + "</td>");
                out.println("<td>" + fc.getNumeclient() + "</td>");
                out.println("<td>");
                out.println("<form method='post' action='furnizoriClienti' style='display:inline;'>");
                out.println("<input type='hidden' name='action' value='delete'>");
                out.println("<input type='hidden' name='furnizorID' value='" + fc.getFurnizorID() + "'>");
                out.println("<input type='hidden' name='clientID' value='" + fc.getClientID() + "'>");
                out.println("<button type='submit'>Șterge</button>");
                out.println("</form>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            // Formular pentru adăugare
            out.println("<h2>Adaugă Legătură</h2>");
            out.println("<form method='post' action='furnizoriClienti'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("Nume Furnizor: <input type='text' name='numeFurnizor' required><br>");
            out.println("Nume Client: <input type='text' name='numeClient' required><br>");
            out.println("<button type='submit'>Adaugă</button>");
            out.println("</form>");

            out.println("</body></html>");
        }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                String numeFurnizor = request.getParameter("numeFurnizor");
                String numeClient = request.getParameter("numeClient");
                dao.add(numeFurnizor, numeClient);
            } else if ("delete".equals(action)) {
                int furnizorID = Integer.parseInt(request.getParameter("furnizorID"));
                int clientID = Integer.parseInt(request.getParameter("clientID"));
                dao.delete(furnizorID, clientID);
            }
        } catch (SQLException e) {
            throw new ServletException("Eroare la procesarea operației", e);
        }
        response.sendRedirect("furnizoriClienti");
    }
}
