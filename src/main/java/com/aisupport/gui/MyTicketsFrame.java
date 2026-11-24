
package com.aisupport.gui;

import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyTicketsFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;

    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    // Stores the actual ticket objects displayed in the table
    private List<Ticket> ticketList;

    public MyTicketsFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();

        setTitle("My Support Tickets");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadTickets();
    }

    private void initializeUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(new Color(245, 247, 250));

        mainPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        // Header
        JPanel header = new JPanel(new BorderLayout());

        header.setOpaque(false);

        JLabel heading = new JLabel("My Support Tickets");

        heading.setFont(
                new Font("Arial", Font.BOLD, 25)
        );

        heading.setForeground(new Color(35, 55, 90));

        header.add(heading, BorderLayout.WEST);

        JButton refreshButton = new JButton("Refresh");

        header.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Ticket count
        countLabel = new JLabel("Loading tickets...");

        countLabel.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        // Table
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

        ticketTable.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        ticketTable.setRowHeight(30);

        ticketTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        ticketTable.getTableHeader().setBackground(
                new Color(35, 75, 130)
        );

        ticketTable.getTableHeader().setForeground(Color.WHITE);

        ticketTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // Open ticket details on double-click
        ticketTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            int selectedRow =
                                    ticketTable.getSelectedRow();

                            if (selectedRow >= 0
                                    && selectedRow < ticketList.size()) {

                                Ticket selectedTicket =
                                        ticketList.get(selectedRow);

                                new TicketDetailsFrame(
        loggedInUser,
        selectedTicket
).setVisible(true);
                            }
                        }
                    }
                }
        );

        JScrollPane scrollPane = new JScrollPane(ticketTable);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        centerPanel.setOpaque(false);

        JLabel instructionLabel = new JLabel(
                "Double-click a ticket to view its details."
        );

        instructionLabel.setFont(
                new Font("Arial", Font.ITALIC, 12)
        );

        instructionLabel.setForeground(Color.GRAY);

        JPanel topCenterPanel = new JPanel(
                new BorderLayout(5, 5)
        );

        topCenterPanel.setOpaque(false);

        topCenterPanel.add(countLabel, BorderLayout.NORTH);
        topCenterPanel.add(instructionLabel, BorderLayout.SOUTH);

        centerPanel.add(topCenterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JButton closeButton = new JButton("Close");

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        footer.setOpaque(false);

        footer.add(closeButton);

        mainPanel.add(footer, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadTickets());

        closeButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void loadTickets() {

        if (loggedInUser.getCustomerId() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Customer profile not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        tableModel.setRowCount(0);

        try {

            ticketList = ticketService.getCustomerTickets(
                    loggedInUser.getCustomerId()
            );

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a");

            for (Ticket ticket : ticketList) {

                Timestamp createdAt = ticket.getCreatedAt();

                String formattedDate = createdAt != null
                        ? dateFormat.format(createdAt)
                        : "N/A";

                Object[] row = {
                        ticket.getTicketNumber(),
                        ticket.getSubject(),
                        ticket.getCategoryName() != null
                                ? ticket.getCategoryName()
                                : "Uncategorized",
                        ticket.getPriority(),
                        ticket.getStatus(),
                        formattedDate
                };

                tableModel.addRow(row);
            }

            countLabel.setText(
                    "Total tickets submitted: " + ticketList.size()
            );

            if (ticketList.isEmpty()) {

                countLabel.setText(
                        "You haven't submitted any support tickets yet."
                );
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load tickets:\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
