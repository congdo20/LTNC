package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.User;
import com.example.hospital.util.ImageUtil;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DeptEquipPanel extends JPanel {

    private JTable table;
    private EquipmentDAO dao = new EquipmentDAO();
    private JLabel imagePreview;

    public DeptEquipPanel(User user) {
        setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "ID", "Mã", "Tên", "Hãng", "Năm", "Tình trạng", "Khoa" }, 0);

        table = new JTable(model);

        // image preview on the right (read-only view for department head)
        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(260, 260));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imagePreview);
        split.setResizeWeight(0.75);
        add(split, BorderLayout.CENTER);

        // selection listener to update preview
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
                Equipment eq = dao.findById(id);
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

        loadData(user);
    }

    private void loadData(User user) {
        try {
            List<Equipment> list = dao.findByDepartment(user.getDepartmentId());

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Equipment e : list) {
                model.addRow(new Object[] {
                        e.getId(), e.getCode(), e.getName(), e.getManufacturer(),
                        e.getYearOfUse(), e.getStatus(), e.getDepartmentId()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
}
