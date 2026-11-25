package com.aisupport.service;

import com.aisupport.dao.AgentDAO;
import com.aisupport.model.Agent;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AgentService {

    private final AgentDAO agentDAO;

    private static final Set<String> VALID_AVAILABILITY =
            Set.of("AVAILABLE", "BUSY", "OFFLINE");

    public AgentService() {
        this.agentDAO = new AgentDAO();
    }

    // Get all active agents.
    public List<Agent> getAllAgents() throws SQLException {
        return agentDAO.getAllAgents();
    }

    // Get active agents who are currently available for new assignments.
public List<Agent> getAvailableAgents() throws SQLException {
    return agentDAO.getAvailableAgents();
}

    // Get all agents, including inactive accounts.
    // Used by the admin dashboard.
    public List<Agent> getAllAgentsForAdmin() throws SQLException {
        return agentDAO.getAllAgentsForAdmin();
    }

    // Get an active agent by ID.
    public Agent getAgentById(int agentId) throws SQLException {

        if (agentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid agent ID."
            );
        }

        return agentDAO.getAgentById(agentId);
    }


    // Get an active agent using the associated user ID.
public Agent getAgentByUserId(int userId) throws SQLException {

    if (userId <= 0) {
        throw new IllegalArgumentException(
                "Invalid user ID."
        );
    }

    return agentDAO.getAgentByUserId(userId);
}

    // Update agent availability.
    public boolean updateAvailability(
            int agentId,
            String availability
    ) throws SQLException {

        if (agentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid agent ID."
            );
        }

        if (availability == null ||
                !VALID_AVAILABILITY.contains(
                        availability.toUpperCase()
                )) {

            throw new IllegalArgumentException(
                    "Invalid availability. Choose AVAILABLE, BUSY, or OFFLINE."
            );
        }

        return agentDAO.updateAvailability(
                agentId,
                availability.toUpperCase()
        );
    }
}
