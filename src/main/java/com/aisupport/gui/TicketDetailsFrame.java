
package com.aisupport.gui;

import com.aisupport.model.Agent;
import com.aisupport.model.Ticket;
import com.aisupport.model.TicketResponse;
import com.aisupport.model.User;
import com.aisupport.service.AgentService;
import com.aisupport.service.TicketResponseService;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class TicketDetailsFrame extends JFrame {

    private final User loggedInUser;
    private final Ticket ticket;

    private final TicketResponseService responseService;
    private final TicketService ticketService;
    private final AgentService agentService;

    private JLabel statusValueLabel;
    private JLabel assignedAgentValueLabel;

    private JComboBox<String> statusComboBox;
    private JComboBox<Agent> agentComboBox;

    private JTextArea conversationArea;
    private JTextArea messageInput;

    private JButton sendButton;
    private JLabel conversationStatusLabel;

    public TicketDetailsFrame(User user, Ticket ticket) {

        this.loggedInUser = user;
        this.ticket = ticket;

        this.responseService = new TicketResponseService();
        this.ticketService = new TicketService();
        this.agentService = new AgentService();

        setTitle("Ticket Details - " + ticket.getTicketNumber());
        setSize(850, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadResponses();
    }

    private void initializeUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        // Header
        JLabel heading = new JLabel("Ticket Details");
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(35, 55, 90));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(
                new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        );
        contentPanel.setOpaque(false);

        // Ticket information panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 232)
                        ),
                        new EmptyBorder(15, 20, 15, 20)
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 5, 6, 5);

        addDetail(
                detailsPanel, gbc,
                "Ticket Number",
                ticket.getTicketNumber()
        );

        addDetail(
                detailsPanel, gbc,
                "Subject",
                ticket.getSubject()
        );

        addDetail(
                detailsPanel, gbc,
                "Category",
                ticket.getCategoryName() != null
                        ? ticket.getCategoryName()
                        : "Uncategorized"
        );

        addDetail(
                detailsPanel, gbc,
                "Priority",
                ticket.getPriority()
        );

        // Status row
        JPanel statusRow = new JPanel(
                new BorderLayout(15, 0)
        );
        statusRow.setOpaque(false);

        JLabel statusNameLabel = new JLabel("Status:");
        statusNameLabel.setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        statusNameLabel.setForeground(
                new Color(90, 100, 115)
        );

        statusValueLabel = new JLabel(ticket.getStatus());
        statusValueLabel.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        statusRow.add(statusNameLabel, BorderLayout.WEST);
        statusRow.add(statusValueLabel, BorderLayout.CENTER);

        gbc.gridy++;
        detailsPanel.add(statusRow, gbc);

        // Assigned agent row
        JPanel assignedAgentRow = new JPanel(
                new BorderLayout(15, 0)
        );
        assignedAgentRow.setOpaque(false);

        JLabel assignedAgentNameLabel =
                new JLabel("Assigned Agent:");

        assignedAgentNameLabel.setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        assignedAgentNameLabel.setForeground(
                new Color(90, 100, 115)
        );

        assignedAgentValueLabel = new JLabel(
                ticket.getAssignedAgentName() != null
                        ? ticket.getAssignedAgentName()
                        : "Unassigned"
        );

        assignedAgentValueLabel.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        assignedAgentRow.add(
                assignedAgentNameLabel,
                BorderLayout.WEST
        );

        assignedAgentRow.add(
                assignedAgentValueLabel,
                BorderLayout.CENTER
        );

        gbc.gridy++;
        detailsPanel.add(assignedAgentRow, gbc);

        addDetail(
                detailsPanel, gbc,
                "Date Created",
                formatDate(ticket.getCreatedAt())
        );

        addDetail(
                detailsPanel, gbc,
                "Last Updated",
                formatDate(ticket.getUpdatedAt())
        );

        addDetail(
                detailsPanel, gbc,
                "Resolved Date",
                formatDate(ticket.getResolvedAt())
        );

        // Description heading
        gbc.gridy++;
        gbc.insets = new Insets(12, 5, 5, 5);

        JLabel descriptionHeading =
                new JLabel("Description");

        descriptionHeading.setFont(
                new Font("Arial", Font.BOLD, 14)
        );
        descriptionHeading.setForeground(
                new Color(35, 55, 90)
        );

        detailsPanel.add(descriptionHeading, gbc);

        // Description
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 10, 5);

        JTextArea descriptionArea = new JTextArea(
                ticket.getDescription() != null
                        ? ticket.getDescription()
                        : "No description available."
        );

        descriptionArea.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setRows(3);

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setPreferredSize(
                new Dimension(700, 75)
        );

        detailsPanel.add(descriptionScroll, gbc);

        contentPanel.add(detailsPanel);

        // AGENT and ADMIN controls
        String role = loggedInUser.getRole();

        if ("AGENT".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            addStatusControls(contentPanel);
            addAssignmentControls(contentPanel);
        }

        contentPanel.add(Box.createVerticalStrut(15));

        // Conversation panel
        JPanel conversationPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        conversationPanel.setBackground(Color.WHITE);
        conversationPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 232)
                        ),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        // Conversation heading
        JPanel conversationHeader = new JPanel(
                new BorderLayout()
        );
        conversationHeader.setOpaque(false);

        JLabel conversationHeading =
                new JLabel("Ticket Conversation");

        conversationHeading.setFont(
                new Font("Arial", Font.BOLD, 17)
        );
        conversationHeading.setForeground(
                new Color(35, 55, 90)
        );

        JButton refreshButton = new JButton("Refresh");

        conversationHeader.add(
                conversationHeading,
                BorderLayout.WEST
        );

        conversationHeader.add(
                refreshButton,
                BorderLayout.EAST
        );

        conversationPanel.add(
                conversationHeader,
                BorderLayout.NORTH
        );

        // Conversation messages
        conversationArea = new JTextArea();

        conversationArea.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );
        conversationArea.setEditable(false);
        conversationArea.setLineWrap(true);
        conversationArea.setWrapStyleWord(true);

        conversationArea.setMargin(
                new Insets(10, 10, 10, 10)
        );

        conversationArea.setBackground(
                new Color(248, 250, 253)
        );

        JScrollPane conversationScroll =
                new JScrollPane(conversationArea);

        conversationPanel.add(
                conversationScroll,
                BorderLayout.CENTER
        );

        // Message input area
        JPanel inputPanel = new JPanel(
                new BorderLayout(8, 8)
        );
        inputPanel.setOpaque(false);

        messageInput = new JTextArea(3, 30);

        messageInput.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);

        messageInput.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 210, 220)
                        ),
                        new EmptyBorder(8, 8, 8, 8)
                )
        );

        JScrollPane inputScroll =
                new JScrollPane(messageInput);

        sendButton = new JButton("Send Reply");

        sendButton.setBackground(
                new Color(35, 75, 130)
        );
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        conversationStatusLabel = new JLabel(
                "Write a message to reply to this ticket."
        );

        conversationStatusLabel.setFont(
                new Font("Arial", Font.ITALIC, 11)
        );
        conversationStatusLabel.setForeground(Color.GRAY);

        JPanel sendPanel = new JPanel(
                new BorderLayout(5, 5)
        );
        sendPanel.setOpaque(false);

        sendPanel.add(
                conversationStatusLabel,
                BorderLayout.NORTH
        );

        sendPanel.add(
                sendButton,
                BorderLayout.EAST
        );

        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(sendPanel, BorderLayout.SOUTH);

        conversationPanel.add(
                inputPanel,
                BorderLayout.SOUTH
        );

        contentPanel.add(conversationPanel);

        // Make the content scrollable
        JScrollPane mainScroll = new JScrollPane(contentPanel);

        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(mainScroll, BorderLayout.CENTER);

        // Footer
        JButton closeButton = new JButton("Close");

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        footer.setOpaque(false);
        footer.add(closeButton);

        mainPanel.add(footer, BorderLayout.SOUTH);

        // Button actions
        refreshButton.addActionListener(
                e -> loadResponses()
        );

        sendButton.addActionListener(
                e -> sendReply()
        );

        closeButton.addActionListener(
                e -> dispose()
        );

        add(mainPanel);
    }

    // Agent and Admin status controls
    private void addStatusControls(JPanel contentPanel) {

        JPanel statusPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 232)
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        JLabel statusHeading =
                new JLabel("Update Ticket Status:");

        statusHeading.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        statusComboBox = new JComboBox<>(
                new String[]{
                        "OPEN",
                        "IN_PROGRESS",
                        "WAITING_FOR_CUSTOMER",
                        "RESOLVED",
                        "CLOSED"
                }
        );

        statusComboBox.setSelectedItem(ticket.getStatus());

        JButton updateStatusButton =
                new JButton("Update Status");

        statusPanel.add(statusHeading);
        statusPanel.add(statusComboBox);
        statusPanel.add(updateStatusButton);

        updateStatusButton.addActionListener(
                e -> updateTicketStatus()
        );

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statusPanel);
    }

    // Agent and Admin assignment controls
    private void addAssignmentControls(JPanel contentPanel) {

        JPanel assignmentPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        assignmentPanel.setBackground(Color.WHITE);
        assignmentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 232)
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        JLabel assignmentHeading =
                new JLabel("Assign Ticket To:");

        assignmentHeading.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        agentComboBox = new JComboBox<>();

        agentComboBox.setPreferredSize(
                new Dimension(220, 28)
        );

        JButton assignButton =
                new JButton("Assign Ticket");

        assignmentPanel.add(assignmentHeading);
        assignmentPanel.add(agentComboBox);
        assignmentPanel.add(assignButton);

        assignButton.addActionListener(
                e -> assignTicket()
        );

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(assignmentPanel);

        loadAgents();
    }

    // Load agents into dropdown

