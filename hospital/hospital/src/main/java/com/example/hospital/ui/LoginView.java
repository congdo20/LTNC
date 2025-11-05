// package com.example.hospital.ui;

// import com.example.hospital.dao.UserDAO;
// import com.example.hospital.model.User;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.GridPane;
// import javafx.stage.Stage;

// public class LoginView {
//     private UserDAO userDAO = new UserDAO();

//     public void show(Stage stage) {
//         stage.setTitle("Đăng nhập hệ thống - Bảo trì thiết bị y tế");

//         GridPane g = new GridPane();
//         g.setPadding(new Insets(20));
//         g.setHgap(10);
//         g.setVgap(10);

//         Label lUser = new Label("Tên đăng nhập:");
//         TextField tfUser = new TextField();
//         Label lPass = new Label("Mật khẩu:");
//         PasswordField pf = new PasswordField();
//         Button btn = new Button("Đăng nhập");
//         Label msg = new Label();

//         g.add(lUser, 0, 0);
//         g.add(tfUser, 1, 0);
//         g.add(lPass, 0, 1);
//         g.add(pf, 1, 1);
//         g.add(btn, 1, 2);
//         g.add(msg, 1, 3);

//         btn.setOnAction(ev -> {
//             String u = tfUser.getText().trim();
//             String p = pf.getText();
//             User user = userDAO.findByUsername(u);
//             if (user != null && userDAO.verifyPassword(user, p)) {
//                 msg.setText("Đăng nhập thành công. Vai trò: " + user.getRole());
//                 // open main view
//                 MainView main = new MainView(user);
//                 try {
//                     main.show(stage); // replace scene
//                 } catch (Exception ex) {
//                     ex.printStackTrace();
//                 }
//             } else {
//                 msg.setText("Sai tên đăng nhập hoặc mật khẩu");
//             }
//         });

//         Scene s = new Scene(g, 420, 220);
//         stage.setScene(s);
//         stage.show();
//     }
// }
