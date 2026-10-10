
package com.shopsphere.controller;

import com.shopsphere.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private void sendJson(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Messages below are fixed server-generated strings.
        response.getWriter().write(
            "{\"message\":\"" + message + "\"}"
        );
    }

    private String hashPassword(String password) throws Exception {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        );

        byte[] hash = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .getEncoded();

        return ITERATIONS + ":"
            + Base64.getEncoder().encodeToString(salt) + ":"
            + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || email == null || password == null
                || name.isBlank() || email.isBlank()
                || password.isBlank()) {
            sendJson(response, 400, "All fields are required.");
            return;
        }

        name = name.trim();
        email = email.trim().toLowerCase(java.util.Locale.ROOT);

        if (name.length() > 100 || email.length() > 150
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || password.length() < 8
                || password.length() > 128) {
            sendJson(response, 400,
                "Check your name, email, and password. "
                + "Password must be 8-128 characters.");
            return;
        }

        try {
            String passwordHash = hashPassword(password);

            String sql = """
                INSERT INTO users (full_name, email, password_hash)
                VALUES (?, ?, ?)
                """;

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement =
                     connection.prepareStatement(sql)) {

                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, passwordHash);

                statement.executeUpdate();
            }

            sendJson(response, 201,
                "Registration successful! You can now log in.");

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                sendJson(response, 409,
                    "This email may already be registered.");
            } else {
                getServletContext().log(
                    "Registration database error", e);
                sendJson(response, 500,
                    "Registration failed. Please try again.");
            }
        } catch (Exception e) {
            getServletContext().log(
                "Password hashing error", e);
            sendJson(response, 500,
                "Registration failed. Please try again.");
        }
    }
}