package com.aisupport.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // Retrieve the existing session without creating a new one.
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        // Prevent browser caching after logout.
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Redirect to login with a success message.
        response.sendRedirect(
                request.getContextPath() + "/login?logout=success"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        doGet(request, response);
    }
}
