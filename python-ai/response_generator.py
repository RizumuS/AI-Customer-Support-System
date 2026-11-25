
class ResponseGenerator:

    def __init__(self):

        self.responses = {

            "TECHNICAL": (
                "Thank you for reporting this technical issue. "
                "We apologize for the inconvenience. "
                "Our technical support team will investigate "
                "the problem and work towards resolving it."
            ),

            "BILLING": (
                "Thank you for contacting us regarding your billing concern. "
                "We understand the importance of accurate transactions. "
                "Our billing team will review the details "
                "and assist you with the issue."
            ),

            "ACCOUNT": (
                "Thank you for bringing this account-related issue "
                "to our attention. We understand that account access "
                "is important. Our support team will review your request "
                "and help you resolve it."
            ),

            "GENERAL": (
                "Thank you for contacting our customer support team. "
                "We appreciate your enquiry and will be happy "
                "to assist you with the information you need."
            )
        }

    def generate_response(
        self,
        ticket_description,
        category,
        sentiment,
        priority
    ):

        if not ticket_description or not ticket_description.strip():
            raise ValueError("Ticket description cannot be empty.")

        category = category.upper()
        sentiment = sentiment.upper()
        priority = priority.upper()

        if category not in self.responses:
            raise ValueError("Invalid ticket category.")

        if sentiment not in ["POSITIVE", "NEGATIVE", "NEUTRAL"]:
            raise ValueError("Invalid sentiment.")

        if priority not in ["LOW", "MEDIUM", "HIGH", "CRITICAL"]:
            raise ValueError("Invalid priority.")

        response = self.responses[category]

        # Add an acknowledgement for dissatisfied customers.
        if sentiment == "NEGATIVE":

            response = (
                "We sincerely apologize for the inconvenience "
                "and understand your frustration. "
                + response
            )

        # Add urgency acknowledgement for high-priority tickets.
        if priority == "HIGH":

            response += (
                " We recognize the urgency of this matter "
                "and will prioritize your request."
            )

        elif priority == "CRITICAL":

            response += (
                " This matter has been identified as critical "
                "and requires immediate attention from our support team."
            )

        return {
            "suggested_response": response,
            "category": category,
            "sentiment": sentiment,
            "priority": priority
        }


# Create a reusable response generator.
response_generator = ResponseGenerator()
