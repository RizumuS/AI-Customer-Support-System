package com.aisupport.model;

public class Agent {

    private int agentId;
    private int userId;
    private String fullName;
    private String email;
    private String department;
    private String availability;
    private boolean active;

    // Workload statistics
    private int totalTickets;
    private int openTickets;
    private int inProgressTickets;
    private int resolvedTickets;

    public Agent() {
    }

    public Agent(
            int agentId,
            int userId,
            String fullName,
            String email,
            String department,
            String availability
    ) {
        this.agentId = agentId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.availability = availability;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    // Account status
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Workload statistics
    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(int openTickets) {
        this.openTickets = openTickets;
    }

    public int getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(int inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public int getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(int resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    @Override
    public String toString() {
        return fullName + " (" + availability + ")";
    }
}
