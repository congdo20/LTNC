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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User login(String username, String password) {
        // include role in select so we can load user's role from DB
        String sql = "SELECT id, username, fullname, role FROM users WHERE username=? AND password=?";
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
                    if (role == null) {
                        // if role column missing or NULL, default to regular user to avoid elevating
                        // privileges
                        role = "USER";
                        // debug: print loaded role for troubleshooting
                        System.out.println("[UserDAO] login succeeded: id=" + rs.getInt("id") + ", username="
                                + rs.getString("username") + ", role=" + role);
                    }
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("fullname"), role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User create(User u, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password, fullname, role) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, u.getUsername());
            p.setString(2, password == null ? "" : password);
            p.setString(3, u.getFullname());
            p.setString(4, u.getRole());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    u.setId(rs.getInt(1));
            }
            return u;
        }
    }

    public void update(User u, String password) throws SQLException {
        String sql = "UPDATE users SET username=?, fullname=?, role=?"
                + (password != null && !password.isEmpty() ? ", password=?" : "") + " WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            int idx = 1;
            p.setString(idx++, u.getUsername());
            p.setString(idx++, u.getFullname());
            p.setString(idx++, u.getRole());
            if (password != null && !password.isEmpty())
                p.setString(idx++, password);
            p.setInt(idx, u.getId());
            p.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, fullname, role FROM users ORDER BY id";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("fullname"),
                        rs.getString("role")));
            }
        }
        return list;
    }
}