package com.shopsphere.servlet;

import com.shopsphere.model.User;
import com.shopsphere.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");


        // =====================================================
        // VALIDATION
        // =====================================================

        if (name == null || email == null || password == null
                || name.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"message\":\"Please fill in all fields.\"}"
            );

            return;
        }


        name = name.trim();
        email = email.trim();


        // =====================================================
        // LENGTH VALIDATION
        // =====================================================

        if (name.length() > 100
                || email.length() > 150
                || password.length() < 8
                || password.length() > 72) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"message\":\"Check your details. Password must be 8-72 characters.\"}"
            );

            return;
        }


        // =====================================================
        // CREATE USER
        // =====================================================

        User user = new User(
                0,
                name,
                email,
                password,
                "USER"
        );


        // =====================================================
        // SAVE USER
        // =====================================================

        try {

            boolean registered =
                    userService.register(user);


            if (registered) {

                response.setStatus(
                        HttpServletResponse.SC_OK);

                response.getWriter().write(
                        "{\"message\":\"Registration successful!\"}"
                );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                response.getWriter().write(
                        "{\"message\":\"Registration failed. User could not be saved.\"}"
                );
            }

        }


        // =====================================================
        // EMAIL ALREADY EXISTS
        // =====================================================

        catch (IllegalArgumentException e) {

            response.setStatus(
                    HttpServletResponse.SC_CONFLICT);

            response.getWriter().write(
                    "{\"message\":\"This email is already registered.\"}"
            );
        }


        // =====================================================
        // DATABASE ERROR
        // =====================================================

        catch (SQLException e) {

            getServletContext().log(
                    "Registration database error",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"message\":\"A database error occurred. Check the Tomcat console.\"}"
            );
        }
    }
}