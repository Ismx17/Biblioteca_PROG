package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Prestamos {

    private List <Prestamo> prestamos;

    public Prestamos() {
        // Creo la lista de prestamos
        this.prestamos = new ArrayList<>();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        // Valido que los parametros no sean nulos, es decir que existan
        if (libro == null || usuario == null || fecha == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        // Valido que la fecha no sea anterior a la actual
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("ERROR: La fecha no puede ser anterior a la actual.");
        }

        // Recorro la lista de prestamos y valido que el usuario no tenga un prestamo activo de este libro
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getLibro().equals(libro) && prestamo.getUsuario().equals(usuario) && !prestamo.isDevuelto()) {
                throw new IllegalArgumentException("ERROR: El usuario ya tiene un prestamo activo de este libro.");
            }
        }
        // Agrego el prestamo a la lista
        prestamos.add(new Prestamo(libro, usuario, fecha));
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        // Valido que los parametros no sean nulos, es decir que existan
        if (libro == null || usuario == null || fecha == null) {
            throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        }
        // Recorro la lista de prestamos y valido que el usuario tenga un prestamo activo de este libro
        for (Prestamo prestamo : prestamos) {
            if (prestamo != null && prestamo.getLibro().equals(libro) && prestamo.getUsuario().equals(usuario) && !prestamo.isDevuelto()) {
                // Marco el prestamo como devuelto y actualizo el estado a true
                prestamo.marcarDevuelto(fecha);
                return true;
            }
        }
        // Actualizo el estado a false si no se ha encontrado el prestamo
        return false;
    }

    public List <Prestamo> todos(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Creo una copia de la lista de prestamos para no modificar la original
        List <Prestamo> copiaPrestamos = new ArrayList<>();
        // Recorro la lista de prestamos y añado los prestamos del usuario a la copia
        for (Prestamo prestamo : prestamos) {
            // Valido que el prestamo existe y que el usuario es el mismo
            if (prestamo != null && prestamo.getUsuario().equals(usuario)) {
                // Añado el prestamo a la copia
                copiaPrestamos.add(new Prestamo(prestamo));
            }
        }
        // Devuelvo la copia de la lista de prestamos
        return copiaPrestamos;
    }

    public List <Prestamo> todos() {
        // Valido que el prestamo existe
        if (prestamos == null) {
            throw new IllegalArgumentException("ERROR: La lista de prestamos no puede ser nula.");
        }
        // Creo una copia de la lista de prestamos para no modificar la original
        List <Prestamo> copiaPrestamos = new ArrayList<>();
        // Recorro la lista de prestamos y añado el prestamo a la copia
        for (Prestamo prestamo : prestamos) {
            // Valido que el prestamo existe
            if (prestamo != null) {
                // Añado el prestamo a la copia
                copiaPrestamos.add(new Prestamo(prestamo));
            }
        }
        // Devuelvo la lista de la copia de prestamos
        return copiaPrestamos;
    }
}