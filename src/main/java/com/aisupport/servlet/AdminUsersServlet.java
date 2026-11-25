package com.aisupport.servlet;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<User> users = userService.getAllUsers();

            request.setAttribute("users", users);

            request.getRequestDispatcher(
                    "/admin/users.jsp"
            ).forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load admin users.",
                    exception
            );
        }
    }
}
