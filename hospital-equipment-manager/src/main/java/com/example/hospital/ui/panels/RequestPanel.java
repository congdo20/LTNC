package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;
import com.example.hospital.ui.RequestDialog;
import com.example.hospital.ui.MaintenanceRequestFormSwing;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RequestPanel extends JPanel {

    private JTable table;
    private MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
    private User currentUser;

    public RequestPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());

        JButton btnAdd = new JButton("Tạo yêu cầu");
        btnAdd.addActionListener(e -> openCreateDialog());

        add(btnAdd, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(
                new String[] { "ID", "Thiết bị", "Mô tả", "Ngày", "Trạng thái" }, 0));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void openCreateDialog() {
        // If the current user is a department head, open the full maintenance form
        if (currentUser != null && currentUser.isTruongKhoa()) {
            MaintenanceRequestFormSwing form = new MaintenanceRequestFormSwing(currentUser);
            // Ensure disposing the form doesn't exit the application
            form.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // When the form is closed, refresh the table to show any new requests
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadData();
                }
            });
            form.setVisible(true);
            return;
        }

        // Default behavior for other users: open modal RequestDialog
        RequestDialog dlg = new RequestDialog(SwingUtilities.getWindowAncestor(this), currentUser);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void loadData() {
        try {
            List<MaintenanceRequest> list;

            if (currentUser.isQLThietBi()) {
                list = dao.findAll(); // ✅ xem tất cả
            } else {
                list = dao.findByDepartment(currentUser.getDepartmentId());
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (MaintenanceRequest r : list) {
                model.addRow(new Object[] {
                        r.getId(),
                        r.getEquipmentId(),
                        r.getIssueDescription(),
                        r.getRequestDate(),
                        r.getStatus()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

}
