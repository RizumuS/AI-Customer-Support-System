package com.aisupport.gui;

import com.aisupport.database.DBConnection;
import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateTicketFrame extends JFrame {

    private final User loggedInUser;
    private final TicketService ticketService;

    private JTextField subjectField;
    private JTextArea descriptionArea;
    private JComboBox<CategoryItem> categoryCombo;
    private JComboBox<String> priorityCombo;

    public CreateTicketFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();

        setTitle("Create Support Ticket");
        setSize(650, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadCategories();
    }

    private void initializeUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);

        // Heading
        JLabel heading = new JLabel("Create a Support Ticket");
        heading.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
                "Describe your issue and our support team will assist you."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headingPanel.setBackground(Color.WHITE);
        headingPanel.add(heading);
        headingPanel.add(subtitle);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Subject
        addLabel(formPanel, gbc, "Subject", 0);

        subjectField = new JTextField();
        subjectField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(subjectField, gbc);

        // Description
        addLabel(formPanel, gbc, "Describe your issue", 2);

        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(descriptionScroll, gbc);

        // Category
        addLabel(formPanel, gbc, "Category", 4);

        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(categoryCombo, gbc);

        // Priority
        addLabel(formPanel, gbc, "Priority", 6);

        priorityCombo = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"}
        );

        priorityCombo.setSelectedItem("MEDIUM");

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(priorityCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JButton submitButton = new JButton("Submit Ticket");
        JButton cancelButton = new JButton("Cancel");

        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(40, 110, 200));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> submitTicket());
        cancelButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void addLabel(
            JPanel panel,
            GridBagConstraints gbc,
            String text,
            int row
    ) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = row;

        panel.add(label, gbc);
    }

    // Load categories from MySQL
    private void loadCategories() {

        String sql = """
                SELECT category_id, category_name
                FROM categories
                ORDER BY category_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            categoryCombo.removeAllItems();

            categoryCombo.addItem(
                    new CategoryItem(null, "Select a category")
            );

            while (result.next()) {

                categoryCombo.addItem(
                        new CategoryItem(
                                result.getInt("category_id"),
                                result.getString("category_name")
                        )
                );
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load categories: "
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Submit ticket
    private void submitTicket() {

        if (loggedInUser.getCustomerId() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "This account does not have a customer profile.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();

        CategoryItem selectedCategory =
                (CategoryItem) categoryCombo.getSelectedItem();

        String priority = (String) priorityCombo.getSelectedItem();

        if (subject.isEmpty() || description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in both subject and description.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (selectedCategory == null ||
                selectedCategory.id == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a support category.",
                    "Missing Category",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Ticket ticket = new Ticket();

        ticket.setCustomerId(loggedInUser.getCustomerId());
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setCategoryId(selectedCategory.id);
        ticket.setPriority(priority);
        ticket.setSource("DESKTOP");

        try {

            boolean created = ticketService.createTicket(ticket);

            if (created) {

                JOptionPane.showMessageDialog(
                        this,
                        "Your ticket has been created successfully!\n\n"
                                + "Ticket Number: "
                                + ticket.getTicketNumber()
                                + "\nStatus: "
                                + ticket.getStatus(),
                        "Ticket Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();
            }

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to create ticket:\n"
                            + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Category model for dropdown
    private static class CategoryItem {

        private final Integer id;
        private final String name;

        public CategoryItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
