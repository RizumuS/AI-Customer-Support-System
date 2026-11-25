<%@ page import="com.aisupport.model.Ticket" %>
<%@ page import="com.aisupport.model.TicketResponse" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%!
    private String escapeHtml(String value) {
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
    Ticket ticket = (Ticket) request.getAttribute("ticket");

    List<TicketResponse> responses =
            (List<TicketResponse>) request.getAttribute("responses");

    if (ticket == null) {
        response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Ticket not found."
        );
        return;
    }

    String status = ticket.getStatus() == null
            ? "UNKNOWN" : ticket.getStatus();

    String priority = ticket.getPriority() == null
            ? "UNKNOWN" : ticket.getPriority();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Ticket Details | Admin Portal</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f6fa;
            color: #263247;
        }

        .navbar {
            background: #14213d;
            color: white;
            padding: 17px 35px;
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
            margin-left: 20px;
            font-size: 14px;
        }

        .container {
            max-width: 1150px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #2455a4;
            text-decoration: none;
            font-weight: bold;
        }

        .page-heading {
            margin-bottom: 25px;
        }

        .page-heading h1 {
            margin: 0 0 8px;
            color: #14213d;
        }

        .page-heading p {
            color: #667085;
            margin: 0;
        }

        .card {
            background: white;
            border-radius: 10px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 2px 9px rgba(0,0,0,0.06);
        }

        .card h2 {
            margin: 0 0 22px;
            color: #14213d;
            font-size: 19px;
        }

        .details-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 22px;
        }

        .detail-label {
            color: #7b8494;
            font-size: 12px;
            text-transform: uppercase;
            margin-bottom: 7px;
        }

        .detail-value {
            font-size: 14px;
            font-weight: 600;
            overflow-wrap: anywhere;
        }

        .description {
            margin-top: 25px;
            padding: 18px;
            background: #f7f8fb;
            border-radius: 8px;
        }

        .description p {
            line-height: 1.7;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            margin-bottom: 0;
        }

        .badge {
            display: inline-block;
            padding: 5px 11px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            background: #e9eef8;
            color: #294b80;
        }

        .conversation {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .message {
            border: 1px solid #e2e6ed;
            border-radius: 9px;
            padding: 17px;
            background: #fbfcfe;
        }

        .message.internal {
            background: #fff8e9;
            border: 1px solid #e8c77d;
        }

        .message-header {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 10px;
            margin-bottom: 12px;
        }

        .sender {
            font-weight: bold;
            color: #14213d;
        }

        .message-time {
            font-size: 12px;
            color: #7b8494;
        }

        .message-content {
            line-height: 1.7;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            font-size: 14px;
        }

        .internal-label {
            display: inline-block;
            background: #f2d99c;
            color: #704c00;
            padding: 4px 9px;
            border-radius: 5px;
            font-size: 11px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .empty-state {
            text-align: center;
            padding: 35px;
            color: #7b8494;
            background: #f7f8fb;
            border-radius: 8px;
        }

        .footer {
            text-align: center;
            padding: 20px;
            color: #8992a1;
            font-size: 12px;
        }

        @media (max-width: 750px) {
            .details-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .navbar {
                padding: 16px 20px;
            }
        }

        @media (max-width: 480px) {
            .details-grid {
                grid-template-columns: 1fr;
            }

            .card {
                padding: 18px;
            }
        }
    </style>
</head>

<body>

<nav class="navbar">
    <h2>AI Support Admin</h2>

    <div>
        <a href="<%= request.getContextPath() %>/admin/dashboard">
            Dashboard
        </a>

        <a href="<%= request.getContextPath() %>/admin/tickets">
            Tickets
        </a>
    </div>
</nav>

<div class="container">

    <a class="back-link"
       href="<%= request.getContextPath() %>/admin/tickets">
        &larr; Back to Ticket Management
    </a>

    <div class="page-heading">
        <h1>Ticket Details</h1>

        <p>
            Review ticket information and the complete conversation history.
        </p>
    </div>

    <!-- Ticket Information -->

    <section class="card">

        <h2>
            <%= escapeHtml(ticket.getTicketNumber()) %>
        </h2>

        <div class="details-grid">

            <div>
                <div class="detail-label">Subject</div>
                <div class="detail-value">
                    <%= escapeHtml(ticket.getSubject()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Status</div>
                <div class="detail-value">
                    <span class="badge">
                        <%= escapeHtml(status) %>
                    </span>
                </div>
            </div>

            <div>
                <div class="detail-label">Priority</div>
                <div class="detail-value">
                    <span class="badge">
                        <%= escapeHtml(priority) %>
                    </span>
                </div>
            </div>

            <div>
                <div class="detail-label">Customer Name</div>
                <div class="detail-value">
                    <%= escapeHtml(ticket.getCustomerName()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Customer Email</div>
                <div class="detail-value">
                    <%= escapeHtml(ticket.getCustomerEmail()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Customer ID</div>
                <div class="detail-value">
                    <%= ticket.getCustomerId() %>
                </div>
            </div>

            <div>
                <div class="detail-label">Category</div>
                <div class="detail-value">
                    <%= escapeHtml(ticket.getCategoryName()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Assigned Agent</div>
                <div class="detail-value">
                    <%= ticket.getAssignedAgentName() == null
                            ? "Unassigned"
                            : escapeHtml(ticket.getAssignedAgentName()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Agent ID</div>
                <div class="detail-value">
                    <%= ticket.getAssignedAgentId() == null
                            ? "Not assigned"
                            : ticket.getAssignedAgentId() %>
                </div>
            </div>

            <div>
                <div class="detail-label">Source</div>
                <div class="detail-value">
                    <%= escapeHtml(ticket.getSource()) %>
                </div>
            </div>

            <div>
                <div class="detail-label">Created At</div>
                <div class="detail-value">
                    <%= ticket.getCreatedAt() == null
                            ? "N/A" : ticket.getCreatedAt() %>
                </div>
            </div>

            <div>
                <div class="detail-label">Last Updated</div>
                <div class="detail-value">
                    <%= ticket.getUpdatedAt() == null
                            ? "N/A" : ticket.getUpdatedAt() %>
                </div>
            </div>

            <div>
                <div class="detail-label">Resolved At</div>
                <div class="detail-value">
                    <%= ticket.getResolvedAt() == null
                            ? "Not resolved" : ticket.getResolvedAt() %>
                </div>
            </div>

        </div>

        <div class="description">
            <div class="detail-label">Ticket Description</div>

            <p><%= escapeHtml(ticket.getDescription()) %></p>
        </div>

    </section>

    <!-- Conversation History -->

    <section class="card">

        <h2>Conversation History</h2>

        <%
            if (responses == null || responses.isEmpty()) {
        %>

            <div class="empty-state">
                No messages or internal notes have been recorded for this ticket.
            </div>

        <%
            } else {
        %>

            <div class="conversation">

                <%
                    for (TicketResponse message : responses) {
                %>

                    <div class="message <%= message.isInternal()
                            ? "internal" : "" %>">

                        <div class="message-header">

                            <div>
                                <span class="sender">
                                    <%= escapeHtml(message.getSenderName()) %>
                                </span>

                                <span class="badge">
                                    <%= escapeHtml(message.getSenderRole()) %>
                                </span>
                            </div>

                            <span class="message-time">
                                <%= message.getCreatedAt() == null
                                        ? "N/A" : message.getCreatedAt() %>
                            </span>

                        </div>

                        <%
                            if (message.isInternal()) {
                        %>

                            <div class="internal-label">
                                INTERNAL NOTE — ADMIN/STAFF ONLY
                            </div>

                        <%
                            } else {
                        %>

                            <div class="badge">
                                PUBLIC REPLY
                            </div>

                        <%
                            }
                        %>

                        <div class="message-content">
                            <%= escapeHtml(message.getMessage()) %>
                        </div>

                    </div>

                <%
                    }
                %>

            </div>

        <%
            }
        %>

    </section>

</div>

<div class="footer">
    AI Customer Support System | Administrator Portal
</div>

</body>
</html>
