package com.example.hospital.ui;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.MaintenanceDAO;
import com.example.hospital.model.Equipment;
import com.example.hospital.model.MaintenanceTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenancePanel extends JPanel {
    private MaintenanceDAO maintDAO = new MaintenanceDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private JTable table;
    private DefaultTableModel model;
    private Map<Integer, Equipment> equipmentCache = new HashMap<>();

    public MaintenancePanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[] { "ID", "Equipment", "Schedule", "Done", "Note" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);
        btns.add(btnRefresh);
        add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> openDialog(null));
        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            try {
                List<MaintenanceTask> list = maintDAO.findAll();
                for (MaintenanceTask m : list)
                    if (m.getId() == id) {
                        openDialog(m);
                        break;
                    }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        // load data in background to avoid blocking EDT
        new SwingWorker<java.util.List<MaintenanceTask>, Void>() {
            private Exception error;

            @Override
            protected java.util.List<MaintenanceTask> doInBackground() {
                try {
                    return maintDAO.findAll();
                } catch (SQLException ex) {
                    this.error = ex;
                    return java.util.Collections.emptyList();
                }
            }

            @Override
            protected void done() {
                if (error != null) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(MaintenancePanel.this, "Lỗi load: " + error.getMessage());
                    return;
                }
                try {
                    java.util.List<MaintenanceTask> list = get();
                    model.setRowCount(0);
                    equipmentCache.clear();
                    for (MaintenanceTask m : list) {
                        Equipment eq = equipmentDAO.findById(m.getEquipmentId());
                        if (eq != null)
                            equipmentCache.put(eq.getId(), eq);
                        model.addRow(new Object[] { m.getId(), eq == null ? m.getEquipmentId() : eq.getName(),
                                m.getScheduleDate() == null ? "" : m.getScheduleDate().toString(), m.isCompleted(),
                                m.getNote() });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void openDialog(MaintenanceTask m) {
        MaintenanceDialog d = new MaintenanceDialog(SwingUtilities.getWindowAncestor(this), m);
        d.setVisible(true);
        if (d.isSaved())
            loadData();
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        int c = JOptionPane.showConfirmDialog(this, "Xóa công việc #" + id + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                maintDAO.delete(id);
                loadData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
            }
        }
    }
}