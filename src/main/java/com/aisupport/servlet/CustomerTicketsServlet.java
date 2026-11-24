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
import java.util.List;

@WebServlet("/customer/tickets")
public class CustomerTicketsServlet extends HttpServlet {

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

        try {

            Integer customerId = loggedInUser.getCustomerId();

if (customerId == null || customerId <= 0) {
    throw new SQLException(
            "Customer profile not found for logged-in user."
    );
}

List<Ticket> tickets =
        ticketService.getCustomerTickets(customerId);

            request.setAttribute("tickets", tickets);

            request.getRequestDispatcher(
                    "/customer/tickets.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving customer tickets.", e
            );

            request.setAttribute(
                    "errorMessage",
                    "Unable to load your tickets. Please try again."
            );

            request.getRequestDispatcher(
                    "/customer/tickets.jsp"
            ).forward(request, response);
        }
    }
}
