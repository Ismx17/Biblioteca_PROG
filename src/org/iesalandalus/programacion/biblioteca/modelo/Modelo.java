package org.iesalandalus.programacion.biblioteca.modelo;

import java.time.LocalDate;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Libros;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Prestamos;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Usuarios;

public class Modelo {

    private Libros libros;
    private Usuarios usuarios;
    private Prestamos prestamos;

    public Modelo() {}

    public void comenzar() {
        // Inicializo las estructuras de datos
        this.libros = new Libros();
        this.usuarios = new Usuarios();
        this.prestamos = new Prestamos();
    }

    public void terminar() {
        this.libros = null;
        this.usuarios = null;
        this.prestamos = null;

        System.out.println("El modelo se ha terminado.");
    }

    public void alta(Libro libro) {
        libros.alta(libro);
    }

    public boolean baja(Libro libro) {
        // Valido que el libro existe en la lista de prestamos y que no se encuentra en prestamos activos
        for (Prestamo prestamo : prestamos.todos()) {
            if (prestamo.getLibro().equals(libro) && !prestamo.isDevuelto()) {
                throw new IllegalArgumentException("ERROR: No se puede borrar un libro con préstamos activos.");
            }
        }
        // Actualizo el estado a true si se ha eliminado el libro correctamente
        return libros.baja(libro);
    }

    public Libro buscar(Libro libro) {
        return libros.buscar(libro);
    }

    public List <Libro> listadoLibros() {
        return libros.todos();
    }

    public void alta(Usuario usuario) {
        usuarios.alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        // Valido que el usuario existe en la lista de prestamos y que no tiene prestamos activos
        for (Prestamo prestamo : prestamos.todos()) {
            if (prestamo.getUsuario().getDni().equals(usuario.getDni()) && !prestamo.isDevuelto()) {
                throw new IllegalStateException("ERROR: No se puede borrar un usuario con préstamos activos.");
            }
        }
        return usuarios.baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        return usuarios.buscar(usuario);
    }

    public List <Usuario> listadoUsuarios() {
        return usuarios.todos();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        prestamos.prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        return prestamos.devolver(libro, usuario, fecha);
    }

    public List <Prestamo> listadoPrestamos(Usuario usuario) {
        return prestamos.todos(usuario);
    }

    public List <Prestamo> listadoPrestamos() {
        return prestamos.todos();
    }
}