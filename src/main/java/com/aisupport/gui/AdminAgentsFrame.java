package com.aisupport.gui;

import com.aisupport.model.Agent;
import com.aisupport.model.User;
import com.aisupport.service.AgentService;
import com.aisupport.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AdminAgentsFrame extends JFrame {

    private final User loggedInUser;
    private final AgentService agentService;
    private final UserService userService;

    private JTable agentsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> availabilityFilter;
    private JLabel countLabel;

    public AdminAgentsFrame(User user) {

        this.loggedInUser = user;
        this.agentService = new AgentService();
        this.userService = new UserService();

        setTitle("AI Customer Support System - Manage Agents");
        setSize(1250, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JLabel headingLabel = new JLabel("MANAGE AGENTS");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel welcomeLabel = new JLabel(
                "Administrator: " + loggedInUser.getFullName()
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(headingLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);

        // Search and filtering
        searchField = new JTextField(20);

        availabilityFilter = new JComboBox<>(
                new String[]{
                        "ALL",
                        "AVAILABLE",
                        "BUSY",
                        "OFFLINE"
                }
        );

        JButton clearButton = new JButton("Clear Filters");
        JButton refreshButton = new JButton("Refresh");

        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5)
        );
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Availability:"));
        filterPanel.add(availabilityFilter);

        filterPanel.add(clearButton);
        filterPanel.add(refreshButton);

        // Table
        String[] columns = {
                "Agent ID",
                "User ID",
                "Full Name",
                "Email",
                "Department",
                "Availability",
                "Account Status",
                "Total Tickets",
                "Open",
                "In Progress",
                "Resolved"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        agentsTable = new JTable(tableModel);
        agentsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        agentsTable.setRowHeight(25);
        agentsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        agentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sorter = new TableRowSorter<>(tableModel);
        agentsTable.setRowSorter(sorter);

        // Column widths
        int[] columnWidths = {
                70, 70, 140, 190, 130, 110, 110, 100, 65, 100, 80
        };

        for (int i = 0; i < columnWidths.length; i++) {
            agentsTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(agentsTable);

        // Count label
        countLabel = new JLabel("Showing Agents: 0 / 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // Action buttons
        JButton updateAvailabilityButton =
                new JButton("Update Availability");

        JButton activateButton =
                new JButton("Activate Agent");

        JButton deactivateButton =
                new JButton("Deactivate Agent");

        JButton closeButton = new JButton("Close");

        styleButton(updateAvailabilityButton);
        styleButton(activateButton);
        styleButton(deactivateButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(countLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 5)
        );
        actionPanel.setOpaque(false);

        actionPanel.add(updateAvailabilityButton);
        actionPanel.add(activateButton);
        actionPanel.add(deactivateButton);
        actionPanel.add(closeButton);

        bottomPanel.add(actionPanel, BorderLayout.EAST);

        // Search events
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        applyFilters();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        applyFilters();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        applyFilters();
                    }
                }
        );

        availabilityFilter.addActionListener(
                e -> applyFilters()
        );

        clearButton.addActionListener(e -> {
            searchField.setText("");
            availabilityFilter.setSelectedItem("ALL");
            applyFilters();
        });

        refreshButton.addActionListener(e -> loadAgents());

        updateAvailabilityButton.addActionListener(
                e -> updateAgentAvailability()
        );

        activateButton.addActionListener(
                e -> updateAgentAccountStatus(true)
        );

        deactivateButton.addActionListener(
                e -> updateAgentAccountStatus(false)
        );

        closeButton.addActionListener(e -> dispose());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadAgents();
    }

    // Load all agents, including inactive accounts.
    private void loadAgents() {

        try {

            List<Agent> agents =
                    agentService.getAllAgentsForAdmin();

            tableModel.setRowCount(0);

            for (Agent agent : agents) {

                Object[] row = {
                        agent.getAgentId(),
                        agent.getUserId(),
                        agent.getFullName(),
                        agent.getEmail(),
                        agent.getDepartment(),
                        agent.getAvailability(),
                        agent.isActive() ? "ACTIVE" : "INACTIVE",
                        agent.getTotalTickets(),
                        agent.getOpenTickets(),
                        agent.getInProgressTickets(),
                        agent.getResolvedTickets()
                };

                tableModel.addRow(row);
            }

            applyFilters();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load agents:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Update availability of the selected agent.
    private void updateAgentAvailability() {

        int modelRow = getSelectedModelRow();

        if (modelRow == -1) {
            return;
        }

        int agentId = (int) tableModel.getValueAt(modelRow, 0);

        String agentName =
                (String) tableModel.getValueAt(modelRow, 2);

        String accountStatus =
                (String) tableModel.getValueAt(modelRow, 6);

        String currentAvailability =
                (String) tableModel.getValueAt(modelRow, 5);

        if ("INACTIVE".equalsIgnoreCase(accountStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This agent's account is inactive.\n" +
                    "Activate the account before changing availability.",
                    "Inactive Agent",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String[] options = {
                "AVAILABLE",
                "BUSY",
                "OFFLINE"
        };

        JComboBox<String> availabilityComboBox =
                new JComboBox<>(options);

        availabilityComboBox.setSelectedItem(
                currentAvailability
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{
                        "Agent: " + agentName,
                        "Current Availability: " + currentAvailability,
                        "Select New Availability:",
                        availabilityComboBox
                },
                "Update Agent Availability",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newAvailability =
                (String) availabilityComboBox.getSelectedItem();

        if (newAvailability == null ||
                newAvailability.equals(currentAvailability)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No availability change was made.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Change " + agentName + "'s availability from " +
                        currentAvailability + " to " +
                        newAvailability + "?",
                "Confirm Availability Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean updated = agentService.updateAvailability(
                    agentId,
                    newAvailability
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Agent availability updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadAgents();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update agent availability.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException | IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating availability:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Activate or deactivate the selected agent's account.
    private void updateAgentAccountStatus(boolean activate) {

        int modelRow = getSelectedModelRow();

        if (modelRow == -1) {
            return;
        }

        int userId = (int) tableModel.getValueAt(modelRow, 1);

        String agentName =
                (String) tableModel.getValueAt(modelRow, 2);

        String currentStatus =
                (String) tableModel.getValueAt(modelRow, 6);

        boolean currentlyActive =
                "ACTIVE".equalsIgnoreCase(currentStatus);

        if (activate && currentlyActive) {

            JOptionPane.showMessageDialog(
                    this,
                    "This agent's account is already active.",
                    "Already Active",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        if (!activate && !currentlyActive) {

            JOptionPane.showMessageDialog(
                    this,
                    "This agent's account is already inactive.",
                    "Already Inactive",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        String action = activate ? "activate" : "deactivate";

        String message = activate
                ? "Are you sure you want to activate " +
                  agentName + "'s account?"
                : "Are you sure you want to deactivate " +
                  agentName + "'s account?\n\n" +
                  "The agent will no longer be able to log in.";

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Account " +
                        (activate ? "Activation" : "Deactivation"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean updated = userService.updateUserStatus(
                    userId,
                    activate,
                    loggedInUser.getUserId()
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Agent account " + action +
                                "d successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadAgents();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update the agent account.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException | IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating agent account:\n" +
                            e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Get the selected row's model index.
    // This handles sorting and filtering correctly.
    private int getSelectedModelRow() {

        int selectedRow = agentsTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an agent first.",
                    "No Agent Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return -1;
        }

        return agentsTable.convertRowIndexToModel(
                selectedRow
        );
    }

    // Apply search and availability filters.
    private void applyFilters() {

        List<RowFilter<DefaultTableModel, Integer>> filters =
                new ArrayList<>();

        String searchText = searchField.getText().trim();

        if (!searchText.isEmpty()) {

            filters.add(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(searchText),
                            2, 3, 4
                    )
            );
        }

        String availability =
                (String) availabilityFilter.getSelectedItem();

        if (availability != null &&
                !"ALL".equals(availability)) {

            filters.add(
                    RowFilter.regexFilter(
                            "^" + availability + "$",
                            5
                    )
            );
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.andFilter(filters)
            );
        }

        updateCount();
    }

    // Update visible agent count.
    private void updateCount() {

        int visible = sorter.getViewRowCount();
        int total = tableModel.getRowCount();

        countLabel.setText(
                "Showing Agents: " + visible + " / " + total
        );
    }

    // Button styling.
    private void styleButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(40, 90, 160));
        button.setForeground(Color.WHITE);
    }
}
