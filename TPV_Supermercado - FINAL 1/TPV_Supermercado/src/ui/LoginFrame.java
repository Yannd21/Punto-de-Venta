package ui;

import models.Usuario;
import utils.UserManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRecuperar;
    private JLabel lblIntentos;
    private UserManager userManager;
    private int intentosFallidos = 0;
    private static final int MAX_INTENTOS = 3;

    public LoginFrame() {
        userManager = new UserManager();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("TPV - Inicio de Sesión");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con degradado gris
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(52, 52, 52),
                        0, getHeight(), new Color(90, 90, 90)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Panel del formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Logo
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
            // Redimensionar logo
            Image img = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(iconLabel);
        } catch (Exception e) {
            // Si no encuentra el logo, mostrar círculo gris
            JLabel iconLabel = new JLabel("●");
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 80));
            iconLabel.setForeground(new Color(120, 120, 120));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(iconLabel);
        }

        formPanel.add(Box.createVerticalStrut(15));

        // Título
        JLabel titleLabel = new JLabel("Inicio de Sesión");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);

        formPanel.add(Box.createVerticalStrut(10));

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Terminal Punto de Venta");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(subtitleLabel);

        formPanel.add(Box.createVerticalStrut(30));

        // Campo Usuario
        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsername.setForeground(new Color(80, 80, 80));
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblUsername);

        formPanel.add(Box.createVerticalStrut(5));

        txtUsername = new JTextField(20);
        txtUsername.setMaximumSize(new Dimension(300, 35));
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtUsername);

        formPanel.add(Box.createVerticalStrut(15));

        // Campo Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setForeground(new Color(80, 80, 80));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblPassword);

        formPanel.add(Box.createVerticalStrut(5));

        txtPassword = new JPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(300, 35));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtPassword);

        formPanel.add(Box.createVerticalStrut(10));

        // Label de intentos
        lblIntentos = new JLabel(" ");
        lblIntentos.setFont(new Font("Arial", Font.ITALIC, 11));
        lblIntentos.setForeground(new Color(100, 100, 100));
        lblIntentos.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(lblIntentos);

        formPanel.add(Box.createVerticalStrut(20));

        // Botón Login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setMaximumSize(new Dimension(300, 40));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(80, 80, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> intentarLogin());
        formPanel.add(btnLogin);

        formPanel.add(Box.createVerticalStrut(10));

        // Botón Recuperar
        btnRecuperar = new JButton("¿Olvidó su contraseña?");
        btnRecuperar.setMaximumSize(new Dimension(300, 35));
        btnRecuperar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRecuperar.setBackground(Color.WHITE);
        btnRecuperar.setForeground(new Color(100, 100, 100));
        btnRecuperar.setFocusPainted(false);
        btnRecuperar.setBorderPainted(false);
        btnRecuperar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecuperar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRecuperar.addActionListener(e -> abrirRecuperarPassword());
        formPanel.add(btnRecuperar);

        mainPanel.add(formPanel);
        add(mainPanel);

        // Enter para login
        getRootPane().setDefaultButton(btnLogin);

        // Agregar listener para Enter en los campos
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese usuario y contraseña",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = userManager.validarUsuario(username, password);

        if (usuario != null) {
            // Login exitoso
            JOptionPane.showMessageDialog(this,
                    "¡Bienvenido, " + usuario.getNombreCompleto() + "!",
                    "Login Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            // Aquí abriremos la ventana MDI (próximo paso)
            abrirVentanaPrincipal(usuario);

        } else {
            // Login fallido
            intentosFallidos++;
            int intentosRestantes = MAX_INTENTOS - intentosFallidos;

            if (intentosRestantes > 0) {
                lblIntentos.setText("Credenciales incorrectas. Intentos restantes: " + intentosRestantes);
                txtPassword.setText("");
                txtPassword.requestFocus();

                JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos.\nIntentos restantes: " + intentosRestantes,
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                // Se agotaron los intentos
                JOptionPane.showMessageDialog(this,
                        "Ha excedido el número máximo de intentos.\n" +
                                "Por favor, use la opción de recuperar contraseña.",
                        "Acceso Bloqueado",
                        JOptionPane.ERROR_MESSAGE);

                btnLogin.setEnabled(false);
                txtUsername.setEnabled(false);
                txtPassword.setEnabled(false);
                lblIntentos.setText("Acceso bloqueado. Use 'Recuperar Contraseña'");
            }
        }
    }

    private void abrirRecuperarPassword() {
        RecuperarPasswordDialog dialog = new RecuperarPasswordDialog(this, userManager);
        dialog.setVisible(true);

        // Después de recuperar, resetear intentos
        intentosFallidos = 0;
        btnLogin.setEnabled(true);
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);
        lblIntentos.setText(" ");
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    private void abrirVentanaPrincipal(Usuario usuario) {
        // Cerrar login y abrir ventana MDI principal
        dispose();
        SwingUtilities.invokeLater(() -> {
            new MainMDIFrame(usuario);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}