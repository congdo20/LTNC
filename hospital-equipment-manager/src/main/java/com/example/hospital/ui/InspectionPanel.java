package com.example.hospital.ui;

import com.example.hospital.dao.InspectionDAO;
import com.example.hospital.model.InspectionTask;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class InspectionPanel extends JPanel {
    private final InspectionDAO dao = new InspectionDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Bảo trì ID", "Thiết bị ID", "Ngày kiểm tra", "Người kiểm tra", "Kết quả", "Ghi chú", "Người nghiệm thu", "Ngày nghiệm thu"}, 0);
    private final JTable table = new JTable(model);

    public InspectionPanel() {
        setLayout(new BorderLayout());
        JButton btnAdd = new JButton("Thêm kiểm tra & nghiệm thu");
        btnAdd.addActionListener(e -> openDialog());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnAdd, BorderLayout.SOUTH);
        refreshTable();
    }

    private void openDialog() {
        JTextField txtMaintenance = new JTextField();
        JTextField txtInspector = new JTextField();
        JCheckBox chkResult = new JCheckBox("Đạt");
        JTextArea txtNote = new JTextArea(3, 20);
        JTextField txtAcceptedBy = new JTextField();
        JTextField txtAcceptanceDate = new JTextField(LocalDate.now().toString());

        Object[] form = {
                "ID bảo trì:", txtMaintenance,
                "Người kiểm tra:", txtInspector,
                "Kết quả:", chkResult,
                "Ghi chú:", new JScrollPane(txtNote),
                "Người nghiệm thu:", txtAcceptedBy,
                "Ngày nghiệm thu (YYYY-MM-DD):", txtAcceptanceDate
        };

        int option = JOptionPane.showConfirmDialog(this, form, "Thêm kiểm tra", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                InspectionTask t = new InspectionTask();
                t.setMaintenanceId(Integer.parseInt(txtMaintenance.getText()));
                t.setInspectionDate(LocalDate.now());
                t.setInspector(txtInspector.getText());
                t.setResult(chkResult.isSelected());
                t.setNote(txtNote.getText());
                t.setAcceptedBy(txtAcceptedBy.getText());
                t.setAcceptanceDate(LocalDate.parse(txtAcceptanceDate.getText()));
                dao.create(t);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        try {
            List<InspectionTask> list = dao.findAll();
            for (InspectionTask t : list) {
                model.addRow(new Object[]{
                        t.getId(),
                        t.getMaintenanceId(),
                        t.getEquipmentId(),
                        t.getInspectionDate(),
                        t.getInspector(),
                        t.isResult() ? "Đạt" : "Không đạt",
                        t.getNote(),
                        t.getAcceptedBy(),
                        t.getAcceptanceDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
