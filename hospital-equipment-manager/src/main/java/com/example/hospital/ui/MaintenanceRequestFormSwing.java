package com.example.hospital.ui;

import com.example.hospital.dao.DepartmentDAO;
import com.example.hospital.dao.EquipmentDAO;
import com.example.hospital.dao.MaintenanceRequestDAO;
import com.example.hospital.models.Equipment;
import com.example.hospital.models.MaintenanceRequest;
import com.example.hospital.models.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// import com.itextpdf.io.image.ImageData;
// import com.itextpdf.io.image.ImageDataFactory;
// import com.itextpdf.kernel.pdf.PdfDocument;
// import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
// import com.itextpdf.layout.properties.TextAlignment;
// import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.DeviceRgb;

// import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
// import java.text.SimpleDateFormat;
import java.util.Date;

public class MaintenanceRequestFormSwing extends JFrame {

    private final User currentUser;
    private final List<JComponent[]> equipmentRows = new ArrayList<>();
    private List<Equipment> allEquipment = new ArrayList<>();
    private JFormattedTextField tfNgayYeuCau;
    private JTextField tfKhoaPhong;

    public MaintenanceRequestFormSwing(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Phiếu Đề Nghị Bảo Trì");
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

        // Load equipment data based on user role
        try {
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                if (currentUser.isTruongKhoa()) {
                    // For department heads, only show equipment from their department
                    allEquipment = new EquipmentDAO().findByDepartment(currentUser.getDepartmentId());
                } else {
                    // For other roles, show all equipment
                    allEquipment = new EquipmentDAO().findAll();
                }
            } else {
                // Default to all equipment if user or department is not available
                allEquipment = new EquipmentDAO().findAll();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách thiết bị: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

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

        // Set current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = LocalDate.now().format(formatter);
        tfNgayYeuCau = new JFormattedTextField();
        tfNgayYeuCau.setValue(currentDate);
        tfNgayYeuCau.setEditable(false);
        tfNgayYeuCau.setColumns(15);
        ngayYeuCauPanel.add(tfNgayYeuCau);
        ngayYeuCauPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel khoaPhongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        khoaPhongPanel.add(new JLabel("Khoa/Phòng:"));
        tfKhoaPhong = new JTextField(25);
        tfKhoaPhong.setEditable(false);

        // Set department text
        if (currentUser != null && currentUser.getDepartmentId() != null) {
            try {
                DepartmentDAO deptDao = new DepartmentDAO();
                String deptName = deptDao.findNameById(currentUser.getDepartmentId());
                tfKhoaPhong.setText(deptName != null ? deptName : "Chưa xác định");
            } catch (SQLException ex) {
                ex.printStackTrace();
                tfKhoaPhong.setText("Lỗi khi tải thông tin khoa/phòng");
            }
        } else {
            tfKhoaPhong.setText("Chưa xác định");
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

        // Data Rows (3 hàng) — store references so we can read later
        for (int row = 1; row <= 3; row++) {
            JComponent[] rowFields = new JComponent[headers.length];

            // STT column
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = (double) widths[0] / 690.0;
            JTextField sttField = new JTextField(String.valueOf(row));
            sttField.setEditable(false);
            sttField.setHorizontalAlignment(SwingConstants.CENTER);
            sttField.setBorder(new MatteBorder(0, 1, 1, 1, Color.BLACK));
            tablePanel.add(sttField, gbc);
            rowFields[0] = sttField;

            // Tên thiết bị column (Dropdown)
            gbc.gridx = 1;
            gbc.weightx = (double) widths[1] / 690.0;
            JComboBox<String> equipmentCombo = new JComboBox<>();
            equipmentCombo.addItem(""); // Empty first option
            for (Equipment eq : allEquipment) {
                // Show both code and name in the dropdown for better identification
                equipmentCombo.addItem(String.format("%s - %s", eq.getCode(), eq.getName()));
            }
            equipmentCombo.setPreferredSize(new Dimension(widths[1], 30));
            equipmentCombo.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
            tablePanel.add(equipmentCombo, gbc);
            rowFields[1] = equipmentCombo;

            // Model column (auto-filled when equipment is selected)
            gbc.gridx = 2;
            gbc.weightx = (double) widths[2] / 690.0;
            JTextField modelField = new JTextField();
            modelField.setEditable(false);
            modelField.setHorizontalAlignment(SwingConstants.CENTER);
            modelField.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
            tablePanel.add(modelField, gbc);
            rowFields[2] = modelField;

            // Số lượng column
            gbc.gridx = 3;
            gbc.weightx = (double) widths[3] / 690.0;
            JTextField qtyField = new JTextField();
            qtyField.setHorizontalAlignment(SwingConstants.CENTER);
            qtyField.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
            tablePanel.add(qtyField, gbc);
            rowFields[3] = qtyField;

            // Ghi chú column
            gbc.gridx = 4;
            gbc.weightx = (double) widths[4] / 690.0;
            JTextField noteField = new JTextField();
            noteField.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
            tablePanel.add(noteField, gbc);
            rowFields[4] = noteField;

            // Add action listener to auto-fill model when equipment is selected
            equipmentCombo.addActionListener(e -> {
                String selected = (String) equipmentCombo.getSelectedItem();
                if (selected != null && !selected.isEmpty()) {
                    // Extract the equipment code from the selected item
                    String eqCode = selected.split(" - ")[0];
                    for (Equipment eq : allEquipment) {
                        if (eq.getCode().equals(eqCode)) {
                            // Update model field with manufacturer
                            modelField.setText(eq.getManufacturer() != null ? eq.getManufacturer() : "");
                            break;
                        }
                    }
                } else {
                    modelField.setText("");
                }
            });

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
            try {
                // Create a test user for standalone testing
                User testUser = new User();
                testUser.setId(1);
                testUser.setDepartmentId(1);
                testUser.setUsername("testuser");
                testUser.setRole(User.Role.NV_BAO_TRI);

                new MaintenanceRequestFormSwing(testUser).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        JButton btnExport = new JButton("Xuất PDF");

        btnSave.addActionListener(e -> saveRequests());
        btnCancel.addActionListener(e -> dispose());
        btnExport.addActionListener(e -> exportToPdf());

        panel.add(btnSave);
        panel.add(btnExport);
        panel.add(btnCancel);

        return panel;
    }

    private void exportToPdf() {
        // Validate input
        if (equipmentRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng thêm ít nhất một thiết bị trước khi xuất PDF",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu phiếu yêu cầu bảo trì");

        // Set default file name with current date
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new File("PhieuYeuCauBaoTri_" + timeStamp + ".pdf"));

        // Set file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Ensure the file has .pdf extension
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try (PdfWriter writer = new PdfWriter(filePath);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf)) {

                // Set document properties
                document.setMargins(50, 50, 50, 50);

                // Add hospital logo (if available)
                try {
                    URL logoUrl = getClass().getResource("/images/hospital_logo.png");
                    if (logoUrl != null) {
                        ImageData imageData = ImageDataFactory.create(logoUrl);
                        Image logo = new Image(imageData).setWidth(100).setAutoScale(true);
                        document.add(logo);
                    }
                } catch (Exception e) {
                    // Logo not found or error loading, continue without it
                }

                // Add a header that mirrors the on-screen form (Mẫu 03 left, CHXHCNVN right)
                Table headerTable = new Table(new float[] { 1, 1 });
                headerTable.setWidth(UnitValue.createPercentValue(100));

                // Left column: Mẫu 03 and hospital/department
                Paragraph leftHeader = new Paragraph();
                leftHeader.add(new Paragraph("Mẫu 03").setFontSize(10));
                leftHeader.add(new Paragraph("BỆNH VIỆN ĐKKV TÂN CHÂU").setBold().setFontSize(12));
                leftHeader.add(new Paragraph("KHOA/PHÒNG: " + tfKhoaPhong.getText()).setFontSize(11));

                // Right column: CHXHCNVN and date
                Paragraph rightHeader = new Paragraph();
                rightHeader.add(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM").setBold().setFontSize(12));
                rightHeader.add(new Paragraph("Độc lập - Tự do - Hạnh phúc").setFontSize(11));
                rightHeader.add(new Paragraph("Tân Châu, ngày " + tfNgayYeuCau.getText()).setFontSize(10));

                headerTable.addCell(
                        new Cell().add(leftHeader).setBorder(null).setPadding(0).setTextAlignment(TextAlignment.LEFT));
                headerTable.addCell(new Cell().add(rightHeader).setBorder(null).setPadding(0)
                        .setTextAlignment(TextAlignment.RIGHT));

                document.add(headerTable);

                // Thin separator line under header
                document.add(new LineSeparator(new SolidLine(1f)).setMarginBottom(8f));

                // Add title (centered, uppercase)
                Paragraph title = new Paragraph("PHIẾU ĐỀ NGHỊ BẢO TRÌ THIẾT BỊ Y TẾ")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(14)
                        .setMarginTop(6f)
                        .setMarginBottom(8f);
                document.add(title);

                // Add request info
                Table infoTable = new Table(2);
                infoTable.setWidth(UnitValue.createPercentValue(100));

                // Add request date
                infoTable.addCell(createCell("Ngày yêu cầu:", true));
                infoTable.addCell(createCell(tfNgayYeuCau.getText(), false));

                // Add department
                infoTable.addCell(createCell("Khoa/Phòng:", true));
                infoTable.addCell(createCell(tfKhoaPhong.getText(), false));

                // Add requester
                infoTable.addCell(createCell("Người yêu cầu:", true));
                infoTable.addCell(createCell(currentUser.getFullname(), false));

                document.add(infoTable);
                document.add(new Paragraph("\n"));

                // Add equipment list
                Paragraph equipmentTitle = new Paragraph("DANH SÁCH THIẾT BỊ CẦN BẢO TRÌ")
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);
                document.add(equipmentTitle);

                // Create equipment table
                Table equipmentTable = new Table(new float[] { 1, 3, 2, 2, 3 });
                equipmentTable.setWidth(UnitValue.createPercentValue(100));

                // Add table headers
                String[] headers = { "STT", "Tên thiết bị", "Mã thiết bị", "Số lượng", "Tình trạng hư hỏng" };
                for (String header : headers) {
                    equipmentTable.addHeaderCell(createCell(header, true));
                }

                // Add equipment rows
                int stt = 1;
                for (JComponent[] row : equipmentRows) {
                    JComboBox<String> nameCombo = (JComboBox<String>) row[1];
                    String selected = (String) nameCombo.getSelectedItem();

                    if (selected != null && !selected.isEmpty()) {
                        String eqName = selected.contains(" - ") ? selected.split(" - ")[1] : selected;
                        String model = ((JTextField) row[2]).getText().trim();
                        String qty = ((JTextField) row[3]).getText().trim();
                        String note = ((JTextField) row[4]).getText().trim();

                        equipmentTable.addCell(createCell(String.valueOf(stt++), false));
                        equipmentTable.addCell(createCell(eqName, false));
                        equipmentTable.addCell(createCell(model, false));
                        equipmentTable.addCell(createCell(qty, false));
                        equipmentTable.addCell(createCell(note, false));
                    }
                }

                document.add(equipmentTable);

                // Add signature section
                document.add(new Paragraph("\n\n"));
                Table signatureTable = new Table(2);
                signatureTable.setWidth(UnitValue.createPercentValue(100));

                signatureTable.addCell(createCell("Người lập phiếu", true).setTextAlignment(TextAlignment.CENTER));
                signatureTable
                        .addCell(createCell("Xác nhận của trưởng khoa", true).setTextAlignment(TextAlignment.CENTER));

                signatureTable.addCell(createCell("\n\n\n", false).setTextAlignment(TextAlignment.CENTER));
                signatureTable.addCell(createCell("\n\n\n", false).setTextAlignment(TextAlignment.CENTER));

                signatureTable
                        .addCell(createCell(currentUser.getFullname(), true).setTextAlignment(TextAlignment.CENTER));
                signatureTable.addCell(createCell("(Ký, ghi rõ họ tên)", false).setTextAlignment(TextAlignment.CENTER));

                document.add(signatureTable);

                // Show success message
                JOptionPane.showMessageDialog(
                        this,
                        "Xuất phiếu yêu cầu bảo trì thành công!\n" + filePath,
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open the saved PDF
                if (java.awt.Desktop.isDesktopSupported()) {
                    try {
                        File pdfFile = new File(filePath);
                        java.awt.Desktop.getDesktop().open(pdfFile);
                    } catch (IOException e) {
                        // Could not open file, just show success message
                    }
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi xuất file PDF: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Helper method to create table cells
    private Cell createCell(String content, boolean isHeader) {
        Paragraph p = new Paragraph(content);
        p.setFontSize(10);
        Cell cell = new Cell().add(p);
        cell.setPadding(5);

        if (isHeader) {
            cell.setBold();
            cell.setBackgroundColor(new DeviceRgb(221, 221, 221));
        }
        return cell;
    }

    private void saveRequests() {
        try {
            // Get the current date as LocalDateTime
            java.time.LocalDateTime requestDate = java.time.LocalDateTime.now();

            // Determine department id (0 if unknown)
            int departmentId = 0;
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                departmentId = currentUser.getDepartmentId();
            }

            // Create and save maintenance requests for each equipment in the form
            MaintenanceRequestDAO requestDAO = new MaintenanceRequestDAO();
            int savedCount = 0;

            for (JComponent[] row : equipmentRows) {
                @SuppressWarnings("unchecked")
                JComboBox<String> nameCombo = (JComboBox<String>) row[1];
                String selected = (String) nameCombo.getSelectedItem();

                if (selected != null && !selected.isEmpty()) {
                    // Extract equipment code from the selected item (format: "CODE - Name")
                    String eqCode = selected.split(" - ")[0];

                    // Find the equipment by code
                    for (Equipment equipment : allEquipment) {
                        if (equipment.getCode().equals(eqCode)) {
                            // Create a new maintenance request
                            MaintenanceRequest request = new MaintenanceRequest();
                            request.setRequestDate(requestDate);
                            request.setDepartmentId(departmentId);
                            request.setEquipmentId(equipment.getId());
                            request.setStatus("CHO_XU_LY"); // Default status matches model
                            request.setRequesterId(currentUser != null ? currentUser.getId() : 0);

                            // Get quantity and notes from the form
                            String quantity = ((JTextField) row[3]).getText().trim();
                            String notes = ((JTextField) row[4]).getText().trim();

                            // Put notes into issueDescription so it persists
                            request.setIssueDescription(notes);

                            // Save the request
                            requestDAO.create(request);
                            savedCount++;
                            break;
                        }
                    }
                }
            }

            if (savedCount > 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Đã lưu %d yêu cầu bảo trì", savedCount),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Close the form after saving
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không có yêu cầu bảo trì nào được lưu. Vui lòng kiểm tra lại thông tin.",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu yêu cầu bảo trì: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    }
