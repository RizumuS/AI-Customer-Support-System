package com.aisupport.gui;

import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminTicketsFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;

    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private JLabel ticketCountLabel;

    private List<Ticket> tickets;

    public AdminTicketsFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();

        setTitle("Admin - Manage Support Tickets");
        setSize(1150, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel headingLabel = new JLabel("MANAGE SUPPORT TICKETS");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));

        ticketCountLabel = new JLabel("Loading tickets...");
        ticketCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(headingLabel, BorderLayout.NORTH);
        headerPanel.add(ticketCountLabel, BorderLayout.SOUTH);

        // Table
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

        ticketsTable = new JTable(tableModel);
        ticketsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ticketsTable.setRowHeight(28);
        ticketsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        ticketsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(ticketsTable);

        // Double-click to open ticket details
        ticketsTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(
                    java.awt.event.MouseEvent e) {

                if (e.getClickCount() == 2) {
                    openSelectedTicket();
                }
            }
        });

        // Buttons
        JButton viewButton = new JButton("View Ticket Details");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        styleButton(viewButton);
        styleButton(refreshButton);
        styleButton(closeButton);

        viewButton.addActionListener(e -> openSelectedTicket());

        refreshButton.addActionListener(e -> loadTickets());

        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        buttonPanel.setOpaque(false);

        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadTickets();
    }

    private void loadTickets() {

        try {

            tickets = ticketService.getAllTickets();

            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a");

            for (Ticket ticket : tickets) {

                String category =
                        ticket.getCategoryName() != null
                        ? ticket.getCategoryName()
                        : "Uncategorized";

                String assignedAgent =
                        ticket.getAssignedAgentName() != null
                        ? ticket.getAssignedAgentName()
                        : "Unassigned";

                String createdDate =
                        ticket.getCreatedAt() != null
                        ? dateFormat.format(ticket.getCreatedAt())
                        : "N/A";

                Object[] row = {
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    category,
                    ticket.getCustomerId(),
                    assignedAgent,
                    ticket.getPriority(),
                    ticket.getStatus(),
                    createdDate
                };

                tableModel.addRow(row);
            }

            ticketCountLabel.setText(
                    "Total Support Tickets: " + tickets.size()
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load support tickets:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openSelectedTicket() {

        int selectedRow = ticketsTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a ticket first."
            );

            return;
        }

        // Convert sorted table row to original model row
        int modelRow =
                ticketsTable.convertRowIndexToModel(selectedRow);

        Ticket selectedTicket = tickets.get(modelRow);

        new TicketDetailsFrame(
                loggedInUser,
                selectedTicket
        ).setVisible(true);
    }

    private void styleButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(40, 90, 160));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(170, 38));
    }
}
