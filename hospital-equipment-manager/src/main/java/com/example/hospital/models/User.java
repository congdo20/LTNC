// package com.example.hospital.models;

// public class User {
//     private int id;
//     private String username;
//     private String fullname;
//     private String role; // "ADMIN" or "USER"

//     public User() {
//     }

//     public User(int id, String username, String fullname) {
//         this.id = id;
//         this.username = username;
//         this.fullname = fullname;
//     }

//     public User(int id, String username, String fullname, String role) {
//         this.id = id;
//         this.username = username;
//         this.fullname = fullname;
//         this.role = role;
//     }

//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getFullname() {
//         return fullname;
//     }

//     public void setFullname(String fullname) {
//         this.fullname = fullname;
//     }

//     public String getRole() {
//         return role;
//     }

//     public void setRole(String role) {
//         this.role = role;
//     }
// }

package com.example.hospital.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String dob; // YYYY-MM-DD
    private String gender;
    private String position;
    private Role role;
    private Integer departmentId;
    private String phone;
    private String email;

    public enum Role {
        ADMIN, TRUONG_KHOA, QL_THIET_BI, NV_BAO_TRI
    }

    public User() {
    }

    public User(int id, String username, String password, String fullname, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isTruongKhoa() {
        return role == Role.TRUONG_KHOA;
    }

    public boolean isQLThietBi() {
        return role == Role.QL_THIET_BI;
    }

    public boolean isNvBaoTri() {
        return role == Role.NV_BAO_TRI;
    }
}
