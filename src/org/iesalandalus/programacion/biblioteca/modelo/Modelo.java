package org.iesalandalus.programacion.biblioteca.modelo;

import java.time.LocalDate;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Libros;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Prestamos;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Usuarios;

public class Modelo {

    private static final int CAPACIDAD = 50;
    private Libros libros;
    private Usuarios usuarios;
    private Prestamos prestamos;

    public Modelo() {}

    public void comenzar() {
        // Inicializo las estructuras de datos
        libros = new Libros(CAPACIDAD);
        usuarios = new Usuarios(CAPACIDAD);
        prestamos = new Prestamos(CAPACIDAD);
    }

    public void terminar() {
        libros = null;
        usuarios = null;
        prestamos = null;

        System.out.println("El modelo se ha terminado.");
    }

    public void alta(Libro libro) {
        libros.alta(libro);
    }

    public boolean baja(Libro libro) {
        return libros.baja(libro);
    }

    public Libro buscar(Libro libro) {
        return libros.buscar(libro);
    }

    public Libro[] listadoLibros() {
        return libros.todos();
    }

    public void alta(Usuario usuario) {
        usuarios.alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        return usuarios.baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        return usuarios.buscar(usuario);
    }

    public Usuario[] listadoUsuarios() {
        return usuarios.todos();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        prestamos.prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        return prestamos.devolver(libro.getIsbn(), usuario.getDni(), fecha);
    }

    public Prestamo[] listadoPrestamos(Usuario usuario) {
        return prestamos.prestamosUsuario(usuario);
    }

    public Prestamo[] listadoPrestamos() {
        return prestamos.historico();
    }
}