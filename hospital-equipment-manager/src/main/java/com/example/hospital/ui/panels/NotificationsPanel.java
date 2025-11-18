package com.example.hospital.ui.panels;

import com.example.hospital.dao.NotificationDAO;
import com.example.hospital.models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NotificationsPanel extends JPanel {
    private final NotificationDAO dao = new NotificationDAO();
    private final int userId;
    private JTable table;
    private DefaultTableModel model;

    public NotificationsPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[] { "ID", "Thời gian", "Nội dung", "Đã đọc" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnMarkRead = new JButton("Đánh dấu đã đọc");
        JButton btnRefresh = new JButton("Làm mới");
        bottom.add(btnMarkRead);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());
        btnMarkRead.addActionListener(e -> markSelectedRead());

        loadData();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            for (Notification n : dao.findForUser(userId)) {
                model.addRow(new Object[] { n.getId(), n.getCreatedAt(), n.getMessage(), n.isRead() ? "✓" : "" });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải thông báo: " + ex.getMessage());
        }
    }

    private void markSelectedRead() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo để đánh dấu đã đọc");
            return;
        }
        int id = (Integer) model.getValueAt(r, 0);
        try {
            dao.markAsRead(id);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
