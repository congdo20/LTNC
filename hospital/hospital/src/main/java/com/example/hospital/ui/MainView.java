// package com.example.hospital.ui;

// import com.example.hospital.model.User;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.BorderPane;
// import javafx.stage.Stage;

// public class MainView {
//     private User currentUser;

//     public MainView(User user) {
//         this.currentUser = user;
//     }

//     public void show(Stage stage) {
//         BorderPane root = new BorderPane();
//         TabPane tabs = new TabPane();

//         // All users see Dashboard
//         Tab tDashboard = new Tab("Dashboard", new Label("Xin chào " + currentUser.getFullname()));
//         tDashboard.setClosable(false);
//         tabs.getTabs().add(tDashboard);

//         // Equipment tab (Manager, Admin, Dept Head)
//         if (hasRole("ADMIN", "MANAGER", "DEPARTMENT_HEAD")) {
//             EquipmentPane ep = new EquipmentPane();
//             Tab tEq = new Tab("Thiết bị", ep.getPane());
//             tEq.setClosable(false);
//             tabs.getTabs().add(tEq);
//         }

//         // Maintenance Request tab (All roles can create requests; some can manage)
//         MaintenanceRequestPane mrp = new MaintenanceRequestPane(currentUser);
//         Tab tReq = new Tab("Yêu cầu bảo trì", mrp.getPane());
//         tReq.setClosable(false);
//         tabs.getTabs().add(tReq);

//         // Plans tab (Manager, Admin)
//         if (hasRole("ADMIN", "MANAGER")) {
//             MaintenancePlanPane planPane = new MaintenancePlanPane();
//             Tab tPlan = new Tab("Kế hoạch", planPane.getPane());
//             tPlan.setClosable(false);
//             tabs.getTabs().add(tPlan);
//         }

//         // Records tab (Technician, Manager)
//         if (hasRole("TECHNICIAN", "MANAGER", "ADMIN")) {
//             MaintenanceRecordPane recPane = new MaintenanceRecordPane(currentUser);
//             Tab tRec = new Tab("Thực hiện", recPane.getPane());
//             tRec.setClosable(false);
//             tabs.getTabs().add(tRec);
//         }

//         // Users management (Admin only)
//         if (hasRole("ADMIN")) {
//             UserManagementPane up = new UserManagementPane();
//             Tab tUsers = new Tab("Quản lý người dùng", up.getPane());
//             tUsers.setClosable(false);
//             tabs.getTabs().add(tUsers);
//         }

//         root.setCenter(tabs);
//         Scene s = new Scene(root, 1100, 700);
//         stage.setScene(s);
//         stage.setTitle("Hệ thống quản lý bảo trì thiết bị y tế - " + currentUser.getRole());
//         stage.show();
//     }

//     private boolean hasRole(String... roles) {
//         for (String r : roles)
//             if (r.equalsIgnoreCase(currentUser.getRole()))
//                 return true;
//         return false;
//     }
// }
