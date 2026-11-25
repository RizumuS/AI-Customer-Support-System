
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from classifier import ticket_classifier
from sentiment import sentiment_analyzer
from priority_detector import priority_detector
from response_generator import response_generator


app = FastAPI(
    title="AI Customer Support Service",
    description="Python AI service for the Java Customer Support System",
    version="1.0.0"
)


class TicketRequest(BaseModel):
    description: str = Field(..., min_length=1, max_length=10000)


class SentimentRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=10000)


class ResponseRequest(BaseModel):
    description: str = Field(..., min_length=1, max_length=10000)
    category: str
    sentiment: str
    priority: str


@app.get("/")
def home():
    return {
        "message": "AI Customer Support Service is running",
        "status": "success"
    }


@app.get("/health")
def health_check():
    return {
        "service": "Python AI Service",
        "status": "healthy"
    }


@app.post("/classify")
def classify_ticket(request: TicketRequest):
    try:
        result = ticket_classifier.predict(request.description)
        return {
            "status": "success",
            "message": "Ticket classified successfully",
            "predicted_category": result["category"],
            "confidence": result["confidence"]
        }
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception:
        raise HTTPException(
            status_code=500,
            detail="An error occurred during ticket classification."
        )


@app.post("/sentiment")
def analyze_sentiment(request: SentimentRequest):
    try:
        result = sentiment_analyzer.analyze(request.text)
        return {
            "status": "success",
            "message": "Sentiment analyzed successfully",
            **result
        }
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception:
        raise HTTPException(
            status_code=500,
            detail="An error occurred during sentiment analysis."
        )


@app.post("/priority")
def detect_ticket_priority(request: TicketRequest):
    try:
        result = priority_detector.detect_priority(request.description)
        return {
            "status": "success",
            "message": "Ticket priority detected successfully",
            **result
        }
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception:
        raise HTTPException(
            status_code=500,
            detail="An error occurred during priority detection."
        )


@app.post("/suggest-response")
def suggest_response(request: ResponseRequest):
    try:
        result = response_generator.generate_response(
            ticket_description=request.description,
            category=request.category,
            sentiment=request.sentiment,
            priority=request.priority
        )
        return {
            "status": "success",
            "message": "Response suggestion generated successfully",
            **result
        }
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception:
        raise HTTPException(
            status_code=500,
            detail="An error occurred during response generation."
        )


@app.post("/analyze-ticket")
def analyze_ticket(request: TicketRequest):
    try:
        description = request.description

        # 1. Classify the ticket.
        classification = ticket_classifier.predict(description)

        # 2. Analyze customer sentiment.
        sentiment = sentiment_analyzer.analyze(description)

        # 3. Detect suggested priority.
        priority = priority_detector.detect_priority(description)

        # 4. Generate a suggested response.
        response = response_generator.generate_response(
            ticket_description=description,
            category=classification["category"],
            sentiment=sentiment["sentiment"],
            priority=priority["suggested_priority"]
        )

        return {
            "status": "success",
            "message": "Ticket analyzed successfully",
            "classification": classification,
            "sentiment": sentiment,
            "priority": priority,
            "response": response
        }

    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception:
        raise HTTPException(
            status_code=500,
            detail="An error occurred during ticket analysis."
        )
