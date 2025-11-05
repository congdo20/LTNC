// package com.example.hospital.ui;

// import javax.swing.*;

// import com.example.hospital.models.User;
// import com.example.hospital.ui.panels.DeptEquipPanel;
// import com.example.hospital.ui.panels.PlanPanel;
// import com.example.hospital.ui.panels.RequestPanel;
// import com.example.hospital.ui.panels.AssignPanel;
// import com.example.hospital.ui.panels.TaskPanel;
// import com.example.hospital.ui.panels.ReportPanel;

// import java.awt.*;

// public class MainFrame extends JFrame {
//     private User currentUser;
//         private CardLayout card;
//     private JPanel mainPanel;


//     public MainFrame(User user) {
//         this.currentUser = user;
//         setTitle("Hospital Equipment Manager");
//         setSize(1000, 650);
//         setDefaultCloseOperation(EXIT_ON_CLOSE);
//         setLocationRelativeTo(null);

//         showUserInfo(user);

//         JTabbedPane tabs = new JTabbedPane();

//         if (user.isAdmin()) {
//             tabs.addTab("Quản lý tài khoản", new UserManagementPanel());
//         }

//         if (user.isTruongKhoa()) {
//             tabs.addTab("Thiết bị khoa/viện", new DeptEquipPanel());
//             tabs.addTab("Yêu cầu bảo trì", new RequestPanel());
//         }

//         if (user.isQLThietBi()) {
//             tabs.addTab("Thiết bị", new EquipmentPanel());
//             tabs.addTab("Yêu cầu bảo trì", new RequestPanel());
//             tabs.addTab("Lên kế hoạch", new PlanPanel());
//             tabs.addTab("Phân công", new AssignPanel());
//         }

//         if (user.isNvBaoTri()) {
//             tabs.addTab("Nhiệm vụ", new TaskPanel());
//             // tabs.addTab("Báo cáo", new ReportPanel());
//             tabs.addTab("Báo cáo", new ReportPanel(currentUser));

//         }

//         add(tabs, BorderLayout.CENTER);
//     }


//     private void showUserInfo(User user) {
//         JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         JLabel lblUser = new JLabel(
//                 "Người dùng: " + user.getFullname()
//                         + "  |  Chức vụ: " + user.getRole());
//         userPanel.add(lblUser);
//         add(userPanel, BorderLayout.NORTH);
//     }

//     private void doLogout() {
//         dispose(); // đóng main
//         new LoginFrame().setVisible(true); // quay lại login
//     }

// }


package com.example.hospital.ui;

import javax.swing.*;

import com.example.hospital.models.User;
import com.example.hospital.ui.panels.*;

import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Hospital Equipment Manager");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildHeader(); // ✅ header chứa logout

        JTabbedPane tabs = new JTabbedPane();

        if (user.isAdmin()) {
            tabs.addTab("Quản lý tài khoản", new UserManagementPanel());
        }

        if (user.isTruongKhoa()) {
            tabs.addTab("Thiết bị khoa/viện", new DeptEquipPanel(currentUser));
            tabs.addTab("Yêu cầu bảo trì", new RequestPanel(currentUser));
        }

        if (user.isQLThietBi()) {
            tabs.addTab("Thiết bị", new EquipmentPanel());
            tabs.addTab("Yêu cầu bảo trì", new RequestPanel(currentUser));
            tabs.addTab("Lên kế hoạch", new PlanPanel());
            tabs.addTab("Phân công", new AssignPanel());
        }

        if (user.isNvBaoTri()) {
            tabs.addTab("Nhiệm vụ", new TaskPanel(currentUser));
            tabs.addTab("Báo cáo", new ReportPanel(currentUser));
        }

        add(tabs, BorderLayout.CENTER);
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());

        JLabel lbUser = new JLabel(
                "Người dùng: " + currentUser.getFullname() +
                        "  |  Chức vụ: " + currentUser.getRole());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> doLogout());

        header.add(lbUser, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void doLogout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
