package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class NotificationDAO {

    /** Ensure notifications table exists and insert a notification. */
    public void createNotification(int userId, String message, Integer relatedRequestId) throws SQLException {
        // create table if not exists (safe operation)
        String createSql = "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "related_request_id INT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "is_read TINYINT(1) DEFAULT 0" +
                ")";

        String insertSql = "INSERT INTO notifications(user_id, message, related_request_id, created_at, is_read) VALUES(?, ?, ?, ?, 0)";

        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            s.execute(createSql);
        }

        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(insertSql)) {
            p.setInt(1, userId);
            p.setString(2, message == null ? "" : message);
            if (relatedRequestId == null)
                p.setNull(3, java.sql.Types.INTEGER);
            else
                p.setInt(3, relatedRequestId);
            p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            p.executeUpdate();
        }
    }

    /** Optional helper to fetch unread count (not used now) */
    public int countUnreadForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Find notifications for a user, newest first */
    public java.util.List<com.example.hospital.models.Notification> findForUser(int userId) throws SQLException {
        java.util.List<com.example.hospital.models.Notification> list = new java.util.ArrayList<>();
        String sql = "SELECT id, user_id, message, related_request_id, created_at, is_read FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    com.example.hospital.models.Notification n = new com.example.hospital.models.Notification();
                    n.setId(rs.getInt("id"));
                    n.setUserId(rs.getInt("user_id"));
                    n.setMessage(rs.getString("message"));
                    int rid = rs.getInt("related_request_id");
                    n.setRelatedRequestId(rs.wasNull() ? null : rid);
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null)
                        n.setCreatedAt(ts.toLocalDateTime());
                    n.setRead(rs.getInt("is_read") != 0);
                    list.add(n);
                }
            }
        }
        return list;
    }

    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, notificationId);
            p.executeUpdate();
        }
    }
}
