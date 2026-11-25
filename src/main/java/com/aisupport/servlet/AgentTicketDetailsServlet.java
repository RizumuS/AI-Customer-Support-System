package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
import com.aisupport.model.TicketResponse;
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
import java.util.List;

@WebServlet("/agent/ticket")
public class AgentTicketDetailsServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

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

        if (!"AGENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
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

            // Verify that this ticket is assigned to the agent.
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

            // Retrieve the public conversation history.
            List<TicketResponse> responses =
                    responseService.getTicketResponses(ticketId);

            // Pass information to the JSP.
            request.setAttribute("ticket", ticket);
            request.setAttribute("agent", agent);
            request.setAttribute("responses", responses);

            request.getRequestDispatcher(
                    "/agent/ticket-details.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving agent ticket details.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve ticket details."
            );
        }
    }
}
