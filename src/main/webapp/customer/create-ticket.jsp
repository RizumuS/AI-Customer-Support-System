<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Create Ticket | AI Customer Support</title>

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
            background: #1f3a5f;
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
            background: #d9534f;
            padding: 10px 16px;
            border-radius: 5px;
        }

        .container {
            max-width: 900px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        h1, h3 {
            color: #1f3a5f;
        }

        .form-group {
            margin-bottom: 22px;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
        }

        input, textarea, select {
            width: 100%;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 5px;
            font-size: 15px;
            font-family: Arial, sans-serif;
        }

        textarea {
            min-height: 150px;
            resize: vertical;
        }

        .button {
            display: inline-block;
            padding: 11px 18px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            color: white;
        }

        .primary {
            background: #2563eb;
        }

        .primary:hover {
            background: #1d4ed8;
        }

        .ai-button {
            background: #6d28d9;
        }

        .ai-button:hover {
            background: #5b21b6;
        }

        .secondary {
            background: #475569;
        }

        .button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        .actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-top: 20px;
        }

        .ai-panel {
            display: none;
            margin-top: 25px;
            padding: 22px;
            background: #f5f3ff;
            border: 1px solid #ddd6fe;
            border-radius: 8px;
        }

        .ai-panel h3 {
            margin-top: 0;
        }

        .ai-result {
            margin: 12px 0;
            line-height: 1.6;
        }

        .suggested-response {
            background: white;
            border: 1px solid #ddd6fe;
            border-radius: 5px;
            padding: 15px;
            line-height: 1.6;
            white-space: pre-wrap;
        }

        .message {
            display: none;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 18px;
        }

        .error {
            display: block;
            background: #fee2e2;
            color: #991b1b;
        }

        .success {
            display: block;
            background: #dcfce7;
            color: #166534;
        }

        .back-link {
            display: inline-block;
            margin-top: 22px;
            color: #2563eb;
            text-decoration: none;
        }

        @media (max-width: 600px) {
            .navbar {
                padding: 16px 20px;
            }

            .card {
                padding: 20px;
            }
        }
    </style>
</head>

<body>

<div class="navbar">
    <h2>AI Customer Support</h2>

    <a href="${pageContext.request.contextPath}/logout">
        Logout
    </a>
</div>

<div class="container">

    <div class="card">

        <h1>Create a Support Ticket</h1>

        <p>
            Describe the issue you are facing. Our AI assistant can
            analyze your description and suggest a suitable category,
            priority, and response.
        </p>

        <c:if test="${not empty errorMessage}">
            <div class="message error">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form id="ticketForm"
              method="POST"
              action="${pageContext.request.contextPath}/customer/create-ticket">

            <div class="form-group">
                <label for="subject">Subject</label>

                <input type="text"
                       id="subject"
                       name="subject"
                       maxlength="255"
                       value="<c:out value='${subject}'/>"
                       placeholder="Enter a short title for your issue"
                       required>
            </div>

            <div class="form-group">
                <label for="description">Issue Description</label>

                <textarea id="description"
                          name="description"
                          placeholder="Describe your issue in detail..."
                          required><c:out value="${description}"/></textarea>
            </div>

            <div class="form-group">
                <label for="categoryId">Category</label>

                <select id="categoryId"
                        name="categoryId"
                        required>

                    <option value="">Select a category</option>

                    <c:forEach var="category" items="${categories}">
                        <option value="${category.categoryId}"
                            <c:if test="${selectedCategory == category.categoryId}">
                                selected
                            </c:if>>
                            <c:out value="${category.categoryName}"/>
                        </option>
                    </c:forEach>

                </select>
            </div>

            <div class="form-group">
                <label for="priority">Priority</label>

                <select id="priority" name="priority" required>
                    <option value="LOW"
                        <c:if test="${selectedPriority == 'LOW'}">selected</c:if>>
                        Low
                    </option>

                    <option value="MEDIUM"
                        <c:if test="${selectedPriority == 'MEDIUM' or empty selectedPriority}">selected</c:if>>
                        Medium
                    </option>

                    <option value="HIGH"
                        <c:if test="${selectedPriority == 'HIGH'}">selected</c:if>>
                        High
                    </option>

                    <option value="CRITICAL"
                        <c:if test="${selectedPriority == 'CRITICAL'}">selected</c:if>>
                        Critical
                    </option>
                </select>
            </div>

            <div class="actions">

                <button type="button"
                        class="button ai-button"
                        id="analyzeButton">
                    Analyze with AI
                </button>

                <button type="submit"
                        class="button primary">
                    Submit Ticket
                </button>

            </div>

        </form>

        <div class="ai-panel" id="aiPanel">

            <h3>AI Analysis Results</h3>

            <div class="ai-result">
                <strong>Predicted Category:</strong>
                <span id="aiCategory"></span>
            </div>

            <div class="ai-result">
                <strong>Confidence:</strong>
                <span id="aiConfidence"></span>
            </div>

            <div class="ai-result">
                <strong>Sentiment:</strong>
                <span id="aiSentiment"></span>
            </div>

            <div class="ai-result">
                <strong>Suggested Priority:</strong>
                <span id="aiPriority"></span>
            </div>

            <div class="ai-result">
                <strong>Priority Reason:</strong>
                <span id="aiReason"></span>
            </div>

            <div class="ai-result">
                <strong>Suggested Response:</strong>

                <div class="suggested-response"
                     id="aiResponse"></div>
            </div>

            <button type="button"
                    class="button primary"
                    id="applySuggestions">
                Apply AI Suggestions
            </button>

        </div>

        <a class="back-link"
           href="${pageContext.request.contextPath}/customer/dashboard.jsp">
            ← Back to Dashboard
        </a>

    </div>

