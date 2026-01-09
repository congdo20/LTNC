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
    public void refreshTabs() {
        tabs.removeAll(); // Xóa tất cả tabs cũ

        // Quản lý tài khoản (ADMIN only or with permission)
        if (currentUser.isAdmin() || currentUser.hasPermission(Permission.MANAGE_ACCOUNTS)) {
            tabs.addTab("Quản lý tài khoản", new UserManagementPanel(this));
        }

        // Thiết bị khoa/viện (TRUONG_KHOA or permission)
        if (currentUser.isAdmin() || currentUser.isTruongKhoa()
                || currentUser.hasPermission(Permission.VIEW_DEPT_EQUIPMENT)) {
            tabs.addTab("Thiết bị khoa/viện", new DeptEquipPanel(currentUser));
        }

        // Thiết bị (QL_THIET_BI or permission)
        if (currentUser.isAdmin() || currentUser.isQLThietBi()
                || currentUser.hasPermission(Permission.VIEW_EQUIPMENT)) {
            tabs.addTab("Thiết bị", new EquipmentPanel(currentUser));
        }

        // Yêu cầu bảo trì (TRUONG_KHOA, QL_THIET_BI or permission)
        if (currentUser.isAdmin() || currentUser.isTruongKhoa() || currentUser.isQLThietBi()
                || currentUser.hasPermission(Permission.CREATE_REQUEST)) {
            tabs.addTab("Yêu cầu bảo trì", new RequestPanel(currentUser));
        }

        // Lên kế hoạch (QL_THIET_BI or permission)
        if (currentUser.isAdmin() || currentUser.isQLThietBi() || currentUser.hasPermission(Permission.PLAN)) {
            // tabs.addTab("Lên kế hoạch", new PlanPanel());
        }

        // Phân công (QL_THIET_BI or permission)
        if (currentUser.isAdmin() || currentUser.isQLThietBi() || currentUser.hasPermission(Permission.ASSIGN)) {
            tabs.addTab("Phân công", new AssignPanel());
        }

        // Nhiệm vụ (NV_BAO_TRI or permission)
        if (currentUser.isAdmin() || currentUser.isNvBaoTri() || currentUser.hasPermission(Permission.TASK)) {
            tabs.addTab("Nhiệm vụ", new TaskPanel(currentUser));
        }

        // Báo cáo (NV_BAO_TRI or permission)
        if (currentUser.isAdmin() || currentUser.isNvBaoTri() || currentUser.hasPermission(Permission.REPORT)) {
            tabs.addTab("Báo cáo", new ReportPanel(currentUser));
        }

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
