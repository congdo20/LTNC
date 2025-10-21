// package com.example.hospital.dao;

// import com.example.hospital.db.DBUtil;
// import com.example.hospital.model.MaintenanceTask;

// import java.sql.*;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;

// public class MaintenanceDAO {
//     public void create(MaintenanceTask m) throws SQLException {
//         String sql = "INSERT INTO maintenance_tasks(item_id, scheduled_date, done, note) VALUES(?,?,?,?)";
//         try (Connection c = DBUtil.getConnection();
//                 PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//             p.setInt(1, m.getItemId());
//             p.setDate(2, Date.valueOf(m.getScheduledDate()));
//             p.setBoolean(3, m.isDone());
//             p.setString(4, m.getNote());
//             p.executeUpdate();
//             try (ResultSet rs = p.getGeneratedKeys()) {
//                 if (rs.next())
//                     m.setId(rs.getInt(1));
//             }
//         }
//     }

//     public List<MaintenanceTask> findPending() throws SQLException {
//         List<MaintenanceTask> list = new ArrayList<>();
//         String sql = "SELECT * FROM maintenance_tasks WHERE done = FALSE ORDER BY scheduled_date";
//         try (Connection c = DBUtil.getConnection();
//                 Statement s = c.createStatement();
//                 ResultSet rs = s.executeQuery(sql)) {
//             while (rs.next()) {
//                 MaintenanceTask m = new MaintenanceTask();
//                 m.setId(rs.getInt("id"));
//                 m.setItemId(rs.getInt("item_id"));
//                 m.setScheduledDate(rs.getDate("scheduled_date").toLocalDate());
//                 m.setDone(rs.getBoolean("done"));
//                 m.setNote(rs.getString("note"));
//                 list.add(m);
//             }
//         }
//         return list;
//     }

//     public void markDone(int id, LocalDate doneDate) throws SQLException {
//         String sql = "UPDATE maintenance_tasks SET done = TRUE, done_date = ? WHERE id = ?";
//         try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
//             p.setDate(1, Date.valueOf(doneDate));
//             p.setInt(2, id);
//             p.executeUpdate();
//         }
//     }

//     public boolean existsPendingForItem(int itemId) throws SQLException {
//         String sql = "SELECT COUNT(*) FROM maintenance_tasks WHERE item_id = ? AND done = FALSE";
//         try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
//             p.setInt(1, itemId);
//             try (ResultSet rs = p.executeQuery()) {
//                 if (rs.next())
//                     return rs.getInt(1) > 0;
//             }
//         }
//         return false;
//     }
// }

package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.model.MaintenanceTask;

import java.sql.*;
// import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO {
    public MaintenanceTask create(MaintenanceTask m) throws SQLException {
        String sql = "INSERT INTO maintenance(equipment_id, schedule_date, completed, note) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, m.getEquipmentId());
            p.setDate(2, Date.valueOf(m.getScheduleDate()));
            p.setBoolean(3, m.isCompleted());
            p.setString(4, m.getNote());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    m.setId(rs.getInt(1));
            }
            return m;
        }
    }

    public void update(MaintenanceTask m) throws SQLException {
        String sql = "UPDATE maintenance SET schedule_date=?, completed=?, note=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setDate(1, Date.valueOf(m.getScheduleDate()));
            p.setBoolean(2, m.isCompleted());
            p.setString(3, m.getNote());
            p.setInt(4, m.getId());
            p.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM maintenance WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public List<MaintenanceTask> findAll() throws SQLException {
        List<MaintenanceTask> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance ORDER BY schedule_date DESC";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                MaintenanceTask m = new MaintenanceTask();
                m.setId(rs.getInt("id"));
                m.setEquipmentId(rs.getInt("equipment_id"));
                Date d = rs.getDate("schedule_date");
                m.setScheduleDate(d == null ? null : d.toLocalDate());
                m.setCompleted(rs.getBoolean("completed"));
                m.setNote(rs.getString("note"));
                try {
                    m.setAssignedTo(rs.getString("assigned_to"));
                } catch (Exception ex) {
                    // column might not exist in older schemas
                }
                list.add(m);
            }
        }
        return list;
    }
}