package ui;

import models.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainMDIFrame extends JFrame {
    private JDesktopPane desktopPane;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Usuario usuarioActual;
    private Timer relojTimer;

    // Menús
    private JMenuBar menuBar;
    private JMenu menuArchivo;
    private JMenu menuVentas;
    private JMenu menuReportes;
    private JMenu menuVentana;
    private JMenu menuAyuda;

    // Toolbar
    private JToolBar toolBar;

    public MainMDIFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        iniciarReloj();
        setLocationRelativeTo(null);
        setVisible(true);

        // Abrir automáticamente una ventana TPV al iniciar
        SwingUtilities.invokeLater(() -> {
            abrirTPV();
        });
    }

    private void initComponents() {
        setTitle("TPV - Sistema de Punto de Venta");
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Desktop Pane (área de trabajo MDI)
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(220, 220, 220));

        // Agregar imagen de fondo opcional
        desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        // Crear menú
        crearMenu();

        // Crear toolbar
        crearToolBar();

        // Crear barra de estado
        JPanel statusBar = crearBarraEstado();

        // Layout principal
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(desktopPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Listener para cerrar ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
    }

    private void crearMenu() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(60, 60, 60));

        // Menú Archivo
        menuArchivo = new JMenu("Archivo");
        menuArchivo.setForeground(Color.WHITE);
        menuArchivo.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem itemNuevaVenta = new JMenuItem("Nueva Venta");
        itemNuevaVenta.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemNuevaVenta.addActionListener(e -> abrirTPV());

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        itemSalir.addActionListener(e -> confirmarSalida());

        menuArchivo.add(itemNuevaVenta);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrarSesion);
        menuArchivo.add(itemSalir);

        // Menú Ventas
        menuVentas = new JMenu("Ventas");
        menuVentas.setForeground(Color.WHITE);
        menuVentas.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem itemTPV = new JMenuItem("Terminal Punto de Venta");
        itemTPV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        itemTPV.addActionListener(e -> abrirTPV());

        JMenuItem itemHistorialVentas = new JMenuItem("Historial de Ventas");
        itemHistorialVentas.addActionListener(e -> mostrarHistorialVentas());

        menuVentas.add(itemTPV);
        menuVentas.add(itemHistorialVentas);

        // Menú Reportes
        menuReportes = new JMenu("Reportes");
        menuReportes.setForeground(Color.WHITE);
        menuReportes.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem itemVentasDia = new JMenuItem("Ventas del Día");
        itemVentasDia.addActionListener(e -> mostrarReporte("Ventas del Día"));

        JMenuItem itemProductos = new JMenuItem("Productos Vendidos");
        itemProductos.addActionListener(e -> mostrarReporte("Productos Vendidos"));

        menuReportes.add(itemVentasDia);
        menuReportes.add(itemProductos);

        // Menú Ventana
        menuVentana = new JMenu("Ventana");
        menuVentana.setForeground(Color.WHITE);
        menuVentana.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem itemCascada = new JMenuItem("Cascada");
        itemCascada.addActionListener(e -> organizarVentanasCascada());

        JMenuItem itemMosaico = new JMenuItem("Mosaico");
        itemMosaico.addActionListener(e -> organizarVentanasMosaico());

        JMenuItem itemCerrarTodas = new JMenuItem("Cerrar Todas");
        itemCerrarTodas.addActionListener(e -> cerrarTodasVentanas());

        menuVentana.add(itemCascada);
        menuVentana.add(itemMosaico);
        menuVentana.addSeparator();
        menuVentana.add(itemCerrarTodas);

        // Menú Ayuda
        menuAyuda = new JMenu("Ayuda");
        menuAyuda.setForeground(Color.WHITE);
        menuAyuda.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());

        JMenuItem itemManual = new JMenuItem("Manual de Usuario");
        itemManual.addActionListener(e -> mostrarManual());

        menuAyuda.add(itemManual);
        menuAyuda.add(itemAcercaDe);

        // Agregar menús a la barra
        menuBar.add(menuArchivo);
        menuBar.add(menuVentas);
        menuBar.add(menuReportes);
        menuBar.add(menuVentana);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    private void crearToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(70, 70, 70));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Botón Nueva Venta
        JButton btnNuevaVenta = crearBotonToolBar("🛒 Nueva Venta", "Abrir Terminal de Ventas");
        btnNuevaVenta.addActionListener(e -> abrirTPV());
        toolBar.add(btnNuevaVenta);

        toolBar.addSeparator();

        // Botón Historial
        JButton btnHistorial = crearBotonToolBar("📋 Historial", "Ver historial de ventas");
        btnHistorial.addActionListener(e -> mostrarHistorialVentas());
        toolBar.add(btnHistorial);

        toolBar.addSeparator();

        // Botón Reportes
        JButton btnReportes = crearBotonToolBar("📊 Reportes", "Ver reportes");
        btnReportes.addActionListener(e -> mostrarReporte("Reportes"));
        toolBar.add(btnReportes);

        toolBar.addSeparator();

        // Espacio flexible
        toolBar.add(Box.createHorizontalGlue());

        // Botón Cerrar Sesión
        JButton btnCerrarSesion = crearBotonToolBar("🔓 Cerrar Sesión", "Cerrar sesión actual");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        toolBar.add(btnCerrarSesion);
    }

    private JButton crearBotonToolBar(String texto, String tooltip) {
        JButton btn = new JButton(texto);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 60, 60));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setToolTipText(tooltip);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));

        // Efecto hover mejorado
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(100, 100, 100));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }
        });

        return btn;
    }

    private JPanel crearBarraEstado() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(60, 60, 60));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Panel izquierdo - Usuario
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        lblUsuario = new JLabel("👤 Usuario: " + usuarioActual.getNombreCompleto());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 11));
        leftPanel.add(lblUsuario);

        // Panel derecho - Fecha y hora
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        lblFechaHora = new JLabel();
        lblFechaHora.setForeground(Color.WHITE);
        lblFechaHora.setFont(new Font("Arial", Font.PLAIN, 11));
        rightPanel.add(lblFechaHora);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }

    private void iniciarReloj() {
        relojTimer = new Timer(1000, e -> actualizarReloj());
        relojTimer.start();
        actualizarReloj();
    }

    private void actualizarReloj() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy - HH:mm:ss");
        lblFechaHora.setText("🕐 " + sdf.format(new Date()));
    }

    // Métodos de funcionalidad
    private void abrirTPV() {
        JInternalFrame frame = new JInternalFrame("Terminal Punto de Venta", true, true, true, true);
        frame.setSize(1000, 650);
        frame.setLocation(30, 30);

        // Agregar el panel TPV real
        TPVPanel tpvPanel = new TPVPanel(usuarioActual);
        frame.add(tpvPanel);

        desktopPane.add(frame);
        frame.setVisible(true);

        try {
            frame.setMaximum(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarHistorialVentas() {
        JInternalFrame frame = new JInternalFrame("Historial de Ventas", true, true, true, true);
        frame.setSize(900, 600);
        frame.setLocation(50, 50);

        HistorialVentasPanel historialPanel = new HistorialVentasPanel();
        frame.add(historialPanel);

        desktopPane.add(frame);
        frame.setVisible(true);

        try {
            frame.setMaximum(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarReporte(String tipoReporte) {
        JInternalFrame frame = new JInternalFrame("Reportes de Ventas", true, true, true, true);
        frame.setSize(1000, 700);
        frame.setLocation(70, 70);

        ReportesPanel reportesPanel = new ReportesPanel();
        frame.add(reportesPanel);

        desktopPane.add(frame);
        frame.setVisible(true);

        try {
            frame.setMaximum(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void organizarVentanasCascada() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        int offset = 30;
        for (int i = 0; i < frames.length; i++) {
            frames[i].setLocation(offset * i, offset * i);
            frames[i].setSize(700, 500);
            try {
                frames[i].setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void organizarVentanasMosaico() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) return;

        int cols = (int) Math.ceil(Math.sqrt(frames.length));
        int rows = (int) Math.ceil((double) frames.length / cols);

        int width = desktopPane.getWidth() / cols;
        int height = desktopPane.getHeight() / rows;

        int index = 0;
        for (int row = 0; row < rows && index < frames.length; row++) {
            for (int col = 0; col < cols && index < frames.length; col++) {
                frames[index].setBounds(col * width, row * height, width, height);
                index++;
            }
        }
    }

    private void cerrarTodasVentanas() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }

    private void mostrarManual() {
        String manual = "MANUAL DE USUARIO - TPV\n\n" +
                "1. NUEVA VENTA: Ctrl+N o menú Archivo > Nueva Venta\n" +
                "2. ESCANEAR PRODUCTOS: Use el lector de código de barras\n" +
                "3. MODIFICAR CANTIDAD: Seleccione producto y use botón Modificar\n" +
                "4. PROCESAR PAGO: Ingrese efectivo o use tarjeta\n" +
                "5. GENERAR TICKET: Se genera automáticamente al completar venta\n\n" +
                "ATAJOS DE TECLADO:\n" +
                "- Ctrl+N: Nueva venta\n" +
                "- Ctrl+T: Abrir TPV\n" +
                "- Ctrl+Q: Salir\n";

        JTextArea textArea = new JTextArea(manual);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Manual de Usuario",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "TPV - Terminal Punto de Venta\n\n" +
                        "Versión: 1.0\n" +
                        "Año: 2026\n\n" +
                        "Sistema de gestión de ventas para supermercados\n" +
                        "Desarrollado con Java Swing\n\n" +
                        "Características:\n" +
                        "- Lectura de código de barras\n" +
                        "- Gestión de productos\n" +
                        "- Múltiples formas de pago\n" +
                        "- Generación de tickets\n" +
                        "- Interfaz MDI moderna",
                "Acerca de TPV",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            relojTimer.stop();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame());
        }
    }

    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea salir del sistema?",
                "Salir",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            relojTimer.stop();
            System.exit(0);
        } else {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }
}