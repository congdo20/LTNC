package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel model;

    public UserManagementPanel() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] { "ID", "Username", "Fullname", "Role" }, 0) {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);
        btns.add(btnRefresh);
        add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> openDialog(null));

        btnEdit.addActionListener(e -> {
            User selected = getSelectedUser();
            if (selected == null)
                return;
            openDialog(selected);
        });

        btnDelete.addActionListener(e -> {
            User selected = getSelectedUser();
            if (selected == null)
                return;

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Xóa user #" + selected.getId() + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    userDAO.delete(selected.getId());
                    loadData();
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });

        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<User> list = userDAO.findAll();
            for (User u : list) {
                model.addRow(new Object[] {
                        u.getId(),
                        u.getUsername(),
                        u.getFullname(),
                        u.getRole()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private User getSelectedUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 user");
            return null;
        }

        int id = (int) model.getValueAt(r, 0);
        try {
            return userDAO.findById(id);
        } catch (SQLException ex) {
            showError(ex);
            return null;
        }
    }

    private void openDialog(User u) {
        UserDialog d = new UserDialog(SwingUtilities.getWindowAncestor(this), u, userDAO);
        d.setVisible(true);
        if (d.isSaved()) {
            loadData();
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    static class UserDialog extends JDialog {

        private boolean saved = false;
        private JTextField tfUser = new JTextField(20);
        private JPasswordField pfPass = new JPasswordField(20);
        private JTextField tfFull = new JTextField(20);
        private JComboBox<String> cbRole = new JComboBox<>(new String[] { "USER", "ADMIN" });

        private User user;
        private UserDAO dao;

        public UserDialog(Window owner, User u, UserDAO dao) {
            super(owner, "User", ModalityType.APPLICATION_MODAL);
            this.dao = dao;
            this.user = (u == null) ? new User() : u;

            initUI();
            fillData();
        }

        private void initUI() {
            JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            p.add(new JLabel("Username:"));
            p.add(tfUser);

            p.add(new JLabel("Password (để trống nếu không đổi):"));
            p.add(pfPass);

            p.add(new JLabel("Full name:"));
            p.add(tfFull);

            p.add(new JLabel("Role:"));
            p.add(cbRole);

            JButton btnSave = new JButton("Lưu");
            JButton btnCancel = new JButton("Hủy");

            btnSave.addActionListener(e -> save());
            btnCancel.addActionListener(e -> dispose());

            p.add(btnSave);
            p.add(btnCancel);

            setContentPane(p);
            pack();
            setLocationRelativeTo(getOwner());
        }

        private void fillData() {
            tfUser.setText(user.getUsername());
            tfFull.setText(user.getFullname());
            if (user.getRole() != null)
                cbRole.setSelectedItem(user.getRole());
        }

        private void save() {
            try {
                String username = tfUser.getText().trim();
                String password = new String(pfPass.getPassword());
                String fullname = tfFull.getText().trim();
                String role = (String) cbRole.getSelectedItem();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username required");
                    return;
                }

                user.setUsername(username);
                user.setFullname(fullname);
                user.setRole(User.Role.valueOf(role.toUpperCase()));

                // if (user.getId() == 0) {
                //     dao.create(user, password);
                // } else {
                //     dao.update(user, password);
                // }

                saved = true;
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
