package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.InspectionTask;

import java.sql.*;
//import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InspectionDAO {

    public void create(InspectionTask t) throws SQLException {
        String checkSql = "SELECT completed, equipment_id FROM maintenance WHERE id = ?";
        String insertSql = "INSERT INTO inspection(maintenance_id, equipment_id, inspection_date, inspector, result, note, accepted_by, acceptance_date) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection c = DBUtil.getConnection()) {
            // Kiểm tra bảo trì có hoàn tất chưa và lấy equipment_id tương ứng
            int equipmentId;
            try (PreparedStatement chk = c.prepareStatement(checkSql)) {
                chk.setInt(1, t.getMaintenanceId());
                try (ResultSet rs = chk.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("completed")) {
                        throw new SQLException("Không thể kiểm tra. Bảo trì chưa hoàn thành.");
                    }
                    equipmentId = rs.getInt("equipment_id");
                }
            }

            // Thêm bản ghi kiểm tra
            try (PreparedStatement p = c.prepareStatement(insertSql)) {
                p.setInt(1, t.getMaintenanceId());
                p.setInt(2, equipmentId);
                p.setDate(3, Date.valueOf(t.getInspectionDate()));
                p.setString(4, t.getInspector());
                p.setBoolean(5, t.isResult());
                p.setString(6, t.getNote());
                p.setString(7, t.getAcceptedBy());
                if (t.getAcceptanceDate() != null)
                    p.setDate(8, Date.valueOf(t.getAcceptanceDate()));
                else
                    p.setNull(8, Types.DATE);
                p.executeUpdate();
            }
        }
    }

    public List<InspectionTask> findAll() throws SQLException {
        List<InspectionTask> list = new ArrayList<>();
        String sql = "SELECT * FROM inspection ORDER BY inspection_date DESC";
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                InspectionTask t = new InspectionTask();
                t.setId(rs.getInt("id"));
                t.setMaintenanceId(rs.getInt("maintenance_id"));
                t.setEquipmentId(rs.getInt("equipment_id"));
                t.setInspectionDate(rs.getDate("inspection_date").toLocalDate());
                t.setInspector(rs.getString("inspector"));
                t.setResult(rs.getBoolean("result"));
                t.setNote(rs.getString("note"));
                t.setAcceptedBy(rs.getString("accepted_by"));
                Date acc = rs.getDate("acceptance_date");
                t.setAcceptanceDate(acc == null ? null : acc.toLocalDate());
                list.add(t);
            }
        }
        return list;
    }
}
