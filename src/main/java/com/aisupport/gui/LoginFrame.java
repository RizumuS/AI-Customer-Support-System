package com.aisupport.gui;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    private final UserService userService =
            new UserService();

    public LoginFrame() {

        setTitle("AI Customer Support - Login");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(
            new GridBagLayout()
        );

        mainPanel.setBackground(
            new Color(245, 247, 250)
        );

        GridBagConstraints gbc =
            new GridBagConstraints();

        gbc.insets = new Insets(10, 25, 10, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel(
            "AI CUSTOMER SUPPORT",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 23)
        );

        title.setForeground(
            new Color(35, 55, 90)
        );

        gbc.gridy = 0;
        mainPanel.add(title, gbc);

        JLabel subtitle = new JLabel(
            "Sign in to your account",
            SwingConstants.CENTER
        );

        gbc.gridy = 1;
        mainPanel.add(subtitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 2;
        mainPanel.add(new JLabel("Email Address"), gbc);

        emailField = new JTextField(20);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(emailField, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 4;
        mainPanel.add(new JLabel("Password"), gbc);

        passwordField = new JPasswordField(20);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(passwordField, gbc);

        loginButton = new JButton("LOGIN");

        loginButton.setBackground(
            new Color(35, 75, 130)
        );

        loginButton.setForeground(Color.WHITE);

        loginButton.setFocusPainted(false);

        gbc.gridy = 6;
        mainPanel.add(loginButton, gbc);

        JButton registerButton =
            new JButton("Create New Account");

        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);

        gbc.gridy = 7;
        mainPanel.add(registerButton, gbc);

        add(mainPanel);

        loginButton.addActionListener(
            e -> performLogin()
        );

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });

        getRootPane().setDefaultButton(loginButton);
    }

    private void performLogin() {

        String email = emailField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter email and password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("LOGGING IN...");

        SwingWorker<User, Void> worker =
            new SwingWorker<>() {

            @Override
            protected User doInBackground()
                    throws Exception {

                return userService.login(
                    email,
                    password
                );
            }

            @Override
            protected void done() {

                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");

                try {

                    User user = get();

                    if (user == null) {

                        JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Invalid email or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Welcome, " +
                            user.getFullName() + "!"
                        );

                        dispose();

                        dispose();

if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

    new DashboardFrame(user).setVisible(true);

} else if ("AGENT".equalsIgnoreCase(user.getRole())) {

    new AgentDashboardFrame(user).setVisible(true);

} else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
    new AdminDashboardFrame(user).setVisible(true);
} else {

    JOptionPane.showMessageDialog(
        LoginFrame.this,
        "Invalid user role."
    );

    new LoginFrame().setVisible(true);
}dispose();

if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

    new DashboardFrame(user).setVisible(true);

} else if ("AGENT".equalsIgnoreCase(user.getRole())) {

    new AgentDashboardFrame(user).setVisible(true);

} else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
    new AdminDashboardFrame(user).setVisible(true);

}else {

    JOptionPane.showMessageDialog(
        LoginFrame.this,
        "Invalid user role."
    );

    new LoginFrame().setVisible(true);
};
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "Login error: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}
