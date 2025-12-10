package com.example.hospital.ui.panels;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.MaintenanceDAO;
import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.MaintenanceTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AssignPanel extends JPanel {

    private final MaintenanceDAO maintDao = new MaintenanceDAO();
    private final EquipmentDAO equipmentDao = new EquipmentDAO();
    private final UserDAO userDao = new UserDAO();
    private JTable table;
    private DefaultTableModel model;

    public AssignPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Phân công nhiệm vụ", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "ID", "Request ID", "Thiết bị", "Ngày lên kế hoạch", "Người lập",
                "Ghi chú", "Trạng thái", "Người được phân công" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add assign button: assign selected plan(s) to a technician
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAssign = new JButton("Phân công");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnApprove = new JButton("Xác nhận nghiệm thu");
        bottom.add(btnAssign);
        bottom.add(btnApprove);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        btnAssign.addActionListener(e -> assignSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnApprove.addActionListener(e -> approveSelected());

        loadData();
    }

    private void assignSelected() {
        int[] sel = table.getSelectedRows();
        if (sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một kế hoạch để phân công");
            return;
        }

        java.util.List<Integer> planIds = new java.util.ArrayList<>();
        for (int r : sel) {
            planIds.add((Integer) model.getValueAt(r, 0));
        }

        // load technicians
        java.util.List<com.example.hospital.models.User> techs = new java.util.ArrayList<>();
        try {
            for (com.example.hospital.models.User u : userDao.findAll()) {
                if (u != null && u.getRole() == com.example.hospital.models.User.Role.NV_BAO_TRI)
                    techs.add(u);
            }
        } catch (Exception ex) {
            // ignore
        }

        if (techs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có kỹ thuật viên để phân công");
            return;
        }

        JComboBox<String> cb = new JComboBox<>();
        for (com.example.hospital.models.User t : techs)
            cb.addItem(t.getFullname());

        int res = JOptionPane.showConfirmDialog(this, cb, "Chọn kỹ thuật viên để phân công",
                JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION)
            return;

        int idx = cb.getSelectedIndex();
        if (idx < 0)
            return;
        com.example.hospital.models.User chosen = techs.get(idx);

        int assigned = 0;
        try {
            for (int planId : planIds) {
                maintDao.assignPlan(planId, chosen.getId());
                assigned++;
            }
            JOptionPane.showMessageDialog(this, "Đã phân công " + assigned + " kế hoạch cho " + chosen.getFullname());
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi phân công: " + ex.getMessage());
        }
    }

    public void loadData() {
        try {
            model.setRowCount(0);
            List<MaintenanceTask> list = maintDao.findAll();
            for (MaintenanceTask m : list) {
                String eqName = String.valueOf(m.getEquipmentId());
                try {
                    var eq = equipmentDao.findById(m.getEquipmentId());
                    if (eq != null)
                        eqName = eq.getName();
                } catch (Exception ex) {
                    // ignore
                }

                String planner = "";
                try {
                    if (m.getPlannerId() != null) {
                        var u = userDao.findById(m.getPlannerId());
                        if (u != null)
                            planner = u.getFullname();
                    }
                } catch (Exception ex) {
                    // ignore
                }

                String assignedTech = "";
                try {
                    Integer techId = maintDao.getAssignedTechnicianId(m.getId());
                    if (techId != null) {
                        var tu = userDao.findById(techId);
                        if (tu != null)
                            assignedTech = tu.getFullname();
                    }
                } catch (Exception ex) {
                    // ignore
                }

                String planStatus = "";
                try {
                    String code = maintDao.getPlanStatus(m.getId());
                    planStatus = code == null ? "-" : code;
                } catch (Exception ex) {
                    planStatus = m.isCompleted() ? "HOAN_THANH" : "CHO_THUC_HIEN";
                }

                model.addRow(new Object[] { m.getId(), m.getRequestId(), eqName, m.getScheduleDate(), planner,
                        m.getNote(), planStatus, assignedTech });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải phân công: " + ex.getMessage());
        }
    }

    private void approveSelected() {
        int[] sel = table.getSelectedRows();
        if (sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một kế hoạch để nghiệm thu");
            return;
        }

        int approved = 0;
        try {
            for (int r : sel) {
                int planId = (Integer) model.getValueAt(r, 0);
                // only approve plans that are pending approval
                String status = maintDao.getPlanStatus(planId);
                if ("CHO_NGHIEM_THU".equals(status)) {
                    maintDao.approvePlan(planId);
                    approved++;
                }
            }
            if (approved > 0) {
                JOptionPane.showMessageDialog(this, "Đã nghiệm thu và hoàn thành " + approved + " kế hoạch");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không có kế hoạch nào ở trạng thái 'CHỜ NGHIỆM THU' để nghiệm thu");
            }
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi nghiệm thu: " + ex.getMessage());
        }
    }
}
