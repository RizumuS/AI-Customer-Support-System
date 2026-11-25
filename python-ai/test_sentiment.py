
from sentiment import sentiment_analyzer


test_messages = [
    "Thank you so much! Your support team was extremely helpful.",
    "I cannot access my account. This is very frustrating and disappointing.",
    "Ticket number 12345 was created on Monday."
]


for message in test_messages:

    result = sentiment_analyzer.analyze(message)

    print("\nCustomer Message:", message)
    print("Sentiment:", result["sentiment"])
    print("Compound Score:", result["compound_score"])
    print("Positive Score:", result["positive_score"])
    print("Negative Score:", result["negative_score"])
    print("Neutral Score:", result["neutral_score"])
