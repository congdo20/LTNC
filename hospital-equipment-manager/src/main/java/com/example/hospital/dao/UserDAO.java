package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.User;
import com.example.hospital.models.User.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT id, username, fullname, role FROM users WHERE username=? AND password=?";

        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, username);
            p.setString(2, password);

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Role role = Role.valueOf(rs.getString("role"));
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("fullname"),
                            role);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
