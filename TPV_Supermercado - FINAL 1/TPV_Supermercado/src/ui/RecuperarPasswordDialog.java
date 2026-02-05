package ui;

import models.Usuario;
import utils.UserManager;
import javax.swing.*;
import java.awt.*;

public class RecuperarPasswordDialog extends JDialog {
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;
    private JButton btnRecuperar;
    private JButton btnCancelar;
    private UserManager userManager;

    public RecuperarPasswordDialog(Frame parent, UserManager userManager) {
        super(parent, "Recuperar Contraseña", true);
        this.userManager = userManager;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(450, 350);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Título
        JLabel titleLabel = new JLabel("Recuperación de Contraseña");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        // Instrucciones
        JLabel instruccionesLabel = new JLabel("<html><center>Ingrese su usuario y email para restablecer su contraseña</center></html>");
        instruccionesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instruccionesLabel.setForeground(new Color(100, 100, 100));
        instruccionesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instruccionesLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        // Campo Usuario
        mainPanel.add(createFieldPanel("Usuario:", txtUsername = new JTextField(20)));
        mainPanel.add(Box.createVerticalStrut(10));

        // Campo Email
        mainPanel.add(createFieldPanel("Email:", txtEmail = new JTextField(20)));
        mainPanel.add(Box.createVerticalStrut(10));

        // Campo Nueva Contraseña
        mainPanel.add(createFieldPanel("Nueva Contraseña:", txtNuevaPassword = new JPasswordField(20)));
        mainPanel.add(Box.createVerticalStrut(10));

        // Campo Confirmar Contraseña
        mainPanel.add(createFieldPanel("Confirmar Contraseña:", txtConfirmarPassword = new JPasswordField(20)));
        mainPanel.add(Box.createVerticalStrut(20));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnRecuperar = new JButton("Recuperar");
        btnRecuperar.setBackground(new Color(80, 80, 80));
        btnRecuperar.setForeground(Color.WHITE);
        btnRecuperar.setFocusPainted(false);
        btnRecuperar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRecuperar.addActionListener(e -> recuperarPassword());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(120, 120, 120));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnRecuperar);
        buttonPanel.add(btnCancelar);

        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Enter para recuperar
        getRootPane().setDefaultButton(btnRecuperar);
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(400, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setPreferredSize(new Dimension(150, 25));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void recuperarPassword() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String nuevaPassword = new String(txtNuevaPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());

        // Validaciones
        if (username.isEmpty() || email.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtNuevaPassword.setText("");
            txtConfirmarPassword.setText("");
            txtNuevaPassword.requestFocus();
            return;
        }

        if (nuevaPassword.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 4 caracteres",
                    "Contraseña Débil",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar usuario y email
        Usuario usuario = userManager.buscarPorUsername(username);
        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                    "Usuario no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!usuario.getEmail().equalsIgnoreCase(email)) {
            JOptionPane.showMessageDialog(this,
                    "El email no coincide con el usuario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cambiar contraseña
        if (userManager.cambiarPassword(username, nuevaPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Contraseña recuperada exitosamente.\nYa puede iniciar sesión con su nueva contraseña.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar la contraseña",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}