package utils;

import models.Ticket;
import java.io.*;
import java.text.SimpleDateFormat;

public class TicketGenerator {
    private static final String TICKETS_DIR = "tickets/";

    public TicketGenerator() {
        // Crear directorio de tickets si no existe
        File dir = new File(TICKETS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String guardarTicket(Ticket ticket) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String nombreArchivo = TICKETS_DIR + "ticket_" +
                String.format("%06d", ticket.getNumeroTicket()) +
                "_" + sdf.format(ticket.getFecha()) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(ticket.generarTextoTicket());
            return nombreArchivo;
        } catch (IOException e) {
            System.err.println("Error al guardar ticket: " + e.getMessage());
            return null;
        }
    }

    public boolean abrirTicket(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                return false;
            }

            // Intentar abrir con el programa predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Error al abrir ticket: " + e.getMessage());
            return false;
        }
    }
}
