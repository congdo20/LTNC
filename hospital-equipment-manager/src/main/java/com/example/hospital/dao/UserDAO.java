// package com.example.hospital.dao;

// import com.example.hospital.db.DBUtil;
// import com.example.hospital.models.User;
// import com.example.hospital.models.User.Role;
// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class UserDAO {

//     public User login(String username, String password) {
//         String sql = "SELECT id, username, fullname, role FROM users WHERE username=? AND password=?";

//         try (Connection c = DBUtil.getConnection();
//                 PreparedStatement p = c.prepareStatement(sql)) {

//             p.setString(1, username);
//             p.setString(2, password);

//             try (ResultSet rs = p.executeQuery()) {
//                 if (rs.next()) {
//                     Role role = Role.valueOf(rs.getString("role"));
//                     return new User(
//                             rs.getInt("id"),
//                             rs.getString("username"),
//                             rs.getString("fullname"),
//                             role);
//                 }
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         return null;
//     }

// }

package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void create(User user, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password, fullname, dob, gender, position, role, department_id, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, user.getUsername());
            ps.setString(idx++, password);
            ps.setString(idx++, user.getFullname());
            ps.setString(idx++, user.getDob());
            ps.setString(idx++, user.getGender());
            ps.setString(idx++, user.getPosition());
            ps.setString(idx++, user.getRole() == null ? null : user.getRole().name());
            if (user.getDepartmentId() == null)
                ps.setNull(idx++, Types.INTEGER);
            else
                ps.setInt(idx++, user.getDepartmentId());
            ps.setString(idx++, user.getPhone());
            ps.setString(idx++, user.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(User user, String password) throws SQLException {
        String sql;
        if (password == null || password.isEmpty()) {
            sql = "UPDATE users SET username=?, fullname=?, dob=?, gender=?, position=?, role=?, department_id=?, phone=?, email=? WHERE id=?";
        } else {
            sql = "UPDATE users SET username=?, password=?, fullname=?, dob=?, gender=?, position=?, role=?, department_id=?, phone=?, email=? WHERE id=?";
        }

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, user.getUsername());
            if (password != null && !password.isEmpty())
                ps.setString(idx++, password);
            ps.setString(idx++, user.getFullname());
            ps.setString(idx++, user.getDob());
            ps.setString(idx++, user.getGender());
            ps.setString(idx++, user.getPosition());
            ps.setString(idx++, user.getRole() == null ? null : user.getRole().name());
            if (user.getDepartmentId() == null)
                ps.setNull(idx++, Types.INTEGER);
            else
                ps.setInt(idx++, user.getDepartmentId());
            ps.setString(idx++, user.getPhone());
            ps.setString(idx++, user.getEmail());
            ps.setInt(idx, user.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBUtil.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            // while (rs.next()) {
            // User u = new User();
            // u.setId(rs.getInt("id"));
            // u.setUsername(rs.getString("username"));
            // u.setFullname(rs.getString("fullname"));
            // u.setRole(User.Role.valueOf(rs.getString("role")));
            // list.add(u);
            // }

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setFullname(rs.getString("fullname"));
                u.setDob(rs.getString("dob"));
                u.setGender(rs.getString("gender"));
                u.setPosition(rs.getString("position"));
                String r = rs.getString("role");
                if (r != null)
                    u.setRole(User.Role.valueOf(r));

                int dep = rs.getInt("department_id");
                u.setDepartmentId(rs.wasNull() ? null : dep);
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));

                list.add(u);
            }

        }
        return list;
    }

    // public List<User> findByDepartmentAndRole(Integer departmentId, User.Role role) throws SQLException {
    //     List<User> users = new ArrayList<>();
    //     String sql = "SELECT * FROM users WHERE department_id = ? AND role = ?";
        
    //     try (Connection conn = DBUtil.getConnection();
    //          PreparedStatement ps = conn.prepareStatement(sql)) {
            
    //         ps.setInt(1, departmentId);
    //         ps.setString(2, role.name());
            
    //         try (ResultSet rs = ps.executeQuery()) {
    //             while (rs.next()) {
    //                 User user = new User();
    //                 user.setId(rs.getInt("id"));
    //                 user.setUsername(rs.getString("username"));
    //                 user.setFullname(rs.getString("fullname"));
    //                 user.setDob(rs.getString("dob"));
    //                 user.setGender(rs.getString("gender"));
    //                 user.setPosition(rs.getString("position"));
                    
    //                 String roleStr = rs.getString("role");
    //                 if (roleStr != null) {
    //                     user.setRole(User.Role.valueOf(roleStr));
    //                 }
                    
    //                 int depId = rs.getInt("department_id");
    //                 user.setDepartmentId(rs.wasNull() ? null : depId);
    //                 user.setPhone(rs.getString("phone"));
    //                 user.setEmail(rs.getString("email"));
                    
    //                 users.add(user);
    //             }
    //         }
    //     }
    //     return users;
    // }
    
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));

                    u.setFullname(rs.getString("fullname"));
                    u.setDob(rs.getString("dob"));
                    u.setGender(rs.getString("gender"));
                    u.setPosition(rs.getString("position"));
                    String r = rs.getString("role");
                    if (r != null)
                        u.setRole(User.Role.valueOf(r));

                    int dep = rs.getInt("department_id");
                    u.setDepartmentId(rs.wasNull() ? null : dep);
                    u.setPhone(rs.getString("phone"));
                    u.setEmail(rs.getString("email"));

                    return u;
                }
            }
        }
        return null;
    }

    // public User login(String username, String password) throws SQLException {
    // String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
    // try (Connection conn = DBUtil.getConnection();
    // PreparedStatement ps = conn.prepareStatement(sql)) {

    // ps.setString(1, username);
    // ps.setString(2, password);

    // try (ResultSet rs = ps.executeQuery()) {
    // if (rs.next()) {
    // User u = new User();
    // u.setId(rs.getInt("id"));
    // u.setUsername(rs.getString("username"));
    // u.setFullname(rs.getString("fullname"));
    // u.setRole(User.Role.valueOf(rs.getString("role")));
    // return u;
    // }
    // }
    // }
    // return null;
    // }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullname(rs.getString("fullname"));
                    u.setDob(rs.getString("dob"));
                    u.setGender(rs.getString("gender"));
                    u.setPosition(rs.getString("position"));
                    String r = rs.getString("role");
                    if (r != null)
                        u.setRole(User.Role.valueOf(r));

                    int dep = rs.getInt("department_id");
                    u.setDepartmentId(rs.wasNull() ? null : dep);
                    u.setPhone(rs.getString("phone"));
                    u.setEmail(rs.getString("email"));

                    return u;
                }
            }
        }
        return null;
    }
    
    // In UserDAO.java

public List<User> findByRole(User.Role role) throws SQLException {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE role = ?";
    try (Connection c = DBUtil.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
        p.setString(1, role.name());
        try (ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
    }
    return users;
}

public List<User> findByDepartmentAndRole(Integer departmentId, User.Role role) throws SQLException {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE department_id = ? AND role = ?";
    try (Connection c = DBUtil.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
        p.setInt(1, departmentId);
        p.setString(2, role.name());
        try (ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
    }
    return users;
}

private User extractUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getInt("id"));
    user.setUsername(rs.getString("username"));
    user.setFullname(rs.getString("fullname"));
    user.setRole(User.Role.valueOf(rs.getString("role")));
    user.setDepartmentId(rs.getInt("department_id"));
    if (rs.wasNull()) {
        user.setDepartmentId(null);
    }
    // Set other fields as needed
    return user;
}

}
