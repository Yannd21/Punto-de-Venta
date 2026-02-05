package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket {
    private static int contadorTickets = 1;

    private int numeroTicket;
    private Date fecha;
    private String cajero;
    private List<Producto> productos;
    private double total;
    private double efectivo;
    private double tarjeta;
    private double vuelto;
    private String tipoPago; // "EFECTIVO", "TARJETA", "MIXTO"

    public Ticket(String cajero) {
        this.numeroTicket = contadorTickets++;
        this.fecha = new Date();
        this.cajero = cajero;
        this.productos = new ArrayList<>();
        this.total = 0.0;
        this.efectivo = 0.0;
        this.tarjeta = 0.0;
        this.vuelto = 0.0;
        this.tipoPago = "";
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
        calcularTotal();
    }

    public void calcularTotal() {
        total = 0.0;
        for (Producto producto : productos) {
            total += producto.getSubtotal();
        }
    }

    // Getters y Setters
    public int getNumeroTicket() {
        return numeroTicket;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getCajero() {
        return cajero;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        calcularTotal();
    }

    public double getTotal() {
        return total;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }

    public double getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(double tarjeta) {
        this.tarjeta = tarjeta;
    }

    public double getVuelto() {
        return vuelto;
    }

    public void setVuelto(double vuelto) {
        this.vuelto = vuelto;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String generarTextoTicket() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sb.append("================================================\n");
        sb.append("           SUPERMERCADO TPV SYSTEM              \n");
        sb.append("================================================\n");
        sb.append("Ticket #: ").append(String.format("%06d", numeroTicket)).append("\n");
        sb.append("Fecha: ").append(sdf.format(fecha)).append("\n");
        sb.append("Cajero: ").append(cajero).append("\n");
        sb.append("================================================\n\n");

        sb.append(String.format("%-30s %3s %8s %10s\n", "PRODUCTO", "CNT", "PRECIO", "SUBTOTAL"));
        sb.append("------------------------------------------------\n");

        for (Producto p : productos) {
            sb.append(String.format("%-30s %3d $%7.2f  $%9.2f\n",
                    truncar(p.getNombre(), 30),
                    p.getCantidad(),
                    p.getPrecio(),
                    p.getSubtotal()));
        }

        sb.append("================================================\n");
        sb.append(String.format("%-40s $%9.2f\n", "TOTAL:", total));
        sb.append("================================================\n\n");

        sb.append("FORMA DE PAGO: ").append(tipoPago).append("\n");
        if (efectivo > 0) {
            sb.append(String.format("Efectivo recibido: $%.2f\n", efectivo));
        }
        if (tarjeta > 0) {
            sb.append(String.format("Pago con tarjeta: $%.2f\n", tarjeta));
        }
        if (vuelto > 0) {
            sb.append(String.format("Vuelto: $%.2f\n", vuelto));
        }

        sb.append("\n================================================\n");
        sb.append("      ¡GRACIAS POR SU COMPRA!                  \n");
        sb.append("================================================\n");

        return sb.toString();
    }

    private String truncar(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }

    public static void resetContador() {
        contadorTickets = 1;
    }
}