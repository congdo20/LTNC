package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setSize(380, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        tfUser = new JTextField(15);
        pfPass = new JPasswordField(15);
        btnLogin = new JButton("Đăng nhập");
        btnExit = new JButton("Thoát");

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tài khoản:"), gbc);

        gbc.gridx = 1;
        panel.add(tfUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        panel.add(pfPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        gbc.gridx = 1;
        panel.add(btnExit, gbc);

        add(panel);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void doLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin");
            return;
        }

        // UserDAO dao = new UserDAO();
        // User u = dao.login(user, pass);

        // if (u != null) {
        // JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
        // new MainFrame(u).setVisible(true);
        // dispose();
        // } else {
        // JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
        // }

        try {
            UserDAO dao = new UserDAO();
            User u = dao.login(user, pass);

            if (u != null) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainFrame(u).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
