package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin, btnExit;
    private JPanel mainPanel, formPanel, buttonPanel;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Handle window closing manually
        setMinimumSize(new Dimension(400, 300));
        setResizable(false);

        initUI();
        setLocationRelativeTo(null);

        // Add window listener for close confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        // Add ESC key to exit
        getRootPane().registerKeyboardAction(
                e -> confirmExit(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void initUI() {
        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Form panel
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thông tin đăng nhập"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Create components
        JLabel lblUser = new JLabel("Tài khoản:");
        JLabel lblPass = new JLabel("Mật khẩu:");

        tfUser = new JTextField(20);
        pfPass = new JPasswordField(20);

        // Set tooltips
        tfUser.setToolTipText("Nhập tên đăng nhập");
        pfPass.setToolTipText("Nhập mật khẩu");

        // Create buttons
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setMnemonic(KeyEvent.VK_ENTER);
        btnLogin.setPreferredSize(new Dimension(120, 30));

        btnExit = new JButton("Thoát");
        btnExit.setPreferredSize(new Dimension(120, 30));

        // Add action listeners
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> confirmExit());
        pfPass.addActionListener(e -> performLogin());

        // Configure GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Add components to form
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(tfUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(pfPass, gbc);

        // Add buttons to button panel
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        // Add components to main panel
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Focus on username field
        SwingUtilities.invokeLater(() -> tfUser.requestFocusInWindow());
    }

    private void performLogin() {
        String username = tfUser.getText().trim();
        String password = new String(pfPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading state
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);

        // Run login in background thread
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                try {
                    System.out.println("[LOGIN] Attempting login for user: " + username);
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.login(username, password);

                    if (user != null) {
                        System.out
                                .println("[LOGIN] SUCCESS: User '" + username + "' logged in. Role: " + user.getRole());
                    } else {
                        System.out.println("[LOGIN] FAILED: Invalid credentials for user '" + username + "'");
                    }

                    return user;
                } catch (SQLException ex) {
                    System.err.println("[LOGIN] DATABASE ERROR: " + ex.getMessage());
                    ex.printStackTrace();
                    throw new SQLException("Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), ex);
                } catch (Exception ex) {
                    System.err.println("[LOGIN] UNEXPECTED ERROR: " + ex.getMessage());
                    ex.printStackTrace();
                    throw ex;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnLogin.setEnabled(true);

                try {
                    User user = get(); // This will throw any exceptions from doInBackground

                    if (user == null) {
                        // Invalid credentials
                        System.out.println("[LOGIN] Showing invalid credentials message");
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Tên đăng nhập hoặc mật khẩu không đúng.\n\n" +
                                        "Vui lòng kiểm tra lại thông tin đăng nhập.",
                                "Đăng nhập thất bại",
                                JOptionPane.WARNING_MESSAGE);
                        pfPass.setText("");
                        tfUser.requestFocus();
                        return;
                    }

                    // Login successful
                    System.out.println("[LOGIN] Opening main application for user: " + user.getUsername());
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame(user);
                        mainFrame.setLocationRelativeTo(null);
                        mainFrame.setVisible(true);
                        dispose(); // Close login window
                    });

                } catch (java.util.concurrent.ExecutionException ex) {
                    // Handle execution exceptions
                    Throwable cause = ex.getCause();
                    String errorMessage = "Lỗi đăng nhập: ";

                    if (cause instanceof SQLException) {
                        errorMessage = "Lỗi kết nối cơ sở dữ liệu:\n" + cause.getMessage();
                        System.err.println("[LOGIN] Database connection error: " + cause.getMessage());
                    } else {
                        errorMessage += cause != null ? cause.getMessage() : ex.getMessage();
                        System.err.println("[LOGIN] Execution error: " + errorMessage);
                    }

                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            errorMessage,
                            "Lỗi đăng nhập",
                            JOptionPane.ERROR_MESSAGE);

                    pfPass.setText("");
                    tfUser.requestFocus();

                } catch (Exception ex) {
                    // Handle unexpected exceptions
                    String errorMessage = "Lỗi không xác định: " + ex.getMessage();
                    System.err.println("[LOGIN] Unexpected error: " + errorMessage);
                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            errorMessage,
                            "Lỗi đăng nhập",
                            JOptionPane.ERROR_MESSAGE);

                    pfPass.setText("");
                    tfUser.requestFocus();
                }
            }
        }.execute();
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thoát chương trình?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set default font
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("PasswordField.font", defaultFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}