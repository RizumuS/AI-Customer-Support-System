package com.aisupport.gui;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminUsersFrame extends JFrame {

    private final User loggedInUser;
    private final UserService userService;

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JLabel userCountLabel;

    private JTextField searchField;
    private JComboBox<String> roleFilter;

    public AdminUsersFrame(User user) {

        this.loggedInUser = user;
        this.userService = new UserService();

        setTitle("Manage Users");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadUsers();
    }

    private void initializeUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setOpaque(false);

        JLabel heading = new JLabel("Manage Users");

        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(35, 55, 90));

        userCountLabel = new JLabel("Total Users: 0");

        userCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userCountLabel.setForeground(new Color(90, 100, 115));

        headerPanel.add(heading, BorderLayout.WEST);
        headerPanel.add(userCountLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        centerPanel.setOpaque(false);

        // Search and filter panel
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        filterPanel.setBackground(Color.WHITE);

        filterPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 232)
                        ),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );

        JLabel searchLabel = new JLabel("Search:");

        searchLabel.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        searchField = new JTextField(22);

        searchField.setToolTipText(
                "Search by full name or email"
        );

        JLabel roleLabel = new JLabel("Filter by Role:");

        roleLabel.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        roleFilter = new JComboBox<>(
                new String[]{
                        "ALL",
                        "CUSTOMER",
                        "AGENT",
                        "ADMIN"
                }
        );

        roleFilter.setPreferredSize(
                new Dimension(140, 28)
        );

        JButton clearButton = new JButton("Clear Filters");

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(roleLabel);
        filterPanel.add(roleFilter);
        filterPanel.add(clearButton);

        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // User table
        String[] columns = {
                "User ID",
                "Full Name",
                "Email",
                "Role",
                "Phone",
                "Account Status",
                "Registration Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);

        usersTable.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        usersTable.setRowHeight(30);

        usersTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        usersTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        usersTable.getTableHeader().setBackground(
                new Color(35, 75, 130)
        );

        usersTable.getTableHeader().setForeground(
                Color.WHITE
        );

        usersTable.setAutoCreateRowSorter(true);

        rowSorter = new TableRowSorter<>(tableModel);

        usersTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(usersTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)
                )
        );

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );

        footerPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        refreshButton.addActionListener(e -> loadUsers());

        closeButton.addActionListener(e -> dispose());

        footerPanel.add(refreshButton);
        footerPanel.add(closeButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Search listener
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }
                }
        );

        // Role filter listener
        roleFilter.addActionListener(
                e -> applyFilters()
        );

        // Clear filters
        clearButton.addActionListener(e -> {

            searchField.setText("");
            roleFilter.setSelectedItem("ALL");

            applyFilters();
        });

        add(mainPanel);
    }

    // Load users from database
    private void loadUsers() {

        try {

            List<User> users = userService.getAllUsers();

            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a"
                    );

            for (User user : users) {

                String registrationDate = "N/A";

                Timestamp createdAt = user.getCreatedAt();

                if (createdAt != null) {

                    registrationDate =
                            dateFormat.format(createdAt);
                }

                String accountStatus = user.isActive()
                        ? "ACTIVE"
                        : "INACTIVE";

                Object[] row = {

                        user.getUserId(),

                        user.getFullName(),

                        user.getEmail(),

                        user.getRole(),

                        user.getPhone() != null
                                ? user.getPhone()
                                : "N/A",

                        accountStatus,

                        registrationDate
                };

                tableModel.addRow(row);
            }

            applyFilters();

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load users:\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Apply search and role filters
    private void applyFilters() {

        String searchText = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedRole =
                (String) roleFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Integer> filter =
                new RowFilter<>() {

                    @Override
                    public boolean include(
                            Entry<? extends DefaultTableModel,
                                    ? extends Integer> entry) {

                        String fullName = entry.getStringValue(1)
                                .toLowerCase();

                        String email = entry.getStringValue(2)
                                .toLowerCase();

                        String role = entry.getStringValue(3)
                                .toUpperCase();

                        boolean matchesSearch =
                                searchText.isEmpty()
                                        || fullName.contains(searchText)
                                        || email.contains(searchText);

                        boolean matchesRole =
                                selectedRole == null
                                        || selectedRole.equals("ALL")
                                        || role.equals(selectedRole);

                        return matchesSearch && matchesRole;
                    }
                };

        rowSorter.setRowFilter(filter);

        userCountLabel.setText(
                "Showing Users: " + usersTable.getRowCount()
                        + " / " + tableModel.getRowCount()
        );
    }
}
