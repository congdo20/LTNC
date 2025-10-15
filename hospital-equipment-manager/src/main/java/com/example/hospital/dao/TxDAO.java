package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.model.Tx;

import java.sql.*;
// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TxDAO {
    public void create(int itemId, int changeQty, String note) throws SQLException {
        String sql = "INSERT INTO transactions(item_id, change_qty, note) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, itemId);
            p.setInt(2, changeQty);
            p.setString(3, note);
            p.executeUpdate();
        }
    }

    public List<Tx> findAll() throws SQLException {
        List<Tx> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY tx_date DESC";
        try (Connection c = DBUtil.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Tx t = new Tx();
                t.setId(rs.getInt("id"));
                t.setItemId(rs.getInt("item_id"));
                Timestamp ts = rs.getTimestamp("tx_date");
                t.setTxDate(ts == null ? null : ts.toLocalDateTime());
                t.setChangeQty(rs.getInt("change_qty"));
                t.setNote(rs.getString("note"));
                list.add(t);
            }
        }
        return list;
    }
}
