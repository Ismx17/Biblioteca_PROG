package org.iesalandalus.programacion.biblioteca.modelo.negocio;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;

public class Libros {
    private Libro[] libros;

    public Libros(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("ERROR: La capacidad debe ser mayor que cero.");
        }
        this.libros = new Libro[capacidad];
    }

    public void alta(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        int indice = -1;
        for (int i = 0; i < libros.length; i++) {
            if (libros[i] != null) {
                if (libros[i].getIsbn().equals(libro.getIsbn())) {
                    throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN.");
                }
            } else if (indice == -1) {
                indice = i;
            }
        }
        if (indice == -1) {
            throw new IllegalArgumentException("ERROR: No hay espacio para más libros.");
        }
        libros[indice] = new Libro(libro);
    }

    public boolean baja(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        for (int i = 0; i < libros.length; i++) {
            if (libros[i] != null && libros[i].getIsbn().equals(libro.getIsbn())) {
                libros[i] = null;
                return true;
            }
        }
        return false;
    }

    public Libro buscar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        for (int i = 0; i < libros.length; i++) {
            if (libros[i] != null && libros[i].getIsbn().equals(libro.getIsbn())) {
                return new Libro(libros[i]);
            }
        }
        return null;
    }

    public Libro[] todos() {
        int contador = 0;
        for (Libro libro : libros) {
            if (libro != null) {
                contador++;
            }
        }
        Libro[] copiaLibros = new Libro[contador];
        int j = 0;
        for (Libro libro : libros) {
            if (libro != null) {
                copiaLibros[j++] = new Libro(libro);
            }
        }
        return copiaLibros;
    }
}