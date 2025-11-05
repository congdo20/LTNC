// // package com.example.hospital.dao;

// // import java.sql.Connection;
// // import java.sql.PreparedStatement;
// // import java.sql.SQLException;

// // import com.example.hospital.db.DBUtil;
// // import com.example.hospital.models.MaintenanceRequest;

// // public class MaintenanceRequestDAO {
// //     public void create(MaintenanceRequest r) throws SQLException {
// //         String sql = """
// //                     INSERT INTO maintenance_requests
// //                     (requester_id, department_id, equipment_id, issue_description, priority)
// //                     VALUES (?, ?, ?, ?, ?)
// //                 """;

// //         try (Connection c = DBUtil.getConnection();
// //                 PreparedStatement p = c.prepareStatement(sql)) {

// //             p.setInt(1, r.getRequesterId());
// //             p.setInt(2, r.getDepartmentId());
// //             p.setInt(3, r.getEquipmentId());
// //             p.setString(4, r.getIssueDescription());
// //             p.setString(5, r.getPriority());

// //             p.executeUpdate();
// //         }
// //     }

// // }



// package com.example.hospital.dao;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// import com.example.hospital.models.MaintenanceRequest;
// import com.example.hospital.db.DBUtil;

// public class MaintenanceRequestDAO {

//     public void create(MaintenanceRequest r) throws SQLException {
//         String sql = """
//                     INSERT INTO maintenance_requests
//                     (requester_id, department_id, equipment_id, issue_description, request_date, priority, status)
//                     VALUES (?, ?, ?, ?, ?, ?, ?)
//                 """;

//         try (Connection conn = DBUtil.getConnection();
//                 PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setInt(1, r.getRequesterId());
//             ps.setInt(2, r.getDepartmentId());
//             ps.setInt(3, r.getEquipmentId());
//             ps.setString(4, r.getIssueDescription());
//             ps.setTimestamp(5, Timestamp.valueOf(r.getRequestDate()));
//             ps.setString(6, r.getPriority());
//             ps.setString(7, r.getStatus());

//             ps.executeUpdate();
//         }
//     }

//     public List<MaintenanceRequest> findByDepartment(int deptId) throws SQLException {
//         String sql = "SELECT * FROM maintenance_requests WHERE department_id = ?";
//         List<MaintenanceRequest> list = new ArrayList<>();

//         try (Connection conn = DBUtil.getConnection();
//                 PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setInt(1, deptId);
//             ResultSet rs = ps.executeQuery();

//             while (rs.next()) {
//                 MaintenanceRequest r = new MaintenanceRequest();
//                 r.setId(rs.getInt("id"));
//                 r.setRequesterId(rs.getInt("requester_id"));
//                 r.setDepartmentId(rs.getInt("department_id"));
//                 r.setEquipmentId(rs.getInt("equipment_id"));
//                 r.setIssueDescription(rs.getString("issue_description"));
//                 r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
//                 r.setPriority(rs.getString("priority"));
//                 r.setStatus(rs.getString("status"));
//                 list.add(r);
//             }
//         }
//         return list;
//     }


//     public List<MaintenanceRequest> findAll() throws SQLException {
//         List<MaintenanceRequest> list = new ArrayList<>();

//         String sql = "SELECT * FROM maintenance_requests ORDER BY request_date DESC";

//         try (Connection conn = DBUtil.getConnection();
//                 PreparedStatement ps = conn.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {

//             while (rs.next()) {
//                 list.add(map(rs));
//             }
//         }
//         return list;
//     }

// }


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
        r.setEquipmentId(rs.getInt("equipment_id"));
        r.setIssueDescription(rs.getString("issue_description"));
        // r.setRequestDate(rs.getTimestamp("request_date"));
        r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
        r.setPriority(rs.getString("priority"));
        r.setStatus(rs.getString("status"));

        int dep = rs.getInt("department_id");
        r.setDepartmentId(rs.wasNull() ? null : dep);

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

        String sql = "INSERT INTO maintenance_requests(equipment_id, issue_description, request_date, priority, status, department_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, req.getEquipmentId());
            ps.setString(2, req.getIssueDescription());
            // ps.setTimestamp(3, new Timestamp(req.getRequestDate().getTime()));
            ps.setTimestamp(5, Timestamp.valueOf(req.getRequestDate()));
            ps.setString(4, req.getPriority());
            ps.setString(5, req.getStatus());
            ps.setInt(6, req.getDepartmentId());

            ps.executeUpdate();
        }
    }
}
