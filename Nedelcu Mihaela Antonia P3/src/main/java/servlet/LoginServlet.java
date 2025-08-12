package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String parola = request.getParameter("parola");

        try {
            // Conectare la baza de date
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/GestionareProduse", "root", "parola");

            // Verificare username și parola
            String sql = "SELECT UserID, Rol FROM Utilizatori WHERE Username = ? AND Parola = AES_ENCRYPT(?, 'cheie_secreta')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, parola);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("Rol");

                // Setăm sesiunea pentru utilizator
                HttpSession session = request.getSession(); 
                session.setAttribute("username", username);
                session.setAttribute("rol", rol);

                // Redirectare în funcție de rol
                if ("admin".equals(rol)) {
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendRedirect("user.jsp");
                }
            } else {
                response.sendRedirect("login.jsp?error=1");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=2");
        }
    }
}

