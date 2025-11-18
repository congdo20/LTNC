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
import com.example.hospital.util.ImageUtil;

public class RequestDialog extends JDialog {

    private JComboBox<String> cbEquipment;
    private JTextArea txtIssue;
    private JLabel imagePreview;

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

        // image preview area
        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(180, 180));

        JPanel left = new JPanel(new GridLayout(3, 2, 6, 6));
        left.add(new JLabel("Thiết bị:"));
        left.add(cbEquipment);
        left.add(new JLabel("Mô tả sự cố:"));
        left.add(new JScrollPane(txtIssue));

        JPanel p = new JPanel(new BorderLayout());
        p.add(left, BorderLayout.CENTER);
        p.add(imagePreview, BorderLayout.EAST);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // show preview when equipment selection changes
        cbEquipment.addActionListener(ev -> updatePreviewForSelected());

        // show preview for initial selection (if any)
        if (cbEquipment.getItemCount() > 0) {
            cbEquipment.setSelectedIndex(0);
            updatePreviewForSelected();
        }
    }

    private void updatePreviewForSelected() {
        String sel = (String) cbEquipment.getSelectedItem();
        if (sel == null || sel.isEmpty()) {
            imagePreview.setIcon(null);
            imagePreview.setText("");
            return;
        }
        try {
            int id = Integer.parseInt(sel.split(" - ")[0]);
            Equipment eq = equipmentDAO.findById(id);
            if (eq != null && eq.getImagePath() != null && !eq.getImagePath().isEmpty()) {
                ImageIcon scaled = ImageUtil.loadScaledIcon(eq.getImagePath(), 180, 180);
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
            imagePreview.setIcon(null);
            imagePreview.setText("");
        }
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
            // r.setPriority((String) cbPriority.getSelectedItem());

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
