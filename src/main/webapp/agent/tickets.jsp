<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.Ticket" %>
<%@ page import="com.aisupport.model.Agent" %>

<%!
    // Escape HTML to prevent user-provided content from
    // being interpreted as HTML or JavaScript.
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
    List<Ticket> tickets =
            (List<Ticket>) request.getAttribute("tickets");

    Agent agent =
            (Agent) request.getAttribute("agent");

    if (tickets == null) {
        tickets = java.util.Collections.emptyList();
    }

    if (agent == null) {
        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Agent information is unavailable."
        );
        return;
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Assigned Tickets - AI Customer Support</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
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
            max-width: 1200px;
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

        .summary-card {
            background: white;
            padding: 22px;
            border-radius: 9px;
            margin-bottom: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        .summary-card h3 {
            margin-top: 0;
            color: #1e293b;
        }

        .ticket-count {
            font-size: 30px;
            font-weight: bold;
            color: #2563eb;
        }

        .table-container {
            background: white;
            border-radius: 9px;
            overflow-x: auto;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 850px;
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background: #e2e8f0;
            color: #334155;
            font-size: 13px;
            text-transform: uppercase;
        }

        tr:hover td {
            background: #f8fafc;
        }

        .ticket-number {
            font-weight: bold;
            color: #2563eb;
        }

        .status, .priority {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            white-space: nowrap;
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

        .view-button {
            display: inline-block;
            text-decoration: none;
            background: #2563eb;
            color: white;
            padding: 8px 13px;
            border-radius: 5px;
            font-size: 13px;
            white-space: nowrap;
        }

        .empty-state {
            background: white;
            padding: 45px 20px;
            text-align: center;
            border-radius: 9px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        .empty-state h3 {
            color: #334155;
        }

        .empty-state p {
            color: #64748b;
        }

        @media (max-width: 600px) {

            .navbar {
                padding: 16px;
            }

            .container {
                margin: 22px auto;
                padding: 0 12px;
            }

            .page-header {
                align-items: flex-start;
                flex-direction: column;
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

                <h1>Assigned Tickets</h1>

                <p>
                    Welcome,
                    <strong><%= escapeHtml(agent.getFullName()) %></strong>.
                    Manage the tickets assigned to you.
                </p>

            </div>

            <a class="back-button"
               href="<%= contextPath %>/agent/dashboard.jsp">

                Back to Dashboard

            </a>

        </div>

        <section class="summary-card">

            <h3>Your Assigned Tickets</h3>

            <div class="ticket-count">
                <%= tickets.size() %>
            </div>

            <p>Total tickets currently assigned to you.</p>

        </section>

        <% if (tickets.isEmpty()) { %>

            <section class="empty-state">

                <h3>No Assigned Tickets</h3>

                <p>
                    You currently do not have any tickets assigned to you.
                </p>

            </section>

        <% } else { %>

            <section class="table-container">

                <table>

                    <thead>

                        <tr>
                            <th>Ticket Number</th>
                            <th>Subject</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Action</th>
                        </tr>

                    </thead>

                    <tbody>

                        <% for (Ticket ticket : tickets) {

                            String status = ticket.getStatus() == null
                                    ? ""
                                    : ticket.getStatus().toLowerCase();

                            String priority = ticket.getPriority() == null
                                    ? ""
                                    : ticket.getPriority().toLowerCase();

                            String statusClass = status.replace(" ", "_");
                            String priorityClass = priority.replace(" ", "_");

                        %>

                            <tr>

                                <td class="ticket-number">
                                    <%= escapeHtml(ticket.getTicketNumber()) %>
                                </td>

                                <td>
                                    <%= escapeHtml(ticket.getSubject()) %>
                                </td>

                                <td>
                                    <%= escapeHtml(ticket.getCategoryName()) %>
                                </td>

                                <td>
                                    <span class="priority priority-<%= priorityClass %>">
                                        <%= escapeHtml(ticket.getPriority()) %>
                                    </span>
                                </td>

                                <td>
                                    <span class="status status-<%= statusClass %>">
                                        <%= escapeHtml(ticket.getStatus()) %>
                                    </span>
                                </td>

                                <td>
                                    <%= ticket.getCreatedAt() == null
                                            ? "N/A"
                                            : ticket.getCreatedAt() %>
                                </td>

                                <td>
                                    <a class="view-button"
                                       href="<%= contextPath %>/agent/ticket?id=<%= ticket.getTicketId() %>">

                                        View Ticket

                                    </a>
                                </td>

                            </tr>

                        <% } %>

                    </tbody>

                </table>

            </section>

        <% } %>

    </main>

</body>

</html>
