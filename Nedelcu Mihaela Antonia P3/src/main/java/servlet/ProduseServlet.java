package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


// Model pentru Produs
class Produs {
    private int produsID;
    private String denumire;
    private int cantitate;

    // Constructor
    public Produs(int produsID, String denumire, int cantitate) {
        this.produsID = produsID;
        this.denumire = denumire;
        this.cantitate = cantitate;
    }

    // Getteri
    public int getProdusID() { return produsID; }
    public String getDenumire() { return denumire; }
    public int getCantitate() { return cantitate; }
}

//@WebServlet("/produse")
public class ProduseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configurare conexiune baza de date
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GestionareProduse";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "parola";

    // Metodă pentru preluarea produselor din baza de date
    private List<Produs> getProduseFromDatabase() throws Exception {
        List<Produs> produse = new ArrayList<>();
        String query = "SELECT produsID, denumire, cantitate FROM produse";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driverul MySQL nu a fost găsit.", e);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int produsID = rs.getInt("produsID");
                String denumire = rs.getString("denumire");
                int cantitate = rs.getInt("cantitate");
                produse.add(new Produs(produsID, denumire, cantitate));
            }
        }
        return produse;
    }

 // Adăugare produs în baza de date
    private void addProdusToDatabase(String denumire, int cantitate) throws SQLException {
        String sql = "INSERT INTO produse (denumire, cantitate) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, denumire);
            pstmt.setInt(2, cantitate);
            pstmt.executeUpdate();
        }
    }

    // Ștergere produs din baza de date
    private void deleteProdusFromDatabase(int produsID) throws SQLException {
        String sql = "DELETE FROM produse WHERE produsID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, produsID);
            pstmt.executeUpdate();
        }
    }

    // Actualizare produs în baza de date
    private void updateProdusInDatabase(int produsID, String denumire, int cantitate) throws SQLException {
        String sql = "UPDATE produse SET denumire = ?, cantitate = ? WHERE produsID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, denumire);
            pstmt.setInt(2, cantitate);
            pstmt.setInt(3, produsID);
            pstmt.executeUpdate();
        }
    }

    // Generare continut pentru tabel
    private String generateTableContent(List<Produs> produse) {
        StringBuilder tableContent = new StringBuilder();
        for (Produs produs : produse) {
            tableContent.append("<tr>")
                        .append("<td>").append(produs.getProdusID()).append("</td>")
                        .append("<td>").append(produs.getDenumire()).append("</td>")
                        .append("<td>").append(produs.getCantitate()).append("</td>")
                        .append("<td>")
                        .append("<form action='produse' method='get' style='display:inline;'>") // Folosim GET pentru a prepopula formularul
                        .append("<input type='hidden' name='produsID' value='").append(produs.getProdusID()).append("'>")
                        .append("<input type='hidden' name='denumire' value='").append(produs.getDenumire()).append("'>")
                        .append("<input type='hidden' name='cantitate' value='").append(produs.getCantitate()).append("'>")
                        .append("<button type='submit'>Modifică</button>")
                        .append("</form>")
                        .append("<form action='produse' method='post' style='display:inline;'>")
                        .append("<input type='hidden' name='action' value='delete'>")
                        .append("<input type='hidden' name='produsID' value='").append(produs.getProdusID()).append("'>")
                        .append("<button type='submit'>Șterge</button>")
                        .append("</form>")
                        .append("</td>")
                        .append("</tr>");
        }
        return tableContent.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<Produs> produse = getProduseFromDatabase();
            String tableContent = generateTableContent(produse);

         // Preluăm datele din URL pentru a le trimite la formular
            String produsID = request.getParameter("produsID");
            String denumire = request.getParameter("denumire");
            String cantitate = request.getParameter("cantitate");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Gestionare Produse</title></head>");
            out.println("<body>");
            out.println("<h1>Lista Produselor</h1>");
            out.println("<table border='1'>");
            out.println("<thead><tr><th>ID</th><th>Denumire</th><th>Cantitate</th><th>Acțiuni</th></tr></thead>");
            out.println("<tbody>");
            out.println(tableContent);
            out.println("</tbody>");
            out.println("</table>");

            out.println("<h2>Adaugă un Produs</h2>");
            out.println("<form action='produse' method='post'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("<label>Denumire: <input type='text' name='denumire' required></label><br>");
            out.println("<label>Cantitate: <input type='number' name='cantitate' required></label><br>");
            out.println("<button type='submit'>Adaugă</button>");
            out.println("</form>");
            out.println("<h2>Modifică un Produs</h2>");
            out.println("<form action='produse' method='post'>");
            out.println("<input type='hidden' name='action' value='edit'>");
            out.println("<input type='hidden' name='produsID' value='" + produsID + "'>");
            out.println("<label>Denumire: <input type='text' name='denumire' value='" + denumire + "' required></label><br>");
            out.println("<label>Cantitate: <input type='number' name='cantitate' value='" + cantitate + "' required></label><br>");
            out.println("<button type='submit'>Modifica</button>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            throw new ServletException("Eroare la preluarea produselor", e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                String denumire = request.getParameter("denumire");
                int cantitate = Integer.parseInt(request.getParameter("cantitate"));
                addProdusToDatabase(denumire, cantitate);
            } else if ("delete".equals(action)) {
                int produsID = Integer.parseInt(request.getParameter("produsID"));
                deleteProdusFromDatabase(produsID);
            } else if ("edit".equals(action)) {
                int produsID = Integer.parseInt(request.getParameter("produsID"));
                String denumire = request.getParameter("denumire");
                String cantitateParam = request.getParameter("cantitate");

                if (denumire == null || cantitateParam == null) {
                    throw new ServletException("Valori lipsă pentru denumire sau cantitate");
                }

                int cantitate = Integer.parseInt(cantitateParam);
                //int cantitate = Integer.parseInt(request.getParameter("cantitate"));
                updateProdusInDatabase(produsID, denumire, cantitate);
            }
        } catch (SQLException e) {
            throw new ServletException("Eroare la procesarea operației", e);
        }

        response.sendRedirect("produse");
    }
}
