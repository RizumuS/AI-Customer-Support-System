<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.Ticket" %>
<%@ page import="com.aisupport.model.TicketResponse" %>

<%!
    // Escape HTML to prevent message content from being interpreted as HTML
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    Ticket ticket = (Ticket) request.getAttribute("ticket");

    List<TicketResponse> responses =
            (List<TicketResponse>) request.getAttribute("responses");

    if (ticket == null) {
        response.sendRedirect(
            request.getContextPath() + "/customer/tickets"
        );
        return;
    }

    if (responses == null) {
        responses = new java.util.ArrayList<>();
    }

    boolean messageSent =
            "true".equals(request.getParameter("sent"));
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ticket Conversation</title>

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
            margin: 20px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
        }

        h2 {
            margin-top: 0;
            color: #263445;
        }

        .ticket-info {
            background: #f1f5f9;
            padding: 15px;
            border-radius: 7px;
            margin-bottom: 25px;
        }

        .ticket-info p {
            margin: 7px 0;
        }

        .success-message {
            background: #dcfce7;
            color: #166534;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .conversation {
            margin-bottom: 30px;
        }

        .message {
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 8px;
            background: #f1f5f9;
            border-left: 4px solid #64748b;
        }

        .message.customer {
            background: #eff6ff;
            border-left-color: #2563eb;
        }

        .message.agent {
            background: #f0fdf4;
            border-left-color: #16a34a;
        }

        .message-header {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 12px;
        }

        .sender {
            font-weight: bold;
            color: #263445;
        }

        .timestamp {
            color: #64748b;
            font-size: 12px;
        }

        .message-content {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            line-height: 1.6;
        }

        .empty-message {
            text-align: center;
            padding: 35px 15px;
            color: #64748b;
            background: #f8fafc;
            border-radius: 8px;
        }

        .reply-section {
            border-top: 1px solid #e5e7eb;
            padding-top: 25px;
        }

        .reply-section h3 {
            color: #263445;
        }

        textarea {
            width: 100%;
            min-height: 120px;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            resize: vertical;
            font-family: Arial, sans-serif;
            font-size: 14px;
        }

        textarea:focus {
            outline: none;
            border-color: #2563eb;
        }

        .send-button {
            margin-top: 12px;
            padding: 11px 20px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .send-button:hover {
            background: #1d4ed8;
        }

        .back-link {
            display: inline-block;
            margin-top: 25px;
            color: #2563eb;
            text-decoration: none;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        @media (max-width: 600px) {
            body {
                padding: 12px;
            }

            .container {
                padding: 20px;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Ticket Conversation</h2>

    <div class="ticket-info">
        <p>
            <strong>Ticket Number:</strong>
            <%= escapeHtml(ticket.getTicketNumber()) %>
        </p>

        <p>
            <strong>Subject:</strong>
            <%= escapeHtml(ticket.getSubject()) %>
        </p>

        <p>
            <strong>Status:</strong>
            <%= escapeHtml(ticket.getStatus()) %>
        </p>
    </div>

    <% if (messageSent) { %>
        <div class="success-message">
            Your message was sent successfully.
        </div>
    <% } %>

    <div class="conversation">

        <h3>Conversation History</h3>

        <% if (responses.isEmpty()) { %>

            <div class="empty-message">
                No replies yet. Send a message to start the conversation.
            </div>

        <% } else { %>

            <% for (TicketResponse item : responses) {

                String senderRole = item.getSenderRole();

                String messageClass = "message";

                if ("CUSTOMER".equalsIgnoreCase(senderRole)) {
                    messageClass += " customer";
                } else if ("AGENT".equalsIgnoreCase(senderRole)) {
                    messageClass += " agent";
                }
            %>

                <div class="<%= messageClass %>">

                    <div class="message-header">

                        <span class="sender">
                            <%= escapeHtml(item.getSenderName()) %>
                            (<%= escapeHtml(senderRole) %>)
                        </span>

                        <span class="timestamp">
                            <%= item.getCreatedAt() != null
                                ? escapeHtml(item.getCreatedAt().toString())
                                : "-" %>
                        </span>

                    </div>

                    <div class="message-content"><%= escapeHtml(item.getMessage()) %></div>

                </div>

            <% } %>

        <% } %>

    </div>

    <div class="reply-section">

        <h3>Send a Reply</h3>

        <form method="POST"
              action="<%= request.getContextPath() %>/customer/ticket/conversation">

            <input type="hidden"
                   name="ticketId"
                   value="<%= ticket.getTicketId() %>">

            <textarea
                name="message"
                placeholder="Type your message here..."
                maxlength="5000"
                required></textarea>

            <button type="submit" class="send-button">
                Send Message
            </button>

        </form>

    </div>

    <a class="back-link"
       href="<%= request.getContextPath() %>/customer/ticket?id=<%= ticket.getTicketId() %>">
        Back to Ticket Details
    </a>

</div>

</body>
</html>
