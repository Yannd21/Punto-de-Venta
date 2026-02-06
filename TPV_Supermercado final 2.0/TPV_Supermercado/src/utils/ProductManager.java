package utils;

import models.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static final String PRODUCTS_FILE = "src/resources/productos.txt";
    private List<Producto> productos;

    public ProductManager() {
        productos = new ArrayList<>();
        cargarProductos();
    }

    private void cargarProductos() {
        File file = new File(PRODUCTS_FILE);

        // Si el archivo no existe, crear productos por defecto
        if (!file.exists()) {
            crearProductosPorDefecto();
            guardarProductos();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    String codigo = datos[0].trim();
                    String nombre = datos[1].trim();
                    // Quitar el símbolo $ si existe
                    String precioStr = datos[2].trim().replace("$", "");
                    double precio = Double.parseDouble(precioStr);

                    productos.add(new Producto(codigo, nombre, precio));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            crearProductosPorDefecto();
        }
    }

    private void crearProductosPorDefecto() {
        productos.clear();
        // Productos de supermercado
        productos.add(new Producto("7501234567890", "Leche Entera 1L", 1.50));
        productos.add(new Producto("7501234567891", "Pan Integral 500g", 0.80));
        productos.add(new Producto("7501234567892", "Arroz Blanco 1kg", 2.30));
        productos.add(new Producto("7501234567893", "Aceite Oliva 500ml", 4.50));
        productos.add(new Producto("7501234567894", "Huevos x12", 3.20));
        productos.add(new Producto("7501234567895", "Azúcar 1kg", 1.10));
        productos.add(new Producto("7501234567896", "Sal 500g", 0.60));
        productos.add(new Producto("7501234567897", "Café 250g", 5.80));
        productos.add(new Producto("7501234567898", "Mantequilla 250g", 2.40));
        productos.add(new Producto("7501234567899", "Queso Fresco 500g", 4.20));
        productos.add(new Producto("7501234567800", "Yogurt Natural 1L", 2.10));
        productos.add(new Producto("7501234567801", "Pollo Entero 1kg", 3.50));
        productos.add(new Producto("7501234567802", "Carne Molida 500g", 4.80));
        productos.add(new Producto("7501234567803", "Manzanas 1kg", 2.50));
        productos.add(new Producto("7501234567804", "Plátanos 1kg", 1.20));
        productos.add(new Producto("7501234567805", "Tomates 1kg", 1.80));
        productos.add(new Producto("7501234567806", "Papas 2kg", 2.00));
        productos.add(new Producto("7501234567807", "Cebollas 1kg", 1.30));
        productos.add(new Producto("7501234567808", "Pasta Spaghetti 500g", 1.40));
        productos.add(new Producto("7501234567809", "Atún Lata 140g", 1.90));
        productos.add(new Producto("7501234567810", "Jabón Líquido 500ml", 3.20));
        productos.add(new Producto("7501234567811", "Papel Higiénico x4", 4.50));
        productos.add(new Producto("7501234567812", "Detergente 1L", 5.20));
        productos.add(new Producto("7501234567813", "Coca Cola 2L", 2.80));
        productos.add(new Producto("7501234567814", "Jugo Naranja 1L", 2.20));
    }

    public void guardarProductos() {
        try {
            // Crear directorio resources si no existe
            File resourcesDir = new File("src/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
                for (Producto producto : productos) {
                    bw.write(String.format("%s,%s,$%.2f%n",
                            producto.getCodigo(),
                            producto.getNombre(),
                            producto.getPrecio()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
        }
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equals(codigo)) {
                // Retornar una copia para no modificar el original
                return producto.copiar();
            }
        }
        return null;
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public Producto getProductoAleatorio() {
        if (productos.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * productos.size());
        return productos.get(index).copiar();
    }

    // ========== MÉTODOS CRUD ==========

    /**
     * Agregar un nuevo producto
     */
    public boolean agregarProducto(Producto producto) {
        // Verificar que no exista un producto con el mismo código
        if (buscarPorCodigo(producto.getCodigo()) != null) {
            return false; // Ya existe
        }
        productos.add(producto);
        guardarProductos();
        return true;
    }

    /**
     * Modificar un producto existente
     */
    public boolean modificarProducto(String codigoOriginal, Producto productoModificado) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(codigoOriginal)) {
                productos.set(i, productoModificado);
                guardarProductos();
                return true;
            }
        }
        return false;
    }

    /**
     * Eliminar un producto por código
     */
    public boolean eliminarProducto(String codigo) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(codigo)) {
                productos.remove(i);
                guardarProductos();
                return true;
            }
        }
        return false;
    }

    /**
     * Buscar productos por nombre (búsqueda parcial)
     */
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> resultados = new ArrayList<>();
        String nombreBusqueda = nombre.toLowerCase();
        
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(nombreBusqueda)) {
                resultados.add(producto.copiar());
            }
        }
        
        return resultados;
    }

    /**
     * Recargar productos desde el archivo
     */
    public void recargarProductos() {
        productos.clear();
        cargarProductos();
    }
}
