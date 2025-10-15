-- Tạo database
-- CREATE DATABASE IF NOT EXISTS hospital_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE hospital_equipment;

-- Bảng items (thiết bị)
-- CREATE TABLE IF NOT EXISTS items (
--   id INT AUTO_INCREMENT PRIMARY KEY,
--   code VARCHAR(50) NOT NULL UNIQUE, -- mã thiết bị
--   name VARCHAR(255) NOT NULL,
--   quantity INT NOT NULL DEFAULT 0,
--   min_stock INT NOT NULL DEFAULT 0, -- ngưỡng cảnh báo
--   maintenance_interval_days INT NOT NULL DEFAULT 0, -- 0: không lên lịch
--   last_maintenance DATE NULL,
--   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Bảng transactions (nhập/xuất)
-- CREATE TABLE IF NOT EXISTS transactions (
--   id INT AUTO_INCREMENT PRIMARY KEY,
--   item_id INT NOT NULL,
--   tx_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   change_qty INT NOT NULL, -- +in, -out
--   note VARCHAR(512),
--   FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
-- );

-- Bảng maintenance tasks
-- CREATE TABLE IF NOT EXISTS maintenance_tasks (
--   id INT AUTO_INCREMENT PRIMARY KEY,
--   item_id INT NOT NULL,
--   scheduled_date DATE NOT NULL,
--   done BOOLEAN NOT NULL DEFAULT FALSE,
--   done_date DATE NULL,
--   note VARCHAR(512),
--   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
-- );

-- CREATE TABLE users (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) NOT NULL UNIQUE,
--     password_hash VARCHAR(255) NOT NULL,
--     role VARCHAR(20) NOT NULL -- ví dụ: 'ADMIN', 'USER'
-- );

-- INSERT INTO users (username, password_hash, role) VALUES ('trinhdo', 'Do200102', 'ADMIN'); -- Lưu ý: nên dùng mã hóa password
-- select * from items;
-- select * from transactions;
-- select * from maintenance_tasks;

-- Drop database if exists hospital_equipment;
-- CREATE DATABASE hospital_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE hospital_equipment;

-- CREATE TABLE users (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
    -- Database creation script for hospital_equipment
    -- This script creates the tables used by the application DAOs:
    --   users, items, transactions, equipment, maintenance

    DROP DATABASE IF EXISTS hospital_equipment;
    CREATE DATABASE hospital_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE hospital_equipment;

    -- Users: username + password (plaintext here for demo only), fullname, role
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        fullname VARCHAR(100),
        role VARCHAR(20) DEFAULT 'USER'
    );

    -- Items: inventory items used by ItemDAO and transactions
    CREATE TABLE items (
        id INT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,
        name VARCHAR(255) NOT NULL,
        quantity INT NOT NULL DEFAULT 0,
        min_stock INT NOT NULL DEFAULT 0,
        maintenance_interval_days INT NOT NULL DEFAULT 0,
        last_maintenance DATE NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- Transactions (tx): references items
    CREATE TABLE transactions (
        id INT AUTO_INCREMENT PRIMARY KEY,
        item_id INT NOT NULL,
        tx_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        change_qty INT NOT NULL,
        note VARCHAR(512),
        FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
    );

    -- Equipment table (separate from items if you manage equipment records)
    CREATE TABLE equipment (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        model VARCHAR(50),
        location VARCHAR(100),
        quantity INT DEFAULT 0,
        last_maintenance DATE,
        maintenance_interval_days INT DEFAULT 0
    );

    -- Maintenance tasks: uses equipment_id (as DAO expects)
    CREATE TABLE maintenance (
        id INT AUTO_INCREMENT PRIMARY KEY,
        equipment_id INT,
        schedule_date DATE,
        completed BOOLEAN DEFAULT FALSE,
        inspected BOOLEAN DEFAULT FALSE,
        accepted BOOLEAN DEFAULT FALSE,
        inspection_note TEXT,
        accepted_by VARCHAR(100),
        assigned_to VARCHAR(100),
        acceptance_date DATE,
        note VARCHAR(255),
        FOREIGN KEY (equipment_id) REFERENCES equipment(id)
    );

    -- Seed admin user (replace password with hashed in production)
    INSERT INTO users(username, password, fullname, role)
    VALUES ('admin', '123456', 'Quản trị hệ thống', 'ADMIN');
