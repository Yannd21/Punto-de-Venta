package main;

import ui.SplashScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            // Desactivar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // O mejor aún, comentar completamente estas líneas para usar el Look and Feel por defecto de Java
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Iniciar la aplicación con el Splash Screen
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();
        });
    }
}