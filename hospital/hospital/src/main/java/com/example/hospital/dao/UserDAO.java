package com.example.hospital.dao;

import com.example.hospital.db.DBConnection;
import com.example.hospital.models.User;
import java.sql.*;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT id, username, fullname, role FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fullname"),
                        rs.getString("role"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
