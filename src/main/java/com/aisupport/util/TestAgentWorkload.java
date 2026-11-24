package com.aisupport.util;

import com.aisupport.model.Agent;
import com.aisupport.service.AgentService;

import java.sql.SQLException;
import java.util.List;

public class TestAgentWorkload {

    public static void main(String[] args) {

        AgentService agentService = new AgentService();

        try {

            List<Agent> agents = agentService.getAllAgents();

            System.out.println("\n========== AGENT WORKLOAD REPORT ==========\n");

            for (Agent agent : agents) {

                System.out.println("Agent ID: " + agent.getAgentId());
                System.out.println("Name: " + agent.getFullName());
                System.out.println("Availability: " + agent.getAvailability());
                System.out.println("Total Tickets: " + agent.getTotalTickets());
                System.out.println("Open Tickets: " + agent.getOpenTickets());
                System.out.println("In Progress Tickets: " + agent.getInProgressTickets());
                System.out.println("Resolved Tickets: " + agent.getResolvedTickets());

                System.out.println("------------------------------------------");
            }

        } catch (SQLException e) {

            System.out.println("Unable to retrieve agent workload.");
            e.printStackTrace();
        }
    }
}
