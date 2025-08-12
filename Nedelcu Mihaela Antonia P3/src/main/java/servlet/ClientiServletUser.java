package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Clienti; // Importă clasa Clienti

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/clientiuser")
public class ClientiServletUser extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = "jdbc:mysql://localhost:3306/GestionareProduse"; 
        String user = "root"; 
        String password = "parola"; 

        List<Clienti> clienti = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Clienti");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Creează obiecte de tip Clienti
                clienti.add(new Clienti(
                    rs.getInt("ClientID"),
                    rs.getString("Nume"),
                    rs.getString("Prenume"),
                    rs.getString("Adresa"),
                    rs.getString("Email")
                ));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setează lista de clienți în atributul request
        request.setAttribute("clienti", clienti);
        request.getRequestDispatcher("userclienti.jsp").forward(request, response);
    }
}
