package com.aisupport.service;

import com.aisupport.dao.TicketDAO;
import com.aisupport.model.Ticket;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class TicketService {

    private final TicketDAO ticketDAO;

    private static final Set<String> VALID_PRIORITIES =
            Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    private static final Set<String> VALID_STATUSES =
            Set.of(
                    "OPEN",
                    "IN_PROGRESS",
                    "WAITING_FOR_CUSTOMER",
                    "RESOLVED",
                    "CLOSED"
            );

    public TicketService() {
        this.ticketDAO = new TicketDAO();
    }

    // Create a support ticket
    public boolean createTicket(Ticket ticket) throws SQLException {

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be empty.");
        }

        if (ticket.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customer.");
        }

        if (ticket.getSubject() == null ||
                ticket.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required.");
        }

        if (ticket.getDescription() == null ||
                ticket.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }

        if (ticket.getSubject().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Subject cannot exceed 255 characters."
            );
        }

        if (ticket.getPriority() == null ||
                !VALID_PRIORITIES.contains(
                        ticket.getPriority().toUpperCase()
                )) {
            throw new IllegalArgumentException("Invalid priority.");
        }

        if (ticket.getSource() == null ||
                !(ticket.getSource().equalsIgnoreCase("WEB") ||
                  ticket.getSource().equalsIgnoreCase("DESKTOP"))) {
            throw new IllegalArgumentException("Invalid ticket source.");
        }

        ticket.setSubject(ticket.getSubject().trim());
        ticket.setDescription(ticket.getDescription().trim());
        ticket.setPriority(ticket.getPriority().toUpperCase());
        ticket.setSource(ticket.getSource().toUpperCase());

        return ticketDAO.createTicket(ticket);
    }

    // Get tickets for a customer
    public List<Ticket> getCustomerTickets(
            int customerId
    ) throws SQLException {

        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID.");
        }

        return ticketDAO.getTicketsByCustomer(customerId);
    }

    // Get a specific ticket belonging to a customer
    public Ticket getCustomerTicket(
            int ticketId,
            int customerId
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID.");
        }

        return ticketDAO.getTicketByCustomer(
                ticketId,
                customerId
        );
    }


    // Get a specific ticket assigned to a particular agent
public Ticket getAgentTicket(
        int ticketId,
        int agentId
) throws SQLException {

    if (ticketId <= 0) {
        throw new IllegalArgumentException(
                "Invalid ticket ID."
        );
    }

    if (agentId <= 0) {
        throw new IllegalArgumentException(
                "Invalid agent ID."
        );
    }

    return ticketDAO.getTicketByAgent(
            ticketId,
            agentId
    );
}

    // Get all tickets
    public List<Ticket> getAllTickets() throws SQLException {
        return ticketDAO.getAllTickets();
    }

    // Get tickets assigned to a particular agent
    public List<Ticket> getTicketsByAgent(
            int agentId
    ) throws SQLException {

        if (agentId <= 0) {
            throw new IllegalArgumentException("Invalid agent ID.");
        }

        return ticketDAO.getTicketsByAgent(agentId);
    }

    // Update ticket status
    public boolean updateTicketStatus(
            int ticketId,
            String newStatus,
            int changedBy
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        if (changedBy <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (newStatus == null ||
                !VALID_STATUSES.contains(newStatus.toUpperCase())) {
            throw new IllegalArgumentException("Invalid ticket status.");
        }

        return ticketDAO.updateTicketStatus(
                ticketId,
                newStatus.toUpperCase(),
                changedBy
        );
    }


    // Update ticket priority
    public boolean updateTicketPriority(
            int ticketId,
            String newPriority,
            int changedBy
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid ticket ID."
            );
        }

        if (changedBy <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        if (newPriority == null ||
                !VALID_PRIORITIES.contains(
                        newPriority.toUpperCase()
                )) {

            throw new IllegalArgumentException(
                    "Invalid ticket priority."
            );
        }

        return ticketDAO.updateTicketPriority(
                ticketId,
                newPriority.toUpperCase(),
                changedBy
        );
    }



    // Update ticket status for a ticket assigned to a particular agent
    public boolean updateAgentTicketStatus(
            int ticketId,
            int agentId,
            String newStatus,
            int changedBy
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid ticket ID."
            );
        }

        if (agentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid agent ID."
            );
        }

        if (changedBy <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        if (newStatus == null ||
                !VALID_STATUSES.contains(newStatus.toUpperCase())) {

            throw new IllegalArgumentException(
                    "Invalid ticket status."
            );
        }

        return ticketDAO.updateAgentTicketStatus(
                ticketId,
                agentId,
                newStatus.toUpperCase(),
                changedBy
        );
    }


    // Assign a ticket to an agent
    public boolean assignTicketToAgent(
            int ticketId,
            int agentId,
            int changedBy
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        if (agentId <= 0) {
            throw new IllegalArgumentException("Invalid agent ID.");
        }

        if (changedBy <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return ticketDAO.assignTicketToAgent(
                ticketId,
                agentId,
                changedBy
        );
    }
}
