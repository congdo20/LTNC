DROP DATABASE IF EXISTS hospital_equipment;
CREATE DATABASE hospital_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_equipment;

CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(100),
    position VARCHAR(100),
    role ENUM('ADMIN','DEPARTMENT_HEAD','MANAGER','TECHNICIAN') DEFAULT 'TECHNICIAN',
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    manufacturer VARCHAR(100),
    year_of_use YEAR,
    status ENUM('TOT','BAO_TRI','HU_HONG') DEFAULT 'TOT',
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE maintenance_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    department_id INT NOT NULL,
    equipment_id INT NOT NULL,
    issue_description TEXT,
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    priority ENUM('CAO','TRUNG_BINH','THAP') DEFAULT 'TRUNG_BINH',
    status ENUM('CHO_XU_LY','DA_LAP_KE_HOACH','DA_TU_CHOI') DEFAULT 'CHO_XU_LY',
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

CREATE TABLE maintenance_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    equipment_id INT NOT NULL,
    plan_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    planner_id INT NOT NULL,
    status ENUM('CHO_THUC_HIEN','DANG_THUC_HIEN','HOAN_THANH') DEFAULT 'CHO_THUC_HIEN',
    scheduled_start DATE,
    scheduled_end DATE,
    note TEXT,
    FOREIGN KEY (request_id) REFERENCES maintenance_requests(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (planner_id) REFERENCES users(id)
);

CREATE TABLE maintenance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plan_id INT NOT NULL,
    technician_id INT NOT NULL,
    actual_start DATETIME,
    actual_end DATETIME,
    work_description TEXT,
    replaced_parts TEXT,
    replaced_quantity INT DEFAULT 0,
    attachment VARCHAR(255),
    note TEXT,
    FOREIGN KEY (plan_id) REFERENCES maintenance_plans(id),
    FOREIGN KEY (technician_id) REFERENCES users(id)
);

CREATE TABLE maintenance_approvals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    approve_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    approver_id INT NOT NULL,
    department_representative VARCHAR(100),
    approval_status ENUM('DAT','KHONG_DAT') DEFAULT 'DAT',
    comments TEXT,
    FOREIGN KEY (record_id) REFERENCES maintenance_records(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

CREATE TABLE maintenance_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_period ENUM('NGAY','THANG','QUY','NAM'),
    total_maintenance INT DEFAULT 0,
    completed_count INT DEFAULT 0,
    pending_count INT DEFAULT 0,
    total_cost DECIMAL(12,2) DEFAULT 0,
    most_maintained_equipment VARCHAR(150),
    generated_by INT,
    generated_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    note TEXT,
    FOREIGN KEY (generated_by) REFERENCES users(id)
);

-- sample data
INSERT INTO departments (name) VALUES ('Khoa Nội'), ('Khoa Ngoại'), ('Hồi sức cấp cứu'), ('Phòng Vật tư - Thiết bị y tế');

-- create admin user with password 'admin123' hashed in app OR for quick test you can insert plain (but in app we hash)
INSERT INTO users(username, password, fullname, position, role, department_id)
VALUES ('admin', 'admin123', 'Quản trị hệ thống', 'Admin', 'ADMIN', 4);
