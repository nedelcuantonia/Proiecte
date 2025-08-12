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
import java.sql.Statement; // 
import java.util.ArrayList;
import java.util.List;

import model.Produse;  // Asigură-te că importi corect model.Produse

@WebServlet("/produseuser")
public class ProduseServletuser extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = "jdbc:mysql://localhost:3306/GestionareProduse"; 
        String user = "root"; 
        String password = "parola"; 

        List<Produse> produse = new ArrayList<>();  // Folosește model.Produse, nu servlet.Produs
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            Connection con = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Produse");  // Folosește PreparedStatement
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Creează obiecte de tip Produse din pachetul model
                produse.add(new Produse(rs.getInt("ProdusID"), rs.getString("Denumire"), rs.getInt("Cantitate")));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setează lista de produse în atributul request
        request.setAttribute("produse", produse);
        request.getRequestDispatcher("userproduse.jsp").forward(request, response);
    }
}