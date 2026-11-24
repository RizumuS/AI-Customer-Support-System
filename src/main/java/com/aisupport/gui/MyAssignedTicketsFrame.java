package com.aisupport.gui;

import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyAssignedTicketsFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;

    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public MyAssignedTicketsFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();

        setTitle("Agent - My Assigned Tickets");
        setSize(1100, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel heading = new JLabel(
            "MY ASSIGNED TICKETS",
            SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        String[] columns = {
            "Ticket Number",
            "Subject",
            "Category",
            "Priority",
            "Status",
            "Date Created"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketTable = new JTable(tableModel);

        ticketTable.setRowHeight(28);

        ticketTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(ticketTable);

        countLabel = new JLabel("Assigned Tickets: 0");

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(countLabel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshButton.addActionListener(e -> loadTickets());

        closeButton.addActionListener(e -> dispose());

        // Open ticket details on double-click
        ticketTable.addMouseListener(
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if (e.getClickCount() == 2) {

                        int selectedRow = ticketTable.getSelectedRow();

                        if (selectedRow >= 0) {

                            String ticketNumber =
                                tableModel.getValueAt(
                                    selectedRow, 0
                                ).toString();

                            openTicketDetails(ticketNumber);
                        }
                    }
                }
            }
        );

        loadTickets();
    }

    private void loadTickets() {

        try {

            Integer agentId = getAgentId();

            if (agentId == null) {

                tableModel.setRowCount(0);

                countLabel.setText("Assigned Tickets: 0");

                JOptionPane.showMessageDialog(
                    this,
                    "No agent profile is associated with this account.",
                    "Agent Profile Not Found",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            List<Ticket> tickets =
                ticketService.getTicketsByAgent(agentId);

            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a");

            for (Ticket ticket : tickets) {

                String category = ticket.getCategoryName();

                if (category == null || category.isBlank()) {
                    category = "Uncategorized";
                }

                String date = ticket.getCreatedAt() == null
                    ? "N/A"
                    : dateFormat.format(ticket.getCreatedAt());

                Object[] row = {
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    category,
                    ticket.getPriority(),
                    ticket.getStatus(),
                    date
                };

                tableModel.addRow(row);
            }

            countLabel.setText(
                "Assigned Tickets: " + tickets.size()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to load assigned tickets: "
                    + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Integer getAgentId() throws Exception {

        com.aisupport.service.AgentService agentService =
            new com.aisupport.service.AgentService();

        List<com.aisupport.model.Agent> agents =
            agentService.getAllAgents();

        for (com.aisupport.model.Agent agent : agents) {

            if (agent.getUserId() == loggedInUser.getUserId()) {
                return agent.getAgentId();
            }
        }

        return null;
    }

    private void openTicketDetails(String ticketNumber) {

        try {

            List<Ticket> tickets =
                ticketService.getTicketsByAgent(getAgentId());

            for (Ticket ticket : tickets) {

                if (ticket.getTicketNumber().equals(ticketNumber)) {

                    new TicketDetailsFrame(
                        loggedInUser,
                        ticket
                    ).setVisible(true);

                    return;
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to open ticket: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
