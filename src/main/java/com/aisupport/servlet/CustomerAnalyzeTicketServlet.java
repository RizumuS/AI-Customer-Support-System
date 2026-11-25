package com.aisupport.servlet;

import com.aisupport.model.AIAnalysis;
import com.aisupport.model.User;
import com.aisupport.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/customer/analyze-ticket")
public class CustomerAnalyzeTicketServlet extends HttpServlet {

    private final AIService aiService = new AIService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("loggedInUser") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            writeError(response, "Please log in to analyze a ticket.");
            return;
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        Integer customerId = loggedInUser.getCustomerId();

        if (customerId == null || customerId <= 0) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            writeError(response, "Customer profile not found.");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String description = request.getParameter("description");

        if (description == null || description.trim().isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            writeError(response, "Please enter a ticket description.");
            return;
        }

        try {

            AIAnalysis analysis =
                    aiService.analyzeTicket(description);

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            objectMapper.writeValue(
                    response.getWriter(),
                    analysis
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            getServletContext().log(
                    "AI ticket analysis was interrupted.", e
            );

            response.setStatus(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE
            );

            writeError(
                    response,
                    "AI analysis was interrupted. Please try again."
            );

        } catch (IOException e) {

            getServletContext().log(
                    "Error communicating with the Python AI service.", e
            );

            response.setStatus(
                    HttpServletResponse.SC_BAD_GATEWAY
            );

            writeError(
                    response,
                    "Unable to connect to the AI service. Please try again."
            );
        }
    }

    private void writeError(
            HttpServletResponse response,
            String message
    ) throws IOException {

        Map<String, String> error = new HashMap<>();
        error.put("error", message);

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}
