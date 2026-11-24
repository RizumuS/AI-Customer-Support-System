
package com.aisupport.service;

import com.aisupport.database.DBConnection;
import com.aisupport.model.Ticket;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    @Test
    void testCreateTicket() throws Exception {

        int customerId;

        // Find the existing test customer's ID
        String customerSql = """
                SELECT c.customer_id
                FROM customers c
                JOIN users u ON c.user_id = u.user_id
                WHERE u.email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(customerSql)) {

            statement.setString(1, "testcustomer@example.com");

            try (ResultSet result = statement.executeQuery()) {

                assertTrue(
                        result.next(),
                        "Test customer must exist before creating a ticket."
                );

                customerId = result.getInt("customer_id");
            }
        }

        // Create a new ticket
        Ticket ticket = new Ticket();

        ticket.setCustomerId(customerId);
        ticket.setSubject("Unable to access my account");
        ticket.setDescription(
                "I am unable to log in to my account despite using the correct password."
        );
        ticket.setPriority("HIGH");
        ticket.setSource("DESKTOP");
        ticket.setCategoryId(null);

        TicketService ticketService = new TicketService();

        boolean created = ticketService.createTicket(ticket);

        // Verify ticket creation
        assertTrue(created);
        assertTrue(ticket.getTicketId() > 0);
        assertNotNull(ticket.getTicketNumber());
        assertEquals("OPEN", ticket.getStatus());

        // Verify ticket history
        String historySql = """
                SELECT field_changed, COUNT(*) AS record_count
                FROM ticket_history
                WHERE ticket_id = ?
                GROUP BY field_changed
                """;

        int statusHistoryCount = 0;
        int assignmentHistoryCount = 0;
        int totalHistoryCount = 0;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(historySql)) {

            statement.setInt(1, ticket.getTicketId());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    String fieldChanged =
                            result.getString("field_changed");

                    int recordCount =
                            result.getInt("record_count");

                    totalHistoryCount += recordCount;

                    if ("STATUS".equalsIgnoreCase(fieldChanged)) {
                        statusHistoryCount = recordCount;
                    }

                    if ("ASSIGNED_AGENT".equalsIgnoreCase(fieldChanged)) {
                        assignmentHistoryCount = recordCount;
                    }
                }
            }
        }

        // Every newly created ticket must have one STATUS history record.
        assertEquals(
                1,
                statusHistoryCount,
                "Ticket must have exactly one initial STATUS history record."
        );

        // Automatic assignment may create one additional history record.
        assertTrue(
                assignmentHistoryCount == 0 ||
                assignmentHistoryCount == 1,
                "Ticket may have at most one automatic assignment history record."
        );

        assertEquals(
                1 + assignmentHistoryCount,
                totalHistoryCount,
                "Ticket history should contain only the expected creation records."
        );

        System.out.println("Ticket created successfully!");
        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Ticket Number: " + ticket.getTicketNumber());
        System.out.println("Ticket Status: " + ticket.getStatus());
        System.out.println(
                "Assignment History Records: " + assignmentHistoryCount
        );
        System.out.println(
                "Total History Records: " + totalHistoryCount
        );
    }
}
