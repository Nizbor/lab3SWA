import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/giveMeUsers")
public class UserList extends HttpServlet {
    private DataSource ds;
    private void getDataSource() throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        ds = (DataSource) envCtx.lookup("jdbc/simplewebapp");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            getDataSource();
            Connection con = ds.getConnection();
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT id, login, password, email, role FROM users");

            PrintWriter out = resp.getWriter();
            resp.setContentType("text/html");
            out.write("<html>");
            out.write("<body>");

            out.write("<h3>Database Records</h3>");
            while(rs.next())  {
                out.write("id: " + rs.getInt("id") + " ");
                out.write("login: " + rs.getString("login") + " ");
                out.write("password: " + rs.getString("password") + " ");
                out.write("mail: " + rs.getString("email") + " ");
                out.write("role: " + rs.getString("role") + " ");
                out.write("</br>");
            }


        } catch (SQLException | NamingException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Exception in closing DB resources");
            }
        }
    }
}