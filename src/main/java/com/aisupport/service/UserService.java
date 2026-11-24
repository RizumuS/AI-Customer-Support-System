package com.aisupport.service;

import com.aisupport.dao.UserDAO;
import com.aisupport.model.User;
import com.aisupport.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // Register a new customer
    public boolean registerUser(User user)
            throws SQLException {

        validateName(user.getFullName());
        validateEmail(user.getEmail());
        validatePassword(user.getPasswordHash());

        if (user.getRole() == null ||
                !user.getRole().equals("CUSTOMER")) {

            throw new IllegalArgumentException(
                    "Public registration is only available for customers."
            );
        }

        if (userDAO.findByEmail(user.getEmail()) != null) {

            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        user.setPasswordHash(
                PasswordUtil.hashPassword(user.getPasswordHash())
        );

        return userDAO.registerUser(user);
    }

    // Create an agent account internally
    public boolean createAgent(
            String fullName,
            String email,
            String password,
            String phone,
            String department
    ) throws SQLException {

        validateName(fullName);
        validateEmail(email);
        validatePassword(password);

        if (department == null || department.isBlank()) {

            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        if (userDAO.findByEmail(email.trim()) != null) {

            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        User agent = new User();

        agent.setFullName(fullName.trim());
        agent.setEmail(email.trim());
        agent.setPhone(phone);
        agent.setRole("AGENT");

        agent.setPasswordHash(
                PasswordUtil.hashPassword(password)
        );

        return userDAO.registerUser(agent);
    }

    // Create an admin account internally
    public boolean createAdmin(User admin)
            throws SQLException {

        if (admin == null) {

            throw new IllegalArgumentException(
                    "Admin details are required."
            );
        }

        validateName(admin.getFullName());
        validateEmail(admin.getEmail());
        validatePassword(admin.getPasswordHash());

        admin.setEmail(admin.getEmail().trim());

        if (userDAO.findByEmail(admin.getEmail()) != null) {

            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        admin.setFullName(admin.getFullName().trim());
        admin.setRole("ADMIN");

        admin.setPasswordHash(
                PasswordUtil.hashPassword(admin.getPasswordHash())
        );

        return userDAO.registerUser(admin);
    }

    // Authenticate a user
    public User login(String email, String password)
            throws SQLException {

        User user = userDAO.findByEmail(email);

        if (user == null || !user.isActive()) {
            return null;
        }

        if (!PasswordUtil.verifyPassword(
                password, user.getPasswordHash())) {

            return null;
        }

        // Never expose password hash to the caller
        user.setPasswordHash(null);

        return user;
    }

    // Retrieve all registered users
    public List<User> getAllUsers() throws SQLException {

        return userDAO.getAllUsers();
    }

    // Common validation methods
    private void validateName(String name) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Full name is required."
            );
        }
    }

    private void validateEmail(String email) {

        if (email == null ||
                !email.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }
    }

    private void validatePassword(String password) {

        if (password == null || password.length() < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }
    }
}
