package com.aisupport.servlet;

import com.aisupport.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/customer/*",
        "/agent/*",
        "/admin/*"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(
            javax.servlet.ServletRequest request,
            javax.servlet.ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        // Prevent protected pages from being cached.
        httpResponse.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        HttpSession session = httpRequest.getSession(false);

        // Check whether the user is logged in.
        User loggedInUser = null;

        if (session != null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
        }

        if (loggedInUser == null) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login"
            );

            return;
        }

        // Determine the required role from the requested path.
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        String path = requestURI.substring(contextPath.length());

        String requiredRole;

        if (path.startsWith("/customer/")) {
            requiredRole = "CUSTOMER";
        } else if (path.startsWith("/agent/")) {
            requiredRole = "AGENT";
        } else if (path.startsWith("/admin/")) {
            requiredRole = "ADMIN";
        } else {
            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        // Verify the user's role.
        if (!requiredRole.equalsIgnoreCase(loggedInUser.getRole())) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this page."
            );

            return;
        }

        // User is authenticated and authorized.
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
