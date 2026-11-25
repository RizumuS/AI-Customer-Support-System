<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Agent Dashboard | AI Customer Support</title>

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
            background: #183b35;
            color: white;
            padding: 18px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .navbar h2 {
            margin: 0;
        }

        .logout {
            color: white;
            text-decoration: none;
            background: #d9534f;
            padding: 10px 16px;
            border-radius: 5px;
        }

        .container {
            max-width: 1100px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .welcome {
            margin-bottom: 30px;
        }

        .welcome h1 {
            margin-bottom: 8px;
        }

        .cards {
            display: grid;
            grid-template-columns: repeat(
                auto-fit,
                minmax(230px, 1fr)
            );
            gap: 20px;
        }

        .card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .card h3 {
            margin-top: 0;
            color: #183b35;
        }

        .card p {
            color: #65727e;
            line-height: 1.5;
        }

        .card a {
            display: inline-block;
            margin-top: 10px;
            padding: 10px 15px;
            background: #198754;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        .availability {
            margin-top: 30px;
        }

        .availability select {
            width: 100%;
            max-width: 350px;
            padding: 11px;
            margin-top: 8px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 15px;
        }

        .availability button {
            display: block;
            margin-top: 15px;
            padding: 11px 20px;
            background: #198754;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 15px;
        }

        .availability button:hover {
            background: #146c43;
        }

        .current-status {
            font-weight: bold;
            color: #183b35;
        }

        .success-message {
            background: #d1e7dd;
            color: #0f5132;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .error-message {
            background: #f8d7da;
            color: #842029;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .account {
            margin-top: 30px;
        }

        .account p {
            margin: 10px 0;
        }

        @media (max-width: 600px) {
            .navbar {
                padding: 16px 20px;
            }

            .navbar h2 {
                font-size: 18px;
            }
        }
    </style>
</head>

<body>

<div class="navbar">
    <h2>AI Customer Support</h2>

    <a class="logout"
       href="${pageContext.request.contextPath}/logout">
        Logout
    </a>
</div>

<div class="container">

    <div class="welcome">
        <h1>
            Welcome,
            <c:out value="${sessionScope.loggedInUser.fullName}"/>
        </h1>

        <p>
            Manage your assigned support requests and agent activities.
        </p>
    </div>

    <!-- Success and error messages -->

    <c:if test="${param.availabilityUpdated == 'true'}">
        <div class="success-message">
            Your availability has been updated successfully.
        </div>
    </c:if>

    <c:if test="${param.error == 'invalidAvailability'}">
        <div class="error-message">
            Invalid availability selected. Please choose a valid option.
        </div>
    </c:if>

    <div class="cards">

        <div class="card">
            <h3>Assigned Tickets</h3>

            <p>
                View and manage the support tickets assigned to you.
            </p>

            <a href="${pageContext.request.contextPath}/agent/tickets">
                View Tickets
            </a>
        </div>

        <div class="card">
            <h3>Ticket Management</h3>

            <p>
                Update ticket statuses and communicate with customers.
            </p>

            <a href="${pageContext.request.contextPath}/agent/tickets">
                Manage Tickets
            </a>
        </div>

        <div class="card">
            <h3>Availability</h3>

            <p>
                Manage your availability for handling incoming support requests.
            </p>

            <p>
                Current Status:
                <span class="current-status">
                    <c:out value="${agent.availability}"/>
                </span>
            </p>
        </div>

    </div>

    <!-- Availability Management Form -->

    <div class="card availability">

        <h3>Update Your Availability</h3>

        <p>
            Select your current availability status.
        </p>

        <form
            action="${pageContext.request.contextPath}/agent/availability"
            method="POST">

            <label for="availability">
                Availability Status
            </label>

            <select id="availability" name="availability" required>

                <option value="AVAILABLE"
                    <c:if test="${agent.availability == 'AVAILABLE'}">
                        selected
                    </c:if>>
                    Available
                </option>

                <option value="BUSY"
                    <c:if test="${agent.availability == 'BUSY'}">
                        selected
                    </c:if>>
                    Busy
                </option>

                <option value="OFFLINE"
                    <c:if test="${agent.availability == 'OFFLINE'}">
                        selected
                    </c:if>>
                    Offline
                </option>

            </select>

            <button type="submit">
                Update Availability
            </button>

        </form>

    </div>

    <!-- Agent Information -->

    <div class="card account">

        <h3>Agent Information</h3>

        <p>
            <strong>Name:</strong>
            <c:out value="${sessionScope.loggedInUser.fullName}"/>
        </p>

        <p>
            <strong>Email:</strong>
            <c:out value="${sessionScope.loggedInUser.email}"/>
        </p>

        <p>
            <strong>Role:</strong>
            <c:out value="${sessionScope.loggedInUser.role}"/>
        </p>

        <p>
            <strong>Department:</strong>
            <c:out value="${agent.department}"/>
        </p>

    </div>

</div>

</body>
</html>
