package com.example.hospital.ui.panels;

import javax.swing.*;

import com.example.hospital.models.User;

import java.awt.*;

public class TaskPanel extends JPanel {

    public TaskPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Danh sách nhiệm vụ bảo trì", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Mã nhiệm vụ", "Thiết bị", "Hạn", "Trạng thái" });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnComplete = new JButton("Hoàn thành");
        bottom.add(btnComplete);

        add(bottom, BorderLayout.SOUTH);
    }

    public TaskPanel(User user) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Danh sách nhiệm vụ bảo trì", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Mã nhiệm vụ", "Thiết bị", "Hạn", "Trạng thái" });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnComplete = new JButton("Hoàn thành");
        bottom.add(btnComplete);

        add(bottom, BorderLayout.SOUTH);
    }
}
