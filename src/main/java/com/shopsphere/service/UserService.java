
package com.shopsphere.service;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.exception.InvalidLoginException;
import com.shopsphere.model.User;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // Register a new user
    public boolean register(User user) throws SQLException {

        if (user == null ||
            user.getName() == null || user.getName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {

            throw new IllegalArgumentException(
                "Name, email, and password are required."
            );
        }

        String email = user.getEmail().trim().toLowerCase();
        user.setEmail(email);

        // Check whether the email is already registered
        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException(
                "This email is already registered."
            );
        }

        // Hash the password before saving it
        String hashedPassword = BCrypt.hashpw(
            user.getPassword(),
            BCrypt.gensalt(12)
        );

        user.setPassword(hashedPassword);

        // Assign the default role
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }

        return userDAO.saveUser(user);
    }

    // Login an existing user
    public User login(String email, String password)
            throws InvalidLoginException {

        if (email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {

            throw new InvalidLoginException(
                "Invalid email or password."
            );
        }

        try {
            User user = userDAO.findByEmail(
                email.trim().toLowerCase()
            );

            if (user == null ||
                user.getPassword() == null ||
                !user.getPassword().startsWith("$2") ||
                !BCrypt.checkpw(password, user.getPassword())) {

                throw new InvalidLoginException(
                    "Invalid email or password."
                );
            }

            return user;

        } catch (SQLException e) {
            throw new InvalidLoginException(
                "Unable to log in right now. Please try again later."
            );
        }
    }
}