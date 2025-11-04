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

    -- DROP DATABASE IF EXISTS hospital_equipment;
    -- CREATE DATABASE hospital_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    -- USE hospital_equipment;

    -- -- Users: username + password (plaintext here for demo only), fullname, role
    -- CREATE TABLE users (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     username VARCHAR(50) UNIQUE NOT NULL,
    --     password VARCHAR(255) NOT NULL,
    --     fullname VARCHAR(100),
    --     role VARCHAR(20) DEFAULT 'USER'
    -- );

    -- -- Items: inventory items used by ItemDAO and transactions
    -- CREATE TABLE items (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     code VARCHAR(50) NOT NULL UNIQUE,
    --     name VARCHAR(255) NOT NULL,
    --     quantity INT NOT NULL DEFAULT 0,
    --     min_stock INT NOT NULL DEFAULT 0,
    --     maintenance_interval_days INT NOT NULL DEFAULT 0,
    --     last_maintenance DATE NULL,
    --     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- );

    -- -- Transactions (tx): references items
    -- CREATE TABLE transactions (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     item_id INT NOT NULL,
    --     tx_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    --     change_qty INT NOT NULL,
    --     note VARCHAR(512),
    --     FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
    -- );

    -- -- Equipment table (separate from items if you manage equipment records)
    -- CREATE TABLE equipment (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     name VARCHAR(100) NOT NULL,
    --     model VARCHAR(50),
    --     location VARCHAR(100),
    --     quantity INT DEFAULT 0,
    --     last_maintenance DATE,
    --     maintenance_interval_days INT DEFAULT 0
    -- );

    -- -- Maintenance tasks: uses equipment_id (as DAO expects)
    -- CREATE TABLE maintenance (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     equipment_id INT,
    --     schedule_date DATE,
    --     completed BOOLEAN DEFAULT FALSE,
    --     note VARCHAR(255),
    --     -- added to store who is assigned to handle the maintenance and who accepted it
    --     assigned_to VARCHAR(100),
    --     accepted_by VARCHAR(100),
    --     acceptance_date DATE,
    --     -- flag to mark that an inspection was performed
    --     inspected BOOLEAN DEFAULT FALSE,
    --     FOREIGN KEY (equipment_id) REFERENCES equipment(id)
    -- );
    -- -- Inspection tasks: uses equipment_id (as DAO expects)
    -- CREATE TABLE inspection (
    --     id INT AUTO_INCREMENT PRIMARY KEY,
    --     maintenance_id INT NOT NULL,
    --     equipment_id INT NOT NULL,
    --     inspection_date DATE NOT NULL,
    --     inspector VARCHAR(100),
    --     result BOOLEAN,
    --     note TEXT,
    --     accepted_by VARCHAR(100),
    --     acceptance_date DATE,
    --     FOREIGN KEY (maintenance_id) REFERENCES maintenance(id),
    --     FOREIGN KEY (equipment_id) REFERENCES equipment(id)
    -- );   
    -- -- Seed admin user (replace password with hashed in production)
    -- INSERT INTO users(username, password, fullname, role)
    -- VALUES ('admin', '123456', 'Quản trị hệ thống', 'ADMIN');


DROP DATABASE IF EXISTS hospital_equipment1;
CREATE DATABASE hospital_equipment1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_equipment1;

-- 1️⃣ BẢNG NGƯỜI DÙNG
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(100),
    role ENUM('ADMIN','MANAGER','TECHNICIAN','DEPARTMENT_HEAD') DEFAULT 'TECHNICIAN',
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2️⃣ BẢNG KHOA / PHÒNG
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- 3️⃣ BẢNG THIẾT BỊ Y TẾ
CREATE TABLE equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    model VARCHAR(100),
    serial_number VARCHAR(100),
    location VARCHAR(150),
    department_id INT,
    purchase_date DATE,
    warranty_expiry DATE,
    maintenance_interval_days INT DEFAULT 180,
    status ENUM('HOAT_DONG','BAO_TRI','HU_HONG','NGUNG_SU_DUNG') DEFAULT 'HOAT_DONG',
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 4️⃣ BẢNG YÊU CẦU BẢO TRÌ (Phiếu đề nghị)
CREATE TABLE maintenance_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    requester_id INT,
    equipment_id INT,
    issue_description TEXT,
    priority ENUM('CAO','TRUNG_BINH','THAP') DEFAULT 'TRUNG_BINH',
    status ENUM('CHO_XU_LY','DA_LAP_KE_HOACH','DA_TU_CHOI') DEFAULT 'CHO_XU_LY',
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

-- 5️⃣ BẢNG KẾ HOẠCH BẢO TRÌ
CREATE TABLE maintenance_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plan_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    request_id INT,
    manager_id INT,
    scheduled_start DATE,
    scheduled_end DATE,
    status ENUM('CHO_THUC_HIEN','DANG_THUC_HIEN','HOAN_THANH') DEFAULT 'CHO_THUC_HIEN',
    note TEXT,
    FOREIGN KEY (request_id) REFERENCES maintenance_requests(id),
    FOREIGN KEY (manager_id) REFERENCES users(id)
);

-- 6️⃣ BẢNG THỰC HIỆN BẢO TRÌ
CREATE TABLE maintenance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plan_id INT,
    technician_id INT,
    actual_start DATETIME,
    actual_end DATETIME,
    work_description TEXT,
    replaced_parts TEXT,
    attachment VARCHAR(255),
    FOREIGN KEY (plan_id) REFERENCES maintenance_plans(id),
    FOREIGN KEY (technician_id) REFERENCES users(id)
);

-- 7️⃣ BẢNG NGHIỆM THU
CREATE TABLE maintenance_approvals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT,
    approver_id INT,
    approval_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    approval_status ENUM('DA_DUYET','KHONG_DUYET') DEFAULT 'DA_DUYET',
    comments TEXT,
    FOREIGN KEY (record_id) REFERENCES maintenance_records(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

-- 8️⃣ BẢNG BÁO CÁO KẾT QUẢ
CREATE TABLE maintenance_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_period ENUM('NGAY','THANG','QUY','NAM'),
    generated_by INT,
    generated_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_maintenance INT DEFAULT 0,
    total_cost DECIMAL(12,2) DEFAULT 0,
    most_faulty_equipment VARCHAR(150),
    note TEXT,
    FOREIGN KEY (generated_by) REFERENCES users(id)
);

-- 🔹 DỮ LIỆU KHỞI TẠO MẪU
INSERT INTO departments(name) VALUES
('Khoa Nội'), ('Khoa Ngoại'), ('Khoa Hồi sức Cấp cứu'), ('Phòng Vật tư - Thiết bị y tế');

INSERT INTO users(username, password, fullname, role, department_id)
VALUES 
('admin', '123456', 'Quản trị hệ thống', 'ADMIN', 4),
('truongkhoa', '123456', 'Trưởng khoa Nội', 'DEPARTMENT_HEAD', 1),
('kythuatvien', '123456', 'Kỹ thuật viên A', 'TECHNICIAN', 4),
('quanlytb', '123456', 'Nguyễn Văn B', 'MANAGER', 4);
