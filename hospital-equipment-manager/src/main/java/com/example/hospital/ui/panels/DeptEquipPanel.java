// package com.example.hospital.ui.panels;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;

// import com.example.hospital.dao.EquipmentDAO;
// import com.example.hospital.dao.DepartmentDAO;
// import com.example.hospital.models.Equipment;
// import com.example.hospital.models.User;
// import com.example.hospital.util.ImageUtil;

// import java.awt.*;
// import java.sql.SQLException;
// import java.util.List;

// public class DeptEquipPanel extends JPanel {

//     private JTable table;
//     private EquipmentDAO dao = new EquipmentDAO();
//     private DepartmentDAO depDao = new DepartmentDAO();
//     private JLabel imagePreview;

//     public DeptEquipPanel(User user) {
//         setLayout(new BorderLayout());

//         DefaultTableModel model = new DefaultTableModel(
//                 new String[] { "ID", "Mã", "Tên", "Hãng", "Năm", "Tình trạng", "Khoa" }, 0);

//         table = new JTable(model);

//         // image preview on the right (read-only view for department head)
//         imagePreview = new JLabel();
//         imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
//         imagePreview.setPreferredSize(new Dimension(260, 260));

//         JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imagePreview);
//         split.setResizeWeight(0.75);
//         add(split, BorderLayout.CENTER);

//         // selection listener to update preview
//         table.getSelectionModel().addListSelectionListener(ev -> {
//             if (ev.getValueIsAdjusting())
//                 return;
//             int r = table.getSelectedRow();
//             if (r < 0) {
//                 imagePreview.setIcon(null);
//                 imagePreview.setText("");
//                 return;
//             }
//             try {
//                 int id = (int) model.getValueAt(r, 0);
//                 Equipment eq = dao.findById(id);
//                 if (eq != null && eq.getImagePath() != null && !eq.getImagePath().isEmpty()) {
//                     ImageIcon scaled = ImageUtil.loadScaledIcon(eq.getImagePath(), 260, 260);
//                     if (scaled != null) {
//                         imagePreview.setIcon(scaled);
//                         imagePreview.setText("");
//                     } else {
//                         imagePreview.setIcon(null);
//                         imagePreview.setText("Không tải được ảnh");
//                     }
//                 } else {
//                     imagePreview.setIcon(null);
//                     imagePreview.setText("Không có ảnh");
//                 }
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 imagePreview.setIcon(null);
//                 imagePreview.setText("Lỗi");
//             }
//         });

//         loadData(user);
//     }

//     private void loadData(User user) {
//         try {
//             List<Equipment> list = dao.findByDepartment(user.getDepartmentId());

//             DefaultTableModel model = (DefaultTableModel) table.getModel();
//             model.setRowCount(0);

//             for (Equipment e : list) {
//                 String deptName = e.getDepartmentId() == null ? "" : String.valueOf(e.getDepartmentId());
//                 try {
//                     if (e.getDepartmentId() != null) {
//                         String dn = depDao.findNameById(e.getDepartmentId());
//                         if (dn != null)
//                             deptName = dn;
//                     }
//                 } catch (Exception ex) {
//                     // ignore
//                 }

//                 model.addRow(new Object[] {
//                         e.getId(), e.getCode(), e.getName(), e.getManufacturer(),
//                         e.getYearOfUse(), e.getStatus(), deptName
//                 });
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
//         }
//     }
// }

// package com.example.hospital.ui.panels;

// import javax.swing.*;
// import javax.swing.event.DocumentEvent;
// import javax.swing.event.DocumentListener;
// import javax.swing.table.DefaultTableModel;
// import javax.swing.table.TableRowSorter;
// import javax.swing.RowFilter;

// import com.example.hospital.dao.EquipmentDAO;
// import com.example.hospital.dao.DepartmentDAO;
// import com.example.hospital.models.Equipment;
// import com.example.hospital.models.User;
// import com.example.hospital.util.ImageUtil;

// import java.awt.*;
// import java.sql.SQLException;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.regex.Pattern;

// public class DeptEquipPanel extends JPanel {

