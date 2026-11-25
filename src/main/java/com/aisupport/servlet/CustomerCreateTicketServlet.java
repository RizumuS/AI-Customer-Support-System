package com.aisupport.servlet;

import com.aisupport.dao.CategoryDAO;
import com.aisupport.model.Category;
import com.aisupport.model.Ticket;
import com.aisupport.model.User;
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

@WebServlet("/customer/create-ticket")
public class CustomerCreateTicketServlet extends HttpServlet {

    private final TicketService ticketService = new TicketService();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        if (!authenticateCustomer(request, response)) {
            return;
        }

        try {
            loadCategories(request);

            request.getRequestDispatcher(
                    "/customer/create-ticket.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error loading ticket categories.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load ticket categories."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        if (!authenticateCustomer(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        User loggedInUser = (User) request.getSession(false)
                .getAttribute("loggedInUser");

        Integer customerId = loggedInUser.getCustomerId();

        if (customerId == null || customerId <= 0) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Customer profile not found."
            );
            return;
        }

        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String categoryParameter = request.getParameter("categoryId");
        String priority = request.getParameter("priority");

        try {

            if (categoryParameter == null ||
                    categoryParameter.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Please select a ticket category."
                );
            }

            int categoryId = Integer.parseInt(categoryParameter);

            if (categoryId <= 0) {
                throw new IllegalArgumentException(
                        "Invalid ticket category."
                );
            }

            List<Category> categories =
                    categoryDAO.getAllCategories();

            boolean validCategory = categories.stream()
                    .anyMatch(category ->
                            category.getCategoryId() == categoryId
                    );

            if (!validCategory) {
                throw new IllegalArgumentException(
                        "Please select a valid ticket category."
                );
            }

            Ticket ticket = new Ticket();

            ticket.setCustomerId(customerId);
            ticket.setSubject(subject);
            ticket.setDescription(description);
            ticket.setCategoryId(categoryId);
            ticket.setPriority(priority);
            ticket.setSource("WEB");

            boolean created = ticketService.createTicket(ticket);

            if (created) {

                request.getSession().setAttribute(
                        "successMessage",
                        "Your support ticket has been created successfully."
                );

                response.sendRedirect(
                        request.getContextPath() + "/customer/tickets"
                );

                return;
            }

            request.setAttribute(
                    "errorMessage",
                    "Unable to create your ticket. Please try again."
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "errorMessage",
                    "Invalid ticket category."
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error creating customer ticket.", e
            );

            request.setAttribute(
                    "errorMessage",
                    "A database error occurred. Please try again."
            );
        }

        try {

            loadCategories(request);

            request.setAttribute("subject", subject);
            request.setAttribute("description", description);
            request.setAttribute("selectedCategory", categoryParameter);
            request.setAttribute("selectedPriority", priority);

            request.getRequestDispatcher(
                    "/customer/create-ticket.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error reloading ticket categories.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to reload the ticket form."
            );
        }
    }

    private boolean authenticateCustomer(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("loggedInUser") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );

            return false;
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        Integer customerId = loggedInUser.getCustomerId();

        if (customerId == null || customerId <= 0) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Customer profile not found."
            );

            return false;
        }

        return true;
    }

    private void loadCategories(HttpServletRequest request)
            throws SQLException {

        List<Category> categories =
                categoryDAO.getAllCategories();

        request.setAttribute("categories", categories);
    }
}
