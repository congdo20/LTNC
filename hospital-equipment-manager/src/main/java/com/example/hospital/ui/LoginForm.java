package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.model.User;

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
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}