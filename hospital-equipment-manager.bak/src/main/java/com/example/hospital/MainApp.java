// package com.example.hospital;

// import com.example.hospital.ui.MainFrame;
// import javax.swing.*;

// public class MainApp {
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             MainFrame frame = new MainFrame();
//             frame.setVisible(true);
//             Runtime.getRuntime().addShutdownHook(new Thread(() -> frame.shutdown()));
//         });
//     }
// }
package com.example.hospital;

import com.example.hospital.ui.LoginFrame;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                /* ignore */ }
            new LoginFrame().setVisible(true);
        });
    }
}