// package com.example.hospital.ui.panels;

// import javax.swing.*;
// import java.awt.*;

// public class ReportPanel extends JPanel {

//     private JTable tblReports;
//     private JTextArea txtContent;
//     private JComboBox<String> cbTask;
//     private JButton btnSubmit;

//     public ReportPanel() {
//         setLayout(new BorderLayout());

//         // ====== TITLE ======
//         JLabel title = new JLabel("Báo cáo kết quả bảo trì", SwingConstants.CENTER);
//         title.setFont(new Font("Arial", Font.BOLD, 20));
//         add(title, BorderLayout.NORTH);

//         // ====== TABLE ======
//         String[] columns = { "Mã báo cáo", "Thiết bị", "Nhiệm vụ", "Ngày", "Kết quả" };
//         Object[][] data = {};

//         tblReports = new JTable(data, columns);
//         JScrollPane scroll = new JScrollPane(tblReports);
//         add(scroll, BorderLayout.CENTER);

//         // ====== FORM INPUT ======
//         JPanel formPanel = new JPanel();
//         formPanel.setLayout(new BorderLayout());
//         formPanel.setBorder(BorderFactory.createTitledBorder("Tạo báo cáo"));

//         // -- TOP: Lựa chọn nhiệm vụ --
//         JPanel inputTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

//         inputTop.add(new JLabel("Nhiệm vụ:"));
//         cbTask = new JComboBox<>();
//         cbTask.setPreferredSize(new Dimension(250, 25));
//         cbTask.addItem("NV01 - Máy X quang");
//         cbTask.addItem("NV02 - Máy siêu âm");
//         inputTop.add(cbTask);

//         formPanel.add(inputTop, BorderLayout.NORTH);

//         // -- MID: Nội dung báo cáo --
//         txtContent = new JTextArea(5, 50);
//         txtContent.setBorder(BorderFactory.createTitledBorder("Nội dung báo cáo"));
//         JScrollPane spContent = new JScrollPane(txtContent);
//         formPanel.add(spContent, BorderLayout.CENTER);

//         // -- BOTTOM: nút --
//         JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//         btnSubmit = new JButton("Lưu báo cáo");
//         bottom.add(btnSubmit);

//         formPanel.add(bottom, BorderLayout.SOUTH);

//         add(formPanel, BorderLayout.SOUTH);

//         // ====== Event (demo) ======
//         btnSubmit.addActionListener(e -> submitReport());
//     }

//     /**
//      * Demo xử lý lưu báo cáo
//      */
//     private void submitReport() {
//         String task = cbTask.getSelectedItem().toString();
//         String content = txtContent.getText().trim();

//         if (content.isEmpty()) {
//             JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung báo cáo!");
//             return;
//         }

//         JOptionPane.showMessageDialog(
//                 this,
//                 "Đã lưu báo cáo cho nhiệm vụ:\n" + task,
//                 "Thành công",
//                 JOptionPane.INFORMATION_MESSAGE);

//         txtContent.setText("");
//     }
// }



package com.example.hospital.ui.panels;

// import com.example.hospital.dao.ReportDAO;
// import com.example.hospital.dao.MaintenanceDAO;
// import com.example.hospital.models.MaintenanceTask;
// import com.example.hospital.models.Report;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.sql.SQLException;
// import java.util.List;

// public class ReportPanel extends JPanel {

//     private ReportDAO reportDAO = new ReportDAO();
//     private MaintenanceDAO taskDAO = new MaintenanceDAO();
//     private JTable table;
//     private DefaultTableModel model;
//     private com.example.hospital.models.User currentUser;

//     public ReportPanel(com.example.hospital.models.User user) {
//         this.currentUser = user;
//         initUI();
//         loadData();
//     }

//     private void initUI() {
//         setLayout(new BorderLayout());

//         model = new DefaultTableModel(
//                 new Object[] { "ID", "Task ID", "Reporter", "Content", "Date" },
//                 0) {
//             @Override
//             public boolean isCellEditable(int r, int c) {
//                 return false;
//             }
//         };

//         table = new JTable(model);
//         add(new JScrollPane(table), BorderLayout.CENTER);

//         JPanel btnPanel = new JPanel();
//         JButton btnAdd = new JButton("Tạo báo cáo");
//         JButton btnRefresh = new JButton("Làm mới");
//         JButton btnDelete = new JButton("Xóa");

//         btnPanel.add(btnAdd);
//         btnPanel.add(btnRefresh);
//         btnPanel.add(btnDelete);

//         add(btnPanel, BorderLayout.SOUTH);

//         // event
//         btnAdd.addActionListener(e -> addReport());
//         btnRefresh.addActionListener(e -> loadData());
//         btnDelete.addActionListener(e -> deleteReport());
//     }

