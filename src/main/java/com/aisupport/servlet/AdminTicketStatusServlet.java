package com.aisupport.servlet;

import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/admin/tickets/status")
public class AdminTicketStatusServlet extends HttpServlet {

    private final TicketService ticketService = new TicketService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String redirectURL =
                request.getContextPath() + "/admin/tickets";

        try {

            // Retrieve the existing session.
            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            // Verify the logged-in administrator.
            User loggedInUser =
                    (User) session.getAttribute("loggedInUser");

            if (loggedInUser == null ||
                    !"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Administrator access required."
                );
                return;
            }

            int adminId = loggedInUser.getUserId();

            // Read submitted values.
            String ticketIdValue =
                    request.getParameter("ticketId");

            String newStatus =
                    request.getParameter("status");

            if (ticketIdValue == null ||
                    ticketIdValue.isBlank() ||
                    newStatus == null ||
                    newStatus.isBlank()) {

                throw new IllegalArgumentException(
                        "Ticket ID and status are required."
                );
            }

            int ticketId = Integer.parseInt(ticketIdValue);

            // Update ticket status through the service layer.
            boolean updated = ticketService.updateTicketStatus(
                    ticketId,
                    newStatus.trim(),
                    adminId
            );

            if (updated) {

                response.sendRedirect(
                        redirectURL + "?status=updated"
                );

            } else {

                response.sendRedirect(
                        redirectURL + "?error=update_failed"
                );
            }

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    redirectURL + "?error=invalid_ticket_id"
            );

        } catch (IllegalArgumentException e) {

            String message = URLEncoder.encode(
                    e.getMessage(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    redirectURL + "?error=" + message
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to update ticket status.",
                    e
            );
        }
    }
}
