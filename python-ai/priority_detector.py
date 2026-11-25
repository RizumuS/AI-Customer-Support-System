
class PriorityDetector:

    def __init__(self):

        # Critical keywords indicate severe incidents.
        self.critical_keywords = [
    "system down",
    "system is down",
    "production system down",
    "production system is down",
    "complete outage",
    "service unavailable",
    "security breach",
    "data breach",
    "data loss",
    "hacked",
    "unauthorized access",
    "all users affected",
    "all users are affected",
    "everyone is affected",
    "critical failure"
]

        # High-priority keywords indicate significant problems.
        self.high_keywords = [
            "cannot access",
            "unable to login",
            "unable to log in",
            "payment failed",
            "transaction failed",
            "not working",
            "completely broken",
            "urgent",
            "immediately",
            "business stopped",
            "multiple users affected"
        ]

        # Medium-priority keywords indicate moderate issues.
        self.medium_keywords = [
            "error",
            "issue",
            "problem",
            "slow",
            "delayed",
            "incorrect",
            "failed",
            "difficulty",
            "not responding"
        ]

    def detect_priority(self, ticket_text):

        if not ticket_text or not ticket_text.strip():
            raise ValueError("Ticket description cannot be empty.")

        text = ticket_text.lower()

        # Check critical keywords first.
        if any(keyword in text for keyword in self.critical_keywords):

            priority = "CRITICAL"
            reason = "The ticket contains indicators of a critical incident."

        # Check high-priority keywords.
        elif any(keyword in text for keyword in self.high_keywords):

            priority = "HIGH"
            reason = "The ticket indicates a significant customer issue."

        # Check medium-priority keywords.
        elif any(keyword in text for keyword in self.medium_keywords):

            priority = "MEDIUM"
            reason = "The ticket indicates a moderate issue."

        # Default priority.
        else:

            priority = "LOW"
            reason = "No significant urgency indicators were detected."

        return {
            "suggested_priority": priority,
            "reason": reason
        }


# Create a reusable priority detector.
priority_detector = PriorityDetector()
