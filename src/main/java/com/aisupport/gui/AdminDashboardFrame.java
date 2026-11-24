package com.aisupport.gui;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.AgentService;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;
    private final AgentService agentService;

    private JLabel totalTicketsLabel;
    private JLabel openTicketsLabel;
    private JLabel inProgressTicketsLabel;
    private JLabel resolvedTicketsLabel;
    private JLabel totalAgentsLabel;

    public AdminDashboardFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();
        this.agentService = new AgentService();

        setTitle("AI Customer Support System - Admin Dashboard");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel headingLabel = new JLabel("ADMIN DASHBOARD");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + loggedInUser.getFullName()
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        headerPanel.add(headingLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);

        // Statistics heading
        JLabel statisticsHeading = new JLabel("SYSTEM OVERVIEW");
        statisticsHeading.setFont(new Font("Arial", Font.BOLD, 18));

        // Statistics cards
        JPanel statisticsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statisticsPanel.setOpaque(false);

        totalTicketsLabel = new JLabel("Loading...");
        openTicketsLabel = new JLabel("Loading...");
        inProgressTicketsLabel = new JLabel("Loading...");
        resolvedTicketsLabel = new JLabel("Loading...");
        totalAgentsLabel = new JLabel("Loading...");

        statisticsPanel.add(createStatCard("Total Tickets", totalTicketsLabel));
        statisticsPanel.add(createStatCard("Open Tickets", openTicketsLabel));
        statisticsPanel.add(createStatCard("In Progress", inProgressTicketsLabel));
        statisticsPanel.add(createStatCard("Resolved Tickets", resolvedTicketsLabel));
        statisticsPanel.add(createStatCard("Registered Agents", totalAgentsLabel));

        JPanel overviewPanel = new JPanel(new BorderLayout(10, 15));
        overviewPanel.setOpaque(false);
        overviewPanel.add(statisticsHeading, BorderLayout.NORTH);
        overviewPanel.add(statisticsPanel, BorderLayout.CENTER);

        // Management section
        JLabel managementHeading = new JLabel("MANAGEMENT");
        managementHeading.setFont(new Font("Arial", Font.BOLD, 18));

        JButton manageTicketsButton = new JButton("Manage Tickets");
        JButton manageUsersButton = new JButton("Manage Users");
        JButton manageAgentsButton = new JButton("Manage Agents");

        styleButton(manageTicketsButton);
        styleButton(manageUsersButton);
        styleButton(manageAgentsButton);

        manageTicketsButton.addActionListener(e ->
        new AdminTicketsFrame(loggedInUser).setVisible(true)
);

        manageUsersButton.addActionListener(e ->
        new AdminUsersFrame(loggedInUser).setVisible(true)
);

        manageAgentsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Agent management will be implemented later."
                )
        );

        JPanel managementButtonsPanel =
                new JPanel(new GridLayout(1, 3, 15, 15));
        managementButtonsPanel.setOpaque(false);

        managementButtonsPanel.add(manageTicketsButton);
        managementButtonsPanel.add(manageUsersButton);
        managementButtonsPanel.add(manageAgentsButton);

        JPanel managementPanel = new JPanel(new BorderLayout(10, 15));
        managementPanel.setOpaque(false);
        managementPanel.add(managementHeading, BorderLayout.NORTH);
        managementPanel.add(managementButtonsPanel, BorderLayout.CENTER);

        // Center content
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 25));
        centerPanel.setOpaque(false);
        centerPanel.add(overviewPanel);
        centerPanel.add(managementPanel);

        // Logout
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadSystemStatistics();
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {

        JPanel card = new JPanel(new BorderLayout(5, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(new Color(40, 90, 160));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void styleButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(40, 90, 160));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 45));
    }

    private void loadSystemStatistics() {

        try {

            List<Ticket> tickets = ticketService.getAllTickets();
            List<Agent> agents = agentService.getAllAgents();

            int total = tickets.size();
            int open = 0;
            int inProgress = 0;
            int resolved = 0;

            for (Ticket ticket : tickets) {

                String status = ticket.getStatus();

                if ("OPEN".equalsIgnoreCase(status)) {
                    open++;
                } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                    inProgress++;
                } else if ("RESOLVED".equalsIgnoreCase(status)) {
                    resolved++;
                }
            }

            totalTicketsLabel.setText(String.valueOf(total));
            openTicketsLabel.setText(String.valueOf(open));
            inProgressTicketsLabel.setText(String.valueOf(inProgress));
            resolvedTicketsLabel.setText(String.valueOf(resolved));
            totalAgentsLabel.setText(String.valueOf(agents.size()));

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load system statistics:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
