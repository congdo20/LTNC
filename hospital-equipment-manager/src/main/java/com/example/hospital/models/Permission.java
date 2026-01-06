package com.example.hospital.models;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Permission {
    public static final String MANAGE_ACCOUNTS = "manage_accounts";
    public static final String VIEW_DEPT_EQUIPMENT = "view_dept_equipment";
    public static final String VIEW_EQUIPMENT = "view_equipment";
    public static final String CREATE_REQUEST = "create_request";
    public static final String PLAN = "plan";
    public static final String ASSIGN = "assign";
    public static final String TASK = "task";
    public static final String REPORT = "report";

    public static final Map<String, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put(MANAGE_ACCOUNTS, "Quản lý tài khoản");
        LABELS.put(VIEW_DEPT_EQUIPMENT, "Xem Thiết bị khoa/viện");
        LABELS.put(VIEW_EQUIPMENT, "Xem Thiết bị");
        LABELS.put(CREATE_REQUEST, "Tạo yêu cầu bảo trì");
        LABELS.put(PLAN, "Lên kế hoạch");
        LABELS.put(ASSIGN, "Phân công");
        LABELS.put(TASK, "Nhiệm vụ");
        LABELS.put(REPORT, "Báo cáo");
    }

    private Permission() {
    }
}