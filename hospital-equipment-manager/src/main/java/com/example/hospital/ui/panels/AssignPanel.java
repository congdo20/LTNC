package com.example.hospital.ui.panels;

import javax.swing.*;
import java.awt.*;

public class AssignPanel extends JPanel {

    public AssignPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Phân công nhiệm vụ", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Mã nhiệm vụ", "Thiết bị", "Nhân viên", "Thời hạn", "Trạng thái" });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAssign = new JButton("Tạo phân công");
        bottom.add(btnAssign);

        add(bottom, BorderLayout.SOUTH);
    }
}
