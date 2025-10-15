// package com.example.hospital.ui;

// import com.example.hospital.dao.ItemDAO;
// import com.example.hospital.dao.MaintenanceDAO;
// import com.example.hospital.dao.TxDAO;
// import com.example.hospital.model.Item;
// import com.example.hospital.model.MaintenanceTask;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.awt.event.*;
// import java.sql.SQLException;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.concurrent.*;

// public class MainFrame extends JFrame {
//     private final ItemDAO itemDAO = new ItemDAO();
//     private final TxDAO txDAO = new TxDAO();
//     private final MaintenanceDAO maintDAO = new MaintenanceDAO();

//     private JTable itemsTable;
//     private DefaultTableModel itemsModel;

//     private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

//     public MainFrame() {
//         super("Hospital Equipment Manager");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(900, 600);
//         initUI();
//         loadItems();
//         startAutoMaintenanceChecker();
//     }

//     private void initUI() {
//         JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         JButton btnAdd = new JButton("Thêm thiết bị");
//         JButton btnStockIn = new JButton("Nhập kho");
//         JButton btnStockOut = new JButton("Xuất kho");
//         JButton btnRefresh = new JButton("Làm mới");
//         JButton btnViewMaint = new JButton("Công việc bảo trì");
//         JButton btnRunCheck = new JButton("Chạy kiểm tra bảo trì");

//         top.add(btnAdd);
//         top.add(btnStockIn);
//         top.add(btnStockOut);
//         top.add(btnRefresh);
//         top.add(btnViewMaint);
//         top.add(btnRunCheck);

//         itemsModel = new DefaultTableModel(
//                 new Object[] { "ID", "Code", "Name", "Qty", "MinStock", "MaintDays", "LastMaint" }, 0) {
//             public boolean isCellEditable(int row, int col) {
//                 return false;
//             }
//         };
//         itemsTable = new JTable(itemsModel);
//         JScrollPane scroll = new JScrollPane(itemsTable);

//         add(top, BorderLayout.NORTH);
//         add(scroll, BorderLayout.CENTER);

//         btnAdd.addActionListener(e -> onAdd());
//         btnStockIn.addActionListener(e -> onStockChange(true));
//         btnStockOut.addActionListener(e -> onStockChange(false));
//         btnRefresh.addActionListener(e -> loadItems());
//         btnViewMaint.addActionListener(e -> onViewMaint());
//         btnRunCheck.addActionListener(e -> runMaintenanceCheckNow());
//     }

//     private void onAdd() {
//         try {
//             String code = JOptionPane.showInputDialog(this, "Mã thiết bị (code):");
//             if (code == null || code.trim().isEmpty())
//                 return;
//             String name = JOptionPane.showInputDialog(this, "Tên thiết bị:");
//             if (name == null || name.trim().isEmpty())
//                 return;
//             int qty = Integer.parseInt(JOptionPane.showInputDialog(this, "Số lượng ban đầu (int):", "0"));
//             int min = Integer.parseInt(JOptionPane.showInputDialog(this, "Ngưỡng min stock (int):", "0"));
//             int maintDays = Integer.parseInt(JOptionPane.showInputDialog(this, "Khoảng ngày bảo trì (0=không):", "0"));

//             Item it = new Item();
//             it.setCode(code.trim());
//             it.setName(name.trim());
//             it.setQuantity(qty);
//             it.setMinStock(min);
//             it.setMaintenanceIntervalDays(maintDays);
//             itemDAO.create(it);
//             loadItems();
//         } catch (Exception ex) {
//             showError(ex);
//         }
//     }

//     private void onStockChange(boolean isIn) {
//         int row = itemsTable.getSelectedRow();
//         if (row < 0) {
//             JOptionPane.showMessageDialog(this, "Chọn 1 thiết bị trong bảng.");
//             return;
//         }
//         int id = (int) itemsModel.getValueAt(row, 0);
//         try {
//             Item it = itemDAO.findById(id);
//             if (it == null)
//                 return;
//             int delta = Integer.parseInt(JOptionPane.showInputDialog(this, (isIn ? "Nhập" : "Xuất") + " số lượng:"));
//             if (!isIn && delta > it.getQuantity()) {
//                 JOptionPane.showMessageDialog(this, "Không đủ tồn.");
//                 return;
//             }
//             it.setQuantity(isIn ? it.getQuantity() + delta : it.getQuantity() - delta);
//             itemDAO.update(it);
//             txDAO.create(it.getId(), isIn ? delta : -delta, isIn ? "Nhập kho" : "Xuất kho");
//             loadItems();

//             // auto-create maintenance if qty <= minStock
//             if (it.getQuantity() <= it.getMinStock()) {
//                 if (!maintDAO.existsPendingForItem(it.getId())) {
//                     MaintenanceTask mt = new MaintenanceTask();
//                     mt.setItemId(it.getId());
//                     mt.setScheduledDate(LocalDate.now());
//                     mt.setDone(false);
//                     mt.setNote("Auto-created because qty <= min_stock");
//                     maintDAO.create(mt);
//                     JOptionPane.showMessageDialog(this, "Đã tạo công việc bảo trì tự động do tồn <= minStock.");
//                 }
//             }
//         } catch (Exception ex) {
//             showError(ex);
//         }
//     }

