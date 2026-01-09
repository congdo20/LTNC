package com.example.hospital.ui;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;
import com.example.hospital.models.Permission;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private MainFrame mainFrame; // ✅ Để gọi refreshTabs() khi permissions thay đổi
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfUserSearch;
    private JComboBox<String> cbUserSearchField;

    public UserManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame; // ✅ Nhận MainFrame reference
        initUI();
        loadData();
    }

    // ✅ Constructor default cho backward compatibility
    public UserManagementPanel() {
        this(null);
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

        // Setup sorter for search/filter
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search UI
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm:"));
        cbUserSearchField = new JComboBox<>(new String[] { "Tất cả", "Username", "Fullname", "Role", "Department" });
        searchPanel.add(cbUserSearchField);
        tfUserSearch = new JTextField(20);
        searchPanel.add(tfUserSearch);
        JButton btnClearSearch = new JButton("Xóa");
        searchPanel.add(btnClearSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Document listener for real-time search
        tfUserSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        // Clear search button
        btnClearSearch.addActionListener(e -> {
            tfUserSearch.setText("");
            sorter.setRowFilter(null);
        });

        // Combo box to apply filter when selection changes
        cbUserSearchField.addActionListener(e -> applyFilter());

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
        UserDialog d = new UserDialog(SwingUtilities.getWindowAncestor(this), u, userDAO, mainFrame);
        d.setVisible(true);
        if (d.isSaved()) {
            loadData();
            // ✅ Refresh tabs nếu permissions thay đổi
            if (mainFrame != null) {
                mainFrame.refreshTabs();
            }
        }
    }

    private void applyFilter() {
        String txt = tfUserSearch.getText().trim();
        if (txt.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        String rx = "(?i).*" + Pattern.quote(txt) + ".*";
        int sel = cbUserSearchField.getSelectedIndex();
        try {
            if (sel == 0) { // Tất cả
                sorter.setRowFilter(RowFilter.regexFilter(rx));
            } else {
                int colIdx = 1; // default Username
                if (sel == 1)
                    colIdx = 1; // Username
                else if (sel == 2)
                    colIdx = 2; // Fullname
                else if (sel == 3)
                    colIdx = 6; // Role
                else if (sel == 4)
                    colIdx = 7; // Department
                sorter.setRowFilter(RowFilter.regexFilter(rx, colIdx));
            }
        } catch (java.util.regex.PatternSyntaxException ex) {
            sorter.setRowFilter(null);
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
        private Map<String, JCheckBox> permBoxes = new LinkedHashMap<>();

        private User user;
        private UserDAO dao;
        private MainFrame mainFrame; // ✅ Để refresh tabs sau khi save

        public UserDialog(Window owner, User u, UserDAO dao, MainFrame mainFrame) {
            super(owner, "User", ModalityType.APPLICATION_MODAL);
            this.dao = dao;
            this.user = (u == null) ? new User() : u;
            this.mainFrame = mainFrame; // ✅ Lưu reference

            initUI();
            fillData();
        }

        private void initUI() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Main user fields
            JPanel mainPanel = new JPanel(new GridLayout(11, 2, 6, 6));

            mainPanel.add(new JLabel("Username:"));
            mainPanel.add(tfUser);

            mainPanel.add(new JLabel("Password (để trống nếu không đổi):"));
            mainPanel.add(pfPass);

            mainPanel.add(new JLabel("Full name:"));
            mainPanel.add(tfFull);

            mainPanel.add(new JLabel("DOB (YYYY-MM-DD):"));
            mainPanel.add(tfDob);

            mainPanel.add(new JLabel("Gender:"));
            mainPanel.add(tfGender);

            mainPanel.add(new JLabel("Position:"));
            mainPanel.add(tfPosition);

            mainPanel.add(new JLabel("Role:"));
            cbRole = new JComboBox<>();
            for (User.Role r : User.Role.values()) {
                cbRole.addItem(r.name());
            }
            mainPanel.add(cbRole);

            mainPanel.add(new JLabel("Department (optional):"));
            try {
                com.example.hospital.dao.DepartmentDAO depDao = new com.example.hospital.dao.DepartmentDAO();
                java.util.Map<Integer, String> deps = depDao.findAll();
                cbDept.addItem("");
                for (java.util.Map.Entry<Integer, String> en : deps.entrySet()) {
                    cbDept.addItem(en.getKey() + " - " + en.getValue());
                }
            } catch (SQLException ex) {
                // ignore
            }
            mainPanel.add(cbDept);

            mainPanel.add(new JLabel("Phone:"));
            mainPanel.add(tfPhone);

            mainPanel.add(new JLabel("Email:"));
            mainPanel.add(tfEmail);

            p.add(mainPanel);

            // Permissions panel
            JPanel permPanel = new JPanel();
            permPanel.setLayout(new BoxLayout(permPanel, BoxLayout.Y_AXIS));
            permPanel.setBorder(BorderFactory.createTitledBorder("Quyền truy cập"));

            for (String key : Permission.LABELS.keySet()) {
                JCheckBox cb = new JCheckBox(Permission.LABELS.get(key));
                permBoxes.put(key, cb);
                permPanel.add(cb);
            }

            p.add(permPanel);

            // Buttons
            JPanel btnPanel = new JPanel(new FlowLayout());
            JButton btnSave = new JButton("Lưu");
            JButton btnCancel = new JButton("Hủy");

            btnSave.addActionListener(e -> save());
            btnCancel.addActionListener(e -> dispose());

            btnPanel.add(btnSave);
            btnPanel.add(btnCancel);
            p.add(btnPanel);

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

            // Fill permissions
            for (String key : permBoxes.keySet()) {
                JCheckBox cb = permBoxes.get(key);
                cb.setSelected(user.hasPermission(key));
            }
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

                // Build permissions map from checkboxes
                Map<String, Boolean> permsMap = new LinkedHashMap<>();
                for (String key : permBoxes.keySet()) {
                    JCheckBox cb = permBoxes.get(key);
                    permsMap.put(key, cb.isSelected());
                }
                user.setPermissions(permsMap);

                try {
                    if (user.getId() == 0) {
                        if (password.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Yêu cầu mật khẩu cho người dùng mới");
                            return;
                        }
                        dao.create(user, password);
                    } else {
                        dao.update(user, password);
                        // Update permissions after user update
                        dao.setPermissions(user.getId(), user.getPermissions());
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
