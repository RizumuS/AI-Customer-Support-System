<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.aisupport.model.Ticket" %>

<%
    Ticket ticket = (Ticket) request.getAttribute("ticket");

    if (ticket == null) {
        response.sendRedirect(
            request.getContextPath() + "/customer/tickets"
        );
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ticket Details</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 30px;
            color: #333;
        }

        .container {
            max-width: 850px;
            margin: 30px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
        }

        h2 {
            margin-top: 0;
            color: #263445;
        }

        .ticket-number {
            color: #64748b;
            margin-bottom: 25px;
        }

        .details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 22px;
        }

        .detail-item {
            padding-bottom: 12px;
            border-bottom: 1px solid #eee;
        }

        .detail-item.full {
            grid-column: 1 / -1;
        }

        .label {
            display: block;
            color: #64748b;
            font-size: 13px;
            margin-bottom: 7px;
        }

        .value {
            font-size: 15px;
            font-weight: 600;
            overflow-wrap: anywhere;
        }

        .description {
            font-weight: normal;
            line-height: 1.6;
            white-space: pre-wrap;
        }

        .status {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            background: #e0f2fe;
            color: #0369a1;
            font-size: 13px;
        }

        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 30px;
        }

        .action-button {
            display: inline-block;
            padding: 11px 18px;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        .conversation-button {
            background: #2563eb;
        }

        .conversation-button:hover {
            background: #1d4ed8;
        }

        .back-button {
            background: #263445;
        }

        .back-button:hover {
            background: #3b5068;
        }

        @media (max-width: 600px) {
            body {
                padding: 12px;
            }

            .container {
                padding: 20px;
            }

            .details {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Ticket Details</h2>

    <p class="ticket-number">
        Ticket Number: <strong><%= ticket.getTicketNumber() %></strong>
    </p>

    <div class="details">

        <div class="detail-item">
            <span class="label">Subject</span>
            <div class="value">
                <%= ticket.getSubject() %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Category</span>
            <div class="value">
                <%= ticket.getCategoryName() != null
                    ? ticket.getCategoryName()
                    : "Uncategorized" %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Priority</span>
            <div class="value">
                <%= ticket.getPriority() %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Status</span>
            <div class="value">
                <span class="status">
                    <%= ticket.getStatus() %>
                </span>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Assigned Agent</span>
            <div class="value">
                <%= ticket.getAssignedAgentName() != null
                    ? ticket.getAssignedAgentName()
                    : "Not assigned" %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Created At</span>
            <div class="value">
                <%= ticket.getCreatedAt() != null
                    ? ticket.getCreatedAt()
                    : "-" %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Last Updated</span>
            <div class="value">
                <%= ticket.getUpdatedAt() != null
                    ? ticket.getUpdatedAt()
                    : "-" %>
            </div>
        </div>

        <div class="detail-item">
            <span class="label">Resolved At</span>
            <div class="value">
                <%= ticket.getResolvedAt() != null
                    ? ticket.getResolvedAt()
                    : "-" %>
            </div>
        </div>

        <div class="detail-item full">
            <span class="label">Description</span>
            <div class="value description"><%= ticket.getDescription() %></div>
        </div>

    </div>

    <div class="actions">

        <a class="action-button conversation-button"
           href="<%= request.getContextPath() %>/customer/ticket/conversation?id=<%= ticket.getTicketId() %>">
            View Conversation
        </a>

        <a class="action-button back-button"
           href="<%= request.getContextPath() %>/customer/tickets">
            Back to My Tickets
        </a>

    </div>

</div>

</body>
</html>
