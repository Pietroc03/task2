package control;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DriverManagerConnectionPool;
import model.OrderModel;
import model.UserBean;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("j_email");
        String password = request.getParameter("j_password");
        String redirectedPage = "/loginPage.jsp";
        Boolean control = false;
        try {
            Connection con = DriverManagerConnectionPool.getConnection();
            String sql = "SELECT email, passwordUser, ruolo, nome, cognome, indirizzo, telefono, numero, intestatario, CVV FROM UserAccount";

            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                if (email.compareTo(rs.getString(1)) == 0) {
                    String hashedPassword = hashPassword(password);
                    if (hashedPassword.equals(rs.getString(2))) {
                        control = true;
                        UserBean registeredUser = new UserBean();
                        registeredUser.setEmail(rs.getString(1));
                        registeredUser.setNome(rs.getString(4));
                        registeredUser.setCognome(rs.getString(5));
                        registeredUser.setIndirizzo(rs.getString(6));
                        registeredUser.setTelefono(rs.getString(7));
                        registeredUser.setNumero(rs.getString(8));
                        registeredUser.setIntestatario(rs.getString(9));
                        registeredUser.setCvv(rs.getString(10));
                        registeredUser.setRole(rs.getString(3));
                        request.getSession().setAttribute("registeredUser", registeredUser);
                        request.getSession().setAttribute("role", registeredUser.getRole());
                        request.getSession().setAttribute("email", rs.getString(1));
                        request.getSession().setAttribute("nome", rs.getString(6));

                        OrderModel model = new OrderModel();
                        request.getSession().setAttribute("listaOrdini", model.getOrders(rs.getString(1)));

                        redirectedPage = "/index.jsp";
                        DriverManagerConnectionPool.releaseConnection(con);
                    }
                }
            }
        } catch (Exception e) {
            redirectedPage = "/loginPage.jsp";
        }
        if (!control) {
            request.getSession().setAttribute("login-error", true);
        } else {
            request.getSession().setAttribute("login-error", false);
        }
        response.sendRedirect(request.getContextPath() + redirectedPage);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hashedBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
