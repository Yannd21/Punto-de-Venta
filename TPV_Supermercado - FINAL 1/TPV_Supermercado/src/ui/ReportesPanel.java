package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ReportesPanel extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblVentasHoy;
    private JLabel lblTotalHoy;
    private JLabel lblProductoMasVendido;

    public ReportesPanel() {
        initComponents();
        generarReporte();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel superior - Título
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(70, 70, 70));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("📊 REPORTES DE VENTAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panelSuperior.add(titulo, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con dos secciones
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);

        // Sección 1: Estadísticas generales
        JPanel panelEstadisticas = crearPanelEstadisticas();
        splitPane.setTopComponent(panelEstadisticas);

        // Sección 2: Productos más vendidos
        JPanel panelProductos = crearPanelProductos();
        splitPane.setBottomComponent(panelProductos);

        add(splitPane, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelInferior.setBackground(Color.WHITE);

        JButton btnActualizar = crearBoton("Actualizar", new Color(100, 100, 100));
        btnActualizar.addActionListener(e -> {
            generarReporte();
            JOptionPane.showMessageDialog(this, "Reporte actualizado", "Actualizar", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnExportar = crearBoton("Exportar PDF", new Color(80, 80, 80));
        btnExportar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Funcionalidad de exportar a PDF\n(Requiere librería iText - no implementada)",
                    "Exportar",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        panelInferior.add(btnActualizar);
        panelInferior.add(btnExportar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Estadísticas del Día"),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);

        // Tarjeta 1: Ventas de hoy
        JPanel tarjeta1 = crearTarjeta("VENTAS HOY", "0", new Color(90, 90, 90));
        lblVentasHoy = (JLabel) ((JPanel) tarjeta1.getComponent(1)).getComponent(0);

        // Tarjeta 2: Total recaudado
        JPanel tarjeta2 = crearTarjeta("TOTAL RECAUDADO", "$0.00", new Color(100, 100, 100));
        lblTotalHoy = (JLabel) ((JPanel) tarjeta2.getComponent(1)).getComponent(0);

        // Tarjeta 3: Producto más vendido
        JPanel tarjeta3 = crearTarjeta("PRODUCTO MÁS VENDIDO", "-", new Color(110, 110, 110));
        lblProductoMasVendido = (JLabel) ((JPanel) tarjeta3.getComponent(1)).getComponent(0);

        // Tarjeta 4: Promedio por venta
        JPanel tarjeta4 = crearTarjeta("PROMEDIO/VENTA", "$0.00", new Color(120, 120, 120));

        // Tarjeta 5: Ventas efectivo
        JPanel tarjeta5 = crearTarjeta("EFECTIVO", "0", new Color(80, 80, 80));

        // Tarjeta 6: Ventas tarjeta
        JPanel tarjeta6 = crearTarjeta("TARJETA", "0", new Color(70, 70, 70));

        panel.add(tarjeta1);
        panel.add(tarjeta2);
        panel.add(tarjeta3);
        panel.add(tarjeta4);
        panel.add(tarjeta5);
        panel.add(tarjeta6);

        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout(5, 5));
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 11));
        lblTitulo.setForeground(Color.WHITE);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelValor = new JPanel();
        panelValor.setOpaque(false);
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblValor.setForeground(Color.WHITE);
        panelValor.add(lblValor);
        tarjeta.add(panelValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Top 10 Productos Más Vendidos"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        String[] columnas = {"#", "Producto", "Cantidad Vendida", "Total Vendido"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setRowHeight(25);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(70, 70, 70));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    private void generarReporte() {
        modeloTabla.setRowCount(0);

        File ticketsDir = new File("tickets/");
        if (!ticketsDir.exists() || !ticketsDir.isDirectory()) {
            return;
        }

        File[] archivos = ticketsDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            return;
        }

        // Filtrar tickets de hoy
        SimpleDateFormat sdfHoy = new SimpleDateFormat("dd/MM/yyyy");
        String fechaHoy = sdfHoy.format(new Date());

        int ventasHoy = 0;
        double totalHoy = 0.0;
        Map<String, ProductoVendido> productosVendidos = new HashMap<>();

        for (File archivo : archivos) {
            try {
                Scanner scanner = new Scanner(archivo);
                boolean esHoy = false;
                double totalTicket = 0.0;

                while (scanner.hasNextLine()) {
                    String linea = scanner.nextLine();

                    if (linea.startsWith("Fecha:")) {
                        String fecha = linea.substring(6).trim().split(" ")[0];
                        if (fecha.equals(fechaHoy)) {
                            esHoy = true;
                            ventasHoy++;
                        }
                    }

                    if (esHoy && linea.startsWith("TOTAL:")) {
                        String totalStr = linea.substring(6).trim().replace("$", "").trim();
                        totalTicket = Double.parseDouble(totalStr);
                        totalHoy += totalTicket;
                    }
                }
                scanner.close();

                // Segunda pasada para productos (simplificado)
                if (esHoy) {
                    scanner = new Scanner(archivo);
                    boolean enSeccionProductos = false;

                    while (scanner.hasNextLine()) {
                        String linea = scanner.nextLine();

                        if (linea.contains("PRODUCTO") && linea.contains("CNT")) {
                            enSeccionProductos = true;
                            scanner.nextLine(); // Saltar línea separadora
                            continue;
                        }

                        if (enSeccionProductos && linea.contains("=====")) {
                            break;
                        }

                        if (enSeccionProductos && !linea.trim().isEmpty()) {
                            // Parsear línea de producto (simplificado)
                            String[] partes = linea.trim().split("\\s+");
                            if (partes.length >= 3) {
                                try {
                                    String nombreProducto = "";
                                    int cantidad = 0;
                                    double precio = 0.0;

                                    // Extraer nombre (primeras palabras)
                                    int i = 0;
                                    while (i < partes.length - 3) {
                                        nombreProducto += partes[i] + " ";
                                        i++;
                                    }
                                    nombreProducto = nombreProducto.trim();

                                    // Extraer cantidad
                                    cantidad = Integer.parseInt(partes[i]);

                                    // Extraer subtotal (última parte)
                                    String subtotalStr = partes[partes.length - 1].replace("$", "");
                                    double subtotal = Double.parseDouble(subtotalStr);

                                    if (!nombreProducto.isEmpty()) {
                                        ProductoVendido pv = productosVendidos.getOrDefault(nombreProducto,
                                                new ProductoVendido(nombreProducto));
                                        pv.cantidad += cantidad;
                                        pv.totalVendido += subtotal;
                                        productosVendidos.put(nombreProducto, pv);
                                    }
                                } catch (Exception e) {
                                    // Ignorar errores de parseo
                                }
                            }
                        }
                    }
                    scanner.close();
                }

            } catch (Exception e) {
                System.err.println("Error al procesar ticket: " + e.getMessage());
            }
        }

        // Actualizar estadísticas
        lblVentasHoy.setText(String.valueOf(ventasHoy));
        lblTotalHoy.setText(String.format("$%.2f", totalHoy));

        // Ordenar productos por cantidad vendida
        List<ProductoVendido> listaProductos = new ArrayList<>(productosVendidos.values());
        listaProductos.sort((p1, p2) -> Integer.compare(p2.cantidad, p1.cantidad));

        // Producto más vendido
        if (!listaProductos.isEmpty()) {
            lblProductoMasVendido.setText(listaProductos.get(0).nombre);
        }

        // Llenar tabla (top 10)
        int count = 0;
        for (ProductoVendido pv : listaProductos) {
            if (count >= 10) break;

            Object[] fila = {
                    (count + 1),
                    pv.nombre,
                    pv.cantidad,
                    String.format("$%.2f", pv.totalVendido)
            };
            modeloTabla.addRow(fila);
            count++;
        }
    }

    // Clase interna para producto vendido
    private class ProductoVendido {
        String nombre;
        int cantidad;
        double totalVendido;

        ProductoVendido(String nombre) {
            this.nombre = nombre;
            this.cantidad = 0;
            this.totalVendido = 0.0;
        }
    }
}