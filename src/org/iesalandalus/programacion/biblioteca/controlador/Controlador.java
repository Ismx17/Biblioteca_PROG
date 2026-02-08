package org.iesalandalus.programacion.biblioteca.controlador;

import java.time.LocalDate;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.Modelo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;
import org.iesalandalus.programacion.biblioteca.vista.Vista;

public class Controlador {
    private Modelo modelo;
    private Vista vista;

public Controlador(Modelo modelo, Vista vista) {
        if(modelo == null || vista == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        this.modelo = modelo;
        this.vista = vista;
    }

    public void comenzar() {
        modelo.comenzar();
        vista.comenzar();
    }

    public void terminar() {
        vista.terminar();
        modelo.terminar();
    }

    public void alta(Libro libro) {
        modelo.alta(libro);
    } 

    public boolean baja(Libro libro) {
        return modelo.baja(libro);
    }

    public Libro buscar(Libro libro) {
        return modelo.buscar(libro);
    }

    public List <Libro> listadoLibros() {
        return modelo.listadoLibros();
    }

    public void alta(Usuario usuario) {
        modelo.alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        return modelo.baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        return modelo.buscar(usuario);
    }

    public List <Usuario> listadoUsuarios() {
        return modelo.listadoUsuarios();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        modelo.prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        return modelo.devolver(libro, usuario, fecha);
    }

    public List <Prestamo> listadoPrestamos(Usuario usuario) {
        return modelo.listadoPrestamos(usuario);
    }

    public List <Prestamo> listadoPrestamos() {
        return modelo.listadoPrestamos();
    }
}