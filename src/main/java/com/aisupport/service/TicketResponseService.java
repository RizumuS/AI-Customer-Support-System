package com.aisupport.service;

import com.aisupport.dao.TicketResponseDAO;
import com.aisupport.model.TicketResponse;

import java.sql.SQLException;
import java.util.List;

public class TicketResponseService {

    private final TicketResponseDAO responseDAO;

    public TicketResponseService() {
        this.responseDAO = new TicketResponseDAO();
    }

    // Send a response
    public boolean sendResponse(
            int ticketId,
            int senderUserId,
            String message
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid ticket ID."
            );
        }

        if (senderUserId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid sender ID."
            );
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Message cannot be empty."
            );
        }

        TicketResponse response = new TicketResponse();

        response.setTicketId(ticketId);
        response.setSenderUserId(senderUserId);
        response.setMessage(message.trim());
        response.setInternal(false);

        return responseDAO.addResponse(response);
    }

    // Get conversation history
    public List<TicketResponse> getTicketResponses(
            int ticketId
    ) throws SQLException {

        if (ticketId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid ticket ID."
            );
        }

        return responseDAO.getResponsesByTicket(ticketId);
    }
}
