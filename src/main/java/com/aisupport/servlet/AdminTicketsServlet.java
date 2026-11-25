
package com.aisupport.servlet;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
import com.aisupport.service.AgentService;
import com.aisupport.service.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/tickets")
public class AdminTicketsServlet extends HttpServlet {

    private TicketService ticketService;
    private AgentService agentService;

    @Override
    public void init() throws ServletException {
        ticketService = new TicketService();
        agentService = new AgentService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {

            // Retrieve all tickets in the system.
            List<Ticket> tickets =
                    ticketService.getAllTickets();

            // Retrieve all active agents for ticket assignment.
            List<Agent> agents =
                    agentService.getAllAgents();

            request.setAttribute("tickets", tickets);
            request.setAttribute("agents", agents);

            // Forward to the Admin ticket management page.
            request.getRequestDispatcher(
                    "/admin/tickets.jsp"
            ).forward(request, response);

        } catch (Exception exception) {

            throw new ServletException(
                    "Unable to load admin tickets.",
                    exception
            );
        }
    }
}