//     private void loadItems() {
//         try {
//             itemsModel.setRowCount(0);
//             List<Item> list = itemDAO.findAll();
//             for (Item it : list) {
//                 itemsModel.addRow(new Object[] {
//                         it.getId(), it.getCode(), it.getName(), it.getQuantity(), it.getMinStock(),
//                         it.getMaintenanceIntervalDays(),
//                         it.getLastMaintenance() == null ? "" : it.getLastMaintenance().toString()
//                 });
//             }
//         } catch (SQLException e) {
//             showError(e);
//         }
//     }

//     private void onViewMaint() {
//         try {
//             java.util.List<MaintenanceTask> list = maintDAO.findPending();
//             StringBuilder sb = new StringBuilder();
//             for (MaintenanceTask m : list) {
//                 Item it = itemDAO.findById(m.getItemId());
//                 sb.append("Task ID: ").append(m.getId()).append(" | Item: ")
//                         .append(it == null ? m.getItemId() : it.getName())
//                         .append(" | Scheduled: ").append(m.getScheduledDate()).append(" | Note: ").append(m.getNote())
//                         .append("\n");
//             }
//             if (sb.length() == 0)
//                 sb.append("Không có công việc bảo trì đang chờ.");
//             JTextArea area = new JTextArea(sb.toString());
//             area.setEditable(false);
//             int res = JOptionPane.showConfirmDialog(this, new JScrollPane(area), "Công việc bảo trì",
//                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
//             // optionally, allow marking first task done
//             if (res == JOptionPane.OK_OPTION) {
//                 String idS = JOptionPane.showInputDialog(this,
//                         "Nhập ID công việc để đánh dấu hoàn thành (blank=không):");
//                 if (idS != null && !idS.trim().isEmpty()) {
//                     int id = Integer.parseInt(idS.trim());
//                     maintDAO.markDone(id, LocalDate.now());
//                     // update last_maintenance on item
//                     // fetch task to know item
//                     // For brevity, reload all and update lastMaintenance to today for affected item
//                     // (Better to return itemId from markDone, but do second query)
//                     JOptionPane.showMessageDialog(this, "Đã đánh dấu hoàn thành.");
//                     loadItems();
//                 }
//             }
//         } catch (Exception ex) {
//             showError(ex);
//         }
//     }

//     private void runMaintenanceCheckNow() {
//         // run immediate check in UI thread
//         try {
//             int created = doMaintenanceCheck();
//             JOptionPane.showMessageDialog(this, "Kiểm tra bảo trì hoàn tất. Tạo mới " + created + " task(s).");
//         } catch (Exception e) {
//             showError(e);
//         }
//     }

//     // Check items and create tasks per interval (returns number created)
//     private int doMaintenanceCheck() throws SQLException {
//         int created = 0;
//         List<Item> items = itemDAO.findAll();
//         for (Item it : items) {
//             if (it.getMaintenanceIntervalDays() > 0) {
//                 LocalDate due;
//                 if (it.getLastMaintenance() == null)
//                     due = LocalDate.now();
//                 else
//                     due = it.getLastMaintenance().plusDays(it.getMaintenanceIntervalDays());
//                 if (!due.isAfter(LocalDate.now())) {
//                     if (!maintDAO.existsPendingForItem(it.getId())) {
//                         MaintenanceTask mt = new MaintenanceTask();
//                         mt.setItemId(it.getId());
//                         mt.setScheduledDate(LocalDate.now());
//                         mt.setDone(false);
//                         mt.setNote("Auto scheduled by interval");
//                         maintDAO.create(mt);
//                         created++;
//                     }
//                 }
//             }
//         }
//         return created;
//     }

//     private void startAutoMaintenanceChecker() {
//         // chạy mỗi 24h (thực tế có thể set nhỏ hơn để test)
//         scheduler.scheduleAtFixedRate(() -> {
//             try {
//                 int created = doMaintenanceCheck();
//                 if (created > 0) {
//                     System.out.println("Auto maintenance created: " + created);
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }, 0, 24, TimeUnit.HOURS);
//     }

//     private void showError(Exception ex) {
//         ex.printStackTrace();
//         JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//     }

//     public void shutdown() {
//         scheduler.shutdownNow();
//     }
// }


package com.example.hospital.ui;

import com.example.hospital.model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Hệ thống quản lý thiết bị y tế - "
                + (user.getFullname() == null ? user.getUsername() : user.getFullname()));
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Thiết bị", new EquipmentPanel());
        tabs.addTab("Bảo trì", new MaintenancePanel());

        add(tabs, BorderLayout.CENTER);
    }
}