package com.aisupport.gui;

import com.aisupport.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final User loggedInUser;

    public DashboardFrame(User user) {

        this.loggedInUser = user;

        setTitle("AI Customer Support - Dashboard");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(
            new Color(245, 247, 250)
        );

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(
            "Customer Support Dashboard"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 25)
        );

        title.setForeground(
            new Color(35, 55, 90)
        );

        header.add(title, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");

        header.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Dashboard content
        JPanel content = new JPanel(
            new GridLayout(2, 2, 15, 15)
        );

        content.setOpaque(false);

        JLabel welcome = new JLabel(
            "<html><center>Welcome, " +
            loggedInUser.getFullName() +
            "<br><br>Role: " + loggedInUser.getRole() +
            "</center></html>",
            SwingConstants.CENTER
        );

        welcome.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        content.add(welcome);

        // Create Support Ticket card
        JPanel createTicketCard = createCard(
            "Create Support Ticket",
            "Submit a new support request."
        );

        createTicketCard.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        createTicketCard.addMouseListener(
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(
                        java.awt.event.MouseEvent e) {

                    new CreateTicketFrame(
                        loggedInUser
                    ).setVisible(true);
                }
            }
        );

        content.add(createTicketCard);

        // My Tickets card (will be connected next)
        // My Tickets card
JPanel myTicketsCard = createCard(
    "My Tickets",
    "View and track your requests."
);

myTicketsCard.setCursor(
    new Cursor(Cursor.HAND_CURSOR)
);

myTicketsCard.addMouseListener(
    new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(
                java.awt.event.MouseEvent e) {

            new MyTicketsFrame(
                loggedInUser
            ).setVisible(true);
        }
    }
);

content.add(myTicketsCard);

        // AI Support Assistant card (future feature)
        content.add(createCard(
            "AI Support Assistant",
            "Get intelligent assistance."
        ));

        mainPanel.add(content, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel(
            "AI-Powered Customer Support System",
            SwingConstants.CENTER
        );

        footer.setForeground(Color.GRAY);

        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Logout
        logoutButton.addActionListener(e -> {

            dispose();

            new LoginFrame().setVisible(true);
        });
    }

    private JPanel createCard(
            String heading, String description) {

        JPanel card = new JPanel(
            new GridLayout(2, 1, 5, 5)
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 235)
                ),
                BorderFactory.createEmptyBorder(
                    20, 15, 20, 15
                )
            )
        );

        JLabel title = new JLabel(
            heading,
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 17)
        );

        JLabel desc = new JLabel(
            description,
            SwingConstants.CENTER
        );

        card.add(title);
        card.add(desc);

        return card;
    }
}
