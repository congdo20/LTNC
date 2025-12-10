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
import com.example.hospital.models.MaintenanceTask;
import com.example.hospital.models.User;

import java.sql.*;
// import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO {
    public MaintenanceTask create(MaintenanceTask m) throws SQLException {
        String sql = "INSERT INTO maintenance_plans(request_id, equipment_id, plan_date, planner_id, scheduled_start, scheduled_end, note) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (m.getRequestId() == null) {
                p.setNull(1, java.sql.Types.INTEGER);
            } else {
                p.setInt(1, m.getRequestId());
            }
            p.setInt(2, m.getEquipmentId());
            p.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // plan_date
            if (m.getPlannerId() == null)
                p.setNull(4, java.sql.Types.INTEGER);
            else
                p.setInt(4, m.getPlannerId());
            if (m.getScheduleDate() == null)
                p.setNull(5, java.sql.Types.DATE);
            else
                p.setDate(5, Date.valueOf(m.getScheduleDate()));
            // scheduled_end use same as start for now
            if (m.getScheduleDate() == null)
                p.setNull(6, java.sql.Types.DATE);
            else
                p.setDate(6, Date.valueOf(m.getScheduleDate()));
            p.setString(7, m.getNote() == null ? "" : m.getNote());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    m.setId(rs.getInt(1));
            }
            return m;
        }
    }

    public void update(MaintenanceTask m) throws SQLException {
        String sql = "UPDATE maintenance_plans SET scheduled_start = ?, scheduled_end = ?, note = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            if (m.getScheduleDate() == null)
                p.setNull(1, java.sql.Types.DATE);
            else
                p.setDate(1, Date.valueOf(m.getScheduleDate()));
            if (m.getScheduleDate() == null)
                p.setNull(2, java.sql.Types.DATE);
            else
                p.setDate(2, Date.valueOf(m.getScheduleDate()));
            p.setString(3, m.getNote() == null ? "" : m.getNote());
            p.setInt(4, m.getId());
            p.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM maintenance_plans WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public List<MaintenanceTask> findAll() throws SQLException {
        List<MaintenanceTask> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_plans ORDER BY scheduled_start DESC";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                MaintenanceTask m = new MaintenanceTask();
                m.setId(rs.getInt("id"));
                m.setEquipmentId(rs.getInt("equipment_id"));
                Date d = rs.getDate("scheduled_start");
                m.setScheduleDate(d == null ? null : d.toLocalDate());
                String status = null;
                try {
                    status = rs.getString("status");
                } catch (Exception ex) {
                }
                m.setCompleted("HOAN_THANH".equals(status));
                m.setNote(rs.getString("note"));
                // assignedTo not stored here; leave empty
                m.setAssignedTo("");
                try {
                    m.setRequestId(rs.getInt("request_id"));
                } catch (Exception ex) {
                }
                try {
                    m.setPlannerId(rs.getInt("planner_id"));
                } catch (Exception ex) {
                }
                list.add(m);
            }
        }
        return list;
    }

    /**
     * Find maintenance plans that have been assigned to a specific technician.
     */
    public List<MaintenanceTask> findPlansAssignedToTechnician(int technicianId) throws SQLException {
        List<MaintenanceTask> list = new ArrayList<>();
        String sql = "SELECT p.* FROM maintenance_plans p JOIN maintenance_records r ON p.id = r.plan_id WHERE r.technician_id = ? ORDER BY p.scheduled_start DESC";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, technicianId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    MaintenanceTask m = new MaintenanceTask();
                    m.setId(rs.getInt("id"));
                    m.setEquipmentId(rs.getInt("equipment_id"));
                    Date d = rs.getDate("scheduled_start");
                    m.setScheduleDate(d == null ? null : d.toLocalDate());
                    String status = null;
                    try {
                        status = rs.getString("status");
                    } catch (Exception ex) {
                    }
                    m.setCompleted("HOAN_THANH".equals(status));
                    m.setNote(rs.getString("note"));
                    try {
                        m.setRequestId(rs.getInt("request_id"));
                    } catch (Exception ex) {
                    }
                    try {
                        m.setPlannerId(rs.getInt("planner_id"));
                    } catch (Exception ex) {
                    }
                    list.add(m);
                }
            }
        }
        return list;
    }

    /**
     * Find maintenance plans that are waiting for approval (status =
     * 'CHO_NGHIEM_THU')
     * Used by equipment managers to see tasks pending their review
     */
    public List<MaintenanceTask> findPlansWaitingForApproval() throws SQLException {
        List<MaintenanceTask> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_plans WHERE status = 'CHO_NGHIEM_THU' ORDER BY scheduled_start DESC";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                MaintenanceTask m = new MaintenanceTask();
                m.setId(rs.getInt("id"));
                m.setEquipmentId(rs.getInt("equipment_id"));
                Date d = rs.getDate("scheduled_start");
                m.setScheduleDate(d == null ? null : d.toLocalDate());
                String status = null;
                try {
                    status = rs.getString("status");
                } catch (Exception ex) {
                }
                m.setCompleted("HOAN_THANH".equals(status));
                m.setNote(rs.getString("note"));
                // assignedTo not stored here; leave empty
                m.setAssignedTo("");
                try {
                    m.setRequestId(rs.getInt("request_id"));
                } catch (Exception ex) {
                }
                try {
                    m.setPlannerId(rs.getInt("planner_id"));
                } catch (Exception ex) {
                }
                list.add(m);
            }
        }
        return list;
    }

    public void accept(int id, String acceptedBy, String note, String assignedTo) throws SQLException {
        String sql = "UPDATE maintenance_plans SET status = 'DANG_THUC_HIEN', note = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, note == null ? "" : note);
            p.setInt(2, id);
            p.executeUpdate();
        }
    }

    public void inspect(int id, String note) throws SQLException {
        String sql = "UPDATE maintenance_plans SET note = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, note == null ? "" : note);
            p.setInt(2, id);
            p.executeUpdate();
        }
    }

    /**
     * Check if there is already a non-completed maintenance scheduled for the
     * equipment on the given date.
     */
    public boolean existsForEquipmentOnDate(int equipmentId, java.time.LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM maintenance_plans WHERE equipment_id = ? AND scheduled_start = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, equipmentId);
            p.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Assigns a plan to a technician by creating a maintenance_records row and
     * updating the plan status to DANG_THUC_HIEN.
     */
    public void assignPlan(int planId, int technicianId) throws SQLException {
        String insertSql = "INSERT INTO maintenance_records(plan_id, technician_id) VALUES(?,?)";
        String updatePlanSql = "UPDATE maintenance_plans SET status = 'DANG_THUC_HIEN' WHERE id = ?";
        try (Connection c = DBUtil.getConnection()) {
            try (PreparedStatement p = c.prepareStatement(insertSql)) {
                p.setInt(1, planId);
                p.setInt(2, technicianId);
                p.executeUpdate();
            }
            try (PreparedStatement p2 = c.prepareStatement(updatePlanSql)) {
                p2.setInt(1, planId);
                p2.executeUpdate();
            }
        }
    }

    /**
     * Return the technician_id assigned for a given plan, or null if none.
     */
    public Integer getAssignedTechnicianId(int planId) throws SQLException {
        String sql = "SELECT technician_id FROM maintenance_records WHERE plan_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, planId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return rs.wasNull() ? null : id;
                }
            }
        }
        return null;
    }

    /**
     * Get the status code for a plan
     */
    public String getPlanStatus(int planId) throws SQLException {
        String sql = "SELECT status FROM maintenance_plans WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, planId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    /**
     * Mark a plan completed, update its status and notify the requester of the
     * linked maintenance request.
     */
    public void markPlanCompleted(int planId) throws SQLException {
        // 1) update plan status
        String upd = "UPDATE maintenance_plans SET status = 'HOAN_THANH' WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(upd)) {
            p.setInt(1, planId);
            p.executeUpdate();
        }

        // 2) find request_id for the plan
        Integer requestId = null;
        String q = "SELECT request_id FROM maintenance_plans WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
            p.setInt(1, planId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    int rid = rs.getInt(1);
                    requestId = rs.wasNull() ? null : rid;
                }
            }
        }

        if (requestId == null)
            return; // nothing to notify

        // 3) find requester id from maintenance_requests
        Integer requesterId = null;
        String q2 = "SELECT requester_id FROM maintenance_requests WHERE id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(q2)) {
            p.setInt(1, requestId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    int uid = rs.getInt(1);
                    requesterId = rs.wasNull() ? null : uid;
                }
            }
        }

        if (requesterId == null)
            return;

        // 4) create notification
        com.example.hospital.dao.NotificationDAO nd = new com.example.hospital.dao.NotificationDAO();
        String message = "Yêu cầu bảo trì (ID=" + requestId + ") của bạn đã được hoàn thành.";
        nd.createNotification(requesterId, message, requestId);
    }

    // In MaintenanceDAO.java

    /**
     * Mark a plan as pending approval (when technician completes the task)
     */
    public void markPlanPendingApproval(int planId) throws SQLException {
        // 1) update plan status to pending approval
        String upd = "UPDATE maintenance_plans SET status = 'CHO_NGHIEM_THU' WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(upd)) {
            p.setInt(1, planId);
            p.executeUpdate();
        }

        // 2) find request_id and department_id for the plan
        Integer requestId = null;
        Integer departmentId = null;
        String q = "SELECT r.id, r.department_id FROM maintenance_requests r " +
                "JOIN maintenance_plans p ON r.id = p.request_id WHERE p.id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(q)) {
            p.setInt(1, planId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    requestId = rs.getInt(1);
                    departmentId = rs.getInt(2);
                    if (rs.wasNull())
                        departmentId = null;
                }
            }
        }

        if (requestId == null)
            return;

        // 3) find equipment managers (QL_THIET_BI) to notify
        UserDAO userDao = new UserDAO();
        NotificationDAO nd = new NotificationDAO();
        String message = "Yêu cầu bảo trì (ID=" + requestId + ") đang chờ nghiệm thu.";

        // Notify all equipment managers
        List<User> qlThietBiUsers = userDao.findByRole(User.Role.QL_THIET_BI);
        for (User user : qlThietBiUsers) {
            nd.createNotification(user.getId(), message, requestId);
        }
    }

    /**
     * Approve a completed maintenance plan
     */
    public void approvePlan(int planId) throws SQLException {
        // 1) update plan status to completed
        String upd = "UPDATE maintenance_plans SET status = 'HOAN_THANH' WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(upd)) {
            p.setInt(1, planId);
            p.executeUpdate();
        }

        // 2) find request_id, department_id, and requester_id for the plan
        Integer requestId = null;
        Integer departmentId = null;
        Integer requesterId = null;
        String q = "SELECT r.id, r.department_id, r.requester_id FROM maintenance_requests r " +
                "JOIN maintenance_plans p ON r.id = p.request_id WHERE p.id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(q)) {
            p.setInt(1, planId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    requestId = rs.getInt(1);
                    departmentId = rs.getInt(2);
                    requesterId = rs.getInt(3);
                    if (rs.wasNull())
                        departmentId = null;
                }
            }
        }

        if (requestId == null || departmentId == null)
            return;

        // 3) update maintenance request status
        String updateRequest = "UPDATE maintenance_requests SET status = 'DA_LAP_KE_HOACH' WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(updateRequest)) {
            p.setInt(1, requestId);
            p.executeUpdate();
        }

        // 4) notify the requester (person who created the request)
        NotificationDAO nd = new NotificationDAO();
        if (requesterId != null) {
            String requesterMessage = "Yêu cầu bảo trì (ID=" + requestId + ") đã được nghiệm thu và hoàn thành.";
            nd.createNotification(requesterId, requesterMessage, requestId);
        }

        // 5) optionally also notify department head (TRUONG_KHOA)
        UserDAO userDao = new UserDAO();
        List<User> truongKhoaUsers = userDao.findByDepartmentAndRole(departmentId, User.Role.TRUONG_KHOA);

        // 6) notify department head
        String deptHeadMessage = "Yêu cầu bảo trì (ID=" + requestId + ") đã được nghiệm thu và hoàn thành.";
        for (User user : truongKhoaUsers) {
            nd.createNotification(user.getId(), deptHeadMessage, requestId);
        }
    }
}