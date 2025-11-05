// package com.example.hospital.ui;

// import com.example.hospital.dao.EquipmentDAO;
// import com.example.hospital.dao.MaintenanceDAO;
// import com.example.hospital.models.Equipment;
// import com.example.hospital.models.MaintenanceTask;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.sql.SQLException;
// // import java.time.LocalDate;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class MaintenancePanel extends JPanel {
//     private MaintenanceDAO maintDAO = new MaintenanceDAO();
//     private EquipmentDAO equipmentDAO = new EquipmentDAO();
//     private JTable table;
//     private DefaultTableModel model;
//     private Map<Integer, Equipment> equipmentCache = new HashMap<>();
//     private com.example.hospital.models.User currentUser;

//     public MaintenancePanel() {
//         this(null);
//     }

//     public MaintenancePanel(com.example.hospital.models.User user) {
//         this.currentUser = user;
//         setLayout(new BorderLayout());
//         model = new DefaultTableModel(new Object[] { "ID", "Equipment", "Schedule", "Done", "Note", "Assigned" }, 0) {
//             public boolean isCellEditable(int r, int c) {
//                 return false;
//             }
//         };
//         table = new JTable(model);
//         add(new JScrollPane(table), BorderLayout.CENTER);

//         JPanel btns = new JPanel();
//         JButton btnAdd = new JButton("Thêm yêu cầu");
//         JButton btnEdit = new JButton("Sửa");
//         JButton btnDelete = new JButton("Xóa");
//         JButton btnAccept = new JButton("Duyệt / Chấp nhận");
//         JButton btnInspect = new JButton("Kiểm tra");
//         JButton btnRefresh = new JButton("Làm mới");
//         btns.add(btnAdd);
//         btns.add(btnEdit);
//         btns.add(btnDelete);
//         // show admin actions only if user is admin
//         if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
//             btns.add(btnAccept);
//             btns.add(btnInspect);
//         }
//         btns.add(btnRefresh);
//         add(btns, BorderLayout.SOUTH);

//         btnAdd.addActionListener(e -> openDialog(null));
//         btnEdit.addActionListener(e -> {
//             int r = table.getSelectedRow();
//             if (r < 0) {
//                 JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
//                 return;
//             }
//             int id = (int) model.getValueAt(r, 0);
//             try {
//                 List<MaintenanceTask> list = maintDAO.findAll();
//                 for (MaintenanceTask m : list)
//                     if (m.getId() == id) {
//                         openDialog(m);
//                         break;
//                     }
//             } catch (SQLException ex) {
//                 ex.printStackTrace();
//             }
//         });
//         btnDelete.addActionListener(e -> deleteSelected());
//         btnAccept.addActionListener(e -> acceptSelected());
//         btnInspect.addActionListener(e -> inspectSelected());
//         btnRefresh.addActionListener(e -> loadData());

//         loadData();
//     }

//     private void loadData() {
//         // load data in background to avoid blocking EDT
//         new SwingWorker<java.util.List<MaintenanceTask>, Void>() {
//             private Exception error;

//             @Override
//             protected java.util.List<MaintenanceTask> doInBackground() {
//                 try {
//                     return maintDAO.findAll();
//                 } catch (SQLException ex) {
//                     this.error = ex;
//                     return java.util.Collections.emptyList();
//                 }
//             }

//             @Override
//             protected void done() {
//                 if (error != null) {
//                     error.printStackTrace();
//                     JOptionPane.showMessageDialog(MaintenancePanel.this, "Lỗi load: " + error.getMessage());
//                     return;
//                 }
//                 try {
//                     java.util.List<MaintenanceTask> list = get();
//                     model.setRowCount(0);
//                     equipmentCache.clear();
//                     for (MaintenanceTask m : list) {
//                         Equipment eq = equipmentDAO.findById(m.getEquipmentId());
//                         if (eq != null)
//                             equipmentCache.put(eq.getId(), eq);
//                         model.addRow(new Object[] { m.getId(), eq == null ? m.getEquipmentId() : eq.getName(),
//                                 m.getScheduleDate() == null ? "" : m.getScheduleDate().toString(), m.isCompleted(),
//                                 m.getNote(), m.getAssignedTo() == null ? "" : m.getAssignedTo() });
//                     }
//                 } catch (Exception ex) {
//                     ex.printStackTrace();
//                 }
//             }
//         }.execute();
//     }

//     private void openDialog(MaintenanceTask m) {
//         MaintenanceDialog d = new MaintenanceDialog(SwingUtilities.getWindowAncestor(this), m);
//         d.setVisible(true);
//         if (d.isSaved())
//             loadData();
//     }

//     private void deleteSelected() {
//         int r = table.getSelectedRow();
//         if (r < 0) {
//             JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
//             return;
//         }
//         int id = (int) model.getValueAt(r, 0);
//         int c = JOptionPane.showConfirmDialog(this, "Xóa công việc #" + id + "?", "Xác nhận",
//                 JOptionPane.YES_NO_OPTION);
//         if (c == JOptionPane.YES_OPTION) {
//             try {
//                 maintDAO.delete(id);
//                 loadData();
//             } catch (SQLException ex) {
//                 ex.printStackTrace();
//                 JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
//             }
//         }
//     }

//     private void acceptSelected() {
//         int r = table.getSelectedRow();
//         if (r < 0) {
//             JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
//             return;
//         }
//         int id = (int) model.getValueAt(r, 0);
//         String note = JOptionPane.showInputDialog(this, "Ghi chú kiểm tra/duyệt (optional):");
//         String assigned = JOptionPane.showInputDialog(this, "Chỉ định cho (tên nhân viên hoặc bộ phận):");
//         try {
//             String acceptedBy = currentUser == null ? "" : currentUser.getUsername();
//             maintDAO.accept(id, acceptedBy, note == null ? "" : note, assigned == null ? "" : assigned);
//             loadData();
//             JOptionPane.showMessageDialog(this, "Đã duyệt và chỉ định công việc #" + id);
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//         }
//     }

//     private void inspectSelected() {
//         // For simplicity, inspect flows same as accept (mark inspected)
//         int r = table.getSelectedRow();
//         if (r < 0) {
//             JOptionPane.showMessageDialog(this, "Chọn 1 công việc");
//             return;
//         }
//         int id = (int) model.getValueAt(r, 0);
//         String note = JOptionPane.showInputDialog(this, "Ghi chú kiểm tra (optional):");
//         try {
//             maintDAO.inspect(id, note == null ? "" : note);
//             loadData();
//             JOptionPane.showMessageDialog(this, "Đã đánh dấu kiểm tra cho công việc #" + id);
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//         }
//     }
// }