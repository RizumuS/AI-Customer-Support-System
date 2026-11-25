
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.aisupport.model.Ticket" %>
<%@ page import="com.aisupport.model.Agent" %>
<%@ page import="java.util.Arrays" %>

<%
    List<Ticket> tickets =
            (List<Ticket>) request.getAttribute("tickets");

    if (tickets == null) {
        tickets = new ArrayList<Ticket>();
    }

    List<Agent> agents =
            (List<Agent>) request.getAttribute("agents");

    if (agents == null) {
        agents = new ArrayList<Agent>();
    }

    String statusMessage = request.getParameter("status");
    String errorMessage = request.getParameter("error");

    String assignmentMessage =
            request.getParameter("assignment");

    String assignmentError =
            request.getParameter("assignmentError");

    List<String> validStatuses = Arrays.asList(
            "OPEN",
            "IN_PROGRESS",
            "WAITING_FOR_CUSTOMER",
            "RESOLVED",
            "CLOSED"
    );
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Manage Tickets | AI Customer Support</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263238;
        }

        .navbar {
            background: #292d3e;
            color: white;
            padding: 18px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .navbar h2 {
            margin: 0;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            background: #4b55a1;
            padding: 10px 15px;
            border-radius: 5px;
        }

        .container {
            max-width: 1450px;
            margin: 35px auto;
            padding: 0 20px;
        }

        .heading {
            margin-bottom: 25px;
        }

        .heading h1 {
            margin-bottom: 8px;
        }

        .summary {
            background: white;
            padding: 18px 22px;
            border-radius: 8px;
            margin-bottom: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        .table-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            overflow-x: auto;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 1450px;
        }

        th, td {
            padding: 13px 12px;
            text-align: left;
            border-bottom: 1px solid #e5e8ec;
        }

        th {
            background: #f0f2f7;
            color: #292d3e;
        }

        tr:hover {
            background: #f8f9fc;
        }

        .status, .priority {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
        }

        .status {
            background: #e8edff;
            color: #35449b;
        }

        .priority {
            background: #fff0d9;
            color: #9b6200;
        }

        .status-form,
        .assignment-form {
            display: flex;
            flex-direction: column;
            gap: 8px;
            min-width: 160px;
        }

        .status-form select,
        .assignment-form select {
            padding: 8px;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
            background: white;
            font-size: 12px;
            width: 100%;
        }

        .update-button {
            padding: 8px 10px;
            border: none;
            border-radius: 5px;
            background: #4b55a1;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        .update-button:hover {
            background: #363f83;
        }

        .assign-button {
            padding: 8px 10px;
            border: none;
            border-radius: 5px;
            background: #238653;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        .assign-button:hover {
            background: #17683e;
        }

        .message {
            padding: 13px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-weight: bold;
        }

        .success {
            background: #e4f5e9;
            color: #216b38;
            border: 1px solid #b9e2c5;
        }

        .error {
            background: #fde8e8;
            color: #a52828;
            border: 1px solid #f2c1c1;
        }

        .empty {
            text-align: center;
            padding: 35px;
            color: #65727e;
        }

        .agent-availability {
            display: block;
            color: #65727e;
            font-size: 11px;
            margin-top: 4px;
        }

        @media (max-width: 600px) {
            .navbar {
                padding: 16px 20px;
            }

            .navbar h2 {
                font-size: 17px;
            }
        }
    </style>
</head>

<body>

<div class="navbar">

    <h2>AI Customer Support - Admin</h2>

    <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
        Back to Dashboard
    </a>

</div>

<div class="container">

    <div class="heading">

        <h1>Ticket Management</h1>

        <p>
            View, monitor and manage all support tickets in the system.
        </p>

    </div>

    <% if ("updated".equals(statusMessage)) { %>

        <div class="message success">
            Ticket status updated successfully.
        </div>

    <% } else if (errorMessage != null) { %>

        <div class="message error">
            Unable to update ticket status.
            <%= errorMessage.equals("update_failed")
                    ? "Please check the ticket and try again."
                    : errorMessage %>
        </div>

    <% } %>

    <% if ("updated".equals(assignmentMessage)) { %>

        <div class="message success">
            Ticket assigned or reassigned successfully.
        </div>

    <% } else if ("failed".equals(assignmentMessage)) { %>

        <div class="message error">
            Unable to assign the ticket. Please check the ticket and agent.
        </div>

    <% } else if ("invalid".equals(assignmentMessage)) { %>

        <div class="message error">
            Invalid ticket ID or agent ID.
        </div>

    <% } %>

    <% if (assignmentError != null) { %>

        <div class="message error">
            Assignment error: <%= assignmentError %>
        </div>

    <% } %>

    <div class="summary">

        <strong>Total Tickets:</strong>
        <%= tickets.size() %>

        &nbsp;&nbsp; | &nbsp;&nbsp;

        <strong>Active Agents:</strong>
        <%= agents.size() %>

    </div>

    <div class="table-container">

        <% if (tickets.isEmpty()) { %>

            <div class="empty">
                <h3>No tickets found.</h3>
                <p>There are currently no support tickets in the system.</p>
            </div>

        <% } else { %>

            <table>

                <thead>

                    <tr>
                        <th>Ticket Number</th>
                        <th>Subject</th>
                        <th>Customer ID</th>
                        <th>Category</th>
                        <th>Assigned Agent</th>
                        <th>Priority</th>
                        <th>Current Status</th>
                        <th>Source</th>
                        <th>Created At</th>
                        <th>Manage Status</th>
                        <th>Manage Assignment</th>
                    </tr>

                </thead>

                <tbody>

                    <% for (Ticket ticket : tickets) { %>

                        <tr>

                            <td>
                                <%= ticket.getTicketNumber() %>
                            </td>

                            <td>
                                <%= ticket.getSubject() %>
                            </td>

                            <td>
                                <%= ticket.getCustomerId() %>
                            </td>

                            <td>
                                <%= ticket.getCategoryName() != null
                                        ? ticket.getCategoryName()
                                        : "Uncategorized" %>
                            </td>

                            <td>
                                <%= ticket.getAssignedAgentName() != null
                                        ? ticket.getAssignedAgentName()
                                        : "Unassigned" %>
                            </td>

                            <td>
                                <span class="priority">
                                    <%= ticket.getPriority() %>
                                </span>
                            </td>

                            <td>
                                <span class="status">
                                    <%= ticket.getStatus() %>
                                </span>
                            </td>

                            <td>
                                <%= ticket.getSource() %>
                            </td>

                            <td>
                                <%= ticket.getCreatedAt() != null
                                        ? ticket.getCreatedAt()
                                        : "N/A" %>
                            </td>

                            <!-- Ticket status management -->

                            <td>

                                <form
                                    class="status-form"
                                    method="post"
                                    action="${pageContext.request.contextPath}/admin/tickets/status"
                                    onsubmit="return confirmStatusUpdate(this);"
                                >

                                    <input
                                        type="hidden"
                                        name="ticketId"
                                        value="<%= ticket.getTicketId() %>"
                                    >

                                    <select name="status" required>

                                        <% for (String validStatus : validStatuses) { %>

                                            <option
                                                value="<%= validStatus %>"
                                                <%= validStatus.equalsIgnoreCase(ticket.getStatus())
                                                        ? "selected"
                                                        : "" %>
                                            >
                                                <%= validStatus.replace("_", " ") %>
                                            </option>

                                        <% } %>

                                    </select>

                                    <button
                                        type="submit"
                                        class="update-button"
                                    >
                                        Update Status
                                    </button>

                                </form>

                            </td>

                            <!-- Ticket assignment management -->

                            <td>

                                <form
                                    class="assignment-form"
                                    method="post"
                                    action="${pageContext.request.contextPath}/admin/tickets/assign"
                                    onsubmit="return confirmAssignment(this);"
                                >

                                    <input
                                        type="hidden"
                                        name="ticketId"
                                        value="<%= ticket.getTicketId() %>"
                                    >

                                    <select name="agentId" required>

                                        <option value="">
                                            Select Agent
                                        </option>

                                        <% for (Agent agent : agents) { %>

                                            <option
                                                value="<%= agent.getAgentId() %>"
                                                <%= ticket.getAssignedAgentId() != null
                                                        && ticket.getAssignedAgentId() == agent.getAgentId()
                                                        ? "selected"
                                                        : "" %>
                                            >
                                                <%= agent.getFullName() %>
                                                (<%= agent.getAvailability() %>)
                                            </option>

                                        <% } %>

                                    </select>

                                    <button
                                        type="submit"
                                        class="assign-button"
                                        <%= agents.isEmpty() ? "disabled" : "" %>
                                    >
                                        Assign / Reassign
                                    </button>

                                </form>

                                <% if (agents.isEmpty()) { %>

                                    <small>
                                        No active agents available.
                                    </small>

                                <% } %>

                            </td>

                        </tr>

                    <% } %>

                </tbody>

            </table>

        <% } %>

    </div>

</div>

<script>

    function confirmStatusUpdate(form) {

        const selectedStatus =
            form.elements["status"].value.replaceAll("_", " ");

        return confirm(
            "Are you sure you want to change this ticket's status to "
            + selectedStatus + "?"
        );
    }

    function confirmAssignment(form) {

        const selectedAgent =
            form.elements["agentId"].selectedOptions[0].text;

        return confirm(
            "Are you sure you want to assign or reassign this ticket to "
            + selectedAgent + "?"
        );
    }

</script>

</body>
</html>
