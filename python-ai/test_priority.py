
from priority_detector import priority_detector


test_tickets = [
    "I have a general question about your services.",
    "The application is running slowly.",
    "I cannot access my account and need help immediately.",
    "Our production system is down and all users are affected.",
    "There has been a security breach involving customer data."
]


for ticket in test_tickets:

    result = priority_detector.detect_priority(ticket)

    print("\nTicket:", ticket)
    print("Suggested Priority:", result["suggested_priority"])
    print("Reason:", result["reason"])
