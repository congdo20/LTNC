package com.example.hospital.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;

public class RequestDialog extends JDialog {

    private JComboBox<String> cbEquipment;
    private JTextArea txtIssue;
    private JComboBox<String> cbPriority;

    private boolean saved = false;

    private MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private User user;

    public RequestDialog(Window owner, User user) {
        super(owner, "Tạo yêu cầu bảo trì", ModalityType.APPLICATION_MODAL);
        this.user = user;

        initUI();
        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    private void initUI() {

        // Load equipment of user's department
        List<Equipment> eqList;
        try {
            eqList = equipmentDAO.findByDepartment(user.getDepartmentId());
        } catch (SQLException e) {
            eqList = java.util.Collections.emptyList();
        }

        cbEquipment = new JComboBox<>();
        for (Equipment e : eqList)
            cbEquipment.addItem(e.getId() + " - " + e.getName());

        txtIssue = new JTextArea(3, 20);

        cbPriority = new JComboBox<>(new String[] { "CAO", "TRUNG_BINH", "THAP" });

        JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
        p.add(new JLabel("Thiết bị:"));
        p.add(cbEquipment);
        p.add(new JLabel("Mô tả sự cố:"));
        p.add(new JScrollPane(txtIssue));
        p.add(new JLabel("Độ ưu tiên:"));
        p.add(cbPriority);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void save() {
        try {
            MaintenanceRequest r = new MaintenanceRequest();

            String equipStr = (String) cbEquipment.getSelectedItem();
            int eqId = Integer.parseInt(equipStr.split(" - ")[0]);

            r.setEquipmentId(eqId);
            r.setRequesterId(user.getId());
            r.setDepartmentId(user.getDepartmentId());
            r.setIssueDescription(txtIssue.getText());
            r.setPriority((String) cbPriority.getSelectedItem());

            dao.create(r);

            saved = true;
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu: " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