//     private JTable table;
//     private EquipmentDAO dao = new EquipmentDAO();
//     private DepartmentDAO depDao = new DepartmentDAO();
//     private JLabel imagePreview;
//     private TableRowSorter<DefaultTableModel> sorter;
//     private JTextField tfSearch;
//     private JComboBox<String> cbSearchField;

//     public DeptEquipPanel(User user) {
//         setLayout(new BorderLayout());

//         DefaultTableModel model = new DefaultTableModel(
//                 new String[] { "ID", "Mã", "Tên", "Hãng", "Năm", "Tình trạng", "Khoa" }, 0);

//         table = new JTable(model);

//         // Search UI (show for department head)
//         JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         searchPanel.add(new JLabel("Tìm:"));
//         tfSearch = new JTextField(20);
//         searchPanel.add(tfSearch);
//         cbSearchField = new JComboBox<>(new String[] { "Tất cả", "Mã", "Tên", "Hãng", "Trạng Thái", "Khoa" });
//         searchPanel.add(cbSearchField);
//         JButton btnClear = new JButton("Xóa");
//         searchPanel.add(btnClear);
//         add(searchPanel, BorderLayout.NORTH);

//         // Setup sorter and listeners
//         sorter = new TableRowSorter<>(model);
//         table.setRowSorter(sorter);
//         tfSearch.getDocument().addDocumentListener(new DocumentListener() {
//             @Override
//             public void insertUpdate(DocumentEvent e) {
//                 applyFilter();
//             }

//             @Override
//             public void removeUpdate(DocumentEvent e) {
//                 applyFilter();
//             }

//             @Override
//             public void changedUpdate(DocumentEvent e) {
//                 applyFilter();
//             }
//         });
//         cbSearchField.addActionListener(e -> applyFilter());
//         btnClear.addActionListener(e -> {
//             tfSearch.setText("");
//             if (sorter != null)
//                 sorter.setRowFilter(null);
//         });

//         // image preview on the right (read-only view for department head)
//         imagePreview = new JLabel();
//         imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
//         imagePreview.setPreferredSize(new Dimension(260, 260));

//         JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imagePreview);
//         split.setResizeWeight(0.75);
//         add(split, BorderLayout.CENTER);

//         // selection listener to update preview
//         table.getSelectionModel().addListSelectionListener(ev -> {
//             if (ev.getValueIsAdjusting())
//                 return;
//             int r = table.getSelectedRow();
//             if (r < 0) {
//                 imagePreview.setIcon(null);
//                 imagePreview.setText("");
//                 return;
//             }
//             try {
//                 int id = (int) model.getValueAt(r, 0);
//                 Equipment eq = dao.findById(id);
//                 if (eq != null && eq.getImagePath() != null && !eq.getImagePath().isEmpty()) {
//                     ImageIcon scaled = ImageUtil.loadScaledIcon(eq.getImagePath(), 260, 260);
//                     if (scaled != null) {
//                         imagePreview.setIcon(scaled);
//                         imagePreview.setText("");
//                     } else {
//                         imagePreview.setIcon(null);
//                         imagePreview.setText("Không tải được ảnh");
//                     }
//                 } else {
//                     imagePreview.setIcon(null);
//                     imagePreview.setText("Không có ảnh");
//                 }
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 imagePreview.setIcon(null);
//                 imagePreview.setText("Lỗi");
//             }
//         });

//         loadData(user);
//     }

//     private void loadData(User user) {
//         try {
//             List<Equipment> list = dao.findByDepartment(user.getDepartmentId());

//             DefaultTableModel model = (DefaultTableModel) table.getModel();
//             model.setRowCount(0);

//             for (Equipment e : list) {
//                 String deptName = e.getDepartmentId() == null ? "" : String.valueOf(e.getDepartmentId());
//                 try {
//                     if (e.getDepartmentId() != null) {
//                         String dn = depDao.findNameById(e.getDepartmentId());
//                         if (dn != null)
//                             deptName = dn;
//                     }
//                 } catch (Exception ex) {
//                     // ignore
//                 }

