package com.aisupport.service;

import com.aisupport.model.AIAnalysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AIServiceTest {

    @Test
    void testAnalyzeTicket() throws Exception {

        AIService aiService = new AIService();

        String description =
                "My payment was deducted twice and I cannot access "
                + "my account. This is extremely frustrating and I "
                + "need an immediate resolution.";

        // Send ticket description to the Python AI service.
        AIAnalysis analysis = aiService.analyzeTicket(description);

        // Verify the returned AI analysis.
        assertNotNull(analysis, "AI analysis must not be null.");

        assertNotNull(
                analysis.getCategory(),
                "Predicted category must not be null."
        );

        assertTrue(
                analysis.getConfidence() >= 0.0
                        && analysis.getConfidence() <= 1.0,
                "Classification confidence must be between 0 and 1."
        );

        assertNotNull(
                analysis.getSentiment(),
                "Sentiment must not be null."
        );

        assertNotNull(
                analysis.getSuggestedPriority(),
                "Suggested priority must not be null."
        );

        assertNotNull(
                analysis.getPriorityReason(),
                "Priority reason must not be null."
        );

        assertNotNull(
                analysis.getSuggestedResponse(),
                "Suggested response must not be null."
        );

        assertFalse(
                analysis.getSuggestedResponse().trim().isEmpty(),
                "Suggested response must not be empty."
        );

        // Display the AI analysis.
        System.out.println("\n========== JAVA AI INTEGRATION TEST ==========");

        System.out.println(
                "Predicted Category: " + analysis.getCategory()
        );

        System.out.println(
                "Confidence: " + analysis.getConfidence()
        );

        System.out.println(
                "Sentiment: " + analysis.getSentiment()
        );

        System.out.println(
                "Compound Score: " + analysis.getCompoundScore()
        );

        System.out.println(
                "Suggested Priority: " + analysis.getSuggestedPriority()
        );

        System.out.println(
                "Priority Reason: " + analysis.getPriorityReason()
        );

        System.out.println(
                "Suggested Response: " + analysis.getSuggestedResponse()
        );

        System.out.println("==============================================");
    }
}
