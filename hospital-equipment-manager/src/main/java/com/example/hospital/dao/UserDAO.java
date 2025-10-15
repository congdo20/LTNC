// package com.example.hospital.dao;

// import com.example.hospital.db.DBUtil;
// import com.example.hospital.model.User;
// import java.sql.*;

// public class UserDAO {
//     public User login(String username, String password) {
//         try (Connection conn = DBUtil.getConnection()) {
//             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
//             ps.setString(1, username);
//             ps.setString(2, password);
//             ResultSet rs = ps.executeQuery();
//             if (rs.next()) {
//                 return new User(rs.getInt("id"), rs.getString("username"), rs.getString("fullname"));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }
// }

package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public User login(String username, String password) {
        String sql = "SELECT id, username, fullname FROM users WHERE username=? AND password=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, password);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    String role = null;
                    try {
                        role = rs.getString("role");
                    } catch (Exception ex) {
                        // ignore if column missing
                    }
                    if (role == null && "admin".equalsIgnoreCase(rs.getString("username")))
                        role = "ADMIN";
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("fullname"), role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}