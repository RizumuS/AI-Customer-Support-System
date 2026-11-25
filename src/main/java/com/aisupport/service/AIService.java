
package com.aisupport.service;

import com.aisupport.model.AIAnalysis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class AIService {

    private static final String AI_API_URL =
            "http://127.0.0.1:8001/analyze-ticket";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService() {

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    // Analyze a ticket using the Python AI service.
    public AIAnalysis analyzeTicket(String description)
            throws IOException, InterruptedException {

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ticket description cannot be empty."
            );
        }

        // Prepare the JSON request.
        String requestBody = objectMapper.writeValueAsString(
                Map.of("description", description.trim())
        );

        // Build the HTTP request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send the request to Python.
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Check the HTTP response.
        if (response.statusCode() != 200) {
            throw new IOException(
                    "AI service returned HTTP "
                            + response.statusCode()
                            + ": " + response.body()
            );
        }

        // Parse the JSON response.
        JsonNode root = objectMapper.readTree(response.body());

        if (!"success".equals(root.path("status").asText())) {
            throw new IOException(
                    "AI analysis was unsuccessful: "
                            + root.path("message").asText()
            );
        }

        JsonNode classification = root.path("classification");
        JsonNode sentiment = root.path("sentiment");
        JsonNode priority = root.path("priority");
        JsonNode responseData = root.path("response");

        if (classification.isMissingNode()
                || sentiment.isMissingNode()
                || priority.isMissingNode()
                || responseData.isMissingNode()) {

            throw new IOException(
                    "AI service returned an incomplete analysis."
            );
        }

        // Map the Python response into the Java model.
        AIAnalysis analysis = new AIAnalysis();

        analysis.setCategory(
                classification.path("category").asText()
        );

        analysis.setConfidence(
                classification.path("confidence").asDouble()
        );

        analysis.setSentiment(
                sentiment.path("sentiment").asText()
        );

        analysis.setCompoundScore(
                sentiment.path("compound_score").asDouble()
        );

        analysis.setPositiveScore(
                sentiment.path("positive_score").asDouble()
        );

        analysis.setNegativeScore(
                sentiment.path("negative_score").asDouble()
        );

        analysis.setNeutralScore(
                sentiment.path("neutral_score").asDouble()
        );

        analysis.setSuggestedPriority(
                priority.path("suggested_priority").asText()
        );

        analysis.setPriorityReason(
                priority.path("reason").asText()
        );

        analysis.setSuggestedResponse(
                responseData.path("suggested_response").asText()
        );

        return analysis;
    }
}
