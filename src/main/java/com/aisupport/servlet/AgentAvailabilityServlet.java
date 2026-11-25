
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

@WebServlet("/agent/availability")
public class AgentAvailabilityServlet extends HttpServlet {

    private final AgentService agentService =
            new AgentService();

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

        // Only agents can update their availability.
        if (!"AGENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
            );
            return;
        }

        // Retrieve the requested availability.
        String availability =
                request.getParameter("availability");

        try {

            // Retrieve the logged-in agent's own profile.
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

            // Update only this agent's availability.
            boolean updated =
                    agentService.updateAvailability(
                            agent.getAgentId(),
                            availability
                    );

            if (!updated) {

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to update availability."
                );

                return;
            }

            // Redirect through the dashboard servlet
            // to reload the latest agent information.
            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/dashboard"
                    + "?availabilityUpdated=true"
            );

        } catch (IllegalArgumentException e) {

            // Redirect through the dashboard servlet.
            response.sendRedirect(
                    request.getContextPath()
                    + "/agent/dashboard"
                    + "?error=invalidAvailability"
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error updating agent availability.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update availability."
            );
        }
    }
}
