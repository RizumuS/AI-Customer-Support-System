package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.User;

import com.aisupport.service.AgentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/agent/dashboard")
public class AgentDashboardServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

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

        // Only agents can access this dashboard.
        if (!"AGENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
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

            // Make agent information available to the JSP.
            request.setAttribute("agent", agent);

            // Display the agent dashboard.
            request.getRequestDispatcher(
                    "/agent/dashboard.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error loading agent dashboard.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load agent dashboard."
            );
        }
    }
}
