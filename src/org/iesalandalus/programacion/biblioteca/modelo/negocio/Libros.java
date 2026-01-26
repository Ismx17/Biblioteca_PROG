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

    public Libro[] todos() {
        Libro[] copiaLibros = new Libro[libros.size()];
        for (int i = 0; i < libros.size(); i++) {
            copiaLibros[i] = new Libro(libros.get(i));
        }
        return copiaLibros;
    }
}