
package com.aisupport.servlet;

import com.aisupport.model.Ticket;
import com.aisupport.model.TicketResponse;
import com.aisupport.model.User;

import com.aisupport.service.TicketService;
import com.aisupport.service.TicketResponseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/ticket")
public class AdminTicketDetailsServlet extends HttpServlet {

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

        if (!"ADMIN".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Administrator access required."
            );
            return;
        }

        String ticketIdParameter =
                request.getParameter("id");

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

            // Retrieve the ticket for administrator review.
            Ticket ticket = ticketService.getTicketById(ticketId);

            if (ticket == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Ticket not found."
                );

                return;
            }

            // Retrieve all messages, including internal notes.
            List<TicketResponse> responses =
                    responseService.getAdminTicketResponses(ticketId);

            request.setAttribute("ticket", ticket);
            request.setAttribute("responses", responses);

            request.getRequestDispatcher(
                    "/admin/ticket-details.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving administrator ticket details.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve ticket details."
            );
        }
    }
}
