package org.servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet("/register")
public class Register extends HttpServlet {
    public Register() {
    }
    private DataSource ds;
    private void getDataSource() throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        ds = (DataSource) envCtx.lookup("jdbc/simplewebapp");
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("pass");
        String email = request.getParameter("email");

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            getDataSource();
            Connection conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT id FROM users WHERE login = ? OR email = ? LIMIT 1");
            pstmt.setString(1, login);
            pstmt.setString(2, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                response.sendRedirect("register.jsp");
            } else {
                String str = String.format("INSERT INTO users (login, password, email) VALUES ('%s', '%s', '%s')", login, pass, email);
                pstmt.execute(str);
                response.sendRedirect("welcome.jsp");
            }

        } catch (SQLException | NamingException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Exception in closing DB resources");
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}