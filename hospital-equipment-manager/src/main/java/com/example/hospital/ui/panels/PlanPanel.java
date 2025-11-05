package com.example.hospital.ui.panels;

import javax.swing.*;
import java.awt.*;

public class PlanPanel extends JPanel {

    public PlanPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Lên kế hoạch bảo trì", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Kế hoạch", "Thiết bị", "Ngày bắt đầu", "Ngày kết thúc", "Ghi chú" });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Tạo kế hoạch");
        bottom.add(btnAdd);

        add(bottom, BorderLayout.SOUTH);
    }
}
