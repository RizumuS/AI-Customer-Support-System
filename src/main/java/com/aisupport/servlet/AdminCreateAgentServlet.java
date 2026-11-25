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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/admin/agents/create")
public class AdminCreateAgentServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String redirectURL =
                request.getContextPath() + "/admin/agents";

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

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");

        try {

            boolean created = userService.createAgent(
                    fullName,
                    email,
                    password,
                    phone,
                    department
            );

            if (created) {
                response.sendRedirect(
                        redirectURL + "?agentCreation=success"
                );
            } else {
                response.sendRedirect(
                        redirectURL + "?agentCreation=failed"
                );
            }

        } catch (IllegalArgumentException e) {

            String message = URLEncoder.encode(
                    e.getMessage(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    redirectURL + "?agentCreationError=" + message
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Unable to create agent account.",
                    e
            );

            throw new ServletException(
                    "Unable to create agent account.",
                    e
            );
        }
    }
}
