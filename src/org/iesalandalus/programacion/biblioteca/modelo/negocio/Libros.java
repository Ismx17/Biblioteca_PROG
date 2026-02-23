package org.iesalandalus.programacion.biblioteca.modelo.negocio;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Audiolibro;

public class Libros {
    private List <Libro> libros;

    public Libros() {
    // Creo la lista de libros
    this.libros = new ArrayList<>();
    }

    public void alta(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Valido que no existe un libro con el mismo ISBN
        if (libros.contains(libro)) {
            throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN.");
        }
        // Agrego el libro a la lista
        if (libro instanceof Audiolibro) {
            libros.add(new Audiolibro((Audiolibro) libro));
        } else {
            libros.add(new Libro(libro));
        }
    }

    public boolean baja(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Elimino el libro usando el método remove de ArrayList que usa equals
        return libros.remove(libro);
    }

    public Libro buscar(Libro libro) {
        // Valido que el libro existe
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        // Busco el índice del libro
        int index = libros.indexOf(libro);
        // Si existe (índice distinto de -1), lo devuelvo
        if (index != -1) {
            return libros.get(index);
        }
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
                if (libro instanceof Audiolibro) {
                    copiaLibros.add(new Audiolibro((Audiolibro) libro));
                } else {
                    copiaLibros.add(new Libro(libro));
                }
            }
        }
        Collections.sort(copiaLibros);
        // Devuelvo la copia de la lista de libros
        return copiaLibros;
    }
}