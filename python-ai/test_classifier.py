
from classifier import ticket_classifier


test_tickets = [
    "I cannot log into my account because my password is not working",
    "My payment was deducted twice",
    "The application crashes when I open the dashboard",
    "I want to know your customer support working hours"
]

for ticket in test_tickets:

    result = ticket_classifier.predict(ticket)

    print("\nTicket:", ticket)
    print("Predicted Category:", result["category"])
    print("Confidence:", result["confidence"])
