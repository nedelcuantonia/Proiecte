package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ProduseFurnizori;

public class ProduseFurnizoriDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/GestionareProduse";
    private static final String USER = "root"; 
    private static final String PASSWORD = "parola";

    // Metoda pentru obținerea tuturor legăturilor dintre produse și furnizori
    public List<ProduseFurnizori> getAll() {
        List<ProduseFurnizori> lista = new ArrayList<>();
        String query = """
            SELECT pf.produsID, p.numeprodus AS numeProdus, pf.furnizorID, f.numefurnizor AS numeFurnizor
            FROM ProduseFurnizari pf
            JOIN Produse p ON pf.produsID = p.produsID
            JOIN Furnizori f ON pf.furnizorID = f.furnizorID
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProduseFurnizori pf = new ProduseFurnizori();
                pf.setProdusID(rs.getInt("produsID"));
                pf.setNumeprodus(rs.getString("numeProdus"));
                pf.setFurnizorID(rs.getInt("furnizorID"));
                pf.setNumefurnizor(rs.getString("numeFurnizor"));
                lista.add(pf);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Adăugarea unei legături între produse și furnizori
    public void add(String numeProdus, String numeFurnizor) throws SQLException {
        String query = """
            INSERT INTO ProduseFurnizari (produsID, furnizorID)
            SELECT p.produsID, f.furnizorID
            FROM Produse p
            JOIN Furnizori f ON 1=1
            WHERE p.numeprodus = ? AND f.numefurnizor = ?
        """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Înregistrare driver
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, numeProdus);
            stmt.setString(2, numeFurnizor);
            stmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driverul MySQL nu a fost găsit!", e);
        }
    }

    // Ștergerea unei legături între produse și furnizori
    public void delete(int produsID, int furnizorID) throws SQLException {
        String query = "DELETE FROM ProduseFurnizori WHERE produsID = ? AND furnizorID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, produsID);
            stmt.setInt(2, furnizorID);
            stmt.executeUpdate();
        }
    }
}

