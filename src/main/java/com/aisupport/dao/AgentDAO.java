package com.aisupport.dao;

import com.aisupport.database.DBConnection;
import com.aisupport.model.Agent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgentDAO {

    // Retrieve active agents only.
    // Used by existing agent assignment functionality.
    public List<Agent> getAllAgents() throws SQLException {
        return getAgents(true);
    }

    // Retrieve all agents, including inactive accounts.
    // Used by the administrator.
    public List<Agent> getAllAgentsForAdmin() throws SQLException {
        return getAgents(false);
    }

    // Shared method for retrieving agents and workload statistics.
    private List<Agent> getAgents(boolean activeOnly)
            throws SQLException {

        String sql = """
                SELECT
                    a.agent_id,
                    a.user_id,
                    u.full_name,
                    u.email,
                    a.department,
                    a.availability,
                    u.is_active,

                    COUNT(t.ticket_id) AS total_tickets,

                    SUM(
                        CASE WHEN t.status = 'OPEN'
                        THEN 1 ELSE 0 END
                    ) AS open_tickets,

                    SUM(
                        CASE WHEN t.status = 'IN_PROGRESS'
                        THEN 1 ELSE 0 END
                    ) AS in_progress_tickets,

                    SUM(
                        CASE WHEN t.status = 'RESOLVED'
                        THEN 1 ELSE 0 END
                    ) AS resolved_tickets

                FROM agents a

                INNER JOIN users u
                    ON a.user_id = u.user_id

                LEFT JOIN tickets t
                    ON a.agent_id = t.assigned_agent_id

                """ + (activeOnly ? "WHERE u.is_active = TRUE " : "") + """

                GROUP BY
                    a.agent_id,
                    a.user_id,
                    u.full_name,
                    u.email,
                    a.department,
                    a.availability,
                    u.is_active

                ORDER BY u.full_name ASC
                """;

        List<Agent> agents = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                agents.add(mapAgent(result));
            }
        }

        return agents;
    }

    // Retrieve one active agent using agent ID.
    public Agent getAgentById(int agentId) throws SQLException {

        String sql = """
                SELECT
                    a.agent_id,
                    a.user_id,
                    u.full_name,
                    u.email,
                    a.department,
                    a.availability,
                    u.is_active,

                    COUNT(t.ticket_id) AS total_tickets,

                    SUM(
                        CASE WHEN t.status = 'OPEN'
                        THEN 1 ELSE 0 END
                    ) AS open_tickets,

                    SUM(
                        CASE WHEN t.status = 'IN_PROGRESS'
                        THEN 1 ELSE 0 END
                    ) AS in_progress_tickets,

                    SUM(
                        CASE WHEN t.status = 'RESOLVED'
                        THEN 1 ELSE 0 END
                    ) AS resolved_tickets

                FROM agents a

                INNER JOIN users u
                    ON a.user_id = u.user_id

                LEFT JOIN tickets t
                    ON a.agent_id = t.assigned_agent_id

                WHERE a.agent_id = ?
                AND u.is_active = TRUE

                GROUP BY
                    a.agent_id,
                    a.user_id,
                    u.full_name,
                    u.email,
                    a.department,
                    a.availability,
                    u.is_active
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, agentId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapAgent(result);
                }
            }
        }

        return null;
    }

    // Update an agent's availability.
    public boolean updateAvailability(
            int agentId,
            String availability
    ) throws SQLException {

        String sql = """
                UPDATE agents
                SET availability = ?
                WHERE agent_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, availability);
            statement.setInt(2, agentId);

            return statement.executeUpdate() > 0;
        }
    }

    // Convert database result into an Agent object.
    private Agent mapAgent(ResultSet result) throws SQLException {

        Agent agent = new Agent();

        agent.setAgentId(result.getInt("agent_id"));
        agent.setUserId(result.getInt("user_id"));
        agent.setFullName(result.getString("full_name"));
        agent.setEmail(result.getString("email"));
        agent.setDepartment(result.getString("department"));
        agent.setAvailability(result.getString("availability"));
        agent.setActive(result.getBoolean("is_active"));

        agent.setTotalTickets(
                result.getInt("total_tickets")
        );

        agent.setOpenTickets(
                result.getInt("open_tickets")
        );

        agent.setInProgressTickets(
                result.getInt("in_progress_tickets")
        );

        agent.setResolvedTickets(
                result.getInt("resolved_tickets")
        );

        return agent;
    }
}
