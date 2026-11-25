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

@WebServlet("/admin/tickets/assign")
public class AdminTicketAssignmentServlet extends HttpServlet {

    private TicketService ticketService;

    @Override
    public void init() throws ServletException {
        ticketService = new TicketService();
    }

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

            // Verify administrator access.
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

            // Retrieve submitted values.
            String ticketIdValue =
                    request.getParameter("ticketId");

            String agentIdValue =
                    request.getParameter("agentId");

            if (ticketIdValue == null ||
                    ticketIdValue.isBlank() ||
                    agentIdValue == null ||
                    agentIdValue.isBlank()) {

                throw new IllegalArgumentException(
                        "Ticket ID and agent ID are required."
                );
            }

            int ticketId = Integer.parseInt(ticketIdValue);
            int agentId = Integer.parseInt(agentIdValue);

            // Assign or reassign the ticket.
            boolean assigned =
                    ticketService.assignTicketToAgent(
                            ticketId,
                            agentId,
                            adminId
                    );

            if (assigned) {

                response.sendRedirect(
                        redirectURL + "?assignment=updated"
                );

            } else {

                response.sendRedirect(
                        redirectURL + "?assignment=failed"
                );
            }

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    redirectURL + "?assignment=invalid"
            );

        } catch (IllegalArgumentException e) {

            String message = URLEncoder.encode(
                    e.getMessage(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    redirectURL + "?assignmentError=" + message
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to assign ticket to agent.",
                    e
            );
        }
    }
}
