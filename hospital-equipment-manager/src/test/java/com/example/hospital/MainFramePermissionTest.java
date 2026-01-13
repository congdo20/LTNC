package com.example.hospital;

import com.example.hospital.dao.UserDAO;
import com.example.hospital.models.User;
import com.example.hospital.ui.MainFrame;
import com.example.hospital.models.Permission;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MainFramePermissionTest {

    @Test
    public void computeTabsFor_roles() {
        User admin = new User();
        admin.setRole(User.Role.ADMIN);
        List<String> tabsAdmin = MainFrame.computeTabsFor(admin);
        assertTrue(tabsAdmin.contains("Quản lý tài khoản"));

        User qltb = new User();
        qltb.setRole(User.Role.QL_THIET_BI);
        List<String> tabsQltb = MainFrame.computeTabsFor(qltb);
        assertTrue(tabsQltb.contains("Thiết bị"));
        assertTrue(tabsQltb.contains("Phân công") || tabsQltb.contains("Yêu cầu bảo trì"));

        User tech = new User();
        tech.setRole(User.Role.NV_BAO_TRI);
        List<String> tabsTech = MainFrame.computeTabsFor(tech);
        assertTrue(tabsTech.contains("Nhiệm vụ") || tabsTech.contains("Báo cáo"));
    }

    @Test
    public void computeTabsFor_permissions_and_dbUsers() throws Exception {
        // This test will load users from DB and print their computed tabs so tester can
        // review
        UserDAO dao = new UserDAO();
        for (User u : dao.findAll()) {
            List<String> tabs = MainFrame.computeTabsFor(u);
            System.out.println("User: " + u.getUsername() + " role=" + u.getRole() + " -> tabs=" + tabs);
            assertNotNull(tabs);
            // basic invariant: admin or others should have at least one tab
            assertFalse("User " + u.getUsername() + " has empty tabs", tabs.isEmpty());
        }
    }
}
