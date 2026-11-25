
package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.service.AgentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/agents")
public class AdminAgentsServlet extends HttpServlet {

    private AgentService agentService;

    @Override
    public void init() throws ServletException {
        agentService = new AgentService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Agent> agents =
                    agentService.getAllAgentsForAdmin();

            request.setAttribute("agents", agents);

            request.getRequestDispatcher(
                    "/admin/agents.jsp"
            ).forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load admin agents.",
                    exception
            );
        }
    }
}
