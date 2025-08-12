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


// Model pentru Furnizor
class Furnizor {
    private int furnizorID;
    private String nume;
    private String adresa;

    // Constructor
    public Furnizor(int furnizorID, String nume, String adresa) {
        this.furnizorID = furnizorID;
        this.nume = nume;
        this.adresa=  adresa;
    }

    // Getteri
    public int getFurnizorID() { return furnizorID; }
    public String getNume() { return nume; }
    public String getAdresa() { return adresa; }
}

//@WebServlet("/furnizori")
public class FurnizoriServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configurare conexiune baza de date
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GestionareProduse";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "parola";

    // Metodă pentru preluarea furnizorilor din baza de date
    private List<Furnizor> getFurnizoriFromDatabase() throws Exception {
        List<Furnizor> furnizori = new ArrayList<>();
        String query = "SELECT furnizorID, nume, adresa FROM furnizori";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driverul MySQL nu a fost găsit.", e);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int furnizorID = rs.getInt("furnizorID");
                String nume = rs.getString("nume");
                String adresa = rs.getString("adresa");
                furnizori.add(new Furnizor(furnizorID, nume, adresa));
            }
        }
        return furnizori;
    }
 // Adăugare furnizor în baza de date
    private void addFurnizorToDatabase(String nume, String adresa) throws SQLException {
        String sql = "INSERT INTO furnizori (nume, adresa) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nume);
            pstmt.setString(2, adresa);
            pstmt.executeUpdate();
        }
    }

    // Ștergere furnizor din baza de date
    private void deleteFurnizorFromDatabase(int furnizorID) throws SQLException {
        String sql = "DELETE FROM furnizori WHERE furnizorID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, furnizorID);
            pstmt.executeUpdate();
        }
    }
    
 // Actualizare furnizor în baza de date
    private void updateFurnizorInDatabase(int furnizorID, String nume, String adresa) throws SQLException {
        String sql = "UPDATE furnizori SET nume = ?, adresa = ? WHERE furnizorID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nume);
            pstmt.setString(2, adresa);
            pstmt.setInt(3, furnizorID);
            pstmt.executeUpdate();
        }
    }

    private String generateTableContent(List<Furnizor> furnizori) {
        StringBuilder tableContent = new StringBuilder();
        for (Furnizor furnizor : furnizori) {
            tableContent.append("<tr>")
                        .append("<td>").append(furnizor.getFurnizorID()).append("</td>")
                        .append("<td>").append(furnizor.getNume()).append("</td>")
                        .append("<td>").append(furnizor.getAdresa()).append("</td>")
                        .append("<td>")
                        .append("<form action='furnizori' method='get' style='display:inline;'>") // Folosim GET pentru a prepopula formularul
                        .append("<input type='hidden' name='furnizorID' value='").append(furnizor.getFurnizorID()).append("'>")
                        .append("<input type='hidden' name='nume' value='").append(furnizor.getNume()).append("'>")
                        .append("<input type='hidden' name='adresa' value='").append(furnizor.getAdresa()).append("'>")
                        .append("<button type='submit'>Modifică</button>")
                        .append("</form>")
                        .append("<form action='furnizori' method='post' style='display:inline;'>")
                        .append("<input type='hidden' name='action' value='delete'>")
                        .append("<input type='hidden' name='furnizorID' value='").append(furnizor.getFurnizorID()).append("'>")
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
            List<Furnizor> furnizori = getFurnizoriFromDatabase();
            String tableContent = generateTableContent(furnizori);

         // Preluăm datele din URL pentru a le trimite la formular
            String furnizorID = request.getParameter("furnizorID");
            String nume = request.getParameter("nume");
            String adresa = request.getParameter("adresa");
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Gestionare Furnizori</title></head>");
            out.println("<body>");
            out.println("<h1>Lista Furnizorilor</h1>");
            out.println("<table border='1'>");
            out.println("<thead><tr><th>ID</th><th>Nume</th><th>Adresă</th><th>Acțiuni</th></tr></thead>");
            out.println("<tbody>");
            out.println(tableContent);
            out.println("</tbody>");
            out.println("</table>");

            out.println("<h2>Adaugă un Furnizor</h2>");
            out.println("<form action='furnizori' method='post'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("<label>Nume: <input type='text' name='nume' required></label><br>");
            out.println("<label>Adresă: <input type='text' name='adresa' required></label><br>");
            out.println("<button type='submit'>Adaugă</button>");
            out.println("</form>");
            
            out.println("</form>");
            out.println("<h2>Modifică un Produs</h2>");
            out.println("<form action='furnizori' method='post'>");
            out.println("<input type='hidden' name='action' value='edit'>");
            out.println("<input type='hidden' name='furnizorID' value='" + furnizorID + "'>");
            out.println("<label>Nume: <input type='text' name='nume' value='" + nume + "' required></label><br>");
            out.println("<label>Adresa: <input type='text' name='adresa' value='" + adresa + "' required></label><br>");
            out.println("<button type='submit'>Modifica</button>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            throw new ServletException("Eroare la preluarea furnizorilor", e);
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
                String nume = request.getParameter("nume");
                String adresa = request.getParameter("adresa");
                addFurnizorToDatabase(nume, adresa);
            } else if ("delete".equals(action)) {
                int furnizorID = Integer.parseInt(request.getParameter("furnizorID"));
                deleteFurnizorFromDatabase(furnizorID);
            } else if ("edit".equals(action)) {
                int furnizorID = Integer.parseInt(request.getParameter("furnizorID"));
                String nume = request.getParameter("nume");
                String adresa = request.getParameter("adresa");
                
                if (nume == null || adresa == null) {
                    throw new ServletException("Valori lipsă pentru nume sau adresa");
                }

                //String adresa = Integer.toString(adresa1);
                //int adresa = Integer.parseInt(request.getParameter("adresa1"));
                updateFurnizorInDatabase(furnizorID, nume, adresa);
            }
        } catch (SQLException e) {
            throw new ServletException("Eroare la procesarea operației", e);
        }

        response.sendRedirect("furnizori");
    }
}

