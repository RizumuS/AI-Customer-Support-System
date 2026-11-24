package com.aisupport.dao;

import com.aisupport.database.DBConnection;
import com.aisupport.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Register a new user
    public boolean registerUser(User user) throws SQLException {

        String userSql = """
                INSERT INTO users
                (full_name, email, password_hash, role, phone)
                VALUES (?, ?, ?, ?, ?)
                """;

        String customerSql = """
                INSERT INTO customers (user_id)
                VALUES (?)
                """;

        String agentSql = """
                INSERT INTO agents (user_id, department, availability)
                VALUES (?, ?, 'AVAILABLE')
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int userId;

                // Insert user details
                try (PreparedStatement userStatement =
                             connection.prepareStatement(
                                     userSql,
                                     PreparedStatement.RETURN_GENERATED_KEYS)) {

                    userStatement.setString(1, user.getFullName());
                    userStatement.setString(2, user.getEmail());
                    userStatement.setString(3, user.getPasswordHash());
                    userStatement.setString(4, user.getRole());
                    userStatement.setString(5, user.getPhone());

                    int rows = userStatement.executeUpdate();

                    if (rows == 0) {
                        throw new SQLException("User registration failed.");
                    }

                    try (ResultSet keys = userStatement.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException("Could not retrieve user ID.");
                        }

                        userId = keys.getInt(1);
                    }
                }

                // Create customer profile
                if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

                    try (PreparedStatement customerStatement =
                                 connection.prepareStatement(customerSql)) {

                        customerStatement.setInt(1, userId);
                        customerStatement.executeUpdate();
                    }
                }

                // Create agent profile
                else if ("AGENT".equalsIgnoreCase(user.getRole())) {

                    try (PreparedStatement agentStatement =
                                 connection.prepareStatement(agentSql)) {

                        agentStatement.setInt(1, userId);
                        agentStatement.setString(2, "Customer Support");
                        agentStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException exception) {

                connection.rollback();
                throw exception;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    // Find user by email
    public User findByEmail(String email) throws SQLException {

        String sql = """
                SELECT u.*, c.customer_id
                FROM users u
                LEFT JOIN customers c ON u.user_id = c.user_id
                WHERE u.email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return mapUser(result);
                }
            }
        }

        return null;
    }

    // Retrieve all registered users
    public List<User> getAllUsers() throws SQLException {

        String sql = """
                SELECT
                    u.*,
                    c.customer_id
                FROM users u
                LEFT JOIN customers c
                    ON u.user_id = c.user_id
                ORDER BY u.created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                users.add(mapUser(result));
            }
        }

        return users;
    }

    // Convert database result into User object
    private User mapUser(ResultSet result) throws SQLException {

        User user = new User();

        user.setUserId(result.getInt("user_id"));

        int customerId = result.getInt("customer_id");

        user.setCustomerId(
                result.wasNull() ? null : customerId
        );

        user.setFullName(result.getString("full_name"));
        user.setEmail(result.getString("email"));
        user.setPasswordHash(result.getString("password_hash"));
        user.setRole(result.getString("role"));
        user.setPhone(result.getString("phone"));
        user.setActive(result.getBoolean("is_active"));
        user.setCreatedAt(result.getTimestamp("created_at"));

        return user;
    }

    // Update user account status
public boolean updateUserStatus(int userId, boolean active)
        throws SQLException {

    String sql = """
            UPDATE users
            SET is_active = ?
            WHERE user_id = ?
            """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setBoolean(1, active);
        statement.setInt(2, userId);

        return statement.executeUpdate() > 0;
    }
}
}
