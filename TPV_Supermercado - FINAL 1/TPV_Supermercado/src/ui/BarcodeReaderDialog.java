package ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BarcodeReaderDialog extends JDialog implements Runnable {
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JLabel lblEstado;
    private JLabel lblCodigoDetectado;
    private boolean scanning = false;
    private String codigoDetectado = null;
    private Executor executor = Executors.newSingleThreadExecutor();

    public BarcodeReaderDialog(Frame parent) {
        super(parent, "Lector de Código de Barras", true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(700, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Panel superior - Información
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(52, 73, 94));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Escáner de Código de Barras");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        panelSuperior.add(titulo, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central - Cámara
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            // Obtener la webcam predeterminada
            webcam = Webcam.getDefault();

            if (webcam == null) {
                JOptionPane.showMessageDialog(this,
                        "No se detectó ninguna cámara.\nPor favor, conecte una cámara y reinicie la aplicación.",
                        "Cámara No Detectada",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            // Configurar resolución
            webcam.setViewSize(WebcamResolution.VGA.getSize());

            // Crear panel de webcam
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setPreferredSize(WebcamResolution.VGA.getSize());
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setMirrored(true);

            panelCentral.add(webcamPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al inicializar la cámara:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Panel de instrucciones
        JPanel panelInstrucciones = new JPanel();
        panelInstrucciones.setLayout(new BoxLayout(panelInstrucciones, BoxLayout.Y_AXIS));
        panelInstrucciones.setBackground(new Color(236, 240, 241));
        panelInstrucciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblInstruccion1 = new JLabel("📷 Coloque el código de barras frente a la cámara");
        lblInstruccion1.setFont(new Font("Arial", Font.BOLD, 12));
        lblInstruccion1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInstruccion2 = new JLabel("📏 Asegúrese de que el código esté completo y enfocado");
        lblInstruccion2.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInstruccion2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInstruccion3 = new JLabel("💡 Mantenga buena iluminación");
        lblInstruccion3.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInstruccion3.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInstrucciones.add(lblInstruccion1);
        panelInstrucciones.add(Box.createVerticalStrut(5));
        panelInstrucciones.add(lblInstruccion2);
        panelInstrucciones.add(Box.createVerticalStrut(5));
        panelInstrucciones.add(lblInstruccion3);

        panelCentral.add(panelInstrucciones, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior - Estado y botones
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Estado
        JPanel panelEstado = new JPanel(new GridLayout(2, 1, 5, 5));
        panelEstado.setBackground(Color.WHITE);
        panelEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        lblEstado = new JLabel("Estado: Esperando código...");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstado.setForeground(new Color(52, 152, 219));

        lblCodigoDetectado = new JLabel("Código: -");
        lblCodigoDetectado.setFont(new Font("Arial", Font.PLAIN, 12));

        panelEstado.add(lblEstado);
        panelEstado.add(lblCodigoDetectado);

        panelInferior.add(panelEstado, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> cancelar());

        panelBotones.add(btnCancelar);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

        // Listener para cerrar la webcam al cerrar el diálogo
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarWebcam();
            }
        });
    }

    public void iniciarEscaneo() {
        scanning = true;
        executor.execute(this);
        setVisible(true);
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100); // Escanear cada 100ms

                if (!scanning) {
                    break;
                }

                BufferedImage image = webcam.getImage();

                if (image == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    Result result = new MultiFormatReader().decode(bitmap);

                    if (result != null) {
                        codigoDetectado = result.getText();

                        SwingUtilities.invokeLater(() -> {
                            lblEstado.setText("Estado: ¡Código detectado!");
                            lblEstado.setForeground(new Color(46, 204, 113));
                            lblCodigoDetectado.setText("Código: " + codigoDetectado);
                        });

                        // Emitir sonido de éxito
                        Toolkit.getDefaultToolkit().beep();

                        // Esperar un momento antes de cerrar
                        Thread.sleep(500);

                        scanning = false;
                        SwingUtilities.invokeLater(() -> {
                            cerrarWebcam();
                            dispose();
                        });
                    }
                } catch (NotFoundException e) {
                    // No se encontró código, continuar escaneando
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error al escanear: " + e.getMessage());
            }
        } while (scanning);

        cerrarWebcam();
    }

    private void cancelar() {
        scanning = false;
        codigoDetectado = null;
        cerrarWebcam();
        dispose();
    }

    private void cerrarWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    public String getCodigoDetectado() {
        return codigoDetectado;
    }

    // Método estático para usar fácilmente
    public static String escanearCodigo(Frame parent) {
        BarcodeReaderDialog dialog = new BarcodeReaderDialog(parent);
        dialog.iniciarEscaneo();
        return dialog.getCodigoDetectado();
    }
}