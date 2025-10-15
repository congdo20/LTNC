package com.example.hospital;

import com.example.hospital.db.DBUtil;

import java.sql.Connection;

public class DbTest {
    public static void main(String[] args) {
        try (Connection c = DBUtil.getConnection()) {
            System.out.println("DB connection OK: " + (c != null && !c.isClosed()));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
