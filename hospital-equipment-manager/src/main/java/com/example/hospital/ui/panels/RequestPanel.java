package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;
import com.example.hospital.ui.RequestDialog;
import com.example.hospital.ui.MaintenanceRequestFormSwing;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RequestPanel extends JPanel {

    private JTable table;
    private MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
    private User currentUser;
    private java.util.List<MaintenanceRequest> currentRequests = new java.util.ArrayList<>();

    public RequestPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());

        // If current user is equipment manager, show status controls (no create button)
        if (currentUser != null && currentUser.isQLThietBi()) {
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            top.add(new JLabel("Chọn trạng thái: "));
            JComboBox<String> cbStatus = new JComboBox<>(
                    new String[] { "CHO_XU_LY", "DA_LAP_KE_HOACH", "DA_TU_CHOI" });
            top.add(cbStatus);
            JButton btnUpdate = new JButton("Cập nhật trạng thái");
            top.add(btnUpdate);
            JButton btnCreatePlan = new JButton("Tạo kế hoạch");
            top.add(btnCreatePlan);
            JButton btnRefresh = new JButton("Làm mới");
            top.add(btnRefresh);

            btnUpdate.addActionListener(ae -> {
                int r = table.getSelectedRow();
                if (r < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu trong danh sách");
                    return;
                }
                int id = (int) table.getValueAt(r, 0);
                String newStatus = (String) cbStatus.getSelectedItem();
                try {
                    dao.updateStatus(id, newStatus);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
                }
            });

            // synchronize combo with selected row
            table = new JTable(new DefaultTableModel(
                    new String[] { "ID", "Thiết bị", "Mô tả", "Ngày", "Trạng thái", "Trạng thái kế hoạch" }, 0));
            table.getSelectionModel().addListSelectionListener(ev -> {
                if (ev.getValueIsAdjusting())
                    return;
                int r = table.getSelectedRow();
                if (r < 0)
                    return;
                Object s = table.getValueAt(r, 4);
                if (s != null) {
                    String statusCode = statusVietnameseToCode(s.toString());
                    cbStatus.setSelectedItem(statusCode);
                }
            });

            add(top, BorderLayout.NORTH);

            btnCreatePlan.addActionListener(ae -> {
                int[] sel = table.getSelectedRows();
                if (sel == null || sel.length == 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một yêu cầu để lập kế hoạch");
                    return;
                }
                java.util.List<MaintenanceRequest> selected = new java.util.ArrayList<>();
                for (int r : sel) {
                    int id = (int) table.getValueAt(r, 0);
                    for (MaintenanceRequest mr : currentRequests) {
                        if (mr.getId() == id) {
                            selected.add(mr);
                            break;
                        }
                    }
                }
                com.example.hospital.ui.panels.PlanPanel helper = new com.example.hospital.ui.panels.PlanPanel(
                        currentUser);
                helper.createPlansFor(selected, this);
                loadData();
            });
            // refresh button for managers
            btnRefresh.addActionListener(ae -> loadData());
        } else {
            JButton btnAdd = new JButton("Tạo yêu cầu");
            btnAdd.addActionListener(e -> openCreateDialog());
            add(btnAdd, BorderLayout.NORTH);
            table = new JTable(new DefaultTableModel(
                    new String[] { "ID", "Thiết bị", "Mô tả", "Ngày", "Trạng thái", "Trạng thái kế hoạch" }, 0));
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void openCreateDialog() {
        // If the current user is a department head, open the full maintenance form
        if (currentUser != null && currentUser.isTruongKhoa()) {
            MaintenanceRequestFormSwing form = new MaintenanceRequestFormSwing(currentUser);
            // Ensure disposing the form doesn't exit the application
            form.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // When the form is closed, refresh the table to show any new requests
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadData();
                }
            });
            form.setVisible(true);
            return;
        }

        // Default behavior for other users: open modal RequestDialog
        RequestDialog dlg = new RequestDialog(SwingUtilities.getWindowAncestor(this), currentUser);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void loadData() {
        try {
            List<MaintenanceRequest> list;

            if (currentUser.isQLThietBi()) {
                list = dao.findAll(); // ✅ xem tất cả
            } else {
                list = dao.findByDepartment(currentUser.getDepartmentId());
            }

            this.currentRequests = list;

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            EquipmentDAO equipmentDao = new EquipmentDAO();
            com.example.hospital.dao.MaintenanceRequestDAO reqDao = new com.example.hospital.dao.MaintenanceRequestDAO();
            for (MaintenanceRequest r : list) {
                String equipmentName = String.valueOf(r.getEquipmentId());
                try {
                    var eq = equipmentDao.findById(r.getEquipmentId());
                    if (eq != null) {
                        equipmentName = eq.getName();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String vietnameseStatus = statusCodeToVietnamese(r.getStatus());
                String planStatusCode = null;
                try {
                    planStatusCode = reqDao.getLatestPlanStatus(r.getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String planVietnamese = (planStatusCode == null) ? "-" : planStatusCodeToVietnamese(planStatusCode);
                model.addRow(new Object[] { r.getId(), equipmentName, r.getIssueDescription(), r.getRequestDate(),
                        vietnameseStatus, planVietnamese });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    /**
     * Convert status code to Vietnamese display text
     */
    private String statusCodeToVietnamese(String statusCode) {
        if (statusCode == null) {
            return "Chưa xác định";
        }
        switch (statusCode) {
            case "CHO_XU_LY":
                return "Chờ xử lý";
            case "DA_LAP_KE_HOACH":
                return "Đã lập kế hoạch";
            case "DA_TU_CHOI":
                return "Đã từ chối";
            case "HOAN_THANH":
                return "Hoàn thành";
            default:
                return statusCode;
        }
    }

    /**
     * Convert Vietnamese status to status code
     */
    private String statusVietnameseToCode(String vietnameseStatus) {
        if (vietnameseStatus == null) {
            return "CHO_XU_LY";
        }
        switch (vietnameseStatus) {
            case "Chờ xử lý":
                return "CHO_XU_LY";
            case "Đã lập kế hoạch":
                return "DA_LAP_KE_HOACH";
            case "Đã từ chối":
                return "DA_TU_CHOI";
            case "Hoàn thành":
                return "HOAN_THANH";
            default:
                return vietnameseStatus;
        }
    }

    private String planStatusCodeToVietnamese(String statusCode) {
        if (statusCode == null)
            return "-";
        switch (statusCode) {
            case "CHO_THUC_HIEN":
                return "Chờ thực hiện";
            case "DANG_THUC_HIEN":
                return "Đang thực hiện";
            case "CHO_NGHIEM_THU":
                return "Chờ nghiệm thu";
            case "HOAN_THANH":
                return "Hoàn thành";
            default:
                return statusCode;
        }
    }

}
