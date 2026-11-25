
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline


class TicketClassifier:

    def __init__(self):

        # Initial labelled training examples.
        # We will improve this dataset using actual ticket data later.

        training_data = [

            # TECHNICAL
            ("The application crashes whenever I open it", "TECHNICAL"),
            ("I am getting an error while using the software", "TECHNICAL"),
            ("The website is not loading properly", "TECHNICAL"),
            ("The application keeps freezing", "TECHNICAL"),
            ("I cannot upload files to the system", "TECHNICAL"),
            ("The system is showing a server error", "TECHNICAL"),
            ("The application has a bug", "TECHNICAL"),
            ("The page displays an unexpected error", "TECHNICAL"),
            ("The software is running very slowly", "TECHNICAL"),
            ("I am unable to access a particular feature", "TECHNICAL"),

            # BILLING
            ("I was charged twice for my subscription", "BILLING"),
            ("I need a refund for my payment", "BILLING"),
            ("My invoice contains an incorrect amount", "BILLING"),
            ("My payment was deducted but the order failed", "BILLING"),
            ("I want to know about my billing details", "BILLING"),
            ("My subscription payment is not reflecting", "BILLING"),
            ("Please send me my payment receipt", "BILLING"),
            ("I have a question about the invoice", "BILLING"),
            ("Why was I charged an additional fee", "BILLING"),
            ("I want to cancel a payment", "BILLING"),

            # ACCOUNT
            ("I forgot my account password", "ACCOUNT"),
            ("I cannot log into my account", "ACCOUNT"),
            ("I want to change my registered email address", "ACCOUNT"),
            ("My account has been locked", "ACCOUNT"),
            ("I am unable to reset my password", "ACCOUNT"),
            ("I want to update my profile information", "ACCOUNT"),
            ("I cannot register a new account", "ACCOUNT"),
            ("My account verification is not working", "ACCOUNT"),
            ("Please help me recover my account", "ACCOUNT"),
            ("I want to deactivate my account", "ACCOUNT"),

            # GENERAL
            ("I would like to know more about your services", "GENERAL"),
            ("What are your business hours", "GENERAL"),
            ("I have a general question about your company", "GENERAL"),
            ("Where can I find more information", "GENERAL"),
            ("I need some information about your products", "GENERAL"),
            ("How can I contact customer support", "GENERAL"),
            ("I would like to provide some feedback", "GENERAL"),
            ("Can you explain how your service works", "GENERAL"),
            ("I have a question about your policies", "GENERAL"),
            ("Thank you for your assistance", "GENERAL")
        ]

        texts = [item[0] for item in training_data]
        labels = [item[1] for item in training_data]

        # Build the machine-learning pipeline.
        self.model = Pipeline([
            (
                "tfidf",
                TfidfVectorizer(
                    lowercase=True,
                    stop_words="english",
                    ngram_range=(1, 2)
                )
            ),
            (
                "classifier",
                LogisticRegression(
                    max_iter=1000,
                    random_state=42
                )
            )
        ])

        # Train the model.
        self.model.fit(texts, labels)

    def predict(self, ticket_text):

        if not ticket_text or not ticket_text.strip():
            raise ValueError("Ticket description cannot be empty.")

        probabilities = self.model.predict_proba([ticket_text])[0]

        predicted_index = probabilities.argmax()

        category = self.model.classes_[predicted_index]
        confidence = float(probabilities[predicted_index])

        return {
            "category": category,
            "confidence": round(confidence, 4)
        }


# Create a reusable classifier instance.
ticket_classifier = TicketClassifier()
