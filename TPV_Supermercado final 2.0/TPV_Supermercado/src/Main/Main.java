package main;

import ui.SplashScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            // Desactivar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();
        });
    }
}