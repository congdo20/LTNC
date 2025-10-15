package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.model.Item;

import java.sql.*;
// import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public Item create(Item it) throws SQLException {
        String sql = "INSERT INTO items(code, name, quantity, min_stock, maintenance_interval_days, last_maintenance) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, it.getCode());
            p.setString(2, it.getName());
            p.setInt(3, it.getQuantity());
            p.setInt(4, it.getMinStock());
            p.setInt(5, it.getMaintenanceIntervalDays());
            if (it.getLastMaintenance() == null)
                p.setNull(6, Types.DATE);
            else
                p.setDate(6, Date.valueOf(it.getLastMaintenance()));
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    it.setId(rs.getInt(1));
            }
            return it;
        }
    }

    public void update(Item it) throws SQLException {
        String sql = "UPDATE items SET name=?, quantity=?, min_stock=?, maintenance_interval_days=?, last_maintenance=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, it.getName());
            p.setInt(2, it.getQuantity());
            p.setInt(3, it.getMinStock());
            p.setInt(4, it.getMaintenanceIntervalDays());
            if (it.getLastMaintenance() == null)
                p.setNull(5, Types.DATE);
            else
                p.setDate(5, Date.valueOf(it.getLastMaintenance()));
            p.setInt(6, it.getId());
            p.executeUpdate();
        }
    }

    public Item findById(int id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        }
        return null;
    }

    public List<Item> findAll() throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY id";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        }
        return list;
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item it = new Item();
        it.setId(rs.getInt("id"));
        it.setCode(rs.getString("code"));
        it.setName(rs.getString("name"));
        it.setQuantity(rs.getInt("quantity"));
        it.setMinStock(rs.getInt("min_stock"));
        it.setMaintenanceIntervalDays(rs.getInt("maintenance_interval_days"));
        Date d = rs.getDate("last_maintenance");
        it.setLastMaintenance(d == null ? null : d.toLocalDate());
        return it;
    }
}
