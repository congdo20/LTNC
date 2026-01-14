package com.example.hospital.ui.panels;

import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.MaintenanceRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EquipmentRequestsDialog extends JDialog {

    private JTable table;
    private final MaintenanceRequestDAO reqDao = new MaintenanceRequestDAO();
    private final UserDAO userDao = new UserDAO();
    private final int equipmentId;
    private final Integer departmentId; // optional department filter

    public EquipmentRequestsDialog(Window parent, int equipmentId) {
        this(parent, equipmentId, null);
    }

    public EquipmentRequestsDialog(Window parent, int equipmentId, Integer departmentId) {
        super(parent, "Yêu cầu cho thiết bị", ModalityType.APPLICATION_MODAL);
        this.equipmentId = equipmentId;
        this.departmentId = departmentId;
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        table = new JTable(new DefaultTableModel(
                new String[] { "ID", "Người yêu cầu", "Mô tả", "Ngày", "Trạng thái", "Trạng thái kế hoạch" }, 0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        JButton btnRefresh = new JButton("Làm mới");
        bottom.add(btnRefresh);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        try {
            List<MaintenanceRequest> list;
            if (departmentId != null && departmentId > 0) {
                list = reqDao.findByEquipmentAndDepartment(equipmentId, departmentId);
            } else {
                list = reqDao.findByEquipment(equipmentId);
            }
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (MaintenanceRequest r : list) {
                String requester = String.valueOf(r.getRequesterId());
                try {
                    var u = userDao.findById(r.getRequesterId());
                    if (u != null)
                        requester = u.getFullname();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String planStatus = null;
                try {
                    planStatus = reqDao.getLatestPlanStatus(r.getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String planVietnamese = (planStatus == null) ? "-" : planStatus;

                model.addRow(new Object[] { r.getId(), requester, r.getIssueDescription(), r.getRequestDate(),
                        r.getStatus(), planVietnamese });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải yêu cầu: " + ex.getMessage());
        }
    }
}
