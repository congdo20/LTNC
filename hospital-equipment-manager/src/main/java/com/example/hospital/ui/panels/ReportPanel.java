package com.example.hospital.ui.panels;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    private JTable tblReports;
    private JTextArea txtContent;
    private JComboBox<String> cbTask;
    private JButton btnSubmit;

    public ReportPanel() {
        setLayout(new BorderLayout());

        // ====== TITLE ======
        JLabel title = new JLabel("Báo cáo kết quả bảo trì", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // ====== TABLE ======
        String[] columns = { "Mã báo cáo", "Thiết bị", "Nhiệm vụ", "Ngày", "Kết quả" };
        Object[][] data = {};

        tblReports = new JTable(data, columns);
        JScrollPane scroll = new JScrollPane(tblReports);
        add(scroll, BorderLayout.CENTER);

        // ====== FORM INPUT ======
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tạo báo cáo"));

        // -- TOP: Lựa chọn nhiệm vụ --
        JPanel inputTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        inputTop.add(new JLabel("Nhiệm vụ:"));
        cbTask = new JComboBox<>();
        cbTask.setPreferredSize(new Dimension(250, 25));
        cbTask.addItem("NV01 - Máy X quang");
        cbTask.addItem("NV02 - Máy siêu âm");
        inputTop.add(cbTask);

        formPanel.add(inputTop, BorderLayout.NORTH);

        // -- MID: Nội dung báo cáo --
        txtContent = new JTextArea(5, 50);
        txtContent.setBorder(BorderFactory.createTitledBorder("Nội dung báo cáo"));
        JScrollPane spContent = new JScrollPane(txtContent);
        formPanel.add(spContent, BorderLayout.CENTER);

        // -- BOTTOM: nút --
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSubmit = new JButton("Lưu báo cáo");
        bottom.add(btnSubmit);

        formPanel.add(bottom, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.SOUTH);

        // ====== Event (demo) ======
        btnSubmit.addActionListener(e -> submitReport());
    }

    /**
     * Demo xử lý lưu báo cáo
     */
    private void submitReport() {
        String task = cbTask.getSelectedItem().toString();
        String content = txtContent.getText().trim();

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung báo cáo!");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Đã lưu báo cáo cho nhiệm vụ:\n" + task,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

        txtContent.setText("");
    }
}
