<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aisupport.model.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

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
    List<User> users =
            (List<User>) request.getAttribute("users");

    if (users == null) {
        users = java.util.Collections.emptyList();
    }

    User loggedInUser =
            (User) session.getAttribute("loggedInUser");

    int loggedInAdminId =
            loggedInUser != null ? loggedInUser.getUserId() : -1;

    int totalUsers = users.size();
    int activeUsers = 0;
    int inactiveUsers = 0;
    int customerCount = 0;
    int agentCount = 0;
    int adminCount = 0;

    for (User user : users) {

        if (user.isActive()) {
            activeUsers++;
        } else {
            inactiveUsers++;
        }

        String role = user.getRole();

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            customerCount++;
        } else if ("AGENT".equalsIgnoreCase(role)) {
            agentCount++;
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            adminCount++;
        }
    }

    String statusMessage = request.getParameter("status");
    String errorMessage = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>User Management | Admin</title>

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
            grid-template-columns:
                repeat(auto-fit, minmax(170px, 1fr));
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

        .table-container {
            background: white;
            padding: 22px;
            border-radius: 9px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            overflow-x: auto;
        }

        .table-heading {
            margin: 0 0 18px;
            font-size: 19px;
        }

        table {
            width: 100%;
            min-width: 1200px;
            border-collapse: collapse;
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

        .active {
            background: #dcfce7;
            color: #166534;
        }

        .inactive {
            background: #fee2e2;
            color: #991b1b;
        }

        .role {
            background: #e0e7ff;
            color: #3730a3;
        }

        .empty-message {
            padding: 30px;
            text-align: center;
            color: #64748b;
        }

        .action-form {
            margin: 0;
        }

        .action-button {
            padding: 8px 12px;
            border: none;
            border-radius: 5px;
            font-size: 12px;
            font-weight: bold;
            cursor: pointer;
            white-space: nowrap;
        }

        .activate-button {
            background: #dcfce7;
            color: #166534;
        }

        .activate-button:hover {
            background: #bbf7d0;
        }

        .deactivate-button {
            background: #fee2e2;
            color: #991b1b;
        }

        .deactivate-button:hover {
            background: #fecaca;
        }

        .current-account {
            display: inline-block;
            padding: 7px 10px;
            background: #e0e7ff;
            color: #3730a3;
            border-radius: 5px;
            font-size: 11px;
            font-weight: bold;
            white-space: nowrap;
        }

        .message {
            padding: 13px 16px;
            margin-bottom: 18px;
            border-radius: 6px;
            font-size: 14px;
        }

        .success-message {
            background: #dcfce7;
            color: #166534;
        }

        .error-message {
            background: #fee2e2;
            color: #991b1b;
        }

        @media (max-width: 600px) {
            body {
                padding: 15px;
            }

            .table-container {
                padding: 12px;
            }
        }
    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <div>
            <h1>User Management</h1>

            <p class="subtitle">
                View registered users and monitor account information.
            </p>
        </div>

        <a class="back-link"
           href="${pageContext.request.contextPath}/admin/dashboard.jsp">
            Back to Dashboard
        </a>

    </div>

    <% if ("updated".equals(statusMessage)) { %>

        <div class="message success-message">
            User account status updated successfully.
        </div>

    <% } else if (errorMessage != null) { %>

        <div class="message error-message">
            <%= escapeHtml(errorMessage) %>
        </div>

    <% } %>

    <div class="summary">

        <div class="summary-card">
            <h3>Total Users</h3>
            <div class="number"><%= totalUsers %></div>
        </div>

        <div class="summary-card">
            <h3>Active Accounts</h3>
            <div class="number"><%= activeUsers %></div>
        </div>

        <div class="summary-card">
            <h3>Inactive Accounts</h3>
            <div class="number"><%= inactiveUsers %></div>
        </div>

        <div class="summary-card">
            <h3>Customers</h3>
            <div class="number"><%= customerCount %></div>
        </div>

        <div class="summary-card">
            <h3>Agents</h3>
            <div class="number"><%= agentCount %></div>
        </div>

        <div class="summary-card">
            <h3>Administrators</h3>
            <div class="number"><%= adminCount %></div>
        </div>

    </div>

    <div class="table-container">

        <h2 class="table-heading">Registered Users</h2>

        <% if (users.isEmpty()) { %>

            <div class="empty-message">
                No registered users found.
            </div>

        <% } else { %>

            <table>

                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Role</th>
                        <th>Account Status</th>
                        <th>Registration Date</th>
                        <th>Account Action</th>
                    </tr>
                </thead>

                <tbody>

                <% for (User user : users) {

                    String role = user.getRole() == null
                            ? "UNKNOWN"
                            : user.getRole().toUpperCase();

                    String accountStatus =
                            user.isActive() ? "ACTIVE" : "INACTIVE";

                    String statusClass =
                            user.isActive() ? "active" : "inactive";

                    boolean isCurrentAdmin =
                            user.getUserId() == loggedInAdminId;

                %>

                    <tr>

                        <td><%= user.getUserId() %></td>

                        <td>
                            <%= escapeHtml(user.getFullName()) %>
                        </td>

                        <td>
                            <%= escapeHtml(user.getEmail()) %>
                        </td>

                        <td>
                            <%= escapeHtml(user.getPhone()) %>
                        </td>

                        <td>
                            <span class="status role">
                                <%= escapeHtml(role) %>
                            </span>
                        </td>

                        <td>
                            <span class="status <%= statusClass %>">
                                <%= accountStatus %>
                            </span>
                        </td>

                        <td>
                            <%
                                if (user.getCreatedAt() != null) {
                            %>

                                <%= user.getCreatedAt().toLocalDateTime()
                                        .format(DateTimeFormatter.ofPattern(
                                                "dd MMM yyyy, hh:mm a"
                                        )) %>

                            <%
                                } else {
                            %>

                                Not available

                            <%
                                }
                            %>
                        </td>

                        <td>

                            <% if (isCurrentAdmin) { %>

                                <span class="current-account">
                                    Current Account
                                </span>

                            <% } else { %>

                                <form class="action-form"
                                      method="post"
                                      action="${pageContext.request.contextPath}/admin/users/status"
                                      onsubmit="return confirmStatusChange(this);">

                                    <input type="hidden"
                                           name="userId"
                                           value="<%= user.getUserId() %>">

                                    <% if (user.isActive()) { %>

                                        <input type="hidden"
                                               name="active"
                                               value="false">

                                        <button type="submit"
                                                class="action-button deactivate-button">
                                            Deactivate
                                        </button>

                                    <% } else { %>

                                        <input type="hidden"
                                               name="active"
                                               value="true">

                                        <button type="submit"
                                                class="action-button activate-button">
                                            Activate
                                        </button>

                                    <% } %>

                                </form>

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
    function confirmStatusChange(form) {

        const activeValue =
            form.querySelector('input[name="active"]').value;

        const action = activeValue === "true"
                ? "activate"
                : "deactivate";

        return confirm(
            "Are you sure you want to " + action +
            " this user account?"
        );
    }
</script>

</body>
</html>
