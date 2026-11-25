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

@WebServlet("/admin/users/status")
public class AdminUserStatusServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String redirectURL =
                request.getContextPath() + "/admin/users";

        try {

            // Retrieve the existing session.
            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            // Verify the logged-in administrator.
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

            // Read submitted values.
            String userIdValue = request.getParameter("userId");
            String activeValue = request.getParameter("active");

            if (userIdValue == null ||
                    activeValue == null ||
                    (!activeValue.equals("true") &&
                     !activeValue.equals("false"))) {

                throw new IllegalArgumentException(
                        "Invalid account status request."
                );
            }

            int userId = Integer.parseInt(userIdValue);

            boolean active = Boolean.parseBoolean(activeValue);

            // Update account status through the service layer.
            boolean updated = userService.updateUserStatus(
                    userId,
                    active,
                    adminId
            );

            if (updated) {

                response.sendRedirect(
                        redirectURL + "?status=updated"
                );

            } else {

                response.sendRedirect(
                        redirectURL + "?error=update_failed"
                );
            }

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    redirectURL + "?error=invalid_id"
            );

        } catch (IllegalArgumentException e) {

            String message = java.net.URLEncoder.encode(
                    e.getMessage(),
                    java.nio.charset.StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    redirectURL + "?error=" + message
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to update user account status.",
                    e
            );
        }
    }
}
