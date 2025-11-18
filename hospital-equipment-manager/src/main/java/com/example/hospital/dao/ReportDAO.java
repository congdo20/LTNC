package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportDAO() {
        try {
            ensureTableExists();
        } catch (SQLException e) {
            // ignore - callers will see errors when attempting to use DAO
            e.printStackTrace();
        }
    }

    public void create(int taskId, String reporter, String content) throws SQLException {
        ensureTableExists();
        String sql = "INSERT INTO reports(task_id, reporter, content, created_date) VALUES (?, ?, ?, NOW())";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, taskId);
            p.setString(2, reporter);
            p.setString(3, content);
            p.executeUpdate();
        }
    }

    public List<Report> findAll() throws SQLException {
        ensureTableExists();
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY created_date DESC";
        try (Connection c = DBUtil.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Report r = new Report();
                r.setId(rs.getInt("id"));
                r.setTaskId(rs.getInt("task_id"));
                r.setReporter(rs.getString("reporter"));
                r.setContent(rs.getString("content"));
                r.setCreatedDate(rs.getTimestamp("created_date"));
                list.add(r);
            }
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        ensureTableExists();
        String sql = "DELETE FROM reports WHERE id=?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    private void ensureTableExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS reports ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "task_id INT,"
                + "reporter VARCHAR(100),"
                + "content TEXT,"
                + "created_date DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }
}
