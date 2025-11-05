// package com.example.hospital.ui;

// import com.example.hospital.dao.UserDAO;
// import com.example.hospital.models.User;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.sql.SQLException;
// import java.util.List;

// public class UserManagementPanel extends JPanel {
//     private UserDAO userDAO = new UserDAO();
//     private JTable table;
//     private DefaultTableModel model;

//     public UserManagementPanel() {
//         setLayout(new BorderLayout());
//         model = new DefaultTableModel(new Object[] { "ID", "Username", "Fullname", "Role" }, 0) {
//             public boolean isCellEditable(int r, int c) {
//                 return false;
//             }
//         };
//         table = new JTable(model);
//         add(new JScrollPane(table), BorderLayout.CENTER);

//         JPanel btns = new JPanel();
//         JButton btnAdd = new JButton("Thêm");
//         JButton btnEdit = new JButton("Sửa");
//         JButton btnDelete = new JButton("Xóa");
//         JButton btnRefresh = new JButton("Làm mới");
//         btns.add(btnAdd);
//         btns.add(btnEdit);
//         btns.add(btnDelete);
//         btns.add(btnRefresh);
//         add(btns, BorderLayout.SOUTH);

//         btnAdd.addActionListener(e -> openDialog(null));
//         btnEdit.addActionListener(e -> {
//             int r = table.getSelectedRow();
//             if (r < 0) {
//                 JOptionPane.showMessageDialog(this, "Chọn 1 user");
//                 return;
//             }
//             int id = (int) model.getValueAt(r, 0);
//             try {
//                 List<User> list = userDAO.findAll();
//                 for (User u : list)
//                     if (u.getId() == id) {
//                         openDialog(u);
//                         break;
//                     }
//             } catch (SQLException ex) {
//                 ex.printStackTrace();
//                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//             }
//         });
//         btnDelete.addActionListener(e -> {
//             int r = table.getSelectedRow();
//             if (r < 0) {
//                 JOptionPane.showMessageDialog(this, "Chọn 1 user");
//                 return;
//             }
//             int id = (int) model.getValueAt(r, 0);
//             int c = JOptionPane.showConfirmDialog(this, "Xóa user #" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//             if (c == JOptionPane.YES_OPTION) {
//                 try {
//                     userDAO.delete(id);
//                     loadData();
//                 } catch (SQLException ex) {
//                     ex.printStackTrace();
//                     JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//                 }
//             }
//         });
//         btnRefresh.addActionListener(e -> loadData());

//         loadData();
//     }

//     private void openDialog(User u) {
//         UserDialog d = new UserDialog(SwingUtilities.getWindowAncestor(this), u);
//         d.setVisible(true);
//         if (d.isSaved())
//             loadData();
//     }

//     private void loadData() {
//         try {
//             model.setRowCount(0);
//             List<User> list = userDAO.findAll();
//             for (User u : list)
//                 model.addRow(new Object[] { u.getId(), u.getUsername(), u.getFullname(), u.getRole() });
//         } catch (SQLException ex) {
//             ex.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi load: " + ex.getMessage());
//         }
//     }

//     // Simple modal dialog for create/edit
//     static class UserDialog extends JDialog {
//         private boolean saved = false;
//         private JTextField tfUser = new JTextField(20);
//         private JPasswordField pfPass = new JPasswordField(20);
//         private JTextField tfFull = new JTextField(20);
//         private JComboBox<String> cbRole = new JComboBox<>(new String[] { "USER", "ADMIN" });
//         private User user;

//         public UserDialog(Window owner, User u) {
//             super(owner, "User", ModalityType.APPLICATION_MODAL);
//             this.user = u == null ? new User() : u;
//             tfUser.setText(user.getUsername());
//             tfFull.setText(user.getFullname());
//             if (user.getRole() != null)
//                 cbRole.setSelectedItem(user.getRole());
//             JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
//             p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//             p.add(new JLabel("Username:"));
//             p.add(tfUser);
//             p.add(new JLabel("Password (leave blank to keep):"));
//             p.add(pfPass);
//             p.add(new JLabel("Full name:"));
//             p.add(tfFull);
//             p.add(new JLabel("Role:"));
//             p.add(cbRole);
//             JButton btnSave = new JButton("Lưu");
//             JButton btnCancel = new JButton("Hủy");
//             p.add(btnSave);
//             p.add(btnCancel);
//             setContentPane(p);
//             pack();
//             setLocationRelativeTo(owner);

//             btnSave.addActionListener(e -> doSave());
//             btnCancel.addActionListener(e -> dispose());
//         }

//         private void doSave() {
//             try {
//                 String u = tfUser.getText().trim();
//                 String pass = new String(pfPass.getPassword());
//                 String full = tfFull.getText().trim();
//                 String role = (String) cbRole.getSelectedItem();
//                 if (u.isEmpty()) {
//                     JOptionPane.showMessageDialog(this, "Username required");
//                     return;
//                 }
//                 UserDAO dao = new UserDAO();
//                 user.setUsername(u);
//                 user.setFullname(full);
//                 user.setRole(role);
//                 if (user.getId() == 0) {
//                     // create
//                     dao.create(user, pass);
//                 } else {
//                     dao.update(user, pass);
//                 }
//                 saved = true;
//                 dispose();
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//             }
//         }

//         public boolean isSaved() {
//             return saved;
//         }
//     }
// }
