package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {
    private float opacity = 0.0f;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer displayTimer;
    private JLabel logoLabel;
    private JLabel messageLabel;

    public SplashScreen() {
        initComponents();
        setLocationRelativeTo(null);
        startSplashSequence();
    }

    private void initComponents() {
        // Panel principal con degradado gris
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Degradado gris
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(40, 40, 40),
                        0, getHeight(), new Color(80, 80, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 3));

        // Panel central para el logo y textos
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Espacio superior
        centerPanel.add(Box.createVerticalStrut(80));

        // Cargar logo
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
            // Redimensionar logo si es necesario
            Image img = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            // Si no encuentra el logo, crea uno de texto
            logoLabel = new JLabel("TPV SYSTEM");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(30));

        // Título
        JLabel titleLabel = new JLabel("Terminal Punto de Venta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Sistema de Gestión de Ventas");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(subtitleLabel);

        centerPanel.add(Box.createVerticalStrut(50));

        // Mensaje de carga
        messageLabel = new JLabel("Iniciando sistema...");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(messageLabel);

        centerPanel.add(Box.createVerticalStrut(20));

        // Versión
        JLabel versionLabel = new JLabel("Versión 1.0 - 2026");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(160, 160, 160));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(versionLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setSize(600, 500);
    }

    private void startSplashSequence() {
        // Hacer la ventana transparente inicialmente
        setOpacity(0.0f);
        setVisible(true);

        // FADE IN - Aparecer gradualmente (0.5 segundos)
        fadeInTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    setOpacity(opacity);
                    fadeInTimer.stop();
                    startDisplayTimer();
                } else {
                    setOpacity(opacity);
                }
            }
        });
        fadeInTimer.start();
    }

    private void startDisplayTimer() {
        // Mostrar por 2 segundos (ya llevamos 0.5 del fade in)
        displayTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTimer.stop();
                startFadeOut();
            }
        });
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    private void startFadeOut() {
        // FADE OUT - Desvanecer gradualmente (0.5 segundos)
        fadeOutTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    setOpacity(opacity);
                    fadeOutTimer.stop();
                    closeSplash();
                } else {
                    setOpacity(opacity);
                }
            }
        });
        fadeOutTimer.start();
    }

    private void closeSplash() {
        dispose();
        // Abrir LoginFrame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();
        });
    }
}