//                 model.addRow(new Object[] {
//                         e.getId(), e.getCode(), e.getName(), e.getManufacturer(),
//                         e.getYearOfUse(), e.getStatus(), deptName
//                 });
//             }
//             if (sorter != null)
//                 applyFilter();
//         } catch (SQLException e) {
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
//         }
//     }

//     private void applyFilter() {
//         if (sorter == null || tfSearch == null)
//             return;
//         String text = tfSearch.getText();
//         if (text == null || text.trim().isEmpty()) {
//             sorter.setRowFilter(null);
//             return;
//         }
//         String q = "(?i)" + Pattern.quote(text.trim());
//         String selected = (String) cbSearchField.getSelectedItem();
//         try {
//             if ("Mã".equals(selected)) {
//                 sorter.setRowFilter(RowFilter.regexFilter(q, 1));
//             } else if ("Tên".equals(selected)) {
//                 sorter.setRowFilter(RowFilter.regexFilter(q, 2));
//             } else if ("Hãng".equals(selected)) {
//                 sorter.setRowFilter(RowFilter.regexFilter(q, 3));
//             } else if ("Trạng Thái".equals(selected)) {
//                 sorter.setRowFilter(RowFilter.regexFilter(q, 5));
//             } else if ("Khoa".equals(selected)) {
//                 sorter.setRowFilter(RowFilter.regexFilter(q, 6));
//             } else { // Tất cả
//                 List<RowFilter<Object, Object>> filters = new ArrayList<>();
//                 filters.add(RowFilter.regexFilter(q, 1));
//                 filters.add(RowFilter.regexFilter(q, 2));
//                 filters.add(RowFilter.regexFilter(q, 3));
//                 filters.add(RowFilter.regexFilter(q, 5));
//                 filters.add(RowFilter.regexFilter(q, 6));
//                 sorter.setRowFilter(RowFilter.orFilter(filters));
//             }
//         } catch (java.util.regex.PatternSyntaxException ex) {
//             sorter.setRowFilter(null);
//         }
//     }
// }

