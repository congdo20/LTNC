package com.example.hospital.ui;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.models.Equipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EquipmentPanel extends JPanel {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private JTable table;
    private DefaultTableModel model;
    private JLabel imagePreview;

    public EquipmentPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] { "ID", "Mã", "Tên", "Nhà sản xuất", "Năm", "Trạng Thái", "ID Khoa/Phòng" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);

        // Right side: image preview
        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(260, 260));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imagePreview);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);

        // update image preview when selection changes
        table.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting())
                return;
            int r = table.getSelectedRow();
            if (r < 0) {
                imagePreview.setIcon(null);
                imagePreview.setText("");
                return;
            }
            try {
                int id = (int) model.getValueAt(r, 0);
                Equipment eq = equipmentDAO.findById(id);
                if (eq != null && eq.getImagePath() != null && !eq.getImagePath().isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(eq.getImagePath());
                        Image img = icon.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH);
                        imagePreview.setIcon(new ImageIcon(img));
                        imagePreview.setText("");
                    } catch (Exception ex) {
                        imagePreview.setIcon(null);
                        imagePreview.setText("Không tải được ảnh");
                    }
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Không có ảnh");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                imagePreview.setIcon(null);
                imagePreview.setText("Lỗi");
            }
        });

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnRefresh = new JButton("Làm mới");

        JPanel panel = new JPanel();
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnRefresh);
        add(panel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addEquipment());
        btnEdit.addActionListener(e -> editEquipment());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        try {
            List<Equipment> list = equipmentDAO.findAll();
            model.setRowCount(0);
            com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
            for (Equipment e : list) {
                String deptName = null;
                try {
                    deptName = depDao.findNameById(e.getDepartmentId());
                } catch (SQLException ex) {
                    // ignore and show id fallback
                }
                model.addRow(new Object[] {
                        e.getId(),
                        e.getCode(),
                        e.getName(),
                        e.getManufacturer(),
                        e.getYearOfUse(),
                        e.getStatus(),
                        deptName == null ? (e.getDepartmentId() == null ? "" : e.getDepartmentId()) : deptName
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu");
        }
    }

    private void addEquipment() {
        JTextField tfCode = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfManu = new JTextField();
        JTextField tfYear = new JTextField();
        JTextField tfStatus = new JTextField("TOT");
        JTextField tfDept = new JTextField();
        JTextField tfImage = new JTextField();

        Object[] inputs = {
                "Mã:", tfCode,
                "Tên thiết bị:", tfName,
                "Nhà sản xuất:", tfManu,
                "Năm:", tfYear,
                "Trang Thai (TOT/BAO_TRI/HU_HONG):", tfStatus,
                "Đường dẫn ảnh (local path or URL):", tfImage,
                "Khoa/Phòng:", tfDept
        };

        if (JOptionPane.showConfirmDialog(this, inputs, "Thêm thiết bị",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            Equipment e = new Equipment();
            e.setCode(tfCode.getText());
            e.setName(tfName.getText());
            e.setManufacturer(tfManu.getText());
            e.setImagePath(tfImage.getText().isEmpty() ? null : tfImage.getText());
            e.setYearOfUse(tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText()));
            e.setStatus(tfStatus.getText());
            e.setDepartmentId(tfDept.getText().isEmpty() ? null : Integer.parseInt(tfDept.getText()));

            try {
                equipmentDAO.create(e);
                loadData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi thêm thiết bị");
            }
        }
    }

    private void editEquipment() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;

        int id = (int) model.getValueAt(row, 0);

        try {
            Equipment e = equipmentDAO.findById(id);

            JTextField tfCode = new JTextField(e.getCode());
            JTextField tfName = new JTextField(e.getName());
            JTextField tfManu = new JTextField(e.getManufacturer());
            JTextField tfYear = new JTextField(e.getYearOfUse() + "");
            JTextField tfStatus = new JTextField(e.getStatus());
            JTextField tfImage = new JTextField(e.getImagePath() == null ? "" : e.getImagePath());
            JTextField tfDept = new JTextField(e.getDepartmentId() + "");

            Object[] inputs = {
                    "Mã:", tfCode,
                    "Tên thiết bị:", tfName,
                    "Nhà sản xuất:", tfManu,
                    "Năm:", tfYear,
                    "Trang Thai (TOT/BAO_TRI/HU_HONG):", tfStatus,
                    "Đường dẫn ảnh (local path or URL):", tfImage,
                    "Khoa/Phòng:", tfDept
            };

            if (JOptionPane.showConfirmDialog(this, inputs, "Sửa thiết bị",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                e.setCode(tfCode.getText());
                e.setName(tfName.getText());
                e.setManufacturer(tfManu.getText());
                e.setImagePath(tfImage.getText().isEmpty() ? null : tfImage.getText());
                e.setYearOfUse(tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText()));
                e.setStatus(tfStatus.getText());
                e.setDepartmentId(tfDept.getText().isEmpty() ? null : Integer.parseInt(tfDept.getText()));

                equipmentDAO.update(e);
                loadData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa thiết bị");
        }
    }
}
