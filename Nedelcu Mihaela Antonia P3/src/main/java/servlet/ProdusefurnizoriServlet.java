package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import db.ProduseFurnizoriDAO;
import model.ProduseFurnizori;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

//@WebServlet("/produseFurnizori")
public class ProdusefurnizoriServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProduseFurnizoriDAO dao = new ProduseFurnizoriDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            List<ProduseFurnizori> lista = dao.getAll();

            // Pagina HTML
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Lista Produse-Furnizori</title></head><body>");
            out.println("<h1>Legături Produse-Furnizori</h1>");
            out.println("<table border='1'><tr><th>ProdusID</th><th>FurnizorID</th><th>Nume Produs</th><th>Nume Furnizor</th><th>Acțiuni</th></tr>");

            for (ProduseFurnizori pf : lista) {
                out.println("<tr>");
                out.println("<td>" + pf.getProdusID() + "</td>");
                out.println("<td>" + pf.getFurnizorID() + "</td>");
                out.println("<td>" + pf.getNumeprodus() + "</td>");
                out.println("<td>" + pf.getNumefurnizor() + "</td>");
                out.println("<td>");
                out.println("<form method='post' action='produseFurnizori' style='display:inline;'>");
                out.println("<input type='hidden' name='action' value='delete'>");
                out.println("<input type='hidden' name='produsID' value='" + pf.getProdusID() + "'>");
                out.println("<input type='hidden' name='furnizorID' value='" + pf.getFurnizorID() + "'>");
                out.println("<button type='submit'>Șterge</button>");
                out.println("</form>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            // Formular pentru adăugare
            out.println("<h2>Adaugă Legătură</h2>");
            out.println("<form method='post' action='produseFurnizori'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("Nume Produs: <input type='text' name='numeProdus' required><br>");
            out.println("Nume Furnizor: <input type='text' name='numeFurnizor' required><br>");
            out.println("<button type='submit'>Adaugă</button>");
            out.println("</form>");

            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                String numeProdus = request.getParameter("numeProdus");
                String numeFurnizor = request.getParameter("numeFurnizor");
                dao.add(numeProdus, numeFurnizor);
            } else if ("delete".equals(action)) {
                int produsID = Integer.parseInt(request.getParameter("produsID"));
                int furnizorID = Integer.parseInt(request.getParameter("furnizorID"));
                dao.delete(produsID, furnizorID);
            }
        } catch (SQLException e) {
            throw new ServletException("Eroare la procesarea operației", e);
        }
        response.sendRedirect("produseFurnizori");
    }
}
