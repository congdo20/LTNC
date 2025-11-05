// package com.example.hospital.ui.panels;

// import javax.swing.*;
// import java.awt.*;

// public class RequestPanel extends JPanel {

//     public RequestPanel() {
//         setLayout(new BorderLayout());

//         JLabel title = new JLabel("Danh sách yêu cầu bảo trì", SwingConstants.CENTER);
//         add(title, BorderLayout.NORTH);

//         JTable table = new JTable(
//                 new Object[][] {},
//                 new String[] { "Mã yêu cầu", "Thiết bị", "Người tạo", "Ngày tạo", "Trạng thái" });
//         add(new JScrollPane(table), BorderLayout.CENTER);

//         JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         JButton btnAdd = new JButton("Tạo yêu cầu");
//         bottom.add(btnAdd);

//         add(bottom, BorderLayout.SOUTH);
//     }
// }

package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;
import com.example.hospital.ui.RequestDialog;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RequestPanel extends JPanel {

    private JTable table;
    private MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
    private User currentUser;

    // public RequestPanel() {
    //     // Initialize panel without user
    //     setLayout(new BorderLayout());

    //     JButton btnAdd = new JButton("Tạo yêu cầu");
    //     btnAdd.addActionListener(e -> openCreateDialog());

    //     add(btnAdd, BorderLayout.NORTH);

    //     table = new JTable(new DefaultTableModel(
    //             new String[] { "ID", "Thiết bị", "Mô tả", "Ngày", "Độ ưu tiên", "Trạng thái" }, 0));

    //     add(new JScrollPane(table), BorderLayout.CENTER);

    //     loadData();
    // }

    public RequestPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());

        JButton btnAdd = new JButton("Tạo yêu cầu");
        btnAdd.addActionListener(e -> openCreateDialog());

        add(btnAdd, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(
                new String[] { "ID", "Thiết bị", "Mô tả", "Ngày", "Độ ưu tiên", "Trạng thái" }, 0));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void openCreateDialog() {
        RequestDialog dlg = new RequestDialog(SwingUtilities.getWindowAncestor(this), currentUser);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            loadData();
        }
    }

    // private void loadData() {
    // try {
    // List<MaintenanceRequest> list =
    // dao.findByDepartment(currentUser.getDepartmentId());

    // DefaultTableModel model = (DefaultTableModel) table.getModel();
    // model.setRowCount(0);

    // for (MaintenanceRequest r : list) {
    // model.addRow(new Object[] {
    // r.getId(),
    // r.getEquipmentId(),
    // r.getIssueDescription(),
    // r.getRequestDate(),
    // r.getPriority(),
    // r.getStatus()
    // });
    // }

    // } catch (SQLException e) {
    // e.printStackTrace();
    // JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
    // }
    // }

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
                        r.getPriority(),
                        r.getStatus()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

}
