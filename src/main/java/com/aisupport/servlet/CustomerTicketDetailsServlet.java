package com.aisupport.servlet;

import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customer/ticket")
public class CustomerTicketDetailsServlet extends HttpServlet {

    private final TicketService ticketService = new TicketService();

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

        if (ticketIdParameter == null ||
                ticketIdParameter.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Ticket ID is required."
            );
            return;
        }

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

            request.setAttribute("ticket", ticket);

            request.getRequestDispatcher(
                    "/customer/ticket-details.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving customer ticket details.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load ticket details."
            );
        }
    }
}
