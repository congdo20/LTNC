package com.example.hospital.dao;

import com.example.hospital.db.DBUtil;
import com.example.hospital.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    public void create(User user, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password, fullname, dob, gender, position, role, department_id, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setId(id);
                }
            }
            if (user.getId() > 0) {
                setPermissions(user.getId(), user.getPermissions());
            }
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

                // Load permissions
                u.setPermissions(getPermissions(u.getId()));

                list.add(u);
            }

        }
        return list;
    }

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

                    // Load permissions
                    u.setPermissions(getPermissions(u.getId()));

                    return u;
                }
            }
        }
        return null;
    }

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

                    // Load permissions
                    u.setPermissions(getPermissions(u.getId()));

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

    public Map<String, Boolean> getPermissions(int userId) throws SQLException {
        Map<String, Boolean> perms = new HashMap<>();
        String sql = "SELECT perm_key, allowed FROM user_permissions WHERE user_id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    perms.put(rs.getString("perm_key"), rs.getInt("allowed") == 1);
                }
            }
        }
        return perms;
    }

    public void setPermissions(int userId, Map<String, Boolean> perms) throws SQLException {
        String del = "DELETE FROM user_permissions WHERE user_id = ?";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement pd = c.prepareStatement(del)) {
            pd.setInt(1, userId);
            pd.executeUpdate();
        }
        if (perms == null || perms.isEmpty())
            return;
        String ins = "INSERT INTO user_permissions(user_id, perm_key, allowed) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
                PreparedStatement pi = c.prepareStatement(ins)) {
            for (Map.Entry<String, Boolean> e : perms.entrySet()) {
                pi.setInt(1, userId);
                pi.setString(2, e.getKey());
                pi.setInt(3, e.getValue() ? 1 : 0);
                pi.addBatch();
            }
            pi.executeBatch();
        }
    }

}
