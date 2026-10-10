package com.shopsphere.servlet;

import com.shopsphere.exception.InvalidLoginException;
import com.shopsphere.model.User;
import com.shopsphere.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            User user = userService.login(email, password);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h2>Login successful!</h2>"
            );

            response.getWriter().println(
                    "<p>Welcome, " + user.getName() + "</p>"
            );

        } catch (InvalidLoginException e) {

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h2>Login failed!</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}