package com.example.hospital.ui;

import javax.swing.JComboBox;
import javax.swing.*;

import com.example.hospital.dao.MaintenanceDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.MaintenanceTask;
import com.example.hospital.dao.EquipmentDAO;

import java.awt.Window;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;

public class MaintenanceDialog extends JDialog {
    private boolean saved = false;
    private JComboBox<String> cbEquipment;
    private JTextField tfDate;
    private JCheckBox cbDone;
    private JTextField tfNote;
    private MaintenanceTask task;
    private MaintenanceDAO maintDAO = new MaintenanceDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private java.util.List<Equipment> equipments;

    public MaintenanceDialog(Window owner, MaintenanceTask m) {
        super(owner, "Bảo trì", ModalityType.APPLICATION_MODAL);
        this.task = m == null ? new MaintenanceTask() : m;
        initUI();
        setSize(400, 260);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        try {
            equipments = equipmentDAO.findAll();
        } catch (SQLException e) {
            equipments = java.util.Collections.emptyList();
        }
        cbEquipment = new JComboBox<>();
        for (Equipment e : equipments)
            cbEquipment.addItem(e.getId() + " - " + e.getName());
        if (task.getEquipmentId() != 0) {
            for (int i = 0; i < equipments.size(); i++)
                if (equipments.get(i).getId() == task.getEquipmentId()) {
                    cbEquipment.setSelectedIndex(i);
                    break;
                }
        }
        tfDate = new JTextField(
                task.getScheduleDate() == null ? LocalDate.now().toString() : task.getScheduleDate().toString());
        cbDone = new JCheckBox("Hoàn thành", task.isCompleted());
        tfNote = new JTextField(task.getNote() == null ? "" : task.getNote());

        JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Thiết bị:"));
        p.add(cbEquipment);
        p.add(new JLabel("Ngày (yyyy-mm-dd):"));
        p.add(tfDate);
        p.add(new JLabel());
        p.add(cbDone);
        p.add(new JLabel("Ghi chú:"));
        p.add(tfNote);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        JPanel b = new JPanel();
        b.add(btnSave);
        b.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(b, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> doSave());
        btnCancel.addActionListener(e -> dispose());
    }

    private void doSave() {
        try {
            int idx = cbEquipment.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Chọn thiết bị");
                return;
            }
            Equipment eq = equipments.get(idx);
            task.setEquipmentId(eq.getId());
            task.setScheduleDate(LocalDate.parse(tfDate.getText().trim()));
            task.setCompleted(cbDone.isSelected());
            task.setNote(tfNote.getText().trim());

            if (task.getId() == 0)
                maintDAO.create(task);
            else
                maintDAO.update(task);

            // if marked completed, update equipment.last_maintenance
            if (task.isCompleted()) {
                eq.setLastMaintenance(task.getScheduleDate());
                equipmentDAO.update(eq);
            }

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