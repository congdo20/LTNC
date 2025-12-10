package com.example.hospital.ui.panels;

import javax.swing.*;

import java.awt.*;
import java.sql.SQLException;

public class TaskPanel extends JPanel {

    private final com.example.hospital.dao.MaintenanceDAO maintDao = new com.example.hospital.dao.MaintenanceDAO();
    private final com.example.hospital.dao.EquipmentDAO equipmentDao = new com.example.hospital.dao.EquipmentDAO();
    private final com.example.hospital.dao.UserDAO userDao = new com.example.hospital.dao.UserDAO();

    private JTable table;
    private javax.swing.table.DefaultTableModel model;
    private JButton btnComplete;
    private JButton btnRefresh;

    public TaskPanel() {
        this(null);
    }

    public TaskPanel(com.example.hospital.models.User user) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Danh sách nhiệm vụ bảo trì", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        model = new javax.swing.table.DefaultTableModel(
                new String[] { "Mã nhiệm vụ", "Thiết bị", "Hạn", "Người phân công", "Trạng thái", "Task Object" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnComplete = new JButton("Đánh dấu hoàn thành");
        btnRefresh = new JButton("Làm mới");
        bottom.add(btnComplete);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        // Update button text based on user role after buttons are created
        updateButtonText(user);

        btnRefresh.addActionListener(e -> loadData(user));
        btnComplete.addActionListener(e -> markSelectedComplete(user));

        loadData(user);
    }

    private void loadData(com.example.hospital.models.User user) {
        try {
            model.setRowCount(0);
            java.util.List<com.example.hospital.models.MaintenanceTask> list;
            if (user != null && user.isNvBaoTri()) {
                // Technician: show tasks assigned to them
                list = maintDao.findPlansAssignedToTechnician(user.getId());
            } else if (user != null && user.isQLThietBi()) {
                // Equipment manager: show tasks waiting for approval (CHO_NGHIEM_THU)
                list = maintDao.findPlansWaitingForApproval();
            } else {
                // Other roles: show all plans
                list = maintDao.findAll();
            }

            for (com.example.hospital.models.MaintenanceTask m : list) {
                String eqName = String.valueOf(m.getEquipmentId());
                try {
                    var eq = equipmentDao.findById(m.getEquipmentId());
                    if (eq != null)
                        eqName = eq.getName();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String planner = "";
                try {
                    if (m.getPlannerId() != null) {
                        var u = userDao.findById(m.getPlannerId());
                        if (u != null)
                            planner = u.getFullname();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Get the actual status from the task
                String status = getStatusText(m);

                model.addRow(new Object[] { m.getId(), eqName, m.getScheduleDate(), planner, status, m });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải nhiệm vụ: " + ex.getMessage());
        }
    }

    /**
     * Get the display text for a task status
     */
    private String getStatusText(com.example.hospital.models.MaintenanceTask task) {
        try {
            // Get the actual status from database
            String sql = "SELECT status FROM maintenance_plans WHERE id = ?";
            try (java.sql.Connection c = com.example.hospital.db.DBUtil.getConnection();
                    java.sql.PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, task.getId());
                try (java.sql.ResultSet rs = p.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if ("CHO_THUC_HIEN".equals(status)) {
                            return "CHỜ THỰC HIỆN";
                        } else if ("DANG_THUC_HIEN".equals(status)) {
                            return "ĐANG THỰC HIỆN";
                        } else if ("CHO_NGHIEM_THU".equals(status)) {
                            return "CHỜ NGHIỆM THU";
                        } else if ("HOAN_THANH".equals(status)) {
                            return "HOÀN THÀNH";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return task.isCompleted() ? "HOÀN THÀNH" : "CHỜ THỰC HIỆN";
    }

    private void markSelectedComplete(com.example.hospital.models.User user) {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhiệm vụ");
            return;
        }

        int taskId = (Integer) model.getValueAt(r, 0);
        com.example.hospital.models.MaintenanceTask task = (com.example.hospital.models.MaintenanceTask) model
                .getValueAt(r, 5);

        try {
            // If user is technician, mark as pending approval
            if (user.isNvBaoTri()) {
                maintDao.markPlanPendingApproval(taskId);
                JOptionPane.showMessageDialog(this, "Đã đánh dấu hoàn thành. Chờ quản lý thiết bị nghiệm thu.");
            }
            // If user is equipment manager, approve the task
            else if (user.isQLThietBi()) {
                maintDao.approvePlan(taskId);
                JOptionPane.showMessageDialog(this, "Đã xác nhận nghiệm thu và hoàn thành nhiệm vụ");
            }

            loadData(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái: " + ex.getMessage());
        }
    }

    // Add this method to update button text based on user role
    private void updateButtonText(com.example.hospital.models.User user) {
        if (user == null)
            return;

        if (user.isNvBaoTri()) {
            btnComplete.setText("Hoàn thành");
        } else if (user.isQLThietBi()) {
            btnComplete.setText("Xác nhận nghiệm thu");
        }
    }
}
