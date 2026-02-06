package ui;

import models.Producto;
import models.Ticket;
import models.Usuario;
import utils.ProductManager;
import utils.TicketGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class TPVPanel extends JPanel {
    // Componentes principales
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JTextField txtCodigoBarras;
    private JButton btnEscanear;
    private JButton btnCamara;
    private JTextField txtEfectivo;
    private JTextField txtTarjeta;
    private JLabel lblVuelta;
    private JButton btnPagarEfectivo;
    private JButton btnPagarTarjeta;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JButton btnGenerarTicket;
    private JButton btnNuevaVenta;

    // Managers
    private ProductManager productManager;
    private TicketGenerator ticketGenerator;
    private Usuario usuarioActual;

    // Variables
    private double totalVenta = 0.0;

    public TPVPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.productManager = new ProductManager();
        this.ticketGenerator = new TicketGenerator();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Panel principal con dos columnas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.6);

        // PANEL IZQUIERDO - Lista de productos
        JPanel panelIzquierdo = crearPanelIzquierdo();
        splitPane.setLeftComponent(panelIzquierdo);

        // PANEL DERECHO - Lector y pagos
        JPanel panelDerecho = crearPanelDerecho();
        splitPane.setRightComponent(panelDerecho);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Lista de Productos"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Cantidad", "Precio", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable directamente
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setRowHeight(25);
        tablaProductos.setBackground(Color.WHITE);  // Fondo blanco para las celdas
        tablaProductos.setForeground(Color.BLACK);  // Texto negro para las celdas
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(70, 70, 70));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ancho de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));  // Fondo del scroll
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones y total
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBackground(new Color(245, 245, 245));

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBotones.setBackground(new Color(245, 245, 245));

        btnEliminar = crearBoton("ELIMINAR", new Color(120, 120, 120));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        btnModificar = crearBoton("MODIFICAR", new Color(90, 90, 90));
        btnModificar.addActionListener(e -> modificarCantidad());
        panelBotones.add(btnModificar);

        btnNuevaVenta = crearBoton("NUEVA VENTA", new Color(60, 60, 60));
        btnNuevaVenta.addActionListener(e -> nuevaVenta());
        panelBotones.add(btnNuevaVenta);

        panelInferior.add(panelBotones, BorderLayout.WEST);

        // Total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(new Color(240, 240, 240));
        panelTotal.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));

        JLabel lblTotalTexto = new JLabel("TOTAL: ");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalTexto.setForeground(new Color(60, 60, 60));
        panelTotal.add(lblTotalTexto);

        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 28));
        lblTotal.setForeground(new Color(60, 60, 60));
        panelTotal.add(lblTotal);

        panelInferior.add(panelTotal, BorderLayout.EAST);

        panel.add(panelInferior, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // SECCIÓN: LECTOR CÓDIGO DE BARRAS
        panel.add(crearSeccionLectorBarras());
        panel.add(Box.createVerticalStrut(20));

        // SECCIÓN: CÓDIGO DE BARRAS (visualización)
        panel.add(crearSeccionCodigoBarras());
        panel.add(Box.createVerticalStrut(20));

        // SECCIÓN: PAGO EFECTIVO
        panel.add(crearSeccionPagoEfectivo());
        panel.add(Box.createVerticalStrut(20));

        // SECCIÓN: PAGO TARJETA
        panel.add(crearSeccionPagoTarjeta());
        panel.add(Box.createVerticalStrut(20));

        // SECCIÓN: TICKET
        panel.add(crearSeccionTicket());

        // Espacio flexible
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearSeccionLectorBarras() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setMaximumSize(new Dimension(400, 200));  // Aumenté la altura para el escáner simulado
        panel.setBackground(new Color(90, 90, 90));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("LECTOR CÓDIGO BARRAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);

        // Primera fila - Código manual
        JPanel panelManual = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelManual.setOpaque(false);

        JLabel lblCodigo = new JLabel("Código Barras:");
        lblCodigo.setForeground(Color.WHITE);
        lblCodigo.setFont(new Font("Arial", Font.BOLD, 12));
        panelManual.add(lblCodigo);

        txtCodigoBarras = new JTextField(15);
        txtCodigoBarras.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCodigoBarras.addActionListener(e -> escanearProducto());
        panelManual.add(txtCodigoBarras);

        btnEscanear = new JButton(">>");
        btnEscanear.setFont(new Font("Arial", Font.BOLD, 12));
        btnEscanear.setBackground(new Color(100, 100, 100));
        btnEscanear.setForeground(Color.WHITE);
        btnEscanear.setFocusPainted(false);
        btnEscanear.setBorderPainted(false);
        btnEscanear.setOpaque(true);
        btnEscanear.addActionListener(e -> escanearProducto());
        panelManual.add(btnEscanear);

        panelCentral.add(panelManual);

        panelCentral.add(Box.createVerticalStrut(10));

        // NUEVA SECCIÓN: ESCÁNER SIMULADO
        JPanel panelEscaner = crearPanelEscanerSimulado();
        panelCentral.add(panelEscaner);

        panelCentral.add(Box.createVerticalStrut(5));

        // Segunda fila - Botón de cámara
        JPanel panelCamara = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelCamara.setOpaque(false);

        btnCamara = new JButton("USAR CÁMARA");
        btnCamara.setFont(new Font("Arial", Font.BOLD, 12));
        btnCamara.setBackground(new Color(70, 70, 70));
        btnCamara.setForeground(Color.WHITE);
        btnCamara.setFocusPainted(false);
        btnCamara.setBorderPainted(false);
        btnCamara.setOpaque(true);
        btnCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCamara.setPreferredSize(new Dimension(200, 35));
        btnCamara.addActionListener(e -> usarCamara());
        panelCamara.add(btnCamara);

        panelCentral.add(panelCamara);

        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSeccionCodigoBarras() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(400, 60));
        panel.setBackground(new Color(200, 200, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel titulo = new JLabel("CÓDIGO BARRAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(70, 70, 70));

        panel.add(titulo, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSeccionPagoEfectivo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(400, 150));
        panel.setBackground(new Color(100, 100, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("PAGO EFECTIVO");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(10));

        // Campo efectivo
        JPanel panelEfectivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelEfectivo.setOpaque(false);

        JLabel lblEfectivo = new JLabel("Efectivo:");
        lblEfectivo.setForeground(Color.WHITE);
        lblEfectivo.setFont(new Font("Arial", Font.BOLD, 12));
        panelEfectivo.add(lblEfectivo);

        txtEfectivo = new JTextField(12);
        txtEfectivo.setFont(new Font("Arial", Font.PLAIN, 14));
        panelEfectivo.add(txtEfectivo);

        btnPagarEfectivo = new JButton(">>");
        btnPagarEfectivo.setFont(new Font("Arial", Font.BOLD, 12));
        btnPagarEfectivo.setBackground(new Color(80, 80, 80));
        btnPagarEfectivo.setForeground(Color.WHITE);
        btnPagarEfectivo.setFocusPainted(false);
        btnPagarEfectivo.setBorderPainted(false);
        btnPagarEfectivo.setOpaque(true);
        btnPagarEfectivo.addActionListener(e -> procesarPagoEfectivo());
        panelEfectivo.add(btnPagarEfectivo);

        panelEfectivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelEfectivo);

        panel.add(Box.createVerticalStrut(10));

        // Vuelta
        JPanel panelVuelta = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelVuelta.setOpaque(false);

        JLabel lblVueltaTexto = new JLabel("Vuelta:");
        lblVueltaTexto.setForeground(Color.WHITE);
        lblVueltaTexto.setFont(new Font("Arial", Font.BOLD, 12));
        panelVuelta.add(lblVueltaTexto);

        lblVuelta = new JLabel("$0.00");
        lblVuelta.setFont(new Font("Arial", Font.BOLD, 18));
        lblVuelta.setForeground(Color.WHITE);
        panelVuelta.add(lblVuelta);

        panelVuelta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelVuelta);

        return panel;
    }

    private JPanel crearSeccionPagoTarjeta() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(400, 100));
        panel.setBackground(new Color(80, 80, 80));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("PAGO TARJETA");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(10));

        JPanel panelTarjeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTarjeta.setOpaque(false);

        JLabel lblTarjeta = new JLabel("Tarjeta:");
        lblTarjeta.setForeground(Color.WHITE);
        lblTarjeta.setFont(new Font("Arial", Font.BOLD, 12));
        panelTarjeta.add(lblTarjeta);

        txtTarjeta = new JTextField(12);
        txtTarjeta.setFont(new Font("Arial", Font.PLAIN, 14));
        panelTarjeta.add(txtTarjeta);

        btnPagarTarjeta = new JButton(">>");
        btnPagarTarjeta.setFont(new Font("Arial", Font.BOLD, 12));
        btnPagarTarjeta.setBackground(new Color(60, 60, 60));
        btnPagarTarjeta.setForeground(Color.WHITE);
        btnPagarTarjeta.setFocusPainted(false);
        btnPagarTarjeta.setBorderPainted(false);
        btnPagarTarjeta.setOpaque(true);
        btnPagarTarjeta.addActionListener(e -> procesarPagoTarjeta());
        panelTarjeta.add(btnPagarTarjeta);

        panelTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelTarjeta);

        return panel;
    }

    private JPanel crearSeccionTicket() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(400, 70));
        panel.setBackground(new Color(120, 120, 120));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        btnGenerarTicket = new JButton("GENERAR TICKET DE COMPRA");
        btnGenerarTicket.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerarTicket.setBackground(new Color(90, 90, 90));
        btnGenerarTicket.setForeground(Color.WHITE);
        btnGenerarTicket.setFocusPainted(false);
        btnGenerarTicket.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerarTicket.setEnabled(false);
        btnGenerarTicket.addActionListener(e -> generarTicket());

        panel.add(btnGenerarTicket, BorderLayout.CENTER);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // NUEVO MÉTODO: Panel del escáner simulado
