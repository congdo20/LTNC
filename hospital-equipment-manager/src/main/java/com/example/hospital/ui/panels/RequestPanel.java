package com.example.hospital.ui.panels;

import javax.swing.*;
import java.awt.*;

public class RequestPanel extends JPanel {

    public RequestPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Danh sách yêu cầu bảo trì", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTable table = new JTable(
                new Object[][] {},
                new String[] { "Mã yêu cầu", "Thiết bị", "Người tạo", "Ngày tạo", "Trạng thái" });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Tạo yêu cầu");
        bottom.add(btnAdd);

        add(bottom, BorderLayout.SOUTH);
    }
}
