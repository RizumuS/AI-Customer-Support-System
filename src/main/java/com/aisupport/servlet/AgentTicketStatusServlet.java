package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.User;

import com.aisupport.service.AgentService;
import com.aisupport.service.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/agent/ticket/status")
public class AgentTicketStatusServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

    private final TicketService ticketService =
            new TicketService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Verify the existing session.
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        // Retrieve the logged-in user.
        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        // Only agents can update ticket status.
        if (!"AGENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
            );
            return;
        }

        // Validate ticket ID.
        int ticketId;

        try {

            ticketId = Integer.parseInt(
                    request.getParameter("ticketId")
            );

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

        // Retrieve the requested status.
        String newStatus = request.getParameter("status");

        try {

            // Retrieve the logged-in agent's profile.
            Agent agent = agentService.getAgentByUserId(
                    loggedInUser.getUserId()
            );

            if (agent == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Active agent profile not found."
                );

                return;
            }

            // Verify that the ticket is assigned to this agent.
            if (ticketService.getAgentTicket(
                    ticketId,
                    agent.getAgentId()
            ) == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Ticket not found or not assigned to you."
                );

                return;
            }

            // Update the status securely.
            boolean updated =
                    ticketService.updateAgentTicketStatus(
                            ticketId,
                            agent.getAgentId(),
                            newStatus,
                            loggedInUser.getUserId()
                    );

            if (!updated) {

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to update ticket status."
                );

                return;
            }

            // Redirect back to ticket details.
            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/ticket?id="
                    + ticketId
                    + "&statusUpdated=true"
            );

        } catch (IllegalArgumentException e) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/ticket?id="
                    + ticketId
                    + "&error=invalidStatus"
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error updating agent ticket status.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update ticket status."
            );
        }
    }
}
