
from response_generator import response_generator


test_cases = [

    {
        "description": "The application keeps crashing whenever I log in.",
        "category": "TECHNICAL",
        "sentiment": "NEGATIVE",
        "priority": "HIGH"
    },

    {
        "description": "My payment was deducted twice.",
        "category": "BILLING",
        "sentiment": "NEGATIVE",
        "priority": "CRITICAL"
    },

    {
        "description": "I want to know how to create an account.",
        "category": "ACCOUNT",
        "sentiment": "NEUTRAL",
        "priority": "LOW"
    },

    {
        "description": "I would like information about your services.",
        "category": "GENERAL",
        "sentiment": "POSITIVE",
        "priority": "MEDIUM"
    }

]


for index, ticket in enumerate(test_cases, start=1):

    result = response_generator.generate_response(
        ticket["description"],
        ticket["category"],
        ticket["sentiment"],
        ticket["priority"]
    )

    print(f"\n========== TEST {index} ==========")
    print("Ticket:", ticket["description"])
    print("Category:", result["category"])
    print("Sentiment:", result["sentiment"])
    print("Priority:", result["priority"])
    print("Suggested Response:", result["suggested_response"])
