package com.example.hospital.ui.panels;

import javax.swing.*;
import java.awt.*;

public class DeptEquipPanel extends JPanel {

    public DeptEquipPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Danh sách thiết bị khoa/viện", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Mã", "Tên thiết bị", "Tình trạng", "Ngày mua" });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
