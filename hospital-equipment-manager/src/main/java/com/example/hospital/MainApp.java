// package com.example.hospital;

// import com.example.hospital.ui.MainFrame;
// import javax.swing.*;

// public class MainApp {
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
package com.example.hospital;

import com.example.hospital.ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        // Sử dụng Event Dispatch Thread cho Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Sử dụng giao diện hệ thống
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Cải thiện giao diện
                UIManager.put("TabbedPane.contentOpaque", false);
                UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 2, 2));
                
                // Cấu hình font chữ mặc định
                Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
                UIManager.put("Button.font", defaultFont);
                UIManager.put("Label.font", defaultFont);
                UIManager.put("TextField.font", defaultFont);
                UIManager.put("Table.font", defaultFont);
                UIManager.put("TableHeader.font", defaultFont.deriveFont(Font.BOLD));
                
                // Hiển thị màn hình đăng nhập
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khi khởi tạo ứng dụng: " + e.getMessage(),
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}