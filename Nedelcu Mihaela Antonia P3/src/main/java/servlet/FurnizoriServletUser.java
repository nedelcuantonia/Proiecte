package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Furnizori; // Importă clasa Furnizori

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/furnizoriuser")
public class FurnizoriServletUser extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = "jdbc:mysql://localhost:3306/GestionareProduse"; 
        String user = "root"; 
        String password = "parola"; 

        List<Furnizori> furnizori = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Furnizori");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Creează obiecte de tip Furnizori
                furnizori.add(new Furnizori(
                    rs.getInt("FurnizorID"),
                    rs.getString("NumeFurnizor"),
                    rs.getString("Adresa")
                ));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setează lista de furnizori în atributul request
        request.setAttribute("furnizori", furnizori);
        request.getRequestDispatcher("userfurnizori.jsp").forward(request, response);
    }
}

