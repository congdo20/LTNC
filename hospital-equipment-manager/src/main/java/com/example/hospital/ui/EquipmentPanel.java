package com.example.hospital.ui;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.util.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.example.hospital.models.User;
import java.sql.SQLException;
import java.util.List;

public class EquipmentPanel extends JPanel {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private JTable table;
    private DefaultTableModel model;
    private JLabel imagePreview;
    private User currentUser;

    public EquipmentPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] { "ID", "Mã", "Tên", "Nhà sản xuất", "Năm", "Trạng Thái", "Khoa/Phòng" }, 0) {
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
                    ImageIcon scaled = ImageUtil.loadScaledIcon(eq.getImagePath(), 260, 260);
                    if (scaled != null) {
                        imagePreview.setIcon(scaled);
                        imagePreview.setText("");
                    } else {
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
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        JPanel panel = new JPanel();
        panel.add(btnAdd);
        panel.add(btnEdit);
        
        // Only show delete button for QL_THIET_BI role
        if (currentUser != null && currentUser.isQLThietBi()) {
            panel.add(btnDelete);
        }
        
        panel.add(btnRefresh);
        add(panel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addEquipment());
        btnEdit.addActionListener(e -> editEquipment());
        btnDelete.addActionListener(e -> deleteEquipment());
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
        
        // Create status combo box with options
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"TOT", "BAO_TRI", "HU_HONG"});
        cbStatus.setSelectedItem("TOT");
        
        JTextField tfImage = new JTextField();
        JComboBox<String> cbDept = new JComboBox<>();

        // populate department combo with names from DepartmentDAO
        try {
            com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
            java.util.Map<Integer, String> map = depDao.findAll();
            // keep insertion order by name is already sorted in DAO
            for (java.util.Map.Entry<Integer, String> ent : map.entrySet()) {
                cbDept.addItem(ent.getValue());
            }
        } catch (Exception ex) {
            // fallback: leave combo empty
        }

        Object[] inputs = {
                "Mã:", tfCode,
                "Tên thiết bị:", tfName,
                "Nhà sản xuất:", tfManu,
                "Năm:", tfYear,
                "Trạng thái:", cbStatus,
                "Đường dẫn ảnh (local path or URL):", tfImage,
                "Khoa/Phòng:", cbDept
        };

        if (JOptionPane.showConfirmDialog(this, inputs, "Thêm thiết bị",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            Equipment e = new Equipment();
            e.setCode(tfCode.getText());
            e.setName(tfName.getText());
            e.setManufacturer(tfManu.getText());
            e.setImagePath(tfImage.getText().isEmpty() ? null : tfImage.getText());
            e.setYearOfUse(tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText()));
            e.setStatus((String) cbStatus.getSelectedItem());
            // map selected department name back to id
            String selDept = (String) cbDept.getSelectedItem();
            Integer deptId = null;
            if (selDept != null && !selDept.isEmpty()) {
                try {
                    com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
                    java.util.Map<Integer, String> map = depDao.findAll();
                    for (java.util.Map.Entry<Integer, String> en : map.entrySet()) {
                        if (selDept.equals(en.getValue())) {
                            deptId = en.getKey();
                            break;
                        }
                    }
                } catch (Exception ex) {
                    // ignore and leave deptId null
                }
            }
            e.setDepartmentId(deptId);

            try {
                equipmentDAO.create(e);
                loadData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi thêm thiết bị");
            }
        }
    }

    private void deleteEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị cần xóa");
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String equipmentName = (String) model.getValueAt(row, 2); // Get equipment name for confirmation
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn xóa thiết bị " + equipmentName + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                equipmentDAO.delete(id);
                JOptionPane.showMessageDialog(this, "Đã xóa thiết bị thành công");
                loadData(); // Refresh the table
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this, 
                    "Lỗi khi xóa thiết bị: " + ex.getMessage(),
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void editEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị cần sửa");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try {
            Equipment e = equipmentDAO.findById(id);

            JTextField tfCode = new JTextField(e.getCode());
            JTextField tfName = new JTextField(e.getName());
            JTextField tfManu = new JTextField(e.getManufacturer());
            JTextField tfYear = new JTextField(e.getYearOfUse() + "");
            
            // Create status combo box with options and select current status
            JComboBox<String> cbStatus = new JComboBox<>(new String[]{"TOT", "BAO_TRI", "HU_HONG"});
            cbStatus.setSelectedItem(e.getStatus());
            
            JTextField tfImage = new JTextField(e.getImagePath() == null ? "" : e.getImagePath());
            JComboBox<String> cbDept = new JComboBox<>();

            // populate departments and preselect current
            try {
                com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
                java.util.Map<Integer, String> map = depDao.findAll();
                String selName = null;
                for (java.util.Map.Entry<Integer, String> ent : map.entrySet()) {
                    cbDept.addItem(ent.getValue());
                    if (e.getDepartmentId() != null && e.getDepartmentId().equals(ent.getKey())) {
                        selName = ent.getValue();
                    }
                }
                if (selName != null)
                    cbDept.setSelectedItem(selName);
            } catch (Exception ex) {
                // ignore
            }

            Object[] inputs = {
                    "Mã:", tfCode,
                    "Tên thiết bị:", tfName,
                    "Nhà sản xuất:", tfManu,
                    "Năm:", tfYear,
                    "Trạng thái:", cbStatus,
                    "Đường dẫn ảnh (local path or URL):", tfImage,
                    "Khoa/Phòng:", cbDept
            };

            if (JOptionPane.showConfirmDialog(this, inputs, "Sửa thiết bị",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                e.setCode(tfCode.getText());
                e.setName(tfName.getText());
                e.setManufacturer(tfManu.getText());
                e.setImagePath(tfImage.getText().isEmpty() ? null : tfImage.getText());
                e.setYearOfUse(tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText()));
                e.setStatus((String) cbStatus.getSelectedItem());

                // map selected department name back to id
                String selDept = (String) cbDept.getSelectedItem();
                Integer deptId = null;
                if (selDept != null && !selDept.isEmpty()) {
                    try {
                        com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
                        java.util.Map<Integer, String> map = depDao.findAll();
                        for (java.util.Map.Entry<Integer, String> en : map.entrySet()) {
                            if (selDept.equals(en.getValue())) {
                                deptId = en.getKey();
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        // ignore
                    }
                }
                e.setDepartmentId(deptId);

                equipmentDAO.update(e);
                loadData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa thiết bị");
        }
    }
}
