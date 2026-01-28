package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;

public class Autor {

    private String nombre;
    private String apellidos;
    private String nacionalidad;

    //Creación del constructor
    public Autor(String nombre, String apellidos, String nacionalidad) {
        setNombre(nombre);
        setApellidos(apellidos);
        setNacionalidad(nacionalidad);
    }

    // Constructor copia
    public Autor(Autor autor) {
        // Valido que el autor existe
        if (autor == null) {
            throw new IllegalArgumentException("ERROR: El autor no puede ser nulo.");
        }
        setNombre(autor.getNombre());
        setApellidos(autor.getApellidos());
        setNacionalidad(autor.getNacionalidad());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El nombre del autor no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: Los apellidos del autor no pueden ser nulos o vacíos.");
        }
        this.apellidos = apellidos;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        if (nacionalidad == null || nacionalidad.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: La nacionalidad del autor no puede ser nula o vacía.");
        }
        this.nacionalidad = nacionalidad;
    }

    // Metodo para obtener el nombre completo del autor
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    // Metodo para obtener las iniciales del autor
    public String iniciales() {
        return nombre.substring(0, 1) + "." + apellidos.substring(0, 1) + ".";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Autor autor = (Autor) obj;
        return nombre.equals(autor.nombre) && apellidos.equals(autor.apellidos) && nacionalidad.equals(autor.nacionalidad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, nacionalidad);
    }

    @Override
    public String toString() {
        return "Autor [nombre=" + nombre + ", apellidos=" + apellidos + ", nacionalidad=" + nacionalidad + "]";
    }
}