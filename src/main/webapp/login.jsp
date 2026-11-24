<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Login | AI Customer Support System</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f3f6fb;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .login-container {
            width: 100%;
            max-width: 420px;
            background: white;
            padding: 40px;
            border-radius: 14px;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
        }

        .heading {
            text-align: center;
            margin-bottom: 30px;
        }

        .heading h1 {
            color: #2457a7;
            font-size: 25px;
            margin-bottom: 10px;
        }

        .heading p {
            color: #667085;
            font-size: 14px;
            line-height: 1.5;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #344054;
            font-size: 14px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 13px;
            border: 1px solid #d0d5dd;
            border-radius: 7px;
            font-size: 14px;
            outline: none;
        }

        input:focus {
            border-color: #2457a7;
            box-shadow: 0 0 0 3px rgba(36, 87, 167, 0.1);
        }

        .password-wrapper {
            position: relative;
        }

        .password-wrapper input {
            padding-right: 65px;
        }

        .toggle-password {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            border: none;
            background: none;
            color: #2457a7;
            cursor: pointer;
            font-size: 12px;
            font-weight: bold;
        }

        .login-button {
            width: 100%;
            padding: 14px;
            background: #2457a7;
            color: white;
            border: none;
            border-radius: 7px;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 5px;
        }

        .login-button:hover {
            background: #194581;
        }

        .error-message {
            background: #fdecec;
            color: #b42318;
            padding: 12px;
            border-radius: 7px;
            font-size: 13px;
            margin-bottom: 20px;
        }

        .success-message {
            background: #e7f7ed;
            color: #218348;
            padding: 12px;
            border-radius: 7px;
            font-size: 13px;
            margin-bottom: 20px;
        }

        .register-link {
            text-align: center;
            margin-top: 25px;
            font-size: 14px;
            color: #667085;
        }

        .register-link a {
            color: #2457a7;
            text-decoration: none;
            font-weight: bold;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        .back-link {
            text-align: center;
            margin-top: 18px;
            font-size: 13px;
        }

        .back-link a {
            color: #667085;
            text-decoration: none;
        }

        .back-link a:hover {
            color: #2457a7;
        }

        @media (max-width: 480px) {
            .login-container {
                padding: 28px 22px;
            }

            .heading h1 {
                font-size: 22px;
            }
        }
    </style>
</head>

<body>

<div class="login-container">

    <div class="heading">
        <h1>Welcome Back</h1>
        <p>Login to your AI Customer Support account</p>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="error-message">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="success-message">
            <c:out value="${successMessage}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login"
          method="post">

        <div class="form-group">
            <label for="email">Email Address</label>

            <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email address"
                autocomplete="email"
                required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>

            <div class="password-wrapper">
                <input
                    type="password"
                    id="password"
                    name="password"
                    placeholder="Enter your password"
                    autocomplete="current-password"
                    required>

                <button
                    type="button"
                    class="toggle-password"
                    onclick="togglePassword()">
                    SHOW
                </button>
            </div>
        </div>

        <button type="submit" class="login-button">
            Login
        </button>

    </form>

    <div class="register-link">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register">
            Create Account
        </a>
    </div>

    <div class="back-link">
        <a href="${pageContext.request.contextPath}/">
            Back to Home
        </a>
    </div>

</div>

<script>
    function togglePassword() {

        const passwordField =
            document.getElementById("password");

        const toggleButton =
            document.querySelector(".toggle-password");

        if (passwordField.type === "password") {
            passwordField.type = "text";
            toggleButton.textContent = "HIDE";
        } else {
            passwordField.type = "password";
            toggleButton.textContent = "SHOW";
        }
    }
</script>

</body>
</html>
