package com.example.hospital.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class ImageUtil {

    public static ImageIcon loadScaledIcon(String path, int width, int height) {
        if (path == null || path.trim().isEmpty())
            return null;

        try {
            Image img = null;

            // try URL first
            try {
                if (path.startsWith("http://") || path.startsWith("https://")) {
                    URL url = new URL(path);
                    img = new ImageIcon(url).getImage();
                }
            } catch (Exception ignore) {
                img = null;
            }

            // try file system
            if (img == null) {
                File f = new File(path);
                if (f.exists()) {
                    img = new ImageIcon(path).getImage();
                }
            }

            // try classpath resource
            if (img == null) {
                URL res = ImageUtil.class.getResource(path);
                if (res == null) {
                    res = ImageUtil.class.getResource('/' + path);
                }
                if (res != null) {
                    img = new ImageIcon(res).getImage();
                }
            }

            if (img == null)
                return null;

            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
