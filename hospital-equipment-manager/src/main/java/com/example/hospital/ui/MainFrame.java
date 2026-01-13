package com.example.hospital.ui;

import javax.swing.*;

import com.example.hospital.models.User;
import com.example.hospital.models.Permission;
import com.example.hospital.ui.panels.*;

import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabs;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Hospital Equipment Manager");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildHeader();

        tabs = new JTabbedPane();
        refreshTabs();

        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Refresh tabs based on current user permissions
     * Called when permissions change or app starts
     */
    public static java.util.List<String> computeTabsFor(User user) {
        java.util.List<String> titles = new java.util.ArrayList<>();
        if (user == null)
            return titles;

        if (user.isAdmin() || user.hasPermission(Permission.MANAGE_ACCOUNTS)) {
            titles.add("Quản lý tài khoản");
        }
        if (user.isAdmin() || user.isTruongKhoa() || user.hasPermission(Permission.VIEW_DEPT_EQUIPMENT)) {
            titles.add("Thiết bị khoa/viện");
        }
        if (user.isAdmin() || user.isQLThietBi() || user.hasPermission(Permission.VIEW_EQUIPMENT)) {
            titles.add("Thiết bị");
        }
        if (user.isAdmin() || user.isTruongKhoa() || user.isQLThietBi()
                || user.hasPermission(Permission.CREATE_REQUEST)) {
            titles.add("Yêu cầu bảo trì");
        }
        if (user.isAdmin() || user.isQLThietBi() || user.hasPermission(Permission.PLAN)) {
            // titles.add("Lên kế hoạch");
        }
        if (user.isAdmin() || user.isQLThietBi() || user.hasPermission(Permission.ASSIGN)) {
            titles.add("Phân công");
        }
        if (user.isAdmin() || user.isNvBaoTri() || user.hasPermission(Permission.TASK)) {
            titles.add("Nhiệm vụ");
        }
        if (user.isAdmin() || user.isNvBaoTri() || user.hasPermission(Permission.REPORT)) {
            titles.add("Báo cáo");
        }
        return titles;
    }

    public void refreshTabs() {
        // Rebuild tabs based on computed titles
        tabs.removeAll();
        java.util.List<String> titles = computeTabsFor(currentUser);
        for (String t : titles) {
            switch (t) {
                case "Quản lý tài khoản":
                    tabs.addTab(t, new UserManagementPanel(this));
                    break;
                case "Thiết bị khoa/viện":
                    tabs.addTab(t, new DeptEquipPanel(currentUser));
                    break;
                case "Thiết bị":
                    tabs.addTab(t, new EquipmentPanel(currentUser));
                    break;
                case "Yêu cầu bảo trì":
                    tabs.addTab(t, new RequestPanel(currentUser));
                    break;
                case "Phân công":
                    tabs.addTab(t, new AssignPanel());
                    break;
                case "Nhiệm vụ":
                    tabs.addTab(t, new TaskPanel(currentUser));
                    break;
                case "Báo cáo":
                    tabs.addTab(t, new ReportPanel(currentUser));
                    break;
                default:
                    // ignore
            }
        }
        System.out.println("[TABS] Refreshed. Total tabs: " + tabs.getTabCount());
        tabs.revalidate();
        tabs.repaint();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        JLabel lbUser = new JLabel(
                "Người dùng: " + currentUser.getFullname() +
                        "  |  Chức vụ: " + currentUser.getRole());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> doLogout());

        // notifications button with unread count
        JButton btnNotifications = new JButton("Thông báo");
        btnNotifications.addActionListener(e -> showNotifications());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnNotifications);
        right.add(btnLogout);

        header.add(lbUser, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        // initial unread count update
        updateNotificationCount(btnNotifications);

        add(header, BorderLayout.NORTH);
    }

    private void updateNotificationCount(JButton btn) {
        try {
            if (currentUser == null)
                return;
            int c = new com.example.hospital.dao.NotificationDAO().countUnreadForUser(currentUser.getId());
            btn.setText("Thông báo (" + c + ")");
        } catch (Exception ex) {
            // ignore
        }
    }

    // ✅ Getter để UserManagementPanel có thể truy cập và update permissions
    public User getCurrentUser() {
        return currentUser;
    }

    private void showNotifications() {
        if (currentUser == null)
            return;
        NotificationsPanel panel = new NotificationsPanel(currentUser.getId());
        JDialog dlg = new JDialog(this, "Thông báo", true);
        dlg.getContentPane().add(panel);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void doLogout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
