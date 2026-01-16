package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;

public class Usuario {

    private static final String DNI_PATTERN = "^\\d{8}[A-Za-z]$";
    private static final String EMAIL_BASIC = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private String dni;
    private String nombre;
    private String email;
    private Direccion direccion;

    public Usuario(String dni, String nombre, String email, Direccion direccion) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no puede ser nulo o vacío.");
        }
        if (!dni.matches(DNI_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no es válido.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El nombre del usuario no puede ser nulo o vacío.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El email del usuario no puede ser nulo o vacío.");
        }
        if (!email.matches(EMAIL_BASIC)) {
            throw new IllegalArgumentException("ERROR: El email del usuario no es válido.");
        }
        if (direccion == null) {
            throw new IllegalArgumentException("ERROR: La direccion del usuario no puede ser nula.");
        }
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
    }

    public Usuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        this.dni = usuario.getDni();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        if (usuario.getDireccion() != null) {
            this.direccion = new Direccion(usuario.getDireccion());
        }
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no puede ser nulo o vacío.");
        }
        if (!id.matches(DNI_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no es válido.");
        }
        this.dni = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El nombre del usuario no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El email del usuario no puede ser nulo o vacío.");
        }
        if (!email.matches(EMAIL_BASIC)) {
            throw new IllegalArgumentException("ERROR: El email del usuario no es válido.");
        }
        this.email = email;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) obj;
        return dni.equals(usuario.dni) && nombre.equals(usuario.nombre) && email.equals(usuario.email) && Objects.equals(direccion, usuario.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni, nombre, email, direccion);
    }

    @Override
    public String toString() {
        return "Usuario [dni=" + dni + ", nombre=" + nombre + ", email=" + email + ", direccion=" + direccion + "]";
    }
}