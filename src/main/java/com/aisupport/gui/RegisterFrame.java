package com.aisupport.gui;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;

    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;

    private final JButton registerButton;

    private final UserService userService =
            new UserService();

    public RegisterFrame() {

        setTitle("AI Customer Support - Registration");
        setSize(450, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(
            new GridBagLayout()
        );

        panel.setBackground(
            new Color(245, 247, 250)
        );

        GridBagConstraints gbc =
            new GridBagConstraints();

        gbc.insets = new Insets(8, 25, 8, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel(
            "CREATE ACCOUNT",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 23)
        );

        title.setForeground(
            new Color(35, 55, 90)
        );

        gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        panel.add(new JLabel("Full Name"), gbc);

        nameField = new JTextField(20);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(nameField, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 3;
        panel.add(new JLabel("Email Address"), gbc);

        emailField = new JTextField(20);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(emailField, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 5;
        panel.add(new JLabel("Phone Number"), gbc);

        phoneField = new JTextField(20);

        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(phoneField, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 7;
        panel.add(new JLabel("Password"), gbc);

        passwordField = new JPasswordField(20);

        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 9;
        panel.add(new JLabel("Confirm Password"), gbc);

        confirmPasswordField = new JPasswordField(20);

        gbc.gridy = 10;
        gbc.gridwidth = 2;
        panel.add(confirmPasswordField, gbc);

        registerButton = new JButton("REGISTER");

        registerButton.setBackground(
            new Color(35, 75, 130)
        );

        registerButton.setForeground(Color.WHITE);

        registerButton.setFocusPainted(false);

        gbc.gridy = 11;
        panel.add(registerButton, gbc);

        add(panel);

        registerButton.addActionListener(
            e -> performRegistration()
        );
    }

    private void performRegistration() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        String confirmPassword =
            new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() ||
            password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please fill in all required fields."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match."
            );

            return;
        }

        User user = new User(
            name,
            email,
            password,
            "CUSTOMER",
            phone.isEmpty() ? null : phone
        );

        registerButton.setEnabled(false);
        registerButton.setText("REGISTERING...");

        SwingWorker<Boolean, Void> worker =
            new SwingWorker<>() {

            @Override
            protected Boolean doInBackground()
                    throws Exception {

                return userService.registerUser(user);
            }

            @Override
            protected void done() {

                registerButton.setEnabled(true);
                registerButton.setText("REGISTER");

                try {

                    if (get()) {

                        JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            "Account created successfully! " +
                            "You can now log in."
                        );

                        dispose();

                    } else {

                        JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            "Registration could not be completed."
                        );
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                        RegisterFrame.this,
                        "Registration error: " +
                        ex.getCause().getMessage(),
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}
