package com.example.hospital.ui;

import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRequestFormSwing extends JFrame {

    private final User currentUser;
    private final List<JTextField[]> equipmentRows = new ArrayList<>();
    private JTextField tfNgayYeuCau;
    private JTextField tfKhoaPhong;

    public MaintenanceRequestFormSwing(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Phiếu Đề Nghị Bảo Trì - Java Swing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Sử dụng BoxLayout theo chiều dọc cho khung chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Đặt font mặc định cho toàn bộ ứng dụng (giống Times New Roman)
        UIManager.put("Label.font", new Font("Times New Roman", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Times New Roman", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Times New Roman", Font.PLAIN, 12));
        UIManager.put("Panel.font", new Font("Times New Roman", Font.PLAIN, 14));

        // 1. Header Area (Bệnh viện và CHXHCNVN)
        mainPanel.add(createHeaderPanel());

        // 2. Title
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createTitleLabel("PHIẾU ĐỀ NGHỊ BẢO TRÌ THIẾT BỊ Y TẾ"));
        mainPanel.add(Box.createVerticalStrut(20));

        // 3. Request Info
        mainPanel.add(createRequestInfoPanel());
        mainPanel.add(Box.createVerticalStrut(15));

        // 4. Equipment Table
        mainPanel.add(createEquipmentTablePanel());
        mainPanel.add(Box.createVerticalStrut(50));

        // 5. Signature Area
        mainPanel.add(createSignaturePanel());
        mainPanel.add(Box.createVerticalStrut(50));

        // 6. Note/Lưu ý
        mainPanel.add(createNotePanel());

        // 7. Actions
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createActionPanel());

        add(mainPanel);
        pack(); // Điều chỉnh kích thước cửa sổ cho phù hợp với nội dung
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình
    }

    // --- Phương thức tạo các phần tử con ---

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mẫu 03 (Thêm riêng vì nó nằm ngoài căn chỉnh 2 cột)
        JLabel mauLabel = createStyledLabel("Mẫu 03", new Font("Times New Roman", Font.PLAIN, 14),
                SwingConstants.RIGHT, Component.RIGHT_ALIGNMENT);
        headerPanel.add(mauLabel);

        // Khung chính Header, dùng GridBagLayout để căn chỉnh tốt hơn
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);

        // --- Left Header (Bệnh viện) ---
        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel benhVien = createStyledLabel("BỆNH VIỆN ĐKKV TÂN CHÂU", new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);
        JLabel khoaPhong = createStyledLabel("KHOA/PHÒNG:.....", new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);
        khoaPhong.setText("<html><u>KHOA/PHÒNG:.....</u></html>");

        leftBox.add(benhVien);
        leftBox.add(khoaPhong);

        // --- Right Header (CHXHCNVN) ---
        JPanel rightBox = new JPanel();
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
        rightBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel chxhcnvn = createStyledLabel("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);
        JLabel docLap = createStyledLabel("Độc lập - Tự do - Hạnh phúc", new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);
        docLap.setText("<html><u>Độc lập - Tự do - Hạnh phúc</u></html>");

        JLabel date = createStyledLabel("Tân Châu, ngày ..... tháng ..... năm 20.....",
                new Font("Times New Roman", Font.ITALIC, 12),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);

        rightBox.add(chxhcnvn);
        rightBox.add(docLap);
        rightBox.add(date);

        // Thêm vào Header Panel chính
        JPanel twoColumnsPanel = new JPanel(new GridLayout(1, 2, 200, 0)); // Tạo khoảng cách 200px
        twoColumnsPanel.add(leftBox);
        twoColumnsPanel.add(rightBox);
        twoColumnsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tích hợp Mẫu 03
        JPanel finalHeader = new JPanel(new BorderLayout());
        finalHeader.add(mauLabel, BorderLayout.NORTH);
        finalHeader.add(twoColumnsPanel, BorderLayout.CENTER);

        return finalHeader;
    }

    private JLabel createStyledLabel(String text, Font font, int alignment, float horizontalAlignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(font);
        label.setAlignmentX(horizontalAlignment);
        return label;
    }

    private JLabel createTitleLabel(String text) {
        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    private JPanel createRequestInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kinhGui = createStyledLabel("Kính gửi:", new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.LEFT, Component.LEFT_ALIGNMENT);
        kinhGui.setBorder(new EmptyBorder(0, 0, 5, 0));

        JPanel ngayYeuCauPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ngayYeuCauPanel.add(new JLabel("Ngày yêu cầu:"));
        tfNgayYeuCau = new JTextField(15);
        ngayYeuCauPanel.add(tfNgayYeuCau);
        ngayYeuCauPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel khoaPhongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        khoaPhongPanel.add(new JLabel("Khoa/Phòng:"));
        tfKhoaPhong = new JTextField(15);
        // default to current user's department id/name if available
        if (currentUser != null && currentUser.getDepartmentId() != null) {
            tfKhoaPhong.setText("Khoa/Phòng ID: " + currentUser.getDepartmentId());
        }
        khoaPhongPanel.add(tfKhoaPhong);
        khoaPhongPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(kinhGui);
        infoPanel.add(ngayYeuCauPanel);
        infoPanel.add(khoaPhongPanel);
        return infoPanel;
    }

    private JPanel createEquipmentTablePanel() {
        // Sử dụng GridBagLayout để tạo cấu trúc bảng với đường viền
        JPanel tablePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        String[] headers = { "STT", "Tên thiết bị", "Model", "Số lượng", "Ghi Chú" };
        int[] widths = { 40, 200, 120, 80, 250 }; // Độ rộng tương đối

        // Cấu hình Column
        for (int i = 0; i < headers.length; i++) {
            gbc.gridx = i;
            gbc.weightx = (double) widths[i] / 690.0; // Tổng chiều rộng là 690

            JLabel headerLabel = createStyledLabel(headers[i], new Font("Times New Roman", Font.BOLD, 14),
                    SwingConstants.CENTER,
                    Component.CENTER_ALIGNMENT);
            headerLabel.setPreferredSize(new Dimension(widths[i], 30));
            headerLabel.setBorder(new MatteBorder(1, (i == 0 ? 1 : 0), 1, 1, Color.BLACK));
            tablePanel.add(headerLabel, gbc);
        }

        // Data Rows (3 hàng trống) — store references so we can read later
        for (int row = 1; row <= 3; row++) {
            JTextField[] rowFields = new JTextField[headers.length];
            for (int col = 0; col < headers.length; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.weightx = (double) widths[col] / 690.0;

                JTextField cell = new JTextField();
                cell.setPreferredSize(new Dimension(widths[col], 40));
                cell.setBorder(new MatteBorder(0, (col == 0 ? 1 : 0), 1, 1, Color.BLACK));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                tablePanel.add(cell, gbc);
                rowFields[col] = cell;
            }
            equipmentRows.add(rowFields);
        }
        tablePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return tablePanel;
    }

    private JPanel createSignaturePanel() {
        JPanel signaturePanel = new JPanel(new GridLayout(1, 2, 300, 0)); // Khoảng cách lớn
        signaturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel phuTrach = createSignatureColumn("Phụ trách khoa", "(Ký tên, đóng dấu)");
        JPanel nguoiDeNghi = createSignatureColumn("Người đề nghị", "(Ký tên, họ và tên)");

        signaturePanel.add(phuTrach);
        signaturePanel.add(nguoiDeNghi);
        return signaturePanel;
    }

    private JPanel createSignatureColumn(String position, String note) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel posLabel = createStyledLabel(position, new Font("Times New Roman", Font.BOLD, 14),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);
        JLabel noteLabel = createStyledLabel(note, new Font("Times New Roman", Font.ITALIC, 12),
                SwingConstants.CENTER, Component.CENTER_ALIGNMENT);

        // Khoảng trống cho chữ ký
        column.add(posLabel);
        column.add(noteLabel);
        column.add(Box.createVerticalStrut(70));

        return column;
    }

    private JPanel createNotePanel() {
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));
        notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel luuYTitle = createStyledLabel("* Lưu ý", new Font("Times New Roman", Font.BOLD, 12),
                SwingConstants.LEFT, Component.LEFT_ALIGNMENT);

        JTextArea luuYContent = new JTextArea(
                "Trong quá trình thực hiện, nếu có yêu cầu điều chỉnh, bổ sung từ các cơ quan chức năng hoặc thay đổi quy định pháp luật, các bên tham gia sẽ thực hiện điều chỉnh theo đúng quy định hiện hành và thống nhất thực hiện các nội dung sửa đổi.");
        luuYContent.setWrapStyleWord(true);
        luuYContent.setLineWrap(true);
        luuYContent.setEditable(false);
        luuYContent.setBackground(notePanel.getBackground());
        luuYContent.setBorder(new EmptyBorder(5, 0, 0, 0));

        notePanel.add(luuYTitle);
        notePanel.add(luuYContent);
        return notePanel;
    }

    public static void main(String[] args) {
        // Đảm bảo giao diện được tạo trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // for standalone testing, no user context - pass null
            new MaintenanceRequestFormSwing(null).setVisible(true);
        });
    }

    private JPanel createActionPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> saveRequests());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnSave);
        p.add(btnCancel);
        return p;
    }

    private void saveRequests() {
        // collect rows
        EquipmentDAO equipmentDAO = new EquipmentDAO();
        MaintenanceRequestDAO reqDao = new MaintenanceRequestDAO();

        List<String> missing = new ArrayList<>();
        int created = 0;

        // Require a logged-in requester to create maintenance requests
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Bạn phải đăng nhập để tạo yêu cầu bảo trì.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (JTextField[] row : equipmentRows) {
            // columns: 0=STT,1=Ten,2=Model,3=So luong,4=Ghi chu
            String name = row[1].getText().trim();
            if (name.isEmpty())
                continue;
            String model = row[2].getText().trim();
            String qty = row[3].getText().trim();
            String note = row[4].getText().trim();

            try {
                Equipment eq = equipmentDAO.findByName(name);
                if (eq == null) {
                    missing.add(name);
                    continue;
                }

                MaintenanceRequest r = new MaintenanceRequest();
                r.setEquipmentId(eq.getId());
                // currentUser is guaranteed non-null above
                r.setRequesterId(currentUser.getId());
                // department must be present per DB schema
                Integer depId = currentUser.getDepartmentId();
                if (depId == null || depId <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Người dùng hiện tại chưa có khoa/phòng. Không thể tạo yêu cầu.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                r.setDepartmentId(depId);
                StringBuilder desc = new StringBuilder();
                if (!model.isEmpty())
                    desc.append("Model: ").append(model).append("; ");
                if (!qty.isEmpty())
                    desc.append("Số lượng: ").append(qty).append("; ");
                if (!note.isEmpty())
                    desc.append("Ghi chú: ").append(note).append("; ");
                r.setIssueDescription(desc.toString());
                // keep defaults for priority/status/requestDate

                reqDao.create(r);
                created++;
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu yêu cầu: " + ex.getMessage());
                return;
            }
        }

        if (!missing.isEmpty()) {
            String msg = "Không tìm thấy các thiết bị: \n" + String.join("\n", missing) + "\nBỏ qua chúng.";
            JOptionPane.showMessageDialog(this, msg, "Thiết bị không tồn tại", JOptionPane.WARNING_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Đã tạo " + created + " yêu cầu bảo trì.");
        dispose();
    }
}