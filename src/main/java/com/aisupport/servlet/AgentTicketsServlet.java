package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
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
import java.util.List;

@WebServlet("/agent/tickets")
public class AgentTicketsServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

    private final TicketService ticketService =
            new TicketService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // Check whether a session exists.
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

        // Only agents can access this page.
        if (!"AGENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
            );
            return;
        }

        try {

            // Retrieve the agent profile using the logged-in user ID.
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

            // Retrieve tickets assigned to this agent.
            List<Ticket> tickets =
                    ticketService.getTicketsByAgent(
                            agent.getAgentId()
                    );

            // Make the data available to the JSP.
            request.setAttribute("agent", agent);
            request.setAttribute("tickets", tickets);

            request.getRequestDispatcher(
                    "/agent/tickets.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error retrieving agent assigned tickets.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve assigned tickets."
            );
        }
    }
}
