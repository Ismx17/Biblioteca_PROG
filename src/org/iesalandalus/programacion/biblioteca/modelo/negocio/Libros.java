package org.iesalandalus.programacion.biblioteca.modelo.negocio;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;

public class Libros {
    private List <Libro> libros;

    public Libros() {
    // Creo la lista de libros
    libros = new ArrayList<>();
    }

    public void alta(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Valido que no existe un libro con el mismo ISBN
        for (Libro l : libros) {
            if (l.getIsbn().equals(libro.getIsbn())) {
                throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN.");
            }
        }
        // Agrego el libro a la lista
        libros.add(new Libro(libro));
    }

    public boolean baja(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Valido que existe un libro con el mismo ISBN
        for (int i = 0; i < libros.size(); i++) {
            // Valido que el libro existe y que el ISBN es el mismo
            if (libros.get(i).getIsbn().equals(libro.getIsbn())) {
                // Elimino el libro de la lista
                libros.remove(i);
                // Devuelvo true si se ha eliminado el libro correctamente
                return true;
            }
        }
        // Devuelvo false si no se ha eliminado el libro correctamente
        return false;
    }

    public Libro buscar(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Recorro la lista de libros y devuelvo si el libro existe
        for (Libro l : libros) {
            // Valido que ell libro existe y que el ISBN es el mismo
            if (l.getIsbn().equals(libro.getIsbn())) {
                // Devuelvo el libro si existe
                return l;
            }
        }
        // Devuelvo null si no existe el libro
        return null;
    }

    public List <Libro> todos() {
        // Valido que la lista de libros existe
        if (libros == null) {
            throw new IllegalArgumentException("ERROR: La lista de libros no puede ser nula.");
        }
        // Creo una copia de la lista de libros para no modificar la original
        List <Libro> copiaLibros = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro != null) {
                copiaLibros.add(new Libro(libro));
            }
        }
        // Ordeno la lista por titulo
        copiaLibros.sort(Comparator.comparing(Libro::getTitulo));
        // Devuelvo la copia de la lista de libros
        return copiaLibros;
    }
}