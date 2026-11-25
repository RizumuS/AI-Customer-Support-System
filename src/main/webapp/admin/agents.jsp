<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.Agent" %>

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
    List<Agent> agents =
            (List<Agent>) request.getAttribute("agents");

    if (agents == null) {
        agents = java.util.Collections.emptyList();
    }

    int totalAgents = agents.size();
    int activeAgents = 0;
    int inactiveAgents = 0;
    int availableAgents = 0;
    int busyAgents = 0;
    int offlineAgents = 0;

    for (Agent agent : agents) {

        if (agent.isActive()) {
            activeAgents++;
        } else {
            inactiveAgents++;
        }

        String availability = agent.getAvailability();

        if ("AVAILABLE".equalsIgnoreCase(availability)) {
            availableAgents++;
        } else if ("BUSY".equalsIgnoreCase(availability)) {
            busyAgents++;
        } else if ("OFFLINE".equalsIgnoreCase(availability)) {
            offlineAgents++;
        }
    }

    String creationStatus =
            request.getParameter("agentCreation");

    String creationError =
            request.getParameter("agentCreationError");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Agent Management | Admin</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 30px;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263238;
        }

        .container {
            max-width: 1450px;
            margin: auto;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 25px;
        }

        h1 {
            margin: 0 0 8px;
            color: #1e293b;
        }

        .subtitle {
            color: #64748b;
            margin: 0;
        }

        .back-link {
            display: inline-block;
            padding: 10px 16px;
            background: #334155;
            color: white;
            text-decoration: none;
            border-radius: 6px;
        }

        .back-link:hover {
            background: #1e293b;
        }

        .summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
            gap: 15px;
            margin-bottom: 25px;
        }

        .summary-card {
            background: white;
            padding: 20px;
            border-radius: 9px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }

        .summary-card h3 {
            margin: 0 0 12px;
            font-size: 14px;
            color: #64748b;
        }

        .summary-card .number {
            font-size: 27px;
            font-weight: bold;
            color: #1e293b;
        }

        .form-container,
        .table-container {
            background: white;
            padding: 22px;
            border-radius: 9px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            margin-bottom: 25px;
        }

        .section-heading {
            margin: 0 0 18px;
            font-size: 19px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 7px;
        }

        .form-group label {
            font-size: 13px;
            font-weight: bold;
            color: #475569;
        }

        .form-group input,
        .form-group select {
            padding: 11px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 14px;
        }

        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #4b55a1;
        }

        .submit-button {
            margin-top: 20px;
            padding: 11px 20px;
            background: #4b55a1;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
        }

        .submit-button:hover {
            background: #363f83;
        }

        .message {
            padding: 13px;
            border-radius: 6px;
            margin-bottom: 18px;
            font-size: 14px;
        }

        .success-message {
            background: #e4f5e9;
            color: #216b38;
            border: 1px solid #b9e2c5;
        }

        .error-message {
            background: #fde8e8;
            color: #a52828;
            border: 1px solid #f2c1c1;
        }

        .table-container {
            overflow-x: auto;
        }

        .table-heading {
            margin: 0 0 18px;
            font-size: 19px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 1100px;
        }

        th, td {
            padding: 13px 12px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
            font-size: 13px;
        }

        th {
            background: #f8fafc;
            color: #475569;
            font-weight: bold;
            white-space: nowrap;
        }

        tr:hover td {
            background: #f8fafc;
        }

        .status {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: bold;
            white-space: nowrap;
        }

        .available,
        .active {
            background: #dcfce7;
            color: #166534;
        }

        .busy {
            background: #fef3c7;
            color: #92400e;
        }

        .offline {
            background: #e2e8f0;
            color: #475569;
        }

        .inactive {
            background: #fee2e2;
            color: #991b1b;
        }

        .empty-message {
            padding: 30px;
            text-align: center;
            color: #64748b;
        }

        @media (max-width: 600px) {
            body {
                padding: 15px;
            }

            .table-container,
            .form-container {
                padding: 12px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">

        <div>
            <h1>Agent Management</h1>
            <p class="subtitle">
                Create agent accounts and manage registered agents,
                account status and ticket workload.
            </p>
        </div>

        <a class="back-link"
           href="${pageContext.request.contextPath}/admin/dashboard.jsp">
            Back to Dashboard
        </a>

    </div>

    <% if ("success".equals(creationStatus)) { %>

        <div class="message success-message">
            Agent account created successfully.
            The agent can now log in using their email and password.
        </div>

    <% } else if ("failed".equals(creationStatus)) { %>

        <div class="message error-message">
            Agent account could not be created. Please try again.
        </div>

    <% } %>

    <% if (creationError != null && !creationError.isBlank()) { %>

        <div class="message error-message">
            <%= escapeHtml(creationError) %>
        </div>

    <% } %>

    <div class="form-container">

        <h2 class="section-heading">Create New Agent</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/admin/agents/create">

            <div class="form-grid">

                <div class="form-group">
                    <label for="fullName">Full Name</label>
                    <input type="text"
                           id="fullName"
                           name="fullName"
                           maxlength="100"
                           required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email"
                           id="email"
                           name="email"
                           maxlength="150"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password"
                           id="password"
                           name="password"
                           minlength="8"
                           required>
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel"
                           id="phone"
                           name="phone"
                           maxlength="20">
                </div>

                <div class="form-group">
                    <label for="department">Department</label>
                    <select id="department"
                            name="department"
                            required>

                        <option value="">Select Department</option>
                        <option value="Customer Support">Customer Support</option>
                        <option value="Technical Support">Technical Support</option>
                        <option value="Billing">Billing</option>
                        <option value="Sales">Sales</option>
                        <option value="General Support">General Support</option>

                    </select>
                </div>

            </div>

            <button type="submit" class="submit-button">
                Create Agent Account
            </button>

        </form>

    </div>

    <div class="summary">

        <div class="summary-card">
            <h3>Total Agents</h3>
            <div class="number"><%= totalAgents %></div>
        </div>

        <div class="summary-card">
            <h3>Active Accounts</h3>
            <div class="number"><%= activeAgents %></div>
        </div>

        <div class="summary-card">
            <h3>Inactive Accounts</h3>
            <div class="number"><%= inactiveAgents %></div>
        </div>

        <div class="summary-card">
            <h3>Available Agents</h3>
            <div class="number"><%= availableAgents %></div>
        </div>

        <div class="summary-card">
            <h3>Busy Agents</h3>
            <div class="number"><%= busyAgents %></div>
        </div>

        <div class="summary-card">
            <h3>Offline Agents</h3>
            <div class="number"><%= offlineAgents %></div>
        </div>

    </div>

    <div class="table-container">

        <h2 class="table-heading">Registered Agents</h2>

        <% if (agents.isEmpty()) { %>

            <div class="empty-message">
                No agents are currently registered.
            </div>

        <% } else { %>

            <table>

                <thead>
                    <tr>
                        <th>Agent ID</th>
                        <th>Agent Name</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Availability</th>
                        <th>Account Status</th>
                        <th>Total Tickets</th>
                        <th>Open</th>
                        <th>In Progress</th>
                        <th>Resolved</th>
                    </tr>
                </thead>

                <tbody>

                <% for (Agent agent : agents) {

                    String availability =
                            agent.getAvailability() == null
                                    ? "UNKNOWN"
                                    : agent.getAvailability().toUpperCase();

                    String availabilityClass = "offline";

                    if ("AVAILABLE".equals(availability)) {
                        availabilityClass = "available";
                    } else if ("BUSY".equals(availability)) {
                        availabilityClass = "busy";
                    }

                %>

                    <tr>

                        <td><%= agent.getAgentId() %></td>

                        <td>
                            <%= escapeHtml(agent.getFullName()) %>
                        </td>

                        <td>
                            <%= escapeHtml(agent.getEmail()) %>
                        </td>

                        <td>
                            <%= escapeHtml(agent.getDepartment()) %>
                        </td>

                        <td>
                            <span class="status <%= availabilityClass %>">
                                <%= escapeHtml(availability) %>
                            </span>
                        </td>

                        <td>

                            <% if (agent.isActive()) { %>

                                <span class="status active">
                                    ACTIVE
                                </span>

                            <% } else { %>

                                <span class="status inactive">
                                    INACTIVE
                                </span>

                            <% } %>

                        </td>

                        <td><%= agent.getTotalTickets() %></td>
                        <td><%= agent.getOpenTickets() %></td>
                        <td><%= agent.getInProgressTickets() %></td>
                        <td><%= agent.getResolvedTickets() %></td>

                    </tr>

                <% } %>

                </tbody>

            </table>

        <% } %>

    </div>

</div>

</body>
</html>
