
package com.aisupport.gui;

import com.aisupport.database.DBConnection;
import com.aisupport.model.AIAnalysis;
import com.aisupport.model.Ticket;
import com.aisupport.model.User;
import com.aisupport.service.AIService;
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
    private final AIService aiService;

    private JTextField subjectField;
    private JTextArea descriptionArea;
    private JComboBox<CategoryItem> categoryCombo;
    private JComboBox<String> priorityCombo;

    private JButton analyzeButton;
    private JButton applySuggestionsButton;

    private JLabel aiCategoryLabel;
    private JLabel aiConfidenceLabel;
    private JLabel aiSentimentLabel;
    private JLabel aiPriorityLabel;
    private JLabel aiPriorityReasonLabel;

    private JTextArea suggestedResponseArea;

    private AIAnalysis currentAnalysis;

    public CreateTicketFrame(User user) {

        this.loggedInUser = user;
        this.ticketService = new TicketService();
        this.aiService = new AIService();

        setTitle("Create Support Ticket");
        setSize(700, 850);
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
        gbc.insets = new Insets(8, 5, 8, 5);
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

        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(descriptionScroll, gbc);

        // AI Analysis Button
        analyzeButton = new JButton("Analyze with AI");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 13));
        analyzeButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(analyzeButton, gbc);

        analyzeButton.addActionListener(e -> analyzeTicket());

        // Category
        addLabel(formPanel, gbc, "Category", 5);

        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(categoryCombo, gbc);

        // Priority
        addLabel(formPanel, gbc, "Priority", 7);

        priorityCombo = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"}
        );

        priorityCombo.setSelectedItem("MEDIUM");

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(priorityCombo, gbc);

        // AI Analysis Results Panel
        JPanel aiPanel = new JPanel(new GridBagLayout());
        aiPanel.setBackground(new Color(245, 248, 252));
        aiPanel.setBorder(
                BorderFactory.createTitledBorder("AI Analysis Results")
        );

        GridBagConstraints aiGbc = new GridBagConstraints();
        aiGbc.insets = new Insets(5, 8, 5, 8);
        aiGbc.fill = GridBagConstraints.HORIZONTAL;
        aiGbc.weightx = 1;

        aiCategoryLabel = new JLabel("Category: Not analyzed");
        aiConfidenceLabel = new JLabel("Confidence: -");
        aiSentimentLabel = new JLabel("Sentiment: -");
        aiPriorityLabel = new JLabel("Suggested Priority: -");
        aiPriorityReasonLabel = new JLabel("Reason: -");

        suggestedResponseArea = new JTextArea(4, 30);
        suggestedResponseArea.setLineWrap(true);
        suggestedResponseArea.setWrapStyleWord(true);
        suggestedResponseArea.setEditable(false);
        suggestedResponseArea.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        applySuggestionsButton =
                new JButton("Apply AI Suggestions");

        applySuggestionsButton.setEnabled(false);

        aiGbc.gridx = 0;
        aiGbc.gridy = 0;
        aiPanel.add(aiCategoryLabel, aiGbc);

        aiGbc.gridy = 1;
        aiPanel.add(aiConfidenceLabel, aiGbc);

        aiGbc.gridy = 2;
        aiPanel.add(aiSentimentLabel, aiGbc);

        aiGbc.gridy = 3;
        aiPanel.add(aiPriorityLabel, aiGbc);

        aiGbc.gridy = 4;
        aiPanel.add(aiPriorityReasonLabel, aiGbc);

        aiGbc.gridy = 5;
        aiPanel.add(new JLabel("Suggested Response:"), aiGbc);

        aiGbc.gridy = 6;
        aiPanel.add(
                new JScrollPane(suggestedResponseArea),
                aiGbc
        );

        aiGbc.gridy = 7;
        aiPanel.add(applySuggestionsButton, aiGbc);

        // Apply AI suggestions
        applySuggestionsButton.addActionListener(
                e -> applyAISuggestions()
        );

        // Combine form and AI results
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(aiPanel, BorderLayout.CENTER);

        JScrollPane mainScroll = new JScrollPane(centerPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(mainScroll, BorderLayout.CENTER);

        // Bottom Buttons
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

    // Analyze ticket using Python AI service
    private void analyzeTicket() {

        String description = descriptionArea.getText().trim();

        if (description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a ticket description first.",
                    "Missing Description",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        analyzeButton.setEnabled(false);
        analyzeButton.setText("Analyzing...");

        applySuggestionsButton.setEnabled(false);
        currentAnalysis = null;

        suggestedResponseArea.setText("");

        aiCategoryLabel.setText("Category: Analyzing...");
        aiConfidenceLabel.setText("Confidence: -");
        aiSentimentLabel.setText("Sentiment: -");
        aiPriorityLabel.setText("Suggested Priority: -");
        aiPriorityReasonLabel.setText("Reason: -");

        SwingWorker<AIAnalysis, Void> worker =
                new SwingWorker<>() {

            @Override
            protected AIAnalysis doInBackground() throws Exception {

                return aiService.analyzeTicket(description);
            }

            @Override
            protected void done() {

                analyzeButton.setEnabled(true);
                analyzeButton.setText("Analyze with AI");

                try {

                    currentAnalysis = get();

                    aiCategoryLabel.setText(
                            "Category: "
                                    + currentAnalysis.getCategory()
                    );

                    aiConfidenceLabel.setText(
                            String.format(
                                    "Confidence: %.2f%%",
                                    currentAnalysis.getConfidence() * 100
                            )
                    );

                    aiSentimentLabel.setText(
                            "Sentiment: "
                                    + currentAnalysis.getSentiment()
                    );

                    aiPriorityLabel.setText(
                            "Suggested Priority: "
                                    + currentAnalysis.getSuggestedPriority()
                    );

                    aiPriorityReasonLabel.setText(
                            "Reason: "
                                    + currentAnalysis.getPriorityReason()
                    );

                    suggestedResponseArea.setText(
                            currentAnalysis.getSuggestedResponse()
                    );

                    applySuggestionsButton.setEnabled(true);

                    JOptionPane.showMessageDialog(
                            CreateTicketFrame.this,
                            "AI analysis completed successfully!",
                            "AI Analysis",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception exception) {

                    currentAnalysis = null;

                    aiCategoryLabel.setText(
                            "Category: Analysis failed"
                    );

                    JOptionPane.showMessageDialog(
                            CreateTicketFrame.this,
                            "Unable to analyze ticket:\n"
                                    + exception.getMessage(),
                            "AI Service Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    // Apply AI suggestions to the form
    private void applyAISuggestions() {

        if (currentAnalysis == null) {
            return;
        }

        String suggestedCategory =
                currentAnalysis.getCategory();

        boolean categoryFound = false;

        for (int i = 0; i < categoryCombo.getItemCount(); i++) {

            CategoryItem item = categoryCombo.getItemAt(i);

            if (item.id != null &&
                    item.name.equalsIgnoreCase(suggestedCategory)) {

                categoryCombo.setSelectedIndex(i);
                categoryFound = true;
                break;
            }
        }

        if (!categoryFound) {

            JOptionPane.showMessageDialog(
                    this,
                    "The AI suggested category '"
                            + suggestedCategory
                            + "', but it is not available in the category list.",
                    "Category Not Available",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        priorityCombo.setSelectedItem(
                currentAnalysis.getSuggestedPriority()
        );

        JOptionPane.showMessageDialog(
                this,
                "AI suggestions applied successfully.\n"
                        + "Please review the selected category and priority "
                        + "before submitting.",
                "Suggestions Applied",
                JOptionPane.INFORMATION_MESSAGE
        );
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