// Load available agents and preserve the existing assignment.
private void loadAgents() {

    try {

        // Retrieve only active, available agents.
        List<Agent> agents =
                agentService.getAvailableAgents();

        agentComboBox.removeAllItems();

        // Add available agents to the dropdown.
        for (Agent agent : agents) {
            agentComboBox.addItem(agent);
        }

        // Preserve the currently assigned agent,
        // even if that agent is BUSY or OFFLINE.
        if (ticket.getAssignedAgentId() != null) {

            boolean currentAgentFound = false;

            for (int i = 0;
                 i < agentComboBox.getItemCount();
                 i++) {

                Agent agent =
                        agentComboBox.getItemAt(i);

                if (agent.getAgentId()
                        == ticket.getAssignedAgentId()) {

                    currentAgentFound = true;
                    agentComboBox.setSelectedIndex(i);
                    break;
                }
            }

            // Retrieve the existing agent if they are not
            // in the AVAILABLE agents list.
            if (!currentAgentFound) {

                List<Agent> allAgents =
                        agentService.getAllAgentsForAdmin();

                for (Agent agent : allAgents) {

                    if (agent.getAgentId()
                            == ticket.getAssignedAgentId()) {

                        agentComboBox.addItem(agent);

                        agentComboBox.setSelectedItem(agent);

                        currentAgentFound = true;
                        break;
                    }
                }
            }
        }

        // Show this message only when there is no existing
        // assignment and no available agent.
        if (agents.isEmpty() &&
                ticket.getAssignedAgentId() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No available agents were found.",
                    "No Agents Available",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (Exception exception) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to load agents:\n"
                        + exception.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    // Assign ticket to selected agent
    private void assignTicket() {

        Agent selectedAgent =
                (Agent) agentComboBox.getSelectedItem();

        if (selectedAgent == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an agent.",
                    "No Agent Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Prevent assigning a new ticket to a BUSY or OFFLINE agent.
// Allow the existing assignment to remain unchanged.
boolean isCurrentAssignment =
        ticket.getAssignedAgentId() != null &&
        ticket.getAssignedAgentId()
                == selectedAgent.getAgentId();

if (!"AVAILABLE".equalsIgnoreCase(
        selectedAgent.getAvailability()) &&
        !isCurrentAssignment) {

    JOptionPane.showMessageDialog(
            this,
            "This agent is currently "
                    + selectedAgent.getAvailability()
                    + " and cannot receive new assignments.",
            "Agent Unavailable",
            JOptionPane.WARNING_MESSAGE
    );

    return;
}

        if (ticket.getAssignedAgentId() != null &&
                ticket.getAssignedAgentId()
                        == selectedAgent.getAgentId()) {

            JOptionPane.showMessageDialog(
                    this,
                    "This ticket is already assigned to "
                            + selectedAgent.getFullName() + ".",
                    "No Changes",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Assign this ticket to "
                        + selectedAgent.getFullName() + "?",
                "Confirm Ticket Assignment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean assigned = ticketService.assignTicketToAgent(
                    ticket.getTicketId(),
                    selectedAgent.getAgentId(),
                    loggedInUser.getUserId()
            );

            if (assigned) {

                ticket.setAssignedAgentId(
                        selectedAgent.getAgentId()
                );

                ticket.setAssignedAgentName(
                        selectedAgent.getFullName()
                );

                assignedAgentValueLabel.setText(
                        selectedAgent.getFullName()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket successfully assigned to "
                                + selectedAgent.getFullName() + "!",
                        "Assignment Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to assign this ticket. "
                                + "Please verify the ticket and agent.",
                        "Assignment Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Assignment error:\n"
                            + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Update ticket status
    private void updateTicketStatus() {

        String newStatus =
                (String) statusComboBox.getSelectedItem();

        if (newStatus == null) {
            return;
        }

        if (newStatus.equalsIgnoreCase(ticket.getStatus())) {

            JOptionPane.showMessageDialog(
                    this,
                    "The ticket already has this status.",
                    "No Changes",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Change ticket status to " + newStatus + "?",
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean updated = ticketService.updateTicketStatus(
                    ticket.getTicketId(),
                    newStatus,
                    loggedInUser.getUserId()
            );

            if (updated) {

                ticket.setStatus(newStatus);

                statusValueLabel.setText(newStatus);

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket status updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update ticket status.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Status update error:\n"
                            + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Load conversation history
    private void loadResponses() {

        try {

            List<TicketResponse> responses =
                    responseService.getTicketResponses(
                            ticket.getTicketId()
                    );

            conversationArea.setText("");

            if (responses.isEmpty()) {

                conversationArea.setText(
                        "No messages yet.\n\n"
                                + "You can send a reply using the box below."
                );

            } else {

                for (TicketResponse response : responses) {

                    String senderName =
                            response.getSenderName() != null
                                    ? response.getSenderName()
                                    : "Unknown User";

                    String senderRole =
                            response.getSenderRole() != null
                                    ? response.getSenderRole()
                                    : "USER";

                    String date = formatDate(
                            response.getCreatedAt()
                    );

                    conversationArea.append(
                            senderName + " (" + senderRole + ")\n"
                    );

                    conversationArea.append(
                            date + "\n\n"
                    );

                    conversationArea.append(
                            response.getMessage() + "\n"
                    );

                    conversationArea.append(
                            "----------------------------------------\n\n"
                    );
                }
            }

            conversationArea.setCaretPosition(
                    conversationArea.getDocument().getLength()
            );

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load conversation:\n"
                            + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Send a reply
    private void sendReply() {

        String message = messageInput.getText().trim();

        if (message.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a message before sending.",
                    "Empty Message",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        sendButton.setEnabled(false);

        try {

            boolean sent = responseService.sendResponse(
                    ticket.getTicketId(),
                    loggedInUser.getUserId(),
                    message
            );

            if (sent) {

                messageInput.setText("");

                conversationStatusLabel.setText(
                        "Your reply was sent successfully."
                );

                loadResponses();

            } else {

                conversationStatusLabel.setText(
                        "Unable to send your reply."
                );
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to send reply:\n"
                            + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            sendButton.setEnabled(true);
        }
    }

    // Add ticket information rows
    private void addDetail(
            JPanel panel,
            GridBagConstraints gbc,
            String label,
            String value
    ) {

        JPanel rowPanel = new JPanel(
                new BorderLayout(15, 0)
        );

        rowPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(label + ":");

        nameLabel.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        nameLabel.setForeground(
                new Color(90, 100, 115)
        );

        JLabel valueLabel = new JLabel(
                value != null ? value : "N/A"
        );

        valueLabel.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        valueLabel.setForeground(
                new Color(35, 45, 60)
        );

        rowPanel.add(nameLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy++;

        panel.add(rowPanel, gbc);
    }

    // Format timestamps
    private String formatDate(Timestamp timestamp) {

        if (timestamp == null) {
            return "N/A";
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a"
                );

        return dateFormat.format(timestamp);
    }
}
