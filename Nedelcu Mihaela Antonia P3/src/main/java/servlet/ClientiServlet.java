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


// Model pentru Client
class Client {
    private int clientID;
    private String nume;
    private String prenume;
    private String adresa;
    private String email;

    public Client(int clientID, String nume,String prenume, String adresa, String email) {
        this.clientID = clientID;
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.email = email;
    }

    public int getClientID() { return clientID; }
    public String getNume() { return nume; }
    public String getPrenume() { return prenume; }
    public String getAdresa() { return adresa; }
    public String getEmail() { return email; }
}

//@WebServlet("/clienti")
public class ClientiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Configurare conexiune baza de date
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestionareproduse?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "parola";

    // Preluare lista de clienți din baza de date
    private List<Client> getClientiFromDatabase() throws Exception {
        List<Client> clienti = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driverul MySQL nu a fost găsit.", e);
        }
        String query = "SELECT clientID, nume,prenume, adresa, email FROM clienti";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientID = rs.getInt("clientID");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String adresa = rs.getString("adresa");
                String email = rs.getString("email");
                clienti.add(new Client(clientID, nume,prenume, adresa,email));
            }
        }
        return clienti;
    }

    // Adăugare client în baza de date
    private void addClientToDatabase(String nume, String prenume, String adresa, String email) throws SQLException {
        String sql = "INSERT INTO clienti (nume, prenume, adresa, email) VALUES (?, ?, ?, ?)";

        try (Connection conn =DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, adresa);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
        }
    }

    // Ștergere client din baza de date
    private void deleteClientFromDatabase(int clientID) throws SQLException {
        String sql = "DELETE FROM clienti WHERE clientID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientID);
            pstmt.executeUpdate();
        }
    }

    // Actualizare client în baza de date
    private void updateClientInDatabase(int clientID, String nume, String prenume, String adresa, String email) throws SQLException {
        String sql = "UPDATE clienti SET nume = ?, prenume = ?, adresa = ?, email = ? WHERE clientID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, adresa);
            pstmt.setString(4, email);
            pstmt.setInt(5, clientID);
            pstmt.executeUpdate();
        }
    }


   
    private String generateTableContent(List<Client> clienti) {
        StringBuilder tableContent = new StringBuilder();
        for (Client client : clienti) {
            tableContent.append("<tr>")
                        .append("<td>").append(client.getClientID()).append("</td>")
                        .append("<td>").append(client.getNume()).append("</td>")
                        .append("<td>").append(client.getPrenume()).append("</td>")
                        .append("<td>").append(client.getAdresa()).append("</td>")
                        .append("<td>").append(client.getEmail()).append("</td>")
                        .append("<td>")
                        .append("<form action='clienti' method='get' style='display:inline;'>")// Folosim GET pentru a prepopula formularul
                        .append("<input type='hidden' name='action' value='editForm'>")
                        .append("<input type='hidden' name='clientID' value='").append(client.getClientID()).append("'>")
                        .append("<input type='hidden' name='nume' value='").append(client.getNume()).append("'>")
                        .append("<input type='hidden' name='prenume' value='").append(client.getPrenume()).append("'>")
                        .append("<input type='hidden' name='adresa' value='").append(client.getAdresa()).append("'>")
                        .append("<input type='hidden' name='email' value='").append(client.getEmail()).append("'>")
                        .append("<button type='submit'>Modifică</button>")
                        .append("</form>")
                        .append("<form action='clienti' method='post' style='display:inline;'>")
                        .append("<input type='hidden' name='action' value='delete'>")
                        .append("<input type='hidden' name='clientID' value='").append(client.getClientID()).append("'>")
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
            List<Client> clienti = null;
			try {
				clienti = getClientiFromDatabase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String tableContent = generateTableContent(clienti);

         // Preluăm datele din URL pentru a le trimite la formular
            String action = request.getParameter("action");
            String clientID = request.getParameter("clientID");
            String nume = request.getParameter("nume");
            String prenume = request.getParameter("prenume");
            String adresa = request.getParameter("adresa");
            String email = request.getParameter("email");
            
            if ("editForm".equals(action)) {
                clientID = request.getParameter("clientID");
                nume = request.getParameter("nume");
                prenume = request.getParameter("prenume");
                adresa = request.getParameter("adresa");
                email = request.getParameter("email");
            }
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Gestionare Clienți</title></head>");
            out.println("<body>");
            out.println("<h1>Lista Clienților</h1>");
            out.println("<table border='1'>");
            out.println("<thead><tr><th>ID</th><th>Nume</th><th>Prenume</th><th>Adresa</th><th>Email</th><th>Acțiuni</th></tr></thead>");
            out.println("<tbody>");
            out.println(tableContent);
            out.println("</tbody>");
            out.println("</table>");

            out.println("<h2>Adaugă un Client</h2>");
            out.println("<form action='clienti' method='post'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("<label>Nume: <input type='text' name='nume' required></label><br>");
            out.println("<label>Prenume: <input type='text' name='prenume' required></label><br>");
            out.println("<label>Adresa: <input type='text' name='adresa' required></label><br>");
            out.println("<label>Email: <input type='email' name='email' required></label><br>");
            out.println("<button type='submit'>Adaugă</button>");
            out.println("</form>");
           
            out.println("<h2>Modifică un Client</h2>");
            out.println("<form action='clienti' method='post'>");
            out.println("<input type='hidden' name='action' value='update'>"); // Corectăm acțiunea
            out.println("<input type='hidden' name='clientID' value='" + clientID + "'>");
            out.println("<label>Nume: <input type='text' name='nume' value='" + nume + "' required></label><br>");
            out.println("<label>Prenume: <input type='text' name='prenume' value='" + prenume + "' required></label><br>");
            out.println("<label>Adresa: <input type='text' name='adresa' value='" + adresa + "' required></label><br>");
            out.println("<label>Email: <input type='email' name='email' value='" + email + "' required></label><br>");
            out.println("<button type='submit'>Modifica</button>");
            out.println("</form>");

            out.println("</body>");
            out.println("</html>");
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
                String prenume = request.getParameter("prenume");
                String adresa = request.getParameter("adresa");
                String email = request.getParameter("email");
                addClientToDatabase(nume, prenume, adresa, email);
            } else if ("delete".equals(action)) {
                int clientID = Integer.parseInt(request.getParameter("clientID"));
                deleteClientFromDatabase(clientID);
            } else if ("update".equals(action)) {
                int clientID = Integer.parseInt(request.getParameter("clientID"));
                String nume = request.getParameter("nume");
                String prenume = request.getParameter("prenume");
                String adresa = request.getParameter("adresa");
                String email = request.getParameter("email");
                
                if (nume == null || prenume == null ) {
                    throw new ServletException("Valori lipsă pentru nume sau prenume");
                    
                }
                if (nume == null || adresa == null ) {
                    throw new ServletException("Valori lipsă pentru nume sau adresa");
                    
                }
                if (nume == null || email == null ) {
                    throw new ServletException("Valori lipsă pentru nume sau email");
                    
                }
                updateClientInDatabase(clientID, nume, prenume, adresa, email);
            }
        } catch (SQLException e) {
            throw new ServletException("Eroare la procesarea operației", e);
        }

        // Redirecționează înapoi la lista clienților
        response.sendRedirect("clienti");
    }
}