// NUEVO MÉTODO: Panel del escáner simulado
    private JPanel crearPanelEscanerSimulado() {
        JPanel panelEscaner = new JPanel(new BorderLayout());
        panelEscaner.setMaximumSize(new Dimension(350, 60));
        panelEscaner.setPreferredSize(new Dimension(350, 60));
        panelEscaner.setBackground(new Color(50, 50, 50));
        panelEscaner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 100), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelEscaner.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Label con ícono de código de barras
        JLabel lblEscaner = new JLabel("PASAR PRODUCTO POR AQUÍ");
        lblEscaner.setFont(new Font("Arial", Font.BOLD, 12));
        lblEscaner.setForeground(new Color(100, 200, 100));
        lblEscaner.setHorizontalAlignment(SwingConstants.CENTER);
        panelEscaner.add(lblEscaner, BorderLayout.CENTER);

        // Label de instrucción
        JLabel lblInstruccion = new JLabel("(Pasar cursor para escanear producto aleatorio)");
        lblInstruccion.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInstruccion.setForeground(new Color(180, 180, 180));
        lblInstruccion.setHorizontalAlignment(SwingConstants.CENTER);
        panelEscaner.add(lblInstruccion, BorderLayout.SOUTH);

        // Variable para controlar el cooldown del escaneo
        final boolean[] puedeEscanear = {true};

        // Efectos de hover
        panelEscaner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Cambiar apariencia al entrar
                panelEscaner.setBackground(new Color(70, 70, 70));
                lblEscaner.setForeground(new Color(150, 255, 150));
                panelEscaner.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 255, 150), 3),
                        BorderFactory.createEmptyBorder(7, 9, 7, 9)
                ));

                // Si puede escanear, realizar el escaneo automático
                if (puedeEscanear[0]) {
                    puedeEscanear[0] = false;

                    // Efecto visual de escaneo
                    panelEscaner.setBackground(new Color(100, 255, 100));
                    lblEscaner.setText("⚡ ESCANEANDO... ⚡");

                    // Reproducir sonido del sistema (beep)
                    Toolkit.getDefaultToolkit().beep();

                    // Timer para simular el escaneo y agregar producto
                    Timer timer = new Timer(300, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            // Agregar producto aleatorio SIN mostrar mensaje
                            agregarProductoAleatorioSilencioso();

                            // Restaurar apariencia (si el mouse todavía está encima)
                            Component comp = panelEscaner.getComponentAt(
                                    panelEscaner.getWidth() / 2,
                                    panelEscaner.getHeight() / 2
                            );

                            if (comp != null) {
                                panelEscaner.setBackground(new Color(70, 70, 70));
                                lblEscaner.setForeground(new Color(150, 255, 150));
                                lblEscaner.setText("🔲 PASAR PRODUCTO POR AQUÍ 🔲");
                            }

                            ((Timer)evt.getSource()).stop();

                            // Habilitar nuevo escaneo después de 1 segundo
                            Timer cooldownTimer = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    puedeEscanear[0] = true;
                                    ((Timer)e.getSource()).stop();
                                }
                            });
                            cooldownTimer.setRepeats(false);
                            cooldownTimer.start();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panelEscaner.setBackground(new Color(50, 50, 50));
                lblEscaner.setForeground(new Color(100, 200, 100));
                lblEscaner.setText("PASAR PRODUCTO POR AQUÍ");
                panelEscaner.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 200, 100), 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });

        return panelEscaner;
    }

    // NUEVO MÉTODO: Agregar producto aleatorio
    // NUEVO MÉTODO: Agregar producto aleatorio SIN mostrar mensaje
    private void agregarProductoAleatorioSilencioso() {
        Producto productoAleatorio = productManager.getProductoAleatorio();

        if (productoAleatorio != null) {
            agregarProductoATabla(productoAleatorio);
            // NO mostrar mensaje - se agrega silenciosamente
        } else {
            // Solo mostrar error si no hay productos
            JOptionPane.showMessageDialog(this,
                    "No hay productos disponibles en el sistema",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MÉTODOS DE LÓGICA

    private void usarCamara() {
        try {
            // Obtener el frame padre
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);

            // Abrir el diálogo de escaneo
            String codigo = BarcodeReaderDialog.escanearCodigo(frame);

            if (codigo != null && !codigo.isEmpty()) {
                txtCodigoBarras.setText(codigo);
                escanearProducto();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al acceder a la cámara:\n" + e.getMessage() +
                            "\n\nAsegúrese de que:\n" +
                            "1. Su cámara esté conectada\n" +
                            "2. Las librerías ZXing estén instaladas\n" +
                            "3. No haya otra aplicación usando la cámara",
                    "Error de Cámara",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void escanearProducto() {
        String codigo = txtCodigoBarras.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un código de barras",
                    "Código Vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto producto = productManager.buscarPorCodigo(codigo);

        if (producto != null) {
            agregarProductoATabla(producto);
            txtCodigoBarras.setText("");
            txtCodigoBarras.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Producto no encontrado con código: " + codigo,
                    "Producto No Encontrado",
                    JOptionPane.ERROR_MESSAGE);
            txtCodigoBarras.selectAll();
        }
    }

    private void agregarProductoATabla(Producto producto) {
        // Verificar si el producto ya está en la tabla
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String codigoEnTabla = (String) modeloTabla.getValueAt(i, 0);
            if (codigoEnTabla.equals(producto.getCodigo())) {
                // Producto ya existe, aumentar cantidad
                int cantidadActual = (Integer) modeloTabla.getValueAt(i, 2);
                int nuevaCantidad = cantidadActual + 1;
                double precio = (Double) modeloTabla.getValueAt(i, 3);
                double nuevoSubtotal = precio * nuevaCantidad;

                modeloTabla.setValueAt(nuevaCantidad, i, 2);
                modeloTabla.setValueAt(nuevoSubtotal, i, 4);

                calcularTotal();
                return;
            }
        }

        // Producto nuevo, agregarlo a la tabla
        Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCantidad(),
                producto.getPrecio(),
                producto.getSubtotal()
        };
        modeloTabla.addRow(fila);
        calcularTotal();
    }

    private void calcularTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            double subtotal = (Double) modeloTabla.getValueAt(i, 4);
            totalVenta += subtotal;
        }
        lblTotal.setText(String.format("$%.2f", totalVenta));
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para eliminar",
                    "Sin Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este producto?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaSeleccionada);
            calcularTotal();
        }
    }

    private void modificarCantidad() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para modificar",
                    "Sin Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidadActual = (Integer) modeloTabla.getValueAt(filaSeleccionada, 2);
        String input = JOptionPane.showInputDialog(this,
                "Ingrese la nueva cantidad:",
                cantidadActual);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int nuevaCantidad = Integer.parseInt(input.trim());

                if (nuevaCantidad <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a 0",
                            "Cantidad Inválida",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double precio = (Double) modeloTabla.getValueAt(filaSeleccionada, 3);
                double nuevoSubtotal = precio * nuevaCantidad;

                modeloTabla.setValueAt(nuevaCantidad, filaSeleccionada, 2);
                modeloTabla.setValueAt(nuevoSubtotal, filaSeleccionada, 4);

                calcularTotal();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese un número válido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void procesarPagoEfectivo() {
        if (totalVenta == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta",
                    "Venta Vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String efectivoStr = txtEfectivo.getText().trim();

        if (efectivoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese el monto en efectivo",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double efectivo = Double.parseDouble(efectivoStr);

            if (efectivo < totalVenta) {
                JOptionPane.showMessageDialog(this,
                        String.format("El efectivo ($%.2f) es menor que el total ($%.2f)",
                                efectivo, totalVenta),
                        "Efectivo Insuficiente",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double vuelto = efectivo - totalVenta;
            lblVuelta.setText(String.format("$%.2f", vuelto));

            // Habilitar botón de generar ticket
            btnGenerarTicket.setEnabled(true);

            JOptionPane.showMessageDialog(this,
                    String.format("Pago procesado exitosamente\nVuelto: $%.2f", vuelto),
                    "Pago Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un monto válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarPagoTarjeta() {
        if (totalVenta == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta",
                    "Venta Vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simular procesamiento de tarjeta
        int opcion = JOptionPane.showConfirmDialog(this,
                String.format("¿Confirma el pago con tarjeta por $%.2f?", totalVenta),
                "Confirmar Pago con Tarjeta",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            txtTarjeta.setText(String.format("%.2f", totalVenta));
            lblVuelta.setText("$0.00");

            // Habilitar botón de generar ticket
            btnGenerarTicket.setEnabled(true);

            JOptionPane.showMessageDialog(this,
                    "Pago con tarjeta procesado exitosamente",
                    "Pago Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generarTicket() {
        // Crear ticket
        Ticket ticket = new Ticket(usuarioActual.getNombreCompleto());

        // Agregar productos
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String codigo = (String) modeloTabla.getValueAt(i, 0);
            String nombre = (String) modeloTabla.getValueAt(i, 1);
            int cantidad = (Integer) modeloTabla.getValueAt(i, 2);
            double precio = (Double) modeloTabla.getValueAt(i, 3);

            Producto prod = new Producto(codigo, nombre, precio, cantidad);
            ticket.agregarProducto(prod);
        }

        // Configurar pagos
        String efectivoStr = txtEfectivo.getText().trim();
        String tarjetaStr = txtTarjeta.getText().trim();

        double efectivo = 0.0;
        double tarjeta = 0.0;

        try {
            if (!efectivoStr.isEmpty()) {
                efectivo = Double.parseDouble(efectivoStr);
            }
            if (!tarjetaStr.isEmpty()) {
                tarjeta = Double.parseDouble(tarjetaStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        ticket.setEfectivo(efectivo);
        ticket.setTarjeta(tarjeta);

        if (efectivo > 0 && tarjeta > 0) {
            ticket.setTipoPago("MIXTO");
        } else if (efectivo > 0) {
            ticket.setTipoPago("EFECTIVO");
            double vuelto = efectivo - totalVenta;
            ticket.setVuelto(vuelto);
        } else {
            ticket.setTipoPago("TARJETA");
        }

        // Guardar ticket
        String rutaTicket = ticketGenerator.guardarTicket(ticket);

        if (rutaTicket != null) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Ticket generado exitosamente:\n" + rutaTicket + "\n\n¿Desea abrir el ticket?",
                    "Ticket Generado",
                    JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                ticketGenerator.abrirTicket(rutaTicket);
            }

            // Limpiar para nueva venta
            nuevaVenta();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el ticket",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nuevaVenta() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Limpiar campos
        txtCodigoBarras.setText("");
        txtEfectivo.setText("");
        txtTarjeta.setText("");
        lblVuelta.setText("$0.00");

        // Resetear total
        totalVenta = 0.0;
        lblTotal.setText("$0.00");

        // Deshabilitar botón de ticket
        btnGenerarTicket.setEnabled(false);

        // Focus en código de barras
        txtCodigoBarras.requestFocus();
    }
}