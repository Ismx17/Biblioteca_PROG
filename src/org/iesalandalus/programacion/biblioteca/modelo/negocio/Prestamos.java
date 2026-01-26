package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Prestamos {

    private List <Prestamo> prestamos;

    public Prestamos() {
        prestamos = new ArrayList<>();
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

        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getLibro().getIsbn().equals(libro.getIsbn()) && prestamo.getUsuario().getDni().equals(usuario.getDni()) && !prestamo.isDevuelto()) {
                throw new IllegalArgumentException("ERROR: El usuario ya tiene un prestamo activo de este libro.");
            }
        }
        prestamos.add(new Prestamo(libro, usuario, fecha));
        libro.setUnidadesDisponibles(libro.getUnidadesDisponibles() - 1);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null || usuario == null || fecha == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("ERROR: La fecha no puede ser anterior a la actual.");
        }
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getLibro().getIsbn().equals(libro.getIsbn()) && prestamo.getUsuario().getDni().equals(usuario.getDni()) && !prestamo.isDevuelto()) {
                prestamo.marcarDevuelto(fecha);
                return true;
            }
        }
        return false;
    }

    public List <Prestamo> todos(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        List <Prestamo> copiaPrestamos = new ArrayList<>();
        prestamos.sort(Comparator.comparing(Prestamo::getfInicio).reversed()
                .thenComparing(prestamo -> prestamo.getUsuario().getNombre()));
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getUsuario().getDni().equals(usuario.getDni())) {
                copiaPrestamos.add(new Prestamo(prestamo));
            }
        }
        return copiaPrestamos;
    }

    public List <Prestamo> todos() {
        if (prestamos == null) {
            throw new IllegalArgumentException("ERROR: La lista de prestamos no puede ser nula.");
        }
        List <Prestamo> copiaPrestamos = new ArrayList<>();
        prestamos.sort(Comparator.comparing(Prestamo::getfInicio).reversed()
                .thenComparing(prestamo -> prestamo.getUsuario().getNombre()));
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null) {
                copiaPrestamos.add(new Prestamo(prestamo));
            }
        }
        return copiaPrestamos;
    }
}