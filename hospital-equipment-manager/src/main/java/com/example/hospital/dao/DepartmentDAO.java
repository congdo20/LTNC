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
}
