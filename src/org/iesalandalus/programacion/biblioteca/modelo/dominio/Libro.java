package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Libro {
    private static final String ISBN_PATTERN = "^\\d{13}$";
    private static final int MAX_AUTORES = 2;
    private String titulo;
    private String isbn;
    private Categoria categoria;
    private int unidadesDisponibles;
    private int anio;
    private List<Autor> autores;

    // Creacion del constructor
    public Libro(String isbn, String titulo, int anio, Categoria categoria, int unidadesDisponibles) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ISBN del libro no puede ser nulo o vacío.");
        }
        if (!isbn.matches(ISBN_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El ISBN del libro no es válido.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El título del libro no puede ser nulo o vacío.");
        }
        if (anio < 0) {
            throw new IllegalArgumentException("ERROR: El año del libro no puede ser negativo.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("ERROR: La categoría del libro no puede ser nula.");
        }
        if (unidadesDisponibles < 0) {
            throw new IllegalArgumentException("ERROR: Las unidades disponibles del libro no pueden ser negativas.");
        }

        this.isbn = isbn;
        this.titulo = titulo;
        this.categoria = categoria;
        this.anio = anio;
        this.unidadesDisponibles = unidadesDisponibles;
        this.autores = new ArrayList<>();
    }

    public Libro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        this.isbn = libro.isbn;
        this.titulo = libro.titulo;
        this.categoria = libro.categoria;
        this.anio = libro.anio;
        this.unidadesDisponibles = libro.unidadesDisponibles;
        this.autores = new ArrayList<>(libro.autores);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El ISBN del libro no puede ser nulo o vacío.");
        }
        if (!isbn.matches(ISBN_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El ISBN del libro no es válido.");
        }
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El título del libro no puede ser nulo o vacío.");
        }
        this.titulo = titulo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        if (anio < 0) {
            throw new IllegalArgumentException("ERROR: El año del libro no puede ser negativo.");
        }
        this.anio = anio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {

        if (categoria == null) {
            throw new IllegalArgumentException("ERROR: La categoría del libro no puede ser nula.");
        }
        this.categoria = categoria;
    }

    public int getUnidadesDisponibles() {
        return unidadesDisponibles;
    }

    public void setUnidadesDisponibles(int unidadesDisponibles) {
        if (unidadesDisponibles < 0) {
            throw new IllegalArgumentException("ERROR: Las unidades disponibles del libro no pueden ser negativas.");
        }
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public void addAutor(Autor autor) {
        if (autor == null) {
            throw new IllegalArgumentException("ERROR: El autor no puede ser nulo.");
        }
        if (autores.size() >= MAX_AUTORES) {
            throw new IllegalArgumentException("ERROR: El libro no puede tener más de 2 autores.");
        }
        autores.add(autor);
    }

    public Autor[] getAutores() {
        return autores.toArray(new Autor[0]);
    }

    public void setAutores(Autor[] autores) {
        if (autores == null) {
            throw new IllegalArgumentException("ERROR: Los autores no pueden ser nulos.");
        }
        if (autores.length > MAX_AUTORES) {
            throw new IllegalArgumentException("ERROR: El libro no puede tener más de 2 autores.");
        }
        this.autores = new ArrayList<>();
        for (Autor autor : autores) {
            this.autores.add(autor);
        }
    }

    public String autoresComoCadena() {
        StringBuilder sb = new StringBuilder();
        for (Autor autor : autores) {
            sb.append(autor.getNombreCompleto()).append(", ");
        }
        return sb.toString();
    }

    public void tomarPrestado() {
        if (unidadesDisponibles > 0) {
            unidadesDisponibles--;
        } else {
            throw new IllegalArgumentException("ERROR: No hay unidades disponibles del libro.");
        }
    }

    public void devolverUnidad() {
        if (unidadesDisponibles < 0) {
            throw new IllegalArgumentException("ERROR: Las unidades disponibles del libro no pueden ser negativas.");
    }
}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Libro libro = (Libro) obj;
        return isbn.equals(libro.isbn) && titulo.equals(libro.titulo) && categoria.equals(libro.categoria) && anio == libro.anio && unidadesDisponibles == libro.unidadesDisponibles;
    }

    public int hashCode() {
        return Objects.hash(isbn, titulo, categoria, anio, unidadesDisponibles);
    }

    @Override
    public String toString() {
        return "Libro [isbn=" + isbn + ", titulo=" + titulo + ", categoria=" + categoria + ", anio=" + anio + ", unidadesDisponibles=" + unidadesDisponibles + "]";
    }
}