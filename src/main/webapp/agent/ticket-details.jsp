
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.Ticket" %>
<%@ page import="com.aisupport.model.Agent" %>
<%@ page import="com.aisupport.model.TicketResponse" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%!
    public String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
%>

<%
    Ticket ticket =
            (Ticket) request.getAttribute("ticket");

    Agent agent =
            (Agent) request.getAttribute("agent");

    List<TicketResponse> responses =
            (List<TicketResponse>) request.getAttribute("responses");

    if (ticket == null || agent == null || responses == null) {

        response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Ticket information not found."
        );

        return;
    }

    String contextPath = request.getContextPath();

    String sent = request.getParameter("sent");
    String error = request.getParameter("error");
    String statusUpdated = request.getParameter("statusUpdated");

    String status = ticket.getStatus() == null
            ? ""
            : ticket.getStatus().toLowerCase().replace(" ", "_");

    String priority = ticket.getPriority() == null
            ? ""
            : ticket.getPriority().toLowerCase();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Ticket Details - AI Customer Support</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #334155;
        }

        .navbar {
            background: #1e293b;
            color: white;
            padding: 18px 35px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .navbar h2 {
            margin: 0;
            font-size: 21px;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            background: #dc3545;
            padding: 9px 16px;
            border-radius: 5px;
        }

        .container {
            max-width: 1000px;
            margin: 35px auto;
            padding: 0 20px;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 25px;
        }

        .page-header h1 {
            margin: 0 0 8px;
            color: #1e293b;
        }

        .page-header p {
            color: #64748b;
            margin: 0;
        }

        .back-button {
            display: inline-block;
            text-decoration: none;
            background: #64748b;
            color: white;
            padding: 10px 16px;
            border-radius: 5px;
        }

        .ticket-card,
        .conversation-card {
            background: white;
            border-radius: 9px;
            padding: 28px;
            margin-bottom: 22px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        .ticket-heading {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 20px;
        }

        .ticket-heading h2 {
            margin: 0 0 8px;
            color: #1e293b;
        }

        .ticket-number {
            color: #2563eb;
            font-weight: bold;
        }

        .badges {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }

        .status,
        .priority {
            display: inline-block;
            padding: 6px 11px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }

        .status-open {
            background: #dbeafe;
            color: #1d4ed8;
        }

        .status-in_progress {
            background: #fef3c7;
            color: #92400e;
        }

        .status-waiting_for_customer {
            background: #ede9fe;
            color: #6d28d9;
        }

        .status-resolved {
            background: #dcfce7;
            color: #166534;
        }

        .status-closed {
            background: #e5e7eb;
            color: #374151;
        }

        .priority-low {
            background: #dcfce7;
            color: #166534;
        }

        .priority-medium {
            background: #dbeafe;
            color: #1d4ed8;
        }

        .priority-high {
            background: #ffedd5;
            color: #c2410c;
        }

        .priority-critical {
            background: #fee2e2;
            color: #b91c1c;
        }

        .section-title {
            color: #1e293b;
            font-size: 18px;
            margin-top: 25px;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e5e7eb;
        }

        .description {
            background: #f8fafc;
            padding: 18px;
            border-radius: 7px;
            line-height: 1.7;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }

        .details-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .detail-item {
            padding: 12px;
            background: #f8fafc;
            border-radius: 7px;
        }

        .detail-label {
            display: block;
            color: #64748b;
            font-size: 12px;
            margin-bottom: 7px;
            text-transform: uppercase;
        }

        .detail-value {
            font-weight: bold;
            color: #334155;
            overflow-wrap: anywhere;
        }

        .agent-info {
            background: #eff6ff;
            padding: 18px;
            border-radius: 7px;
            line-height: 1.8;
        }

        /* Status update form */

        .status-form {
            background: #f8fafc;
            padding: 20px;
            border-radius: 7px;
            margin-top: 15px;
        }

        .status-form label {
            display: block;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .status-form select {
            width: 100%;
            max-width: 400px;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            background: white;
            font-size: 14px;
        }

        .status-form button {
            display: block;
            margin-top: 14px;
            padding: 11px 20px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .status-form button:hover {
            background: #1d4ed8;
        }

        /* Conversation section */

        .conversation-message {
            background: #f8fafc;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 15px;
        }

        .message-header {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 12px;
        }

        .sender-name {
            font-weight: bold;
            color: #1e293b;
        }

        .sender-role {
            font-size: 11px;
            background: #dbeafe;
            color: #1d4ed8;
            padding: 4px 8px;
            border-radius: 12px;
            margin-left: 6px;
        }

        .message-date {
            font-size: 12px;
            color: #64748b;
        }

        .message-content {
            line-height: 1.7;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            color: #334155;
        }

        .empty-conversation {
            text-align: center;
            background: #f8fafc;
            padding: 30px 15px;
            border-radius: 7px;
            color: #64748b;
        }

        /* Reply form */

        .reply-form {
            margin-top: 25px;
        }

        .reply-form textarea {
            width: 100%;
            min-height: 130px;
            padding: 14px;
            border: 1px solid #cbd5e1;
            border-radius: 7px;
            font-family: Arial, sans-serif;
            font-size: 14px;
            resize: vertical;
        }

        .reply-form textarea:focus {
            outline: none;
            border-color: #2563eb;
        }

        .reply-form button {
            margin-top: 12px;
            padding: 11px 20px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .reply-form button:hover {
            background: #1d4ed8;
        }

        .success-message {
            background: #dcfce7;
            color: #166534;
            padding: 12px;
            border-radius: 6px;
            margin: 15px 0;
        }

        .error-message {
            background: #fee2e2;
            color: #991b1b;
            padding: 12px;
            border-radius: 6px;
            margin: 15px 0;
        }

        @media (max-width: 600px) {

            .navbar {
                padding: 16px;
            }

            .container {
                margin: 22px auto;
                padding: 0 12px;
            }

            .details-grid {
                grid-template-columns: 1fr;
            }

            .ticket-card,
            .conversation-card {
                padding: 20px;
            }
        }

    </style>

</head>

<body>

    <nav class="navbar">

        <h2>AI Customer Support</h2>

        <a href="<%= contextPath %>/logout">
            Logout
        </a>

    </nav>

    <main class="container">

        <div class="page-header">

            <div>

                <h1>Ticket Details</h1>

                <p>
                    Review the information for your assigned ticket.
                </p>

            </div>

            <a class="back-button"
               href="<%= contextPath %>/agent/tickets">

                Back to Assigned Tickets

            </a>

        </div>

        <!-- Ticket information -->

        <section class="ticket-card">

            <div class="ticket-heading">

                <div>

                    <h2>
                        <%= escapeHtml(ticket.getSubject()) %>
                    </h2>

                    <div class="ticket-number">
                        <%= escapeHtml(ticket.getTicketNumber()) %>
                    </div>

                </div>

                <div class="badges">

                    <span class="status status-<%= status %>">
                        <%= escapeHtml(ticket.getStatus()) %>
                    </span>

                    <span class="priority priority-<%= priority %>">
                        <%= escapeHtml(ticket.getPriority()) %>
                    </span>

                </div>

            </div>

            <h3 class="section-title">
                Ticket Description
            </h3>

            <div class="description">
                <%= escapeHtml(ticket.getDescription()) %>
            </div>

            <h3 class="section-title">
                Ticket Information
            </h3>

            <div class="details-grid">

                <div class="detail-item">
                    <span class="detail-label">Ticket ID</span>
                    <span class="detail-value">
                        <%= ticket.getTicketId() %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Customer ID</span>
                    <span class="detail-value">
                        <%= ticket.getCustomerId() %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Category</span>
                    <span class="detail-value">
                        <%= escapeHtml(ticket.getCategoryName() == null
                                ? "Uncategorized"
                                : ticket.getCategoryName()) %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Source</span>
                    <span class="detail-value">
                        <%= escapeHtml(ticket.getSource()) %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Created At</span>
                    <span class="detail-value">
                        <%= ticket.getCreatedAt() == null
                                ? "N/A"
                                : ticket.getCreatedAt() %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Last Updated</span>
                    <span class="detail-value">
                        <%= ticket.getUpdatedAt() == null
                                ? "N/A"
                                : ticket.getUpdatedAt() %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Resolved At</span>
                    <span class="detail-value">
                        <%= ticket.getResolvedAt() == null
                                ? "Not resolved"
                                : ticket.getResolvedAt() %>
                    </span>
                </div>

                <div class="detail-item">
                    <span class="detail-label">Assigned Agent</span>
                    <span class="detail-value">
                        <%= escapeHtml(agent.getFullName()) %>
                    </span>
                </div>

            </div>

            <!-- Agent ticket status update -->

            <h3 class="section-title">
                Update Ticket Status
            </h3>

            <% if ("true".equals(statusUpdated)) { %>

                <div class="success-message">
                    Ticket status has been updated successfully.
                </div>

            <% } %>

            <% if ("invalidStatus".equals(error)) { %>

                <div class="error-message">
                    Invalid ticket status. Please select a valid status.
                </div>

            <% } %>

            <form class="status-form"
                  method="post"
                  action="<%= contextPath %>/agent/ticket/status">

                <input type="hidden"
                       name="ticketId"
                       value="<%= ticket.getTicketId() %>">

                <label for="status">
                    Select New Status
                </label>

                <select id="status"
                        name="status"
                        required>

                    <option value="OPEN"
                        <%= "OPEN".equalsIgnoreCase(ticket.getStatus())
                                ? "selected" : "" %>>
                        Open
                    </option>

                    <option value="IN_PROGRESS"
                        <%= "IN_PROGRESS".equalsIgnoreCase(ticket.getStatus())
                                ? "selected" : "" %>>
                        In Progress
                    </option>

                    <option value="WAITING_FOR_CUSTOMER"
                        <%= "WAITING_FOR_CUSTOMER".equalsIgnoreCase(ticket.getStatus())
                                ? "selected" : "" %>>
                        Waiting for Customer
                    </option>

                    <option value="RESOLVED"
                        <%= "RESOLVED".equalsIgnoreCase(ticket.getStatus())
                                ? "selected" : "" %>>
                        Resolved
                    </option>

                    <option value="CLOSED"
                        <%= "CLOSED".equalsIgnoreCase(ticket.getStatus())
                                ? "selected" : "" %>>
                        Closed
                    </option>

                </select>

                <button type="submit">
                    Update Status
                </button>

            </form>

            <h3 class="section-title">
                Assigned Agent Information
            </h3>

            <div class="agent-info">

                <strong>Name:</strong>
                <%= escapeHtml(agent.getFullName()) %>
                <br>

                <strong>Email:</strong>
                <%= escapeHtml(agent.getEmail()) %>
                <br>

                <strong>Department:</strong>
                <%= escapeHtml(agent.getDepartment()) %>
                <br>

                <strong>Availability:</strong>
                <%= escapeHtml(agent.getAvailability()) %>

            </div>

        </section>

        <!-- Conversation history and reply form -->

        <section class="conversation-card">

            <h3 class="section-title">
                Customer Conversation
            </h3>

            <p>
                View the messages exchanged between the customer
                and support team.
            </p>

            <% if (responses.isEmpty()) { %>

                <div class="empty-conversation">
                    <p>No messages in this conversation yet.</p>
                </div>

            <% } else { %>

                <% for (TicketResponse message : responses) { %>

                    <div class="conversation-message">

                        <div class="message-header">

                            <div>

                                <span class="sender-name">
                                    <%= escapeHtml(message.getSenderName()) %>
                                </span>

                                <span class="sender-role">
                                    <%= escapeHtml(message.getSenderRole()) %>
                                </span>

                            </div>

                            <span class="message-date">
                                <%= message.getCreatedAt() == null
                                        ? "Date unavailable"
                                        : message.getCreatedAt() %>
                            </span>

                        </div>

                        <div class="message-content">
                            <%= escapeHtml(message.getMessage()) %>
                        </div>

                    </div>

                <% } %>

            <% } %>

            <!-- Reply status messages -->

            <% if ("true".equals(sent)) { %>

                <div class="success-message">
                    Your reply has been sent successfully.
                </div>

            <% } %>

            <% if ("empty".equals(error)) { %>

                <div class="error-message">
                    Please enter a message before submitting.
                </div>

            <% } else if ("invalid".equals(error)) { %>

                <div class="error-message">
                    Please enter a valid reply.
                </div>

            <% } %>

            <!-- Agent reply form -->

            <div class="reply-form">

                <h3 class="section-title">
                    Reply to Customer
                </h3>

                <form method="post"
                      action="<%= contextPath %>/agent/ticket/reply">

                    <input type="hidden"
                           name="ticketId"
                           value="<%= ticket.getTicketId() %>">

                    <textarea
                        name="message"
                        placeholder="Type your reply to the customer..."
                        maxlength="5000"
                        required></textarea>

                    <button type="submit">
                        Send Reply
                    </button>

                </form>

            </div>

        </section>

    </main>

</body>

</html>
