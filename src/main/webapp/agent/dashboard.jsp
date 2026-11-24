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
            grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
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

        <p>Manage your assigned support requests and agent activities.</p>
    </div>

    <div class="cards">

        <div class="card">
            <h3>Assigned Tickets</h3>
            <p>View and manage the support tickets assigned to you.</p>
            <a href="#">View Tickets</a>
        </div>

        <div class="card">
            <h3>Ticket Management</h3>
            <p>Update ticket statuses and communicate with customers.</p>
            <a href="#">Manage Tickets</a>
        </div>

        <div class="card">
            <h3>Availability</h3>
            <p>Manage your availability for handling incoming support requests.</p>
            <a href="#">Manage Availability</a>
        </div>

    </div>

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
    </div>

</div>

</body>
</html>
