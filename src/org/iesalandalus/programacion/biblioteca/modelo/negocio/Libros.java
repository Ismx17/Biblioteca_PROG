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
        // Recorro el array para ver si existe y buscar hueco
        for (int i = 0; i < libros.length; i++) {
            if (libros[i] != null) {
                // Si encuentro el ISBN, lanzo error
                if (libros[i].getIsbn().equals(libro.getIsbn())) {
                    throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN.");
                }
            } else if (indice == -1) {
                indice = i; // Me guardo la posicion libre
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
                // Si lo encuentro, desplazo los demas para no dejar huecos
                int j;
                for (j = i; j < libros.length - 1; j++) {
                    libros[j] = libros[j + 1];
                }
                libros[j] = null;
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
                return libros[i];
            }
        }
        return null;
    }

    public Libro[] todos() {
        // Cuento los libros que hay de verdad
        int contador = 0;
        for (Libro libro : libros) {
            if (libro != null) {
                contador++;
            }
        }
        // Creo un array con el tamaño justo y copio los libros
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