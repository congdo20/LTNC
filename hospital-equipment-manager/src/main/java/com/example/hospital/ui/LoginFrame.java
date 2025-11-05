// package com.example.hospital.ui;

// import com.example.hospital.dao.UserDAO;
// import com.example.hospital.model.User;
// import javax.swing.*;
// import java.awt.*;

// public class LoginFrame extends JFrame {
//     private JTextField userField = new JTextField(15);
//     private JPasswordField passField = new JPasswordField(15);
//     private JButton loginBtn = new JButton("Đăng nhập");

//     public LoginFrame() {
//         setTitle("Đăng nhập hệ thống");
//         setDefaultCloseOperation(EXIT_ON_CLOSE);
//         setLayout(new GridLayout(3, 2, 10, 10));
//         add(new JLabel("Tên đăng nhập:"));
//         add(userField);
//         add(new JLabel("Mật khẩu:"));
//         add(passField);
//         add(new JLabel());
//         add(loginBtn);

//         loginBtn.addActionListener(e -> login());
//         pack();
//         setLocationRelativeTo(null);
//     }

//     private void login() {
//         String username = userField.getText().trim();
//         String password = new String(passField.getPassword());
//         User user = new UserDAO().login(username, password);
//         if (user != null) {
//             JOptionPane.showMessageDialog(this, "Xin chào " + user.getFullname());
//             dispose();
//             new MainFrame(user).setVisible(true);
//         } else {
//             JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
//         }
//     }
// }


// package com.example.hospital.ui;

// import com.example.hospital.dao.UserDAO;
// import com.example.hospital.models.User;

// import javax.swing.*;
// import java.awt.*;

// public class LoginFrame extends JFrame {
//     private JTextField tfUser = new JTextField(15);
//     private JPasswordField pfPass = new JPasswordField(15);
//     private JButton btnLogin = new JButton("Đăng nhập");

//     public LoginFrame() {
//         setTitle("Đăng nhập - Hệ thống quản lý thiết bị y tế");
//         setDefaultCloseOperation(EXIT_ON_CLOSE);
//         setLayout(new BorderLayout());
//         JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
//         p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//         p.add(new JLabel("Tên đăng nhập:"));
//         p.add(tfUser);
//         p.add(new JLabel("Mật khẩu:"));
//         p.add(pfPass);
//         p.add(new JLabel());
//         p.add(btnLogin);
//         add(p, BorderLayout.CENTER);
//         pack();
//         setLocationRelativeTo(null);

//         btnLogin.addActionListener(e -> doLogin());
//     }

//     private void doLogin() {
//         String u = tfUser.getText().trim();
//         String p = new String(pfPass.getPassword());
//         User user = new UserDAO().login(u, p);
//         if (user != null) {
//             SwingUtilities.invokeLater(() -> {
//                 new MainFrame(user).setVisible(true);
//             });
//             dispose();
//         } else {
//             JOptionPane.showMessageDialog(this, "Sai tài khoản/mật khẩu", "Đăng nhập thất bại",
//                     JOptionPane.WARNING_MESSAGE);
//         }
//     }
// }


package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;

import javax.swing.*;
import java.awt.*;

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

        UserDAO dao = new UserDAO();
        User u = dao.login(user, pass);

        if (u != null) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            new MainFrame(u).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
