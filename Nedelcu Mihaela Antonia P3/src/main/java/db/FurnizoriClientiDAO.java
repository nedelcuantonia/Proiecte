package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.FurnizoriClienti;
import java.sql.Connection;
import java.sql.DriverManager;


public class FurnizoriClientiDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/GestionareProduse";
    private static final String USER = "root"; 
    private static final String PASSWORD = "parola"; 

    // Metoda pentru obținerea tuturor legăturilor dintre furnizori și clienți
    public List<FurnizoriClienti> getAll() {
        List<FurnizoriClienti> lista = new ArrayList<>();
        String query = """
            SELECT fc.furnizorID, f.nume AS furnizorNume, fc.clientID, c.nume AS clientNume
            FROM FurnizoriClienti fc
            JOIN Furnizori f ON fc.furnizorID = f.furnizorID
            JOIN Clienti c ON fc.clientID = c.clientID
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FurnizoriClienti fc = new FurnizoriClienti();
                fc.setFurnizorID(rs.getInt("furnizorID"));
                fc.setNumefurnizor(rs.getString("furnizorNume"));
                fc.setClientID(rs.getInt("clientID"));
                fc.setNumeclient(rs.getString("clientNume"));
                lista.add(fc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Adăugarea unei legături între furnizori și clienți
    public void add(String numeFurnizor, String numeClient) throws SQLException {
        String query = """
            INSERT INTO FurnizoriClienti (furnizorID, clientID)
            SELECT f.furnizorID, c.clientID
            FROM Furnizori f
            JOIN Clienti c ON 1=1
            WHERE f.nume = ? AND c.nume = ?
        """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Înregistrare driver
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, numeFurnizor);
            stmt.setString(2, numeClient);
            stmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driverul MySQL nu a fost găsit!", e);
        }
    }
    // Ștergerea unei legături între furnizori și clienți
    public void delete(int furnizorID, int clientID) throws SQLException {
        String query = "DELETE FROM FurnizoriClienti WHERE furnizorID = ? AND clientID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, furnizorID);
            stmt.setInt(2, clientID);
            stmt.executeUpdate();
        }
    }
}
