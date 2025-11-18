package com.example.hospital.ui.panels;

import com.example.hospital.dao.MaintenanceDAO;
import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.MaintenanceTask;
import com.example.hospital.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanPanel extends JPanel {

    private final MaintenanceRequestDAO reqDao = new MaintenanceRequestDAO();
    private final MaintenanceDAO planDao = new MaintenanceDAO();
    private JTable table;
    private DefaultTableModel model;
    private List<MaintenanceRequest> currentRequests = new ArrayList<>();
    private final User currentUser;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PlanPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(5, 5));

        JLabel title = new JLabel("Lên kế hoạch bảo trì", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(
                new Object[] { "ID", "Thiết bị", "Mô tả", "Ngày yêu cầu", "Trạng thái" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCreate = new JButton("Tạo kế hoạch");
        JButton btnRefresh = new JButton("Làm mới");
        bottom.add(btnCreate);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> openCreatePlanDialog());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        try {
            currentRequests = reqDao.findAll();
            model.setRowCount(0);
            for (MaintenanceRequest r : currentRequests) {
                model.addRow(new Object[] {
                        r.getId(),
                        r.getEquipmentId(),
                        r.getIssueDescription(),
                        r.getRequestDate().format(DATE_FORMAT),
                        r.getStatus()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void openCreatePlanDialog() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một yêu cầu để lập kế hoạch");
            return;
        }

        List<MaintenanceRequest> selectedRequests = new ArrayList<>();
        for (int row : selectedRows) {
            int id = (int) model.getValueAt(row, 0);
            for (MaintenanceRequest mr : currentRequests) {
                if (mr.getId() == id) {
                    selectedRequests.add(mr);
                    break;
                }
            }
        }

        createPlansFor(selectedRequests, this);
    }

    /**
     * Tạo kế hoạch cho các yêu cầu được chọn. Có thể tái sử dụng từ các panel khác.
     */
    public void createPlansFor(List<MaintenanceRequest> selected, Component parent) {
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không có yêu cầu để lập kế hoạch");
            return;
        }

        if (currentUser == null) {
            JOptionPane.showMessageDialog(parent, "Người lập kế hoạch chưa được xác định!");
            return;
        }

        // Load danh sách kỹ thuật viên
        List<User> technicians = new ArrayList<>();
        try {
            UserDAO udao = new UserDAO();
            for (User u : udao.findAll()) {
                if (u != null && u.getRole() == User.Role.NV_BAO_TRI) {
                    technicians.add(u);
                }
            }
        } catch (Exception ex) {
            // ignore, list technicians có thể rỗng
        }

        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        List<JSpinner> dateSpinners = new ArrayList<>();

        for (MaintenanceRequest mr : selected) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

            row.add(new JLabel("#" + mr.getId() + " - EQ:" + mr.getEquipmentId()));

            // Ngày
            SpinnerDateModel sdm = new SpinnerDateModel(
                    Date.from(mr.getRequestDate().atZone(ZoneId.systemDefault()).toInstant()),
                    null, null, java.util.Calendar.DAY_OF_MONTH);
            JSpinner spDate = new JSpinner(sdm);
            spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));
            dateSpinners.add(spDate);
            row.add(new JLabel(" Ngày:"));
            row.add(spDate);

            // (Không chọn giao cho ở bước lập kế hoạch)

            rowsPanel.add(row);
        }

        JTextArea taCommonNote = new JTextArea(3, 40);
        JPanel container = new JPanel(new BorderLayout(6, 6));
        JScrollPane scroll = new JScrollPane(rowsPanel);
        scroll.setPreferredSize(new Dimension(600, Math.min(30 * selected.size(), 300)));
        container.add(scroll, BorderLayout.CENTER);

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createTitledBorder("Ghi chú chung (tùy chọn)"));
        notePanel.add(new JScrollPane(taCommonNote), BorderLayout.CENTER);
        container.add(notePanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(parent, container,
                "Tạo kế hoạch cho " + selected.size() + " yêu cầu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        String commonNote = taCommonNote.getText().trim();
        int created = 0;
        List<String> skipped = new ArrayList<>();

        try {
            for (int i = 0; i < selected.size(); i++) {
                MaintenanceRequest mr = selected.get(i);
                Date d = (Date) dateSpinners.get(i).getValue();
                LocalDate schedule = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (planDao.existsForEquipmentOnDate(mr.getEquipmentId(), schedule)) {
                    skipped.add("Req#" + mr.getId() + " (EQ" + mr.getEquipmentId() + ") on " + schedule);
                    continue;
                }

                MaintenanceTask task = new MaintenanceTask();
                task.setEquipmentId(mr.getEquipmentId());
                task.setScheduleDate(schedule);
                task.setCompleted(false);
                // assignment will be done in AssignPanel (create maintenance_records). Leave
                // assignedTo empty for now.
                task.setAssignedTo("");
                task.setNote(commonNote);
                task.setRequestId(mr.getId());
                task.setPlannerId(currentUser.getId());
                planDao.create(task);

                reqDao.updateStatus(mr.getId(), "DA_LAP_KE_HOACH");
                created++;
            }

            String msg = "Đã tạo " + created + " kế hoạch.";
            if (!skipped.isEmpty()) {
                msg += "\nBỏ qua " + skipped.size() + " mục do trùng lịch:\n" + String.join("\n", skipped);
            }
            JOptionPane.showMessageDialog(parent, msg);
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi khi tạo kế hoạch: " + ex.getMessage());
        }
    }
}
