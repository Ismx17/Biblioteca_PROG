package org.iesalandalus.programacion.biblioteca.modelo.negocio;
import java.util.ArrayList;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;

public class Libros {
    private List <Libro> libros;

    public Libros() {
    libros = new ArrayList<>();
    }

    public void alta(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        for (Libro l : libros) {
            if (l.getIsbn().equals(libro.getIsbn())) {
                throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN.");
            }
        }
        libros.add(new Libro(libro));
    }

    public boolean baja(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getIsbn().equals(libro.getIsbn())) {
                libros.remove(i);
                return true;
            }
        }
        return false;
    }

    public Libro buscar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        for (Libro l : libros) {
            if (l.getIsbn().equals(libro.getIsbn())) {
                return new Libro(l);
            }
        }
        return null;
    }

    public List <Libro> todos() {
        if (libros == null) {
            throw new IllegalArgumentException("ERROR: La lista de libros no puede ser nula.");
        }
        List <Libro> copiaLibros = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro != null) {
                copiaLibros.add(new Libro(libro));
            }
        }
        return copiaLibros;
    }
}