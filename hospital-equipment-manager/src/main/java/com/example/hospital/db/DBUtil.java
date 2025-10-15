// package com.example.hospital.db;

// import java.sql.*;

// public class DBUtil {
//     private static final String URL = "jdbc:mysql://localhost:3306/hospital_equipment?useSSL=false&serverTimezone=UTC";
//     private static final String USER = "root";
//     private static final String PASS = "Do200102!"; // <- sửa ở đây

//     static {
//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//         } catch (ClassNotFoundException e) {
//             throw new RuntimeException("MySQL driver not found", e);
//         }
//     }

//     public static Connection getConnection() throws SQLException {
//         return DriverManager.getConnection(URL, USER, PASS);
//     }
// }

package com.example.hospital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Allow overriding via environment variables for safer local config
    private static final String URL = System.getenv().getOrDefault("DB_URL",
            "jdbc:mysql://localhost:3306/hospital_equipment?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "Do200102!"); // <-- change this or set
                                                                                             // DB_PASS

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}