package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
import com.aisupport.model.User;

import com.aisupport.service.AgentService;
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

@WebServlet("/agent/ticket/reply")
public class AgentTicketReplyServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

    private final TicketService ticketService =
            new TicketService();

    private final TicketResponseService responseService =
            new TicketResponseService();

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

        // Only agents can send agent replies.
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

        // Retrieve the submitted reply.
        String message = request.getParameter("message");

        if (message == null || message.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/ticket?id="
                    + ticketId
                    + "&error=empty"
            );

            return;
        }

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

            // Verify ticket assignment before saving the reply.
            Ticket ticket = ticketService.getAgentTicket(
                    ticketId,
                    agent.getAgentId()
            );

            if (ticket == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Ticket not found or not assigned to you."
                );

                return;
            }

            // Save the agent's reply as a public response.
            boolean saved = responseService.sendResponse(
                    ticketId,
                    loggedInUser.getUserId(),
                    message
            );

            if (!saved) {

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to save reply."
                );

                return;
            }

            // Redirect back to the ticket details page.
            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/ticket?id="
                    + ticketId
                    + "&sent=true"
            );

        } catch (IllegalArgumentException e) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/ticket?id="
                    + ticketId
                    + "&error=invalid"
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error saving agent ticket reply.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to save agent reply."
            );
        }
    }
}