//     private void loadData() {
//         try {
//             model.setRowCount(0);
//             List<Report> list = reportDAO.findAll();
//             for (Report r : list) {
//                 model.addRow(new Object[] {
//                         r.getId(),
//                         r.getTaskId(),
//                         r.getReporter(),
//                         r.getContent(),
//                         r.getCreatedDate().toString()
//                 });
//             }
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
//         }
//     }

//     private void addReport() {
//         List<MaintenanceTask> tasks;
//         try {
//             tasks = taskDAO.findAll();
//         } catch (SQLException e) {
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Không tải được danh sách task");
//             return;
//         }

//         JComboBox<String> cbTask = new JComboBox<>();
//         for (MaintenanceTask t : tasks) {
//             cbTask.addItem(t.getId() + " - " + t.getNote());
//         }

//         JTextArea taContent = new JTextArea(5, 20);

//         JPanel p = new JPanel(new BorderLayout());
//         p.add(cbTask, BorderLayout.NORTH);
//         p.add(new JScrollPane(taContent), BorderLayout.CENTER);

//         int c = JOptionPane.showConfirmDialog(
//                 this, p, "Tạo báo cáo", JOptionPane.OK_CANCEL_OPTION);
//         if (c != JOptionPane.OK_OPTION)
//             return;

//         try {
//             int taskId = Integer.parseInt(cbTask.getSelectedItem().toString().split(" - ")[0]);
//             String content = taContent.getText().trim();
//             if (content.isEmpty()) {
//                 JOptionPane.showMessageDialog(this, "Nội dung không được trống");
//                 return;
//             }
//             String username = currentUser == null ? "unknown" : currentUser.getUsername();
//             reportDAO.create(taskId, username, content);
//             loadData();
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi tạo báo cáo: " + ex.getMessage());
//         }
//     }

//     private void deleteReport() {
//         int r = table.getSelectedRow();
//         if (r < 0) {
//             JOptionPane.showMessageDialog(this, "Chọn báo cáo để xóa");
//             return;
//         }
//         int id = (int) model.getValueAt(r, 0);

//         int c = JOptionPane.showConfirmDialog(
//                 this, "Xóa báo cáo #" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//         if (c != JOptionPane.YES_OPTION)
//             return;

//         try {
//             reportDAO.delete(id);
//             loadData();
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
//         }
//     }
// }


import com.example.hospital.dao.ReportDAO;
import com.example.hospital.dao.MaintenanceDAO;
import com.example.hospital.models.MaintenanceTask;
import com.example.hospital.models.Report;
import com.example.hospital.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ReportPanel extends JPanel {

    private ReportDAO reportDAO = new ReportDAO();
    private MaintenanceDAO taskDAO = new MaintenanceDAO();
    private JTable table;
    private DefaultTableModel model;
    private User currentUser;

    public ReportPanel(User user) {
        this.currentUser = user;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] { "ID", "Task ID", "Reporter", "Content", "Date" },
                0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnAdd = new JButton("Tạo báo cáo");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addReport());
        btnDelete.addActionListener(e -> deleteReport());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Report> list = reportDAO.findAll();
            for (Report r : list) {
                model.addRow(new Object[] {
                        r.getId(),
                        r.getTaskId(),
                        r.getReporter(),
                        r.getContent(),
                        r.getCreatedDate()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu: " + ex.getMessage());
        }
    }

    private void addReport() {
        List<MaintenanceTask> tasks;

        try {
            tasks = taskDAO.findAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không tải được danh sách công việc!");
            return;
        }

        JComboBox<String> cbTaskId = new JComboBox<>();
        for (MaintenanceTask t : tasks) {
            cbTaskId.addItem(t.getId() + " - " + t.getNote());
        }

        JTextArea taContent = new JTextArea(5, 20);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(cbTaskId, BorderLayout.NORTH);
        panel.add(new JScrollPane(taContent), BorderLayout.CENTER);

        int opt = JOptionPane.showConfirmDialog(this, panel, "Tạo báo cáo", JOptionPane.OK_CANCEL_OPTION);

        if (opt != JOptionPane.OK_OPTION)
            return;

        try {
            int taskId = Integer.parseInt(cbTaskId.getSelectedItem().toString().split(" - ")[0]);
            String content = taContent.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nội dung không được để trống!");
                return;
            }

            String reporter = currentUser != null ? currentUser.getUsername() : "Unknown";

            reportDAO.create(taskId, reporter, content);
            loadData();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm báo cáo: " + ex.getMessage());
        }
    }

    private void deleteReport() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 báo cáo để xóa!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int c = JOptionPane.showConfirmDialog(this, "Xóa báo cáo #" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (c != JOptionPane.YES_OPTION)
            return;

        try {
            reportDAO.delete(id);
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
        }
    }
}
