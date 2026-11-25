
import nltk
from nltk.sentiment import SentimentIntensityAnalyzer


class SentimentAnalyzer:

    def __init__(self):

        # Download VADER's sentiment dictionary if not available.
        try:
            nltk.data.find("sentiment/vader_lexicon.zip")
        except LookupError:
            nltk.download("vader_lexicon")

        self.analyzer = SentimentIntensityAnalyzer()

    def analyze(self, text):

        if not text or not text.strip():
            raise ValueError("Text cannot be empty.")

        # Calculate sentiment scores.
        scores = self.analyzer.polarity_scores(text)

        compound_score = scores["compound"]

        # Determine sentiment category.
        if compound_score >= 0.05:
            sentiment = "POSITIVE"

        elif compound_score <= -0.05:
            sentiment = "NEGATIVE"

        else:
            sentiment = "NEUTRAL"

        return {
            "sentiment": sentiment,
            "compound_score": round(compound_score, 4),
            "positive_score": round(scores["pos"], 4),
            "negative_score": round(scores["neg"], 4),
            "neutral_score": round(scores["neu"], 4)
        }


# Create a reusable sentiment analyzer.
sentiment_analyzer = SentimentAnalyzer()
