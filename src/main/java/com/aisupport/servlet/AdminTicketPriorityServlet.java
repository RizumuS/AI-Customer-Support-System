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

@WebServlet("/admin/tickets/priority")
public class AdminTicketPriorityServlet extends HttpServlet {

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

        request.setCharacterEncoding("UTF-8");

        String redirectURL =
                request.getContextPath() + "/admin/tickets";

        try {

            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

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

            String ticketIdValue =
                    request.getParameter("ticketId");

            String priority =
                    request.getParameter("priority");

            if (ticketIdValue == null ||
                    ticketIdValue.isBlank() ||
                    priority == null ||
                    priority.isBlank()) {

                throw new IllegalArgumentException(
                        "Ticket ID and priority are required."
                );
            }

            int ticketId = Integer.parseInt(ticketIdValue);

            boolean updated =
                    ticketService.updateTicketPriority(
                            ticketId,
                            priority,
                            adminId
                    );

            if (updated) {

                response.sendRedirect(
                        redirectURL + "?priority=updated"
                );

            } else {

                response.sendRedirect(
                        redirectURL + "?priority=failed"
                );
            }

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    redirectURL + "?priority=invalid"
            );

        } catch (IllegalArgumentException e) {

            String message = URLEncoder.encode(
                    e.getMessage(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    redirectURL + "?priorityError=" + message
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to update ticket priority.",
                    e
            );
        }
    }
}
