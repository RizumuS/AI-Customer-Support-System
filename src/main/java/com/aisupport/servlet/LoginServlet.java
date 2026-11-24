package com.aisupport.servlet;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    // Display login page
@Override
protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
) throws ServletException, IOException {

    // Display a message after successful logout.
    String logout = request.getParameter("logout");

    if ("success".equals(logout)) {
        request.setAttribute(
                "successMessage",
                "You have been logged out successfully."
        );
    }

    request.getRequestDispatcher("/login.jsp")
            .forward(request, response);
}

    // Process login form
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate empty fields
        if (email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty()) {

            request.setAttribute(
                    "errorMessage",
                    "Please enter your email address and password."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);

            return;
        }

        try {

            // Authenticate using existing UserService
            User user = userService.login(
                    email.trim(),
                    password
            );

            // Invalid credentials or inactive account
            if (user == null) {

                request.setAttribute(
                        "errorMessage",
                        "Invalid email or password. Please try again."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);

                return;
            }

            // Create a fresh session after authentication
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);

            // Store authenticated user
            session.setAttribute("loggedInUser", user);

            // Session timeout: 30 minutes
            session.setMaxInactiveInterval(30 * 60);

            // Redirect according to user role
            String role = user.getRole();

            if ("CUSTOMER".equalsIgnoreCase(role)) {

                response.sendRedirect(
                        request.getContextPath() +
                        "/customer/dashboard.jsp"
                );

            } else if ("AGENT".equalsIgnoreCase(role)) {

                response.sendRedirect(
                        request.getContextPath() +
                        "/agent/dashboard.jsp"
                );

            } else if ("ADMIN".equalsIgnoreCase(role)) {

                response.sendRedirect(
                        request.getContextPath() +
                        "/admin/dashboard.jsp"
                );

            } else {

                session.invalidate();

                request.setAttribute(
                        "errorMessage",
                        "Your account has an invalid user role."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Database error during login.",
                    e
            );

            request.setAttribute(
                    "errorMessage",
                    "Unable to process your login right now. Please try again later."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        }
    }
}
