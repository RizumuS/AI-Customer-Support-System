package com.aisupport.dao;

import com.aisupport.database.DBConnection;
import com.aisupport.model.TicketResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketResponseDAO {

    // Add a response to a ticket
    public boolean addResponse(
            TicketResponse response
    ) throws SQLException {

        String sql = """
                INSERT INTO ticket_responses
                (
                    ticket_id,
                    sender_user_id,
                    message,
                    is_internal
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, response.getTicketId());
            statement.setInt(2, response.getSenderUserId());
            statement.setString(3, response.getMessage());
            statement.setBoolean(4, response.isInternal());

            return statement.executeUpdate() > 0;
        }
    }

    // Retrieve all public responses for a ticket
    public List<TicketResponse> getResponsesByTicket(
            int ticketId
    ) throws SQLException {

        String sql = """
                SELECT
                    r.*,
                    u.full_name AS sender_name,
                    u.role AS sender_role
                FROM ticket_responses r
                INNER JOIN users u
                    ON r.sender_user_id = u.user_id
                WHERE r.ticket_id = ?
                  AND r.is_internal = FALSE
                ORDER BY r.created_at ASC
                """;

        List<TicketResponse> responses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, ticketId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    TicketResponse response = new TicketResponse();

                    response.setResponseId(
                            result.getInt("response_id")
                    );

                    response.setTicketId(
                            result.getInt("ticket_id")
                    );

                    response.setSenderUserId(
                            result.getInt("sender_user_id")
                    );

                    response.setSenderName(
                            result.getString("sender_name")
                    );

                    response.setSenderRole(
                            result.getString("sender_role")
                    );

                    response.setMessage(
                            result.getString("message")
                    );

                    response.setInternal(
                            result.getBoolean("is_internal")
                    );

                    response.setCreatedAt(
                            result.getTimestamp("created_at")
                    );

                    responses.add(response);
                }
            }
        }

        return responses;
    }


    // Retrieve all responses, including internal notes, for administrator review
    public List<TicketResponse> getAllResponsesByTicketForAdmin(
            int ticketId
    ) throws SQLException {

        String sql = """
                SELECT
                    r.*,
                    u.full_name AS sender_name,
                    u.role AS sender_role
                FROM ticket_responses r
                INNER JOIN users u
                    ON r.sender_user_id = u.user_id
                WHERE r.ticket_id = ?
                ORDER BY r.created_at ASC
                """;

        List<TicketResponse> responses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, ticketId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    TicketResponse response = new TicketResponse();

                    response.setResponseId(
                            result.getInt("response_id")
                    );

                    response.setTicketId(
                            result.getInt("ticket_id")
                    );

                    response.setSenderUserId(
                            result.getInt("sender_user_id")
                    );

                    response.setSenderName(
                            result.getString("sender_name")
                    );

                    response.setSenderRole(
                            result.getString("sender_role")
                    );

                    response.setMessage(
                            result.getString("message")
                    );

                    response.setInternal(
                            result.getBoolean("is_internal")
                    );

                    response.setCreatedAt(
                            result.getTimestamp("created_at")
                    );

                    responses.add(response);
                }
            }
        }

        return responses;
    }

}
