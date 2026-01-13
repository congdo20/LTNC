package com.example.hospital.dao;

import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.db.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRequestDAO {

    // ✅ MAP RESULTSET → OBJECT
    private MaintenanceRequest map(ResultSet rs) throws SQLException {
        MaintenanceRequest r = new MaintenanceRequest();

        r.setId(rs.getInt("id"));
        r.setRequesterId(rs.getInt("requester_id"));
        r.setEquipmentId(rs.getInt("equipment_id"));
        r.setIssueDescription(rs.getString("issue_description"));
        // r.setRequestDate(rs.getTimestamp("request_date"));
        r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
        r.setStatus(rs.getString("status"));

        int dep = rs.getInt("department_id");
        r.setDepartmentId(rs.wasNull() ? 0 : dep);

        return r;
    }

    // ✅ lấy theo khoa
    public List<MaintenanceRequest> findByDepartment(int depId) throws SQLException {
        List<MaintenanceRequest> list = new ArrayList<>();

        String sql = "SELECT * FROM maintenance_requests WHERE department_id = ? ORDER BY request_date DESC";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, depId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs)); // ✅
                }
            }
        }
        return list;
    }

    // ✅ QL thiết bị — lấy tất cả
    public List<MaintenanceRequest> findAll() throws SQLException {
        List<MaintenanceRequest> list = new ArrayList<>();

        String sql = "SELECT * FROM maintenance_requests ORDER BY request_date DESC";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs)); // ✅
            }
        }
        return list;
    }

    // ✅ insert
    public void create(MaintenanceRequest req) throws SQLException {
        // Columns match schema: requester_id, department_id, equipment_id,
        // issue_description, request_date, status
        String sql = "INSERT INTO maintenance_requests(requester_id, department_id, equipment_id, issue_description, request_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // DEBUG: log SQL and parameter values to diagnose issues
            System.out.println("[DEBUG] MaintenanceRequestDAO.create SQL: " + sql);
            System.out.println("[DEBUG] requesterId=" + req.getRequesterId() + ", equipmentId=" + req.getEquipmentId()
                    + ", departmentId=" + req.getDepartmentId());

            int idx = 1;
            ps.setInt(idx++, req.getRequesterId());
            // department_id is required in schema; setInt directly
            ps.setInt(idx++, req.getDepartmentId());
            ps.setInt(idx++, req.getEquipmentId());
            ps.setString(idx++, req.getIssueDescription());
            ps.setTimestamp(idx++, Timestamp.valueOf(req.getRequestDate()));
            ps.setString(idx++, req.getStatus());

            ps.executeUpdate();
        }
    }

    // update status by id
    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE maintenance_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // lấy theo thiết bị
    public List<MaintenanceRequest> findByEquipment(int equipmentId) throws SQLException {
        List<MaintenanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_requests WHERE equipment_id = ? ORDER BY request_date DESC";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /**
     * Get the latest plan status for a given request (null if no plan exists)
     */
    public String getLatestPlanStatus(int requestId) throws SQLException {
        String sql = "SELECT status FROM maintenance_plans WHERE request_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }
}
