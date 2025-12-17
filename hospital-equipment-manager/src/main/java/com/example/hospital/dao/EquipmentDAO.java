package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public Equipment create(Equipment e) throws SQLException {
        String sql = "INSERT INTO equipment (code, name, manufacturer, year_of_use, status, department_id, image_path) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            p.setString(1, e.getCode());
            p.setString(2, e.getName());
            p.setString(3, e.getManufacturer());
            if (e.getYearOfUse() == null)
                p.setNull(4, Types.INTEGER);
            else
                p.setInt(4, e.getYearOfUse());

            p.setString(5, e.getStatus());
            if (e.getDepartmentId() == null)
                p.setNull(6, Types.INTEGER);
            else
                p.setInt(6, e.getDepartmentId());
            p.setString(7, e.getImagePath());

            p.executeUpdate();

            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    e.setId(rs.getInt(1));
            }
        }
        return e;
    }

    public void update(Equipment e) throws SQLException {
        String sql = "UPDATE equipment SET code=?, name=?, manufacturer=?, year_of_use=?, status=?, department_id=?, image_path=?, last_maintenance=? WHERE id=?";

        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, e.getCode());
            p.setString(2, e.getName());
            p.setString(3, e.getManufacturer());
            if (e.getYearOfUse() == null)
                p.setNull(4, Types.INTEGER);
            else
                p.setInt(4, e.getYearOfUse());

            p.setString(5, e.getStatus());
            if (e.getDepartmentId() == null)
                p.setNull(6, Types.INTEGER);
            else
                p.setInt(6, e.getDepartmentId());

            p.setString(7, e.getImagePath());
            p.setObject(8, e.getLastMaintenance());
            p.setInt(9, e.getId());

            p.executeUpdate();
        }
    }

    public List<Equipment> findAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment ORDER BY id";

        try (Connection c = DBUtil.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Equipment e = new Equipment();
                e.setId(rs.getInt("id"));
                e.setCode(rs.getString("code"));
                e.setName(rs.getString("name"));
                e.setManufacturer(rs.getString("manufacturer"));

                int y = rs.getInt("year_of_use");
                e.setYearOfUse(rs.wasNull() ? null : y);

                e.setStatus(rs.getString("status"));
                int d = rs.getInt("department_id");
                e.setDepartmentId(rs.wasNull() ? null : d);
                e.setImagePath(rs.getString("image_path"));
                list.add(e);
            }
        }
        return list;
    }

    public Equipment findById(int id) throws SQLException {
        String sql = "SELECT * FROM equipment WHERE id=?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, id);

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Equipment e = new Equipment();
                    e.setId(rs.getInt("id"));
                    e.setCode(rs.getString("code"));
                    e.setName(rs.getString("name"));
                    e.setManufacturer(rs.getString("manufacturer"));

                    int y = rs.getInt("year_of_use");
                    e.setYearOfUse(rs.wasNull() ? null : y);

                    e.setStatus(rs.getString("status"));

                    int d = rs.getInt("department_id");
                    e.setDepartmentId(rs.wasNull() ? null : d);
                    e.setImagePath(rs.getString("image_path"));

                    return e;
                }
            }
        }
        return null;
    }

    public Equipment findByName(String name) throws SQLException {
        String sql = "SELECT * FROM equipment WHERE name = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }


    public List<Equipment> findByDepartment(Integer deptId) throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE department_id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, deptId);

            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private Equipment map(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setId(rs.getInt("id"));
        e.setCode(rs.getString("code"));
        e.setName(rs.getString("name"));
        e.setManufacturer(rs.getString("manufacturer"));
        e.setYearOfUse(rs.getInt("year_of_use"));
        e.setStatus(rs.getString("status"));
        e.setDepartmentId(rs.getInt("department_id"));
        e.setImagePath(rs.getString("image_path"));
        e.setLastMaintenance(rs.getDate("last_maintenance") == null ? null
                : rs.getDate("last_maintenance").toLocalDate());

        return e;
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }
}
