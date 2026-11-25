package com.aisupport.servlet;

import com.aisupport.model.Ticket;
import com.aisupport.model.TicketResponse;
import com.aisupport.model.User;
import com.aisupport.service.TicketResponseService;
import com.aisupport.service.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer/ticket/conversation")
public class CustomerTicketConversationServlet extends HttpServlet {

    private final TicketService ticketService =
            new TicketService();

    private final TicketResponseService responseService =
            new TicketResponseService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        Integer customerId = loggedInUser.getCustomerId();

        if (customerId == null || customerId <= 0) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Customer profile not found."
            );
            return;
        }

        String ticketIdParameter = request.getParameter("id");

        int ticketId;

        try {
            ticketId = Integer.parseInt(ticketIdParameter);

            if (ticketId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid ticket ID."
            );
            return;
        }

        try {

            // Verify ticket ownership
            Ticket ticket = ticketService.getCustomerTicket(
                    ticketId,
                    customerId
            );

            if (ticket == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Ticket not found."
                );
                return;
            }

            // Retrieve public conversation messages
            List<TicketResponse> responses =
                    responseService.getTicketResponses(ticketId);

            request.setAttribute("ticket", ticket);
            request.setAttribute("responses", responses);

            request.getRequestDispatcher(
                    "/customer/ticket-conversation.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving ticket conversation.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load ticket conversation."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        Integer customerId = loggedInUser.getCustomerId();

        if (customerId == null || customerId <= 0) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Customer profile not found."
            );
            return;
        }

        String ticketIdParameter = request.getParameter("ticketId");
        String message = request.getParameter("message");

        int ticketId;

        try {
            ticketId = Integer.parseInt(ticketIdParameter);

            if (ticketId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid ticket ID."
            );
            return;
        }

        try {

            // Verify ticket ownership before accepting a reply
            Ticket ticket = ticketService.getCustomerTicket(
                    ticketId,
                    customerId
            );

            if (ticket == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Ticket not found."
                );
                return;
            }

            if (message == null || message.trim().isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Message cannot be empty."
                );
                return;
            }

            // Save the customer reply
            boolean saved = responseService.sendResponse(
                    ticketId,
                    loggedInUser.getUserId(),
                    message
            );

            if (!saved) {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to send message."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/ticket/conversation?id="
                    + ticketId
                    + "&sent=true"
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error sending customer ticket response.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to send message."
            );
        }
    }
}