package com.example.hospital.ui.panels;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.DepartmentDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.User;
import com.example.hospital.util.ImageUtil;
import com.example.hospital.ui.MaintenanceRequestFormSwing;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DeptEquipPanel extends JPanel {

    private JTable table;
    private EquipmentDAO dao = new EquipmentDAO();
    private DepartmentDAO depDao = new DepartmentDAO();
    private JLabel imagePreview;
    private User currentUser;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;
    private JComboBox<String> cbSearchField;

    public DeptEquipPanel(User user) {
        setLayout(new BorderLayout());
        this.currentUser = user;

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "ID", "Mã", "Tên", "Hãng", "Năm", "Tình trạng", "Khoa" }, 0);

        table = new JTable(model);

        // Search UI (show for department head)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm:"));
        tfSearch = new JTextField(20);
        searchPanel.add(tfSearch);
        cbSearchField = new JComboBox<>(new String[] { "Tất cả", "Mã", "Tên", "Hãng", "Trạng Thái", "Khoa" });
        searchPanel.add(cbSearchField);
        final JButton btnCreate = new JButton("Tạo yêu cầu");
        btnCreate.setEnabled(false);
        searchPanel.add(btnCreate);
        final JButton btnViewRequests = new JButton("Xem yêu cầu");
        btnViewRequests.setEnabled(false);
        searchPanel.add(btnViewRequests);
        JButton btnClear = new JButton("Xóa");
        searchPanel.add(btnClear);
        add(searchPanel, BorderLayout.NORTH);

        // Setup sorter and listeners
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
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
        cbSearchField.addActionListener(e -> applyFilter());
        btnClear.addActionListener(e -> {
            tfSearch.setText("");
            if (sorter != null)
                sorter.setRowFilter(null);
        });

        // image preview on the right (read-only view for department head)
        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(260, 260));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), imagePreview);
        split.setResizeWeight(0.75);
        add(split, BorderLayout.CENTER);

        // selection listener to update preview (and enable Create and View buttons)
        table.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting())
                return;
            int r = table.getSelectedRow();
            btnCreate.setEnabled(r >= 0);
            btnViewRequests.setEnabled(r >= 0);
            if (r < 0) {
                imagePreview.setIcon(null);
                imagePreview.setText("");
                return;
            }
            try {
                int id = (int) model.getValueAt(r, 0);
                Equipment eq = dao.findById(id);
                if (eq != null && eq.getImagePath() != null && !eq.getImagePath().isEmpty()) {
                    ImageIcon scaled = ImageUtil.loadScaledIcon(eq.getImagePath(), 260, 260);
                    if (scaled != null) {
                        imagePreview.setIcon(scaled);
                        imagePreview.setText("");
                    } else {
                        imagePreview.setIcon(null);
                        imagePreview.setText("Không tải được ảnh");
                    }
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Không có ảnh");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                imagePreview.setIcon(null);
                imagePreview.setText("Lỗi");
            }
        });

        // Open maintenance request form prefilled with selected equipment
        btnCreate.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị để tạo yêu cầu");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            try {
                int equipmentId = (int) table.getModel().getValueAt(modelRow, 0);
                Equipment eq = dao.findById(equipmentId);
                MaintenanceRequestFormSwing form = new MaintenanceRequestFormSwing(this.currentUser);
                if (eq != null)
                    form.preselectEquipment(eq.getId());
                form.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở form: " + ex.getMessage());
            }
        });

        btnViewRequests.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            try {
                int equipmentId = (int) table.getModel().getValueAt(modelRow, 0);
                Window parent = SwingUtilities.getWindowAncestor(this);
                EquipmentRequestsDialog dlg = new EquipmentRequestsDialog(parent, equipmentId);
                dlg.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở danh sách yêu cầu: " + ex.getMessage());
            }
        });

        loadData(user);
    }

    private void loadData(User user) {
        try {
            List<Equipment> list;
            if (user.getDepartmentId() == null) {
                // Admin or users without department: show all equipment
                list = dao.findAll();
            } else {
                // Regular department head: show only department equipment
                list = dao.findByDepartment(user.getDepartmentId());
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Equipment e : list) {
                String deptName = e.getDepartmentId() == null ? "" : String.valueOf(e.getDepartmentId());
                try {
                    if (e.getDepartmentId() != null) {
                        String dn = depDao.findNameById(e.getDepartmentId());
                        if (dn != null)
                            deptName = dn;
                    }
                } catch (Exception ex) {
                    // ignore
                }

                model.addRow(new Object[] {
                        e.getId(), e.getCode(), e.getName(), e.getManufacturer(),
                        e.getYearOfUse(), e.getStatus(), deptName
                });
            }
            if (sorter != null)
                applyFilter();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void applyFilter() {
        if (sorter == null || tfSearch == null)
            return;
        String text = tfSearch.getText();
        if (text == null || text.trim().isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        String q = "(?i)" + Pattern.quote(text.trim());
        String selected = (String) cbSearchField.getSelectedItem();
        try {
            if ("Mã".equals(selected)) {
                sorter.setRowFilter(RowFilter.regexFilter(q, 1));
            } else if ("Tên".equals(selected)) {
                sorter.setRowFilter(RowFilter.regexFilter(q, 2));
            } else if ("Hãng".equals(selected)) {
                sorter.setRowFilter(RowFilter.regexFilter(q, 3));
            } else if ("Trạng Thái".equals(selected)) {
                sorter.setRowFilter(RowFilter.regexFilter(q, 5));
            } else if ("Khoa".equals(selected)) {
                sorter.setRowFilter(RowFilter.regexFilter(q, 6));
            } else { // Tất cả
                List<RowFilter<Object, Object>> filters = new ArrayList<>();
                filters.add(RowFilter.regexFilter(q, 1));
                filters.add(RowFilter.regexFilter(q, 2));
                filters.add(RowFilter.regexFilter(q, 3));
                filters.add(RowFilter.regexFilter(q, 5));
                filters.add(RowFilter.regexFilter(q, 6));
                sorter.setRowFilter(RowFilter.orFilter(filters));
            }
        } catch (java.util.regex.PatternSyntaxException ex) {
            sorter.setRowFilter(null);
        }
    }
}