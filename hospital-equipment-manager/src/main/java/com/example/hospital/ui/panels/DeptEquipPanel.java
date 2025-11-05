// package com.example.hospital.ui.panels;

// import javax.swing.*;
// import java.awt.*;

// public class DeptEquipPanel extends JPanel {

//     public DeptEquipPanel() {
//         setLayout(new BorderLayout());

//         JLabel title = new JLabel("Danh sách thiết bị khoa/viện", SwingConstants.CENTER);
//         add(title, BorderLayout.NORTH);

//         JTable table = new JTable(
//                 new Object[][] {},
//                 new String[] { "Mã", "Tên thiết bị", "Tình trạng", "Ngày mua" });

//         add(new JScrollPane(table), BorderLayout.CENTER);
//     }
// }

package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.User;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DeptEquipPanel extends JPanel {

    private JTable table;
    private EquipmentDAO dao = new EquipmentDAO();

    public DeptEquipPanel(User user) {
        setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "ID", "Mã", "Tên", "Hãng", "Năm", "Tình trạng", "Khoa" }, 0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData(user);
    }

    private void loadData(User user) {
        try {
            List<Equipment> list = dao.findByDepartment(user.getDepartmentId());

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Equipment e : list) {
                model.addRow(new Object[] {
                        e.getId(), e.getCode(), e.getName(), e.getManufacturer(),
                        e.getYearOfUse(), e.getStatus(), e.getDepartmentId()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
}
