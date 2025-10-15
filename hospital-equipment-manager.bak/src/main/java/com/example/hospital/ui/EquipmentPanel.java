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
import java.util.List;

public class EquipmentPanel extends JPanel {
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private MaintenanceDAO maintDAO = new MaintenanceDAO();
    private JTable table;
    private DefaultTableModel model;

    public EquipmentPanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(
                new Object[] { "ID", "Name", "Model", "Location", "Qty", "LastMaint", "IntervalDays" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnStockIn = new JButton("Nhập");
        JButton btnStockOut = new JButton("Xuất");
        JButton btnRefresh = new JButton("Làm mới");

        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnStockIn);
        btns.add(btnStockOut);
        btns.add(btnRefresh);
        add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addEquipment());
        btnEdit.addActionListener(e -> editEquipment());
        btnStockIn.addActionListener(e -> changeStock(true));
        btnStockOut.addActionListener(e -> changeStock(false));
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        new SwingWorker<java.util.List<Equipment>, Void>() {
            private Exception error;

            @Override
            protected java.util.List<Equipment> doInBackground() {
                try {
                    return equipmentDAO.findAll();
                } catch (SQLException ex) {
                    this.error = ex;
                    return java.util.Collections.emptyList();
                }
            }

            @Override
            protected void done() {
                if (error != null) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(EquipmentPanel.this, "Lỗi load dữ liệu: " + error.getMessage());
                    return;
                }
                try {
                    java.util.List<Equipment> list = get();
                    model.setRowCount(0);
                    for (Equipment e : list) {
                        model.addRow(new Object[] { e.getId(), e.getName(), e.getModel(), e.getLocation(),
                                e.getQuantity(),
                                e.getLastMaintenance() == null ? "" : e.getLastMaintenance().toString(),
                                e.getMaintenanceIntervalDays() });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void addEquipment() {
        try {
            JTextField tfName = new JTextField();
            JTextField tfModel = new JTextField();
            JTextField tfLocation = new JTextField();
            JTextField tfQty = new JTextField("0");
            JTextField tfInterval = new JTextField("0");
            Object[] inputs = { "Tên:", tfName, "Model:", tfModel, "Vị trí:", tfLocation, "Số lượng:", tfQty,
                    "Interval days:", tfInterval };
            int res = JOptionPane.showConfirmDialog(this, inputs, "Thêm thiết bị", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                Equipment e = new Equipment();
                e.setName(tfName.getText().trim());
                e.setModel(tfModel.getText().trim());
                e.setLocation(tfLocation.getText().trim());
                e.setQuantity(Integer.parseInt(tfQty.getText().trim()));
                e.setMaintenanceIntervalDays(Integer.parseInt(tfInterval.getText().trim()));
                new SwingWorker<Void, Void>() {
                    private Exception error;

                    @Override
                    protected Void doInBackground() {
                        try {
                            equipmentDAO.create(e);
                        } catch (SQLException ex) {
                            this.error = ex;
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (error != null) {
                            error.printStackTrace();
                            JOptionPane.showMessageDialog(EquipmentPanel.this,
                                    "Lỗi thêm thiết bị: " + error.getMessage());
                            return;
                        }
                        loadData();
                    }
                }.execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm thiết bị: " + ex.getMessage());
        }
    }

    private void editEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 thiết bị để sửa.");
            return;
        }
        try {
            int id = (int) model.getValueAt(row, 0);
            Equipment e = equipmentDAO.findById(id);
            if (e == null)
                return;
            JTextField tfName = new JTextField(e.getName());
            JTextField tfModel = new JTextField(e.getModel());
            JTextField tfLocation = new JTextField(e.getLocation());
            JTextField tfQty = new JTextField(String.valueOf(e.getQuantity()));
            JTextField tfInterval = new JTextField(String.valueOf(e.getMaintenanceIntervalDays()));
            Object[] inputs = { "Tên:", tfName, "Model:", tfModel, "Vị trí:", tfLocation, "Số lượng:", tfQty,
                    "Interval days:", tfInterval };
            int res = JOptionPane.showConfirmDialog(this, inputs, "Sửa thiết bị", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                e.setName(tfName.getText().trim());
                e.setModel(tfModel.getText().trim());
                e.setLocation(tfLocation.getText().trim());
                e.setQuantity(Integer.parseInt(tfQty.getText().trim()));
                e.setMaintenanceIntervalDays(Integer.parseInt(tfInterval.getText().trim()));
                new SwingWorker<Void, Void>() {
                    private Exception error;

                    @Override
                    protected Void doInBackground() {
                        try {
                            equipmentDAO.update(e);
                        } catch (SQLException ex) {
                            this.error = ex;
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (error != null) {
                            error.printStackTrace();
                            JOptionPane.showMessageDialog(EquipmentPanel.this,
                                    "Lỗi sửa thiết bị: " + error.getMessage());
                            return;
                        }
                        loadData();
                    }
                }.execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa thiết bị: " + ex.getMessage());
        }
    }

    private void changeStock(boolean isIn) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 thiết bị trong bảng.");
            return;
        }
        try {
            int id = (int) model.getValueAt(row, 0);
            Equipment e = equipmentDAO.findById(id);
            if (e == null)
                return;
            String s = JOptionPane.showInputDialog(this, (isIn ? "Nhập" : "Xuất") + " số lượng:", "0");
            if (s == null || s.trim().isEmpty())
                return;
            int delta = Integer.parseInt(s.trim());
            if (!isIn && delta > e.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Không đủ tồn.");
                return;
            }
            e.setQuantity(isIn ? e.getQuantity() + delta : e.getQuantity() - delta);
            new SwingWorker<Void, Void>() {
                private Exception error;

                @Override
                protected Void doInBackground() {
                    try {
                        equipmentDAO.update(e);
                        if (e.getQuantity() <= 0) {
                            if (!hasPendingMaintenance(e.getId())) {
                                MaintenanceTask mt = new MaintenanceTask();
                                mt.setEquipmentId(e.getId());
                                mt.setScheduleDate(LocalDate.now());
                                mt.setCompleted(false);
                                mt.setNote("Auto-created due to stock change");
                                maintDAO.create(mt);
                            }
                        }
                    } catch (SQLException ex) {
                        this.error = ex;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (error != null) {
                        error.printStackTrace();
                        JOptionPane.showMessageDialog(EquipmentPanel.this, "Lỗi thay đổi tồn: " + error.getMessage());
                        return;
                    }
                    loadData();
                }
            }.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thay đổi tồn: " + ex.getMessage());
        }
    }

    private boolean hasPendingMaintenance(int equipmentId) {
        try {
            List<MaintenanceTask> list = maintDAO.findAll();
            for (MaintenanceTask m : list) {
                if (m.getEquipmentId() == equipmentId && !m.isCompleted())
                    return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}