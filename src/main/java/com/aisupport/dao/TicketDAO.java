package com.aisupport.dao;

import com.aisupport.database.DBConnection;
import com.aisupport.model.Ticket;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO {

    // Generate a unique ticket number
    private String generateTicketNumber() {

        String date = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );

        String uniqueCode = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "TKT-" + date + "-" + uniqueCode;
    }

    // Create a new support ticket
    
    // Create a new support ticket with automatic agent assignment
    public boolean createTicket(Ticket ticket) throws SQLException {

        String ticketNumber = generateTicketNumber();

        String sql = """
                INSERT INTO tickets
                (
                    ticket_number,
                    customer_id,
                    assigned_agent_id,
                    category_id,
                    subject,
                    description,
                    status,
                    priority,
                    source
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String historySql = """
                INSERT INTO ticket_history
                (
                    ticket_id,
                    changed_by,
                    field_changed,
                    old_value,
                    new_value
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Get the customer who created the ticket
                int userId = getCustomerUserId(
                        connection,
                        ticket.getCustomerId()
                );

                // Use an existing assignment if one was supplied.
                // Otherwise, find an available agent automatically.
                Integer assignedAgentId = ticket.getAssignedAgentId();

                if (assignedAgentId == null) {
                    assignedAgentId = findAvailableAgentId(connection);
                }

                int ticketId;

                // Insert the ticket
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     sql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    statement.setString(1, ticketNumber);
                    statement.setInt(2, ticket.getCustomerId());

                    if (assignedAgentId != null) {
                        statement.setInt(3, assignedAgentId);
                    } else {
                        statement.setNull(3, Types.INTEGER);
                    }

                    if (ticket.getCategoryId() != null) {
                        statement.setInt(
                                4,
                                ticket.getCategoryId()
                        );
                    } else {
                        statement.setNull(4, Types.INTEGER);
                    }

                    statement.setString(5, ticket.getSubject());
                    statement.setString(6, ticket.getDescription());
                    statement.setString(7, "OPEN");
                    statement.setString(8, ticket.getPriority());
                    statement.setString(9, ticket.getSource());

                    int rowsInserted = statement.executeUpdate();

                    if (rowsInserted == 0) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet generatedKeys =
                                 statement.getGeneratedKeys()) {

                        if (generatedKeys.next()) {
                            ticketId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException(
                                    "Creating ticket failed. No ID generated."
                            );
                        }
                    }
                }

                // Record initial ticket status in history
                try (PreparedStatement statement =
                             connection.prepareStatement(historySql)) {

                    statement.setInt(1, ticketId);
                    statement.setInt(2, userId);
                    statement.setString(3, "STATUS");
                    statement.setNull(4, Types.VARCHAR);
                    statement.setString(5, "OPEN");

                    statement.executeUpdate();
                }

                // Record automatic agent assignment in history
                if (assignedAgentId != null) {

                    try (PreparedStatement statement =
                                 connection.prepareStatement(historySql)) {

                        statement.setInt(1, ticketId);
                        statement.setInt(2, userId);
                        statement.setString(3, "ASSIGNED_AGENT");
                        statement.setNull(4, Types.VARCHAR);
                        statement.setString(
                                5,
                                String.valueOf(assignedAgentId)
                        );

                        statement.executeUpdate();
                    }
                }

                connection.commit();

                // Update the ticket object
                ticket.setTicketId(ticketId);
                ticket.setTicketNumber(ticketNumber);
                ticket.setStatus("OPEN");
                ticket.setAssignedAgentId(assignedAgentId);

                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    // Find an available agent with the fewest unresolved tickets
    private Integer findAvailableAgentId(
            Connection connection
    ) throws SQLException {

        String sql = """
                SELECT a.agent_id
                FROM agents a
                INNER JOIN users u
                    ON a.user_id = u.user_id
                WHERE a.availability = 'AVAILABLE'
                AND u.is_active = TRUE
                ORDER BY
                    (
                        SELECT COUNT(*)
                        FROM tickets t
                        WHERE t.assigned_agent_id = a.agent_id
                        AND t.status NOT IN ('RESOLVED', 'CLOSED')
                    ) ASC,
                    a.agent_id ASC
                LIMIT 1
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt("agent_id");
            }
        }

        // No available agents
        return null;
    }


    // Get the user ID associated with a customer
    private int getCustomerUserId(
            Connection connection,
            int customerId
    ) throws SQLException {

        String sql = """
                SELECT user_id
                FROM customers
                WHERE customer_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("user_id");
                }

                throw new SQLException(
                        "Customer profile not found for customer ID: "
                                + customerId
                );
            }
        }
    }


    // Get a specific ticket belonging to a particular customer
public Ticket getTicketByCustomer(
        int ticketId,
        int customerId
) throws SQLException {

    String sql = """
        SELECT
            t.*,
            c.category_name AS category_name,
            u.full_name AS assigned_agent_name
        FROM tickets t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        LEFT JOIN agents a
            ON t.assigned_agent_id = a.agent_id
        LEFT JOIN users u
            ON a.user_id = u.user_id
        WHERE t.ticket_id = ?
        AND t.customer_id = ?
        """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, ticketId);
        statement.setInt(2, customerId);

        try (ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return mapTicket(result);
            }
        }
    }

    return null;
}


// Get a specific ticket assigned to a particular agent
public Ticket getTicketByAgent(
        int ticketId,
        int agentId
) throws SQLException {

    String sql = """
            SELECT
                t.*,
                c.category_name AS category_name,
                u.full_name AS assigned_agent_name
            FROM tickets t
            LEFT JOIN categories c
                ON t.category_id = c.category_id
            LEFT JOIN agents a
                ON t.assigned_agent_id = a.agent_id
            LEFT JOIN users u
                ON a.user_id = u.user_id
            WHERE t.ticket_id = ?
            AND t.assigned_agent_id = ?
            """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, ticketId);
        statement.setInt(2, agentId);

        try (ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return mapTicket(result);
            }
        }
    }

    return null;
}

    // Get all tickets belonging to a particular customer
    public List<Ticket> getTicketsByCustomer(
            int customerId
    ) throws SQLException {

        String sql = """
        SELECT
            t.*,
            c.category_name AS category_name,
            u.full_name AS assigned_agent_name
        FROM tickets t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        LEFT JOIN agents a
            ON t.assigned_agent_id = a.agent_id
        LEFT JOIN users u
            ON a.user_id = u.user_id
        WHERE t.customer_id = ?
        ORDER BY t.created_at DESC
        """;

        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    tickets.add(mapTicket(result));
                }
            }
        }

        return tickets;
    }

    // Get all tickets in the system
    public List<Ticket> getAllTickets() throws SQLException {

        String sql = """
        SELECT
            t.*,
            c.category_name AS category_name,
            u.full_name AS assigned_agent_name
        FROM tickets t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        LEFT JOIN agents a
            ON t.assigned_agent_id = a.agent_id
        LEFT JOIN users u
            ON a.user_id = u.user_id
        ORDER BY t.created_at DESC
        """;

        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                tickets.add(mapTicket(result));
            }
        }

        return tickets;
    }


    // Get all tickets assigned to a particular agent
    public List<Ticket> getTicketsByAgent(
            int agentId
    ) throws SQLException {

        String sql = """
        SELECT
            t.*,
            c.category_name AS category_name,
            u.full_name AS assigned_agent_name
        FROM tickets t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        LEFT JOIN agents a
            ON t.assigned_agent_id = a.agent_id
        LEFT JOIN users u
            ON a.user_id = u.user_id
        WHERE t.assigned_agent_id = ?
        ORDER BY t.created_at DESC
        """;

        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, agentId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    tickets.add(mapTicket(result));
                }
            }
        }

        return tickets;
    }


    // Update ticket status and record the change in history
    public boolean updateTicketStatus(
            int ticketId,
            String newStatus,
            int changedBy
    ) throws SQLException {

        String selectSql = """
                SELECT status
                FROM tickets
                WHERE ticket_id = ?
                FOR UPDATE
                """;

        String updateSql = """
                UPDATE tickets
                SET status = ?,
                    updated_at = CURRENT_TIMESTAMP,
                    resolved_at = CASE
                        WHEN ? = 'RESOLVED'
                        THEN CURRENT_TIMESTAMP
                        ELSE NULL
                    END
                WHERE ticket_id = ?
                """;

        String historySql = """
                INSERT INTO ticket_history
                (
                    ticket_id,
                    changed_by,
                    field_changed,
                    old_value,
                    new_value
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String oldStatus;

                // Get the current status
                try (PreparedStatement selectStatement =
                             connection.prepareStatement(selectSql)) {

                    selectStatement.setInt(1, ticketId);

                    try (ResultSet result =
                                 selectStatement.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }

                        oldStatus = result.getString("status");
                    }
                }

                // Update the ticket
                try (PreparedStatement updateStatement =
                             connection.prepareStatement(updateSql)) {

                    updateStatement.setString(1, newStatus);
                    updateStatement.setString(2, newStatus);
                    updateStatement.setInt(3, ticketId);

                    int rowsUpdated = updateStatement.executeUpdate();

                    if (rowsUpdated == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // Record the status change
                try (PreparedStatement historyStatement =
                             connection.prepareStatement(historySql)) {

                    historyStatement.setInt(1, ticketId);
                    historyStatement.setInt(2, changedBy);
                    historyStatement.setString(3, "STATUS");
                    historyStatement.setString(4, oldStatus);
                    historyStatement.setString(5, newStatus);

                    historyStatement.executeUpdate();
                }

                connection.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }



// Update ticket status only if it is assigned to the specified agent.
public boolean updateAgentTicketStatus(
        int ticketId,
        int agentId,
        String newStatus,
        int changedBy
) throws SQLException {

    String selectSql = """
            SELECT status
            FROM tickets
            WHERE ticket_id = ?
            AND assigned_agent_id = ?
            FOR UPDATE
            """;

    String updateSql = """
            UPDATE tickets
            SET status = ?,
                updated_at = CURRENT_TIMESTAMP,
                resolved_at = CASE
                    WHEN ? = 'RESOLVED'
                    THEN CURRENT_TIMESTAMP
                    ELSE NULL
                END
            WHERE ticket_id = ?
            AND assigned_agent_id = ?
            """;

    String historySql = """
            INSERT INTO ticket_history
            (
                ticket_id,
                changed_by,
                field_changed,
                old_value,
                new_value
            )
            VALUES (?, ?, ?, ?, ?)
            """;

    try (Connection connection = DBConnection.getConnection()) {

        connection.setAutoCommit(false);

        try {

            String oldStatus;

            // Verify assignment and retrieve current status.
            try (PreparedStatement statement =
                         connection.prepareStatement(selectSql)) {

                statement.setInt(1, ticketId);
                statement.setInt(2, agentId);

                try (ResultSet result = statement.executeQuery()) {

                    if (!result.next()) {
                        connection.rollback();
                        return false;
                    }

                    oldStatus = result.getString("status");
                }
            }

            // No history entry is needed if status is unchanged.
            if (oldStatus.equalsIgnoreCase(newStatus)) {
                connection.commit();
                return true;
            }

            // Update only the ticket assigned to this agent.
            try (PreparedStatement statement =
                         connection.prepareStatement(updateSql)) {

                statement.setString(1, newStatus);
                statement.setString(2, newStatus);
                statement.setInt(3, ticketId);
                statement.setInt(4, agentId);

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Record the status change.
            try (PreparedStatement statement =
                         connection.prepareStatement(historySql)) {

                statement.setInt(1, ticketId);
                statement.setInt(2, changedBy);
                statement.setString(3, "STATUS");
                statement.setString(4, oldStatus);
                statement.setString(5, newStatus);

                statement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException | RuntimeException e) {

            connection.rollback();
            throw e;

        } finally {

            connection.setAutoCommit(true);
        }
    }
}



    // Assign a ticket to an agent and record the change in history
    public boolean assignTicketToAgent(
            int ticketId,
            int agentId,
            int changedBy
    ) throws SQLException {

        String selectSql = """
                SELECT assigned_agent_id
                FROM tickets
                WHERE ticket_id = ?
                FOR UPDATE
                """;

        String updateSql = """
                UPDATE tickets
                SET assigned_agent_id = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE ticket_id = ?
                """;

        String historySql = """
                INSERT INTO ticket_history
                (
                    ticket_id,
                    changed_by,
                    field_changed,
                    old_value,
                    new_value
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                Integer oldAgentId = null;

                // Check the existing assignment
                try (PreparedStatement statement =
                             connection.prepareStatement(selectSql)) {

                    statement.setInt(1, ticketId);

                    try (ResultSet result = statement.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }

                        int value = result.getInt("assigned_agent_id");

                        if (!result.wasNull()) {
                            oldAgentId = value;
                        }
                    }
                }

                // Verify that the selected agent exists
                String agentCheckSql = """
                        SELECT agent_id
                        FROM agents
                        WHERE agent_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(agentCheckSql)) {

                    statement.setInt(1, agentId);

                    try (ResultSet result = statement.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Update ticket assignment
                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setInt(1, agentId);
                    statement.setInt(2, ticketId);

                    statement.executeUpdate();
                }

                // Record assignment history
                try (PreparedStatement statement =
                             connection.prepareStatement(historySql)) {

                    statement.setInt(1, ticketId);
                    statement.setInt(2, changedBy);
                    statement.setString(3, "ASSIGNED_AGENT");

                    if (oldAgentId == null) {
                        statement.setNull(4, Types.VARCHAR);
                    } else {
                        statement.setString(4, oldAgentId.toString());
                    }

                    statement.setString(5, String.valueOf(agentId));

                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }


    // Update ticket priority and record the change in history
    public boolean updateTicketPriority(
            int ticketId,
            String newPriority,
            int changedBy
    ) throws SQLException {

        String selectSql = """
                SELECT priority
                FROM tickets
                WHERE ticket_id = ?
                FOR UPDATE
                """;

        String updateSql = """
                UPDATE tickets
                SET priority = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE ticket_id = ?
                """;

        String historySql = """
                INSERT INTO ticket_history
                (
                    ticket_id,
                    changed_by,
                    field_changed,
                    old_value,
                    new_value
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String oldPriority;

                // Retrieve the existing priority.
                try (PreparedStatement statement =
                             connection.prepareStatement(selectSql)) {

                    statement.setInt(1, ticketId);

                    try (ResultSet result = statement.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }

                        oldPriority = result.getString("priority");
                    }
                }

                // Avoid unnecessary history entries.
                if (oldPriority.equalsIgnoreCase(newPriority)) {
                    connection.commit();
                    return true;
                }

                // Update the ticket priority.
                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setString(1, newPriority);
                    statement.setInt(2, ticketId);

                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // Record the priority change.
                try (PreparedStatement statement =
                             connection.prepareStatement(historySql)) {

                    statement.setInt(1, ticketId);
                    statement.setInt(2, changedBy);
                    statement.setString(3, "PRIORITY");
                    statement.setString(4, oldPriority);
                    statement.setString(5, newPriority);

                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }



    // Convert database result into a Ticket object
    private Ticket mapTicket(ResultSet result) throws SQLException {

        Ticket ticket = new Ticket();

        ticket.setTicketId(
                result.getInt("ticket_id")
        );

        ticket.setTicketNumber(
                result.getString("ticket_number")
        );

        ticket.setCustomerId(
                result.getInt("customer_id")
        );

        // Assigned agent ID can be NULL
        int agentId = result.getInt("assigned_agent_id");

        if (result.wasNull()) {
            ticket.setAssignedAgentId(null);
        } else {
            ticket.setAssignedAgentId(agentId);
        }

        // Category ID can be NULL
        int categoryId = result.getInt("category_id");

        if (result.wasNull()) {
            ticket.setCategoryId(null);
        } else {
            ticket.setCategoryId(categoryId);
        }

        // Category name from categories table
        ticket.setCategoryName(
        result.getString("category_name")
);

ticket.setAssignedAgentName(
        result.getString("assigned_agent_name")
);

        ticket.setSubject(
                result.getString("subject")
        );

        ticket.setDescription(
                result.getString("description")
        );

        ticket.setStatus(
                result.getString("status")
        );

        ticket.setPriority(
                result.getString("priority")
        );

        ticket.setSource(
                result.getString("source")
        );

        ticket.setCreatedAt(
                result.getTimestamp("created_at")
        );

        ticket.setUpdatedAt(
                result.getTimestamp("updated_at")
        );

        ticket.setResolvedAt(
                result.getTimestamp("resolved_at")
        );

        return ticket;
    }
}
