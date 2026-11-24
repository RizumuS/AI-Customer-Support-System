
package com.aisupport.gui;

import com.aisupport.model.Agent;
import com.aisupport.model.User;
import com.aisupport.service.AgentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentDashboardFrame extends JFrame {

    private final User loggedInUser;
    private final AgentService agentService;

    private Integer currentAgentId;

    private JLabel availabilityLabel;
    private JComboBox<String> availabilityComboBox;
    private JButton updateAvailabilityButton;

    private JLabel totalTicketsLabel;
    private JLabel openTicketsLabel;
    private JLabel inProgressTicketsLabel;
    private JLabel resolvedTicketsLabel;

    public AgentDashboardFrame(User user) {

        this.loggedInUser = user;
        this.agentService = new AgentService();

        setTitle("AI Customer Support - Agent Dashboard");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        mainPanel.setBackground(new Color(245, 247, 250));

        // Heading
        JLabel heading = new JLabel(
                "AGENT DASHBOARD",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(35, 55, 90));

        JLabel welcome = new JLabel(
                "Welcome, " + loggedInUser.getFullName(),
                SwingConstants.CENTER
        );

        welcome.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel headerPanel = new JPanel(
                new GridLayout(2, 1, 5, 5)
        );

        headerPanel.setOpaque(false);
        headerPanel.add(heading);
        headerPanel.add(welcome);

        // Availability section
        availabilityLabel = new JLabel(
                "Current Availability: Loading..."
        );

        availabilityLabel.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        availabilityComboBox = new JComboBox<>(
                new String[]{
                        "AVAILABLE",
                        "BUSY",
                        "OFFLINE"
                }
        );

        updateAvailabilityButton =
                new JButton("Update Availability");

        JPanel availabilityPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );

        availabilityPanel.setOpaque(false);

        availabilityPanel.add(availabilityLabel);
        availabilityPanel.add(availabilityComboBox);
        availabilityPanel.add(updateAvailabilityButton);

        // Workload heading
        JLabel workloadHeading = new JLabel(
                "MY WORKLOAD",
                SwingConstants.CENTER
        );

        workloadHeading.setFont(
                new Font("Arial", Font.BOLD, 19)
        );

        workloadHeading.setForeground(
                new Color(35, 55, 90)
        );

        // Workload statistics
        totalTicketsLabel = new JLabel("Total Tickets: Loading...");
        openTicketsLabel = new JLabel("Open Tickets: Loading...");
        inProgressTicketsLabel =
                new JLabel("In Progress: Loading...");
        resolvedTicketsLabel =
                new JLabel("Resolved Tickets: Loading...");

        JLabel[] workloadLabels = {
                totalTicketsLabel,
                openTicketsLabel,
                inProgressTicketsLabel,
                resolvedTicketsLabel
        };

        JPanel workloadGrid = new JPanel(
                new GridLayout(2, 2, 15, 15)
        );

        workloadGrid.setOpaque(false);

        for (JLabel label : workloadLabels) {

            label.setFont(
                    new Font("Arial", Font.BOLD, 16)
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            JPanel card = new JPanel(new BorderLayout());

            card.setBackground(Color.WHITE);

            card.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(215, 220, 230)
                            ),
                            new EmptyBorder(20, 10, 20, 10)
                    )
            );

            card.add(label, BorderLayout.CENTER);

            workloadGrid.add(card);
        }

        // Workload panel
        JPanel workloadPanel = new JPanel(
                new BorderLayout(10, 15)
        );

        workloadPanel.setOpaque(false);

        workloadPanel.add(
                workloadHeading,
                BorderLayout.NORTH
        );

        workloadPanel.add(
                workloadGrid,
                BorderLayout.CENTER
        );

        // Dashboard buttons
        JButton viewTicketsButton =
                new JButton("View All Support Tickets");

        JButton myAssignedTicketsButton =
                new JButton("My Assigned Tickets");

        JButton logoutButton =
                new JButton("Logout");

        viewTicketsButton.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        myAssignedTicketsButton.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        logoutButton.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 15, 10)
        );

        buttonPanel.setOpaque(false);

        buttonPanel.add(viewTicketsButton);
        buttonPanel.add(myAssignedTicketsButton);

        // Center section
        JPanel centerPanel = new JPanel(
                new BorderLayout(15, 20)
        );

        centerPanel.setOpaque(false);

        centerPanel.add(
                availabilityPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                workloadPanel,
                BorderLayout.CENTER
        );

        centerPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        JPanel bottomPanel = new JPanel();

        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load agent information and workload
        loadAgentData();

        // Update availability
        updateAvailabilityButton.addActionListener(e -> {
            updateAvailability();
        });

        // View all support tickets
        viewTicketsButton.addActionListener(e -> {
            new AgentTicketsFrame(loggedInUser).setVisible(true);
        });

        // View assigned tickets
        myAssignedTicketsButton.addActionListener(e -> {
            new MyAssignedTicketsFrame(loggedInUser).setVisible(true);
        });

        // Logout
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    // Load agent availability and workload
    private void loadAgentData() {

        try {

            List<Agent> agents = agentService.getAllAgents();

            for (Agent agent : agents) {

                if (agent.getUserId() == loggedInUser.getUserId()) {

                    currentAgentId = agent.getAgentId();

                    // Availability
                    String availability = agent.getAvailability();

                    availabilityLabel.setText(
                            "Current Availability: " + availability
                    );

                    availabilityComboBox.setSelectedItem(
                            availability
                    );

                    // Workload statistics
                    updateWorkloadDisplay(agent);

                    return;
                }
            }

            availabilityLabel.setText(
                    "Agent profile not found."
            );

            availabilityComboBox.setEnabled(false);
            updateAvailabilityButton.setEnabled(false);

            totalTicketsLabel.setText("Total Tickets: N/A");
            openTicketsLabel.setText("Open Tickets: N/A");
            inProgressTicketsLabel.setText("In Progress: N/A");
            resolvedTicketsLabel.setText("Resolved Tickets: N/A");

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load agent information:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Display workload statistics
    private void updateWorkloadDisplay(Agent agent) {

        totalTicketsLabel.setText(
                "Total Tickets: " + agent.getTotalTickets()
        );

        openTicketsLabel.setText(
                "Open Tickets: " + agent.getOpenTickets()
        );

        inProgressTicketsLabel.setText(
                "In Progress: " + agent.getInProgressTickets()
        );

        resolvedTicketsLabel.setText(
                "Resolved Tickets: " + agent.getResolvedTickets()
        );
    }

    // Update availability in the database
    private void updateAvailability() {

        if (currentAgentId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Agent profile not found."
            );

            return;
        }

        String selectedAvailability =
                (String) availabilityComboBox.getSelectedItem();

        if (selectedAvailability == null) {
            return;
        }

        try {

            boolean updated = agentService.updateAvailability(
                    currentAgentId,
                    selectedAvailability
            );

            if (updated) {

                availabilityLabel.setText(
                        "Current Availability: "
                                + selectedAvailability
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Availability updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh agent information
                loadAgentData();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update availability."
                );
            }

        } catch (SQLException | IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating availability:\n"
                            + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
