
package com.aisupport.model;

public class AIAnalysis {

    private String category;
    private double confidence;

    private String sentiment;
    private double compoundScore;
    private double positiveScore;
    private double negativeScore;
    private double neutralScore;

    private String suggestedPriority;
    private String priorityReason;

    private String suggestedResponse;

    public AIAnalysis() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getCompoundScore() {
        return compoundScore;
    }

    public void setCompoundScore(double compoundScore) {
        this.compoundScore = compoundScore;
    }

    public double getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(double positiveScore) {
        this.positiveScore = positiveScore;
    }

    public double getNegativeScore() {
        return negativeScore;
    }

    public void setNegativeScore(double negativeScore) {
        this.negativeScore = negativeScore;
    }

    public double getNeutralScore() {
        return neutralScore;
    }

    public void setNeutralScore(double neutralScore) {
        this.neutralScore = neutralScore;
    }

    public String getSuggestedPriority() {
        return suggestedPriority;
    }

    public void setSuggestedPriority(String suggestedPriority) {
        this.suggestedPriority = suggestedPriority;
    }

    public String getPriorityReason() {
        return priorityReason;
    }

    public void setPriorityReason(String priorityReason) {
        this.priorityReason = priorityReason;
    }

    public String getSuggestedResponse() {
        return suggestedResponse;
    }

    public void setSuggestedResponse(String suggestedResponse) {
        this.suggestedResponse = suggestedResponse;
    }
}