</div>

<script>
    const contextPath =
        "${pageContext.request.contextPath}";

    const analyzeButton =
        document.getElementById("analyzeButton");

    const aiPanel =
        document.getElementById("aiPanel");

    let latestAnalysis = null;

    analyzeButton.addEventListener("click", async function () {

        const description =
            document.getElementById("description").value.trim();

        if (!description) {
            alert("Please enter an issue description first.");
            return;
        }

        analyzeButton.disabled = true;
        analyzeButton.textContent = "Analyzing...";

        aiPanel.style.display = "none";

        try {

            const response = await fetch(
                contextPath + "/customer/analyze-ticket",
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: new URLSearchParams({
                        description: description
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.error || "AI analysis failed."
                );
            }

            latestAnalysis = data;

            document.getElementById("aiCategory").textContent =
                data.category;

            document.getElementById("aiConfidence").textContent =
                (data.confidence * 100).toFixed(2) + "%";

            document.getElementById("aiSentiment").textContent =
                data.sentiment;

            document.getElementById("aiPriority").textContent =
                data.suggestedPriority;

            document.getElementById("aiReason").textContent =
                data.priorityReason;

            document.getElementById("aiResponse").textContent =
                data.suggestedResponse;

            aiPanel.style.display = "block";

        } catch (error) {

            alert(error.message);

        } finally {

            analyzeButton.disabled = false;
            analyzeButton.textContent = "Analyze with AI";
        }
    });

    document.getElementById("applySuggestions")
        .addEventListener("click", function () {

            if (!latestAnalysis) {
                return;
            }

            const categoryMapping = {
                "TECHNICAL": "Technical Support",
                "BILLING": "Billing",
                "ACCOUNT": "Account Issues",
                "GENERAL": "General Enquiry"
            };

            const predictedCategory =
                categoryMapping[latestAnalysis.category];

            if (predictedCategory) {

                const categorySelect =
                    document.getElementById("categoryId");

                const matchingOption =
                    Array.from(categorySelect.options).find(
                        option =>
                            option.text.trim() === predictedCategory
                    );

                if (matchingOption) {
                    categorySelect.value = matchingOption.value;
                }
            }

            const priority =
                latestAnalysis.suggestedPriority;

            const prioritySelect =
                document.getElementById("priority");

            if (
                Array.from(prioritySelect.options).some(
                    option => option.value === priority
                )
            ) {
                prioritySelect.value = priority;
            }

            alert("AI suggestions have been applied. You can still edit them before submitting.");
        });
</script>

</body>
</html>
