package com.aisupport.gui;

import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AgentTicketsFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;

    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public AgentTicketsFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();

        setTitle("Agent - All Support Tickets");
        setSize(1250, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel heading = new JLabel(
            "ALL SUPPORT TICKETS",
            SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        String[] columns = {
            "Ticket Number",
            "Subject",
            "Category",
            "Customer ID",
            "Assigned Agent",
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

        countLabel = new JLabel("Total Tickets: 0");

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

        // Open ticket details when an agent double-clicks a ticket
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

                            try {

                                List<Ticket> tickets =
                                    ticketService.getAllTickets();

                                for (Ticket ticket : tickets) {

                                    if (ticket.getTicketNumber()
                                            .equals(ticketNumber)) {

                                        new TicketDetailsFrame(
                                            loggedInUser,
                                            ticket
                                        ).setVisible(true);

                                        break;
                                    }
                                }

                            } catch (Exception ex) {

                                JOptionPane.showMessageDialog(
                                    AgentTicketsFrame.this,
                                    "Unable to open ticket: "
                                        + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            }
        );

        loadTickets();
    }

    private void loadTickets() {

        try {

            List<Ticket> tickets =
                ticketService.getAllTickets();

            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a");

            for (Ticket ticket : tickets) {

                String category = ticket.getCategoryName();

                if (category == null || category.isBlank()) {
                    category = "Uncategorized";
                }

                String assignedAgent =
                    ticket.getAssignedAgentName();

                if (assignedAgent == null || assignedAgent.isBlank()) {
                    assignedAgent = "Unassigned";
                }

                String date = ticket.getCreatedAt() == null
                    ? "N/A"
                    : dateFormat.format(ticket.getCreatedAt());

                Object[] row = {
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    category,
                    ticket.getCustomerId(),
                    assignedAgent,
                    ticket.getPriority(),
                    ticket.getStatus(),
                    date
                };

                tableModel.addRow(row);
            }

            countLabel.setText(
                "Total Tickets: " + tickets.size()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to load tickets: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
