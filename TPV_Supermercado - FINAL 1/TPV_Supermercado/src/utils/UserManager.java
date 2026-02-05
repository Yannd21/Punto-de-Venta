package utils;

import models.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String USERS_FILE = "src/resources/usuarios.txt";
    private List<Usuario> usuarios;

    public UserManager() {
        usuarios = new ArrayList<>();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        File file = new File(USERS_FILE);

        // Si el archivo no existe, crear usuarios por defecto
        if (!file.exists()) {
            crearUsuariosPorDefecto();
            guardarUsuarios();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    String username = datos[0].trim();
                    String password = datos[1].trim();
                    String nombreCompleto = datos[2].trim();
                    String email = datos.length > 3 ? datos[3].trim() : "";

                    usuarios.add(new Usuario(username, password, nombreCompleto, email));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            crearUsuariosPorDefecto();
        }
    }

    private void crearUsuariosPorDefecto() {
        usuarios.clear();
        usuarios.add(new Usuario("admin", "admin123", "Administrador", "admin@tpv.com"));
        usuarios.add(new Usuario("cajero1", "cajero123", "Juan Pérez", "juan@tpv.com"));
        usuarios.add(new Usuario("cajero2", "cajero123", "María García", "maria@tpv.com"));
    }

    private void guardarUsuarios() {
        try {
            // Crear directorio resources si no existe
            File resourcesDir = new File("src/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
                for (Usuario usuario : usuarios) {
                    bw.write(String.format("%s,%s,%s,%s%n",
                            usuario.getUsername(),
                            usuario.getPassword(),
                            usuario.getNombreCompleto(),
                            usuario.getEmail()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public Usuario validarUsuario(String username, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username) &&
                    usuario.getPassword().equals(password)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario buscarPorUsername(String username) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean cambiarPassword(String username, String nuevaPassword) {
        Usuario usuario = buscarPorUsername(username);
        if (usuario != null) {
            usuario.setPassword(nuevaPassword);
            guardarUsuarios();
            return true;
        }
        return false;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }
}