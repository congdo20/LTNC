package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DepartmentDAO {

    // Simple cache to avoid repeated DB lookups during table rendering
    private final Map<Integer, String> cache = new HashMap<>();

    public String findNameById(Integer id) throws SQLException {
        if (id == null)
            return null;
        if (cache.containsKey(id))
            return cache.get(id);

        String sql = "SELECT name FROM departments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    cache.put(id, name);
                    return name;
                }
            }
        }
        return null;
    }

    public java.util.Map<Integer, String> findAll() throws SQLException {
        java.util.Map<Integer, String> map = new java.util.HashMap<>();
        String sql = "SELECT id, name FROM departments ORDER BY name";
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    map.put(id, name);
                    cache.put(id, name);
                }
            }
        }
        return map;
    }
}
