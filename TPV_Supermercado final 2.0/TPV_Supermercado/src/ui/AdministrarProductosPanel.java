package ui;

import models.Producto;
import utils.ProductManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdministrarProductosPanel extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private ProductManager productManager;

    public AdministrarProductosPanel() {
        this.productManager = new ProductManager();
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel superior - Título y búsqueda
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central - Tabla de productos
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior - Botones de acción
        JPanel panelInferior = crearPanelBotones();
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(70, 70, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("ADMINISTRAR PRODUCTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.WEST);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBusqueda.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setForeground(Color.WHITE);
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        panelBusqueda.add(lblBuscar);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 12));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarProductos();
            }
        });
        panelBusqueda.add(txtBuscar);

        panel.add(panelBusqueda, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Lista de Productos"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Crear tabla
        String[] columnas = {"Código", "Nombre del Producto", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(70, 70, 70));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ancho de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(400);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);

        btnAgregar = crearBoton("Agregar Producto", new Color(46, 204, 113));
        btnAgregar.addActionListener(e -> agregarProducto());

        btnModificar = crearBoton("Modificar", new Color(52, 152, 219));
        btnModificar.addActionListener(e -> modificarProducto());

        btnEliminar = crearBoton("Eliminar", new Color(231, 76, 60));
        btnEliminar.addActionListener(e -> eliminarProducto());

        btnActualizar = crearBoton("Actualizar Lista", new Color(149, 165, 166));
        btnActualizar.addActionListener(e -> {
            productManager.recargarProductos();
            cargarProductos();
            JOptionPane.showMessageDialog(this, 
                "Lista actualizada", 
                "Actualizar", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(btnAgregar);
        panel.add(btnModificar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        for (Producto producto : productManager.getProductos()) {
            Object[] fila = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    String.format("$%.2f", producto.getPrecio())
            };
            modeloTabla.addRow(fila);
        }
    }

    private void buscarProductos() {
        String textoBusqueda = txtBuscar.getText().trim();
        
        if (textoBusqueda.isEmpty()) {
            cargarProductos();
            return;
        }

        modeloTabla.setRowCount(0);
        
        // Buscar por nombre
        for (Producto producto : productManager.buscarPorNombre(textoBusqueda)) {
            Object[] fila = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    String.format("$%.2f", producto.getPrecio())
            };
            modeloTabla.addRow(fila);
        }

        // Buscar por código
        Producto porCodigo = productManager.buscarPorCodigo(textoBusqueda);
        if (porCodigo != null) {
            boolean existe = false;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (modeloTabla.getValueAt(i, 0).equals(porCodigo.getCodigo())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                Object[] fila = {
                        porCodigo.getCodigo(),
                        porCodigo.getNombre(),
                        String.format("$%.2f", porCodigo.getPrecio())
                };
                modeloTabla.addRow(fila);
            }
        }
    }

    private void agregarProducto() {
        // Crear diálogo para agregar producto
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Agregar Producto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblCodigo = new JLabel("Código de Barras:");
        JTextField txtCodigo = new JTextField();

        JLabel lblNombre = new JLabel("Nombre del Producto:");
        JTextField txtNombre = new JTextField();

        JLabel lblPrecio = new JLabel("Precio:");
        JTextField txtPrecio = new JTextField();

        formPanel.add(lblCodigo);
        formPanel.add(txtCodigo);
        formPanel.add(lblNombre);
        formPanel.add(txtNombre);
        formPanel.add(lblPrecio);
        formPanel.add(txtPrecio);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String precioStr = txtPrecio.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, complete todos los campos",
                        "Campos Vacíos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double precio = Double.parseDouble(precioStr);
                
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "El precio debe ser mayor a 0",
                            "Precio Inválido",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Producto nuevoProducto = new Producto(codigo, nombre, precio);
                
                if (productManager.agregarProducto(nuevoProducto)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Producto agregado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Ya existe un producto con ese código",
                            "Código Duplicado",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "El precio debe ser un número válido",
                        "Precio Inválido",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void modificarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para modificar",
                    "Sin Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoOriginal = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreActual = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String precioActual = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        precioActual = precioActual.replace("$", "");

        // Crear diálogo para modificar producto
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Modificar Producto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblCodigo = new JLabel("Código de Barras:");
        JTextField txtCodigo = new JTextField(codigoOriginal);

        JLabel lblNombre = new JLabel("Nombre del Producto:");
        JTextField txtNombre = new JTextField(nombreActual);

        JLabel lblPrecio = new JLabel("Precio:");
        JTextField txtPrecio = new JTextField(precioActual);

        formPanel.add(lblCodigo);
        formPanel.add(txtCodigo);
        formPanel.add(lblNombre);
        formPanel.add(txtNombre);
        formPanel.add(lblPrecio);
        formPanel.add(txtPrecio);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(52, 152, 219));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String precioStr = txtPrecio.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, complete todos los campos",
                        "Campos Vacíos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double precio = Double.parseDouble(precioStr);
                
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "El precio debe ser mayor a 0",
                            "Precio Inválido",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Producto productoModificado = new Producto(codigo, nombre, precio);
                
                if (productManager.modificarProducto(codigoOriginal, productoModificado)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Producto modificado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Error al modificar el producto",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "El precio debe ser un número válido",
                        "Precio Inválido",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
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

        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el producto?\n\n" +
                        "Código: " + codigo + "\n" +
                        "Nombre: " + nombre + "\n\n" +
                        "Esta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            if (productManager.eliminarProducto(codigo)) {
                JOptionPane.showMessageDialog(this,
                        "Producto eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el producto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
