package models;

public class Usuario {
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private String rol; // "ADMIN" o "CAJERO"

    public Usuario(String username, String password, String nombreCompleto, String email, String rol) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol != null ? rol : "CAJERO"; // Por defecto CAJERO
    }

    // Constructor sin email (compatibilidad)
    public Usuario(String username, String password, String nombreCompleto) {
        this(username, password, nombreCompleto, "", "CAJERO");
    }

    // Constructor con email pero sin rol (compatibilidad)
    public Usuario(String username, String password, String nombreCompleto, String email) {
        this(username, password, nombreCompleto, email, "CAJERO");
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // Método de conveniencia para verificar si es admin
    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(rol);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
