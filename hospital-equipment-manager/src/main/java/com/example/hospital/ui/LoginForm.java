package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private final UserDAO userDAO = new UserDAO();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        super("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            System.out.println("[LOGIN] Attempting login for user: " + username);
            User user = userDAO.login(username, password);

            if (user != null) {
                System.out.println("[LOGIN] SUCCESS: User '" + username + "' logged in. Role: " + user.getRole());
                // Open main screen directly on successful login
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
                dispose();
            } else {
                System.out.println("[LOGIN] FAILED: Invalid credentials for user '" + username + "'");
                JOptionPane.showMessageDialog(this,
                        "Tên đăng nhập hoặc mật khẩu không đúng.\n\nVui lòng kiểm tra lại thông tin.",
                        "Đăng nhập thất bại",
                        JOptionPane.WARNING_MESSAGE);
                passwordField.setText("");
                usernameField.requestFocus();
            }
        } catch (Exception ex) {
            System.err.println("[LOGIN] DATABASE ERROR: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối cơ sở dữ liệu:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}