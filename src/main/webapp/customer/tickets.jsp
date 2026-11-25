<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.Ticket" %>

<%
    List<Ticket> tickets =
            (List<Ticket>) request.getAttribute("tickets");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

    String successMessage =
            (String) session.getAttribute("successMessage");

    session.removeAttribute("successMessage");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Support Tickets</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 30px;
        }

        .container {
            max-width: 1100px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        h2 {
            color: #263445;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .button {
            display: inline-block;
            padding: 10px 16px;
            background: #2563eb;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        .button:hover {
            background: #1d4ed8;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 13px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background: #263445;
            color: white;
        }

        tr:hover {
            background: #f8fafc;
        }

        .status {
            font-weight: bold;
        }

        .empty {
            padding: 25px;
            text-align: center;
            color: #666;
        }

        .error {
            color: #b91c1c;
            background: #fee2e2;
            padding: 12px;
            border-radius: 5px;
        }

        .success {
    color: #166534;
    background: #dcfce7;
    padding: 12px;
    border-radius: 5px;
    margin-bottom: 15px;
}

        .back-link {
            display: inline-block;
            margin-top: 25px;
            color: #2563eb;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="top-bar">
        <h2>My Support Tickets</h2>

        <a class="button"
           href="<%= request.getContextPath() %>/customer/dashboard.jsp">
            Dashboard
        </a>
    </div>

    <% if (successMessage != null) { %>

    <p class="success"><%= successMessage %></p>

<% } %>

<% if (errorMessage != null) { %>

    <p class="error"><%= errorMessage %></p>

<% } %>

<% if (tickets == null || tickets.isEmpty()) { %>

        <div class="empty">
            <h3>No support tickets found.</h3>
            <p>You haven't created any support tickets yet.</p>
        </div>

    <% } else { %>

        <table>

            <thead>
            <tr>
                <th>Ticket Number</th>
                <th>Subject</th>
                <th>Category</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Assigned Agent</th>
                <th>Created At</th>
            </tr>
            </thead>

            <tbody>

            <% for (Ticket ticket : tickets) { %>

                <tr>
                    <td>
    <a href="<%= request.getContextPath() %>/customer/ticket?id=<%= ticket.getTicketId() %>">
        <%= ticket.getTicketNumber() %>
    </a>
</td>

                    <td><%= ticket.getSubject() %></td>

                    <td>
                        <%= ticket.getCategoryName() != null
                                ? ticket.getCategoryName()
                                : "Uncategorized" %>
                    </td>

                    <td><%= ticket.getPriority() %></td>

                    <td class="status">
                        <%= ticket.getStatus() %>
                    </td>

                    <td>
                        <%= ticket.getAssignedAgentName() != null
                                ? ticket.getAssignedAgentName()
                                : "Not assigned" %>
                    </td>

                    <td>
                        <%= ticket.getCreatedAt() != null
                                ? ticket.getCreatedAt()
                                : "-" %>
                    </td>
                </tr>

            <% } %>

            </tbody>

        </table>

    <% } %>

    <a class="back-link"
       href="<%= request.getContextPath() %>/customer/dashboard.jsp">
        ← Back to Dashboard
    </a>

</div>

</body>
</html>
