package com.example.hospital.ui;


import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin;

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        JLabel lblPass = new JLabel("Mật khẩu:");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Đăng nhập");

        panel.add(lblUser);
        panel.add(txtUsername);
        panel.add(lblPass);
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!\nXin chào " + user.getFullname());

            dispose();

            HomePage home = new HomePage(user);
            home.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
