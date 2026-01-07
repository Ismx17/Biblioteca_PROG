package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;

public class Usuario {

    private static final String ID_PATTERN = "^[A-Z0-9]{8}$";
    private static final String EMAIL_BASIC = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private String id;
    private String nombre;
    private String email;
    private Direccion direccion;

    public Usuario(String id, String nombre, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no puede ser nulo o vacío.");
        }
        if (!id.matches(ID_PATTERN)) {
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
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public Usuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        this.id = usuario.id;
        this.nombre = usuario.nombre;
        this.email = usuario.email;
        this.direccion = usuario.direccion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no puede ser nulo o vacío.");
        }
        if (!id.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El ID del usuario no es válido.");
        }
        this.id = id;
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
        if (direccion == null) {
            throw new IllegalArgumentException("ERROR: La dirección del usuario no puede ser nula.");
        }
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
        return id.equals(usuario.id) && nombre.equals(usuario.nombre) && email.equals(usuario.email) && Objects.equals(direccion, usuario.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, email, direccion);
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", direccion=" + direccion + "]";
    }
}