package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.time.LocalDate;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Prestamos {

    private Prestamo[] prestamos;

    public Prestamos(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("ERROR: La capacidad debe ser mayor que cero.");
        }
        this.prestamos = new Prestamo[capacidad];
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null || usuario == null || fecha == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("ERROR: La fecha no puede ser anterior a la actual.");
        }
        if (libro.getUnidadesDisponibles() <= 0) {
            throw new IllegalArgumentException("ERROR: No hay unidades disponibles del libro.");
        }

        int indice = -1;
        // Compruebo que el usuario no tenga prestamos vencidos o repetidos
        for (int i = 0; i < prestamos.length; i++) {
            if (prestamos[i] == null) {
                if (indice == -1) {
                    indice = i; // Guardo el sitio libre
                }
            } else {
                if (prestamos[i].getUsuario().getDni().equals(usuario.getDni())) {
                    if (prestamos[i].estaVencido()) {
                        throw new IllegalArgumentException("ERROR: El usuario tiene un préstamo vencido.");
                    }
                    if (prestamos[i].getLibro().getIsbn().equals(libro.getIsbn()) && !prestamos[i].isDevuelto()) {
                        throw new IllegalArgumentException("ERROR: El usuario ya tiene un préstamo activo para este libro.");
                    }
                }
            }
        }
        if (indice == -1) {
            throw new IllegalArgumentException("ERROR: No hay espacio para mas prestamos.");
        }
        prestamos[indice] = new Prestamo(libro, usuario, fecha);
        libro.tomarPrestado(); // Actualizo las unidades del libro
    }

    public boolean devolver(String isbn, String idUsuario, LocalDate fecha) {
        if (isbn == null || idUsuario == null || fecha == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("ERROR: La fecha no puede ser anterior a la actual.");
        }
        for (int i = 0; i < prestamos.length; i++) {
            // Busco el prestamo activo de ese usuario y libro
            if (prestamos[i] != null && prestamos[i].getLibro().getIsbn().equals(isbn) && prestamos[i].getUsuario().getDni().equals(idUsuario) && !prestamos[i].isDevuelto()) {
                prestamos[i].marcarDevuelto(fecha);
                return true;
            }
        }
        return false;
    }

    public Prestamo[] prestamosUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        int contador = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getUsuario().getDni().equals(usuario.getDni())) {
                contador++;
            }
        }
        Prestamo[] copiaPrestamos = new Prestamo[contador];
        int j = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getUsuario().getDni().equals(usuario.getDni())) {
                copiaPrestamos[j++] = new Prestamo(prestamo);
            }
        }
        return copiaPrestamos;
    }

    public Libro[] librosPrestados() {
        int contador = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && !prestamo.isDevuelto()) {
                contador++;
            }
        }
        Libro[] libros = new Libro[contador];
        int i = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && !prestamo.isDevuelto()) {
                libros[i++] = prestamo.getLibro();
            }
        }
        return libros;
    }

    public Prestamo[] historico() {
        int contador = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null) {
                contador++;
            }
        }
        Prestamo[] copiaPrestamos = new Prestamo[contador];
        int i = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null) {
                copiaPrestamos[i++] = new Prestamo(prestamo);
            }
        }
        return copiaPrestamos;
    }
}