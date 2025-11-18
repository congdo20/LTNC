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
                new Object[] { "ID", "Username", "Fullname", "DOB", "Gender", "Position", "Role", "Department",
                        "Phone", "Email" },
                0) {

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

        // // Theme the edit/delete buttons to make them more visible
        // btnEdit.setBackground(new Color(59, 89, 152));
        // btnEdit.setForeground(Color.WHITE);
        // btnDelete.setBackground(new Color(200, 50, 50));
        // btnDelete.setForeground(Color.WHITE);

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
            com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
            for (User u : list) {
                String deptName = null;
                try {
                    deptName = depDao.findNameById(u.getDepartmentId());
                } catch (SQLException ex) {
                    // ignore and fallback to id
                }
                model.addRow(new Object[] {
                        u.getId(),
                        u.getUsername(),
                        u.getFullname(),
                        u.getDob() == null ? "" : u.getDob(),
                        u.getGender() == null ? "" : u.getGender(),
                        u.getPosition() == null ? "" : u.getPosition(),
                        roleLabel(u.getRole()),
                        deptName == null ? (u.getDepartmentId() == null ? "" : u.getDepartmentId()) : deptName,
                        u.getPhone() == null ? "" : u.getPhone(),
                        u.getEmail() == null ? "" : u.getEmail()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private String roleLabel(User.Role role) {
        if (role == null)
            return "";
        switch (role) {
            case ADMIN:
                return "Quản trị";
            case TRUONG_KHOA:
                return "Trưởng khoa";
            case QL_THIET_BI:
                return "Quản lý thiết bị";
            case NV_BAO_TRI:
                return "Nhân viên bảo trì";
            default:
                return role.name();
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
        private JTextField tfDob = new JTextField(10);
        private JTextField tfGender = new JTextField(8);
        private JTextField tfPosition = new JTextField(20);
        private JComboBox<String> cbDept = new JComboBox<>();
        private JTextField tfPhone = new JTextField(15);
        private JTextField tfEmail = new JTextField(20);
        private JComboBox<String> cbRole;

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
            JPanel p = new JPanel(new GridLayout(11, 2, 6, 6));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            p.add(new JLabel("Username:"));
            p.add(tfUser);

            p.add(new JLabel("Password (để trống nếu không đổi):"));
            p.add(pfPass);

            p.add(new JLabel("Full name:"));
            p.add(tfFull);

            p.add(new JLabel("DOB (YYYY-MM-DD):"));
            p.add(tfDob);

            p.add(new JLabel("Gender:"));
            p.add(tfGender);

            p.add(new JLabel("Position:"));
            p.add(tfPosition);

            p.add(new JLabel("Role:"));
            // populate role combo with enum names
            cbRole = new JComboBox<>();
            for (User.Role r : User.Role.values()) {
                cbRole.addItem(r.name());
            }
            p.add(cbRole);

            p.add(new JLabel("Department (optional):"));
            // populate department combo with id - name entries
            try {
                com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
                java.util.Map<Integer, String> deps = depDao.findAll();
                cbDept.addItem(""); // allow empty (no department)
                for (java.util.Map.Entry<Integer, String> en : deps.entrySet()) {
                    cbDept.addItem(en.getKey() + " - " + en.getValue());
                }
            } catch (SQLException ex) {
                // ignore; leave combo empty
            }
            p.add(cbDept);

            p.add(new JLabel("Phone:"));
            p.add(tfPhone);

            p.add(new JLabel("Email:"));
            p.add(tfEmail);

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
            tfDob.setText(user.getDob() == null ? "" : user.getDob());
            tfGender.setText(user.getGender() == null ? "" : user.getGender());
            tfPosition.setText(user.getPosition() == null ? "" : user.getPosition());
            if (user.getRole() != null)
                cbRole.setSelectedItem(user.getRole().name());
            // select department in combo if present
            if (user.getDepartmentId() == null) {
                cbDept.setSelectedIndex(0);
            } else {
                String targetPrefix = user.getDepartmentId() + " - ";
                for (int i = 0; i < cbDept.getItemCount(); i++) {
                    String it = cbDept.getItemAt(i);
                    if (it != null && it.startsWith(targetPrefix)) {
                        cbDept.setSelectedIndex(i);
                        break;
                    }
                }
            }
            tfPhone.setText(user.getPhone() == null ? "" : user.getPhone());
            tfEmail.setText(user.getEmail() == null ? "" : user.getEmail());
        }

        private void save() {
            try {
                String username = tfUser.getText().trim();
                String password = new String(pfPass.getPassword());
                String fullname = tfFull.getText().trim();
                String dob = tfDob.getText().trim();
                String gender = tfGender.getText().trim();
                String position = tfPosition.getText().trim();
                String role = (String) cbRole.getSelectedItem();
                String deptText = "";
                Object sel = cbDept.getSelectedItem();
                if (sel != null)
                    deptText = sel.toString().trim();
                String phone = tfPhone.getText().trim();
                String email = tfEmail.getText().trim();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Yêu cầu tên người dùng (Username)");
                    return;
                }

                user.setUsername(username);
                user.setFullname(fullname);
                user.setDob(dob.isEmpty() ? null : dob);
                user.setGender(gender.isEmpty() ? null : gender);
                user.setPosition(position.isEmpty() ? null : position);
                user.setRole(User.Role.valueOf(role));
                if (deptText.isEmpty()) {
                    user.setDepartmentId(null);
                } else {
                    // deptText expected as "<id> - <name>"
                    String[] parts = deptText.split("\\s*-\\s*", 2);
                    try {
                        int depId = Integer.parseInt(parts[0]);
                        user.setDepartmentId(depId);
                    } catch (Exception nfe) {
                        JOptionPane.showMessageDialog(this, "Chọn phòng/khoa hợp lệ hoặc để trống");
                        return;
                    }
                }

                user.setPhone(phone.isEmpty() ? null : phone);
                user.setEmail(email.isEmpty() ? null : email);

                try {
                    if (user.getId() == 0) {
                        // Creating new user requires a password
                        if (password.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Yêu cầu mật khẩu cho người dùng mới");
                            return;
                        }
                        dao.create(user, password);
                    } else {
                        // Update; if password empty => no change (handled by DAO)
                        dao.update(user, password);
                    }
                    saved = true;
                    dispose();
                } catch (SQLException s) {
                    s.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi lưu: " + s.getMessage());
                }

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
