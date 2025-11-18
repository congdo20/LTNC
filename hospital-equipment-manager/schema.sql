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
    fullname VARCHAR(150) NOT NULL,
    dob DATE,                         -- ngày tháng năm sinh
    gender ENUM('NAM','NU','KHAC'),   -- giới tính
    position VARCHAR(100),            -- chức vụ công tác
    role ENUM(
        'ADMIN',
        'TRUONG_KHOA',        -- Trưởng khoa/phòng
        'QL_THIET_BI',   -- Tổ quản lý thiết bị y tế
        'NV_BAO_TRI'        -- Nhân viên bảo trì
    ) DEFAULT 'NV_BAO_TRI',
    department_id INT,                -- khoa/phòng công tác
    phone VARCHAR(20),
    email VARCHAR(100),
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
-- INSERT INTO users(username, password, fullname, position, role, department_id)
-- VALUES ('admin', 'admin123', 'Quản trị hệ thống', 'Admin', 'ADMIN', 4);

INSERT INTO users(username, password, fullname, dob, gender, position, role, department_id, phone, email)
VALUES
('admin', '1', 'Nguyễn Công Trình Độ', '2002-01-20', 'NAM', 'Admin hệ thống', 'ADMIN', NULL, '0900000000', 'admin@hospital.vn'),

('truongnoi', '1', 'Trần Văn A', '1980-05-12', 'NAM', 'Trưởng khoa Nội', 'TRUONG_KHOA', 1, '0912345678', 'tva@hospital.vn'),

('qltb', '1', 'Nguyễn Văn B', '1985-03-15', 'NAM', 'Tổ trưởng Quản lý thiết bị', 'QL_THIET_BI', 4, '0933555111', 'qltb@hospital.vn'),

('kthuat1', '1', 'Nguyễn Thị C', '1995-11-20', 'NU', 'Kỹ thuật viên', 'NV_BAO_TRI', 4, '0988666333', '0988666333');

-- update users set fullname='Nguyễn Công Trình Độ' where username='admin' ;


ALTER TABLE equipment
ADD COLUMN last_maintenance DATE;

-- add image path for equipment pictures
ALTER TABLE equipment
ADD COLUMN image_path VARCHAR(255);

-- notifications table: stores messages for users (created when plans complete, etc.)
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    related_request_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

