module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example to javafx.fxml;
    exports com.example;
}

// module com.example.hospital {
//     requires javafx.controls;
//     requires javafx.fxml;
//     requires java.sql;
//     requires mysql.connector.java;

//     opens com.example.hospital to javafx.fxml;
//     exports com.example.hospital;
//     exports com.example.hospital.ui;
//     opens com.example.hospital.ui to javafx.fxml;
//     exports com.example.hospital.dao;
//     opens com.example.hospital.dao to javafx.fxml;
//     exports com.example.hospital.model;
//     opens com.example.hospital.model to javafx.fxml;
//     exports com.example.hospital.db;
//     opens com.example.hospital.db to javafx.fxml;
// }
