package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistorialVentasPanel extends JPanel {
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalVentas;
    private JLabel lblNumeroVentas;
    private List<TicketInfo> ventas;

    public HistorialVentasPanel() {
        initComponents();
        cargarVentas();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel superior - Título y estadísticas
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBackground(new Color(70, 70, 70));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("HISTORIAL DE VENTAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panelSuperior.add(titulo, BorderLayout.WEST);

        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        panelEstadisticas.setOpaque(false);

        lblNumeroVentas = new JLabel("Ventas: 0");
        lblNumeroVentas.setFont(new Font("Arial", Font.BOLD, 14));
        lblNumeroVentas.setForeground(Color.WHITE);

        lblTotalVentas = new JLabel("Total: $0.00");
        lblTotalVentas.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalVentas.setForeground(new Color(200, 200, 200));

        panelEstadisticas.add(lblNumeroVentas);
        panelEstadisticas.add(lblTotalVentas);

        panelSuperior.add(panelEstadisticas, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de ventas
        String[] columnas = {"# Ticket", "Fecha", "Hora", "Cajero", "Total", "Forma Pago"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaVentas.setRowHeight(30);
        tablaVentas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaVentas.getTableHeader().setBackground(new Color(70, 70, 70));
        tablaVentas.getTableHeader().setForeground(Color.WHITE);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ancho de columnas
        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelInferior.setBackground(Color.WHITE);

        JButton btnVerTicket = crearBoton("Ver Ticket", new Color(90, 90, 90));
        btnVerTicket.addActionListener(e -> verTicketSeleccionado());

        JButton btnEliminar = crearBoton("Eliminar", new Color(120, 120, 120));
        btnEliminar.addActionListener(e -> eliminarTicket());

        panelInferior.add(btnVerTicket);
        panelInferior.add(btnEliminar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    private void cargarVentas() {
        modeloTabla.setRowCount(0);
        ventas = new ArrayList<>();

        File ticketsDir = new File("tickets/");
        if (!ticketsDir.exists() || !ticketsDir.isDirectory()) {
            lblNumeroVentas.setText("Ventas: 0");
            lblTotalVentas.setText("Total: $0.00");
            return;
        }

        File[] archivos = ticketsDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            lblNumeroVentas.setText("Ventas: 0");
            lblTotalVentas.setText("Total: $0.00");
            return;
        }

        double totalGeneral = 0.0;

        for (File archivo : archivos) {
            TicketInfo info = leerTicket(archivo);
            if (info != null) {
                ventas.add(info);
                Object[] fila = {
                        info.numeroTicket,
                        info.fecha,
                        info.hora,
                        info.cajero,
                        String.format("$%.2f", info.total),
                        info.formaPago
                };
                modeloTabla.addRow(fila);
                totalGeneral += info.total;
            }
        }

        lblNumeroVentas.setText("Ventas: " + ventas.size());
        lblTotalVentas.setText(String.format("Total: $%.2f", totalGeneral));
    }

    private TicketInfo leerTicket(File archivo) {
        try {
            java.util.Scanner scanner = new java.util.Scanner(archivo);
            TicketInfo info = new TicketInfo();
            info.archivo = archivo;

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();

                if (linea.startsWith("Ticket #:")) {
                    info.numeroTicket = linea.substring(9).trim();
                }
                else if (linea.startsWith("Fecha:")) {
                    String fechaCompleta = linea.substring(6).trim();
                    String[] partes = fechaCompleta.split(" ");
                    if (partes.length >= 2) {
                        info.fecha = partes[0];
                        info.hora = partes[1];
                    }
                }
                else if (linea.startsWith("Cajero:")) {
                    info.cajero = linea.substring(7).trim();
                }
                else if (linea.startsWith("TOTAL:")) {
                    String totalStr = linea.substring(6).trim();
                    totalStr = totalStr.replace("$", "").trim();
                    info.total = Double.parseDouble(totalStr);
                }
                else if (linea.startsWith("FORMA DE PAGO:")) {
                    info.formaPago = linea.substring(14).trim();
                }
            }
            scanner.close();
            return info;
        } catch (Exception e) {
            System.err.println("Error al leer ticket: " + e.getMessage());
            return null;
        }
    }

    private void verTicketSeleccionado() {
        int filaSeleccionada = tablaVentas.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un ticket para ver",
                    "Sin Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketInfo info = ventas.get(filaSeleccionada);

        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(info.archivo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el ticket:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTicket() {
        int filaSeleccionada = tablaVentas.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un ticket para eliminar",
                    "Sin Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este ticket?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            TicketInfo info = ventas.get(filaSeleccionada);
            if (info.archivo.delete()) {
                JOptionPane.showMessageDialog(this,
                        "Ticket eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarVentas();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el ticket",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Clase interna para información del ticket
    private class TicketInfo {
        String numeroTicket;
        String fecha;
        String hora;
        String cajero;
        double total;
        String formaPago;
        File archivo;
    }
}
