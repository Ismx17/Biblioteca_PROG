package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Libro {
    private static final String ISBN_PATTERN = "^\\d{13}$";
    private String titulo;
    private String isbn;
    private Categoria categoria;
    private int unidadesDisponibles;
    private int anio;
    private List <Autor> autores = new ArrayList<>();

    // Constructor 
    public Libro(String isbn, String titulo, int anio, Categoria categoria, int unidadesDisponibles) {
        setIsbn(isbn);
        setTitulo(titulo);
        setAnio(anio);
        setCategoria(categoria);
        setUnidadesDisponibles(unidadesDisponibles);
    }

    // Constructor copia
    public Libro(Libro libro) { 
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        setIsbn(libro.getIsbn());
        setTitulo(libro.getTitulo());
        setAnio(libro.getAnio());
        setCategoria(libro.getCategoria());
        setUnidadesDisponibles(libro.getUnidadesDisponibles());
        for (Autor autor : libro.autores) {
            this.autores.add(new Autor(autor));
        }
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
        if (autores.contains(autor)) {
            throw new IllegalArgumentException("ERROR: El autor ya existe en el libro.");
        }
        autores.add(autor);
    }

    private String autoresComoCadena() {
        StringBuilder sb = new StringBuilder(); // Crea un objeto StringBuilder para construir la cadena
        for (int i = 0; i < autores.size(); i++) { // Recorre el array de autores
            if (i > 0) {
                sb.append(", "); // Agrega una coma y un espacio si no es el primer autor
            }
            sb.append(autores.get(i).getNombreCompleto()); // Agrega el nombre completo del autor a la cadena
        }
        return sb.toString(); // Devuelve la cadena resultante
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { // Comprueba si son el mismo objeto
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) { // Comprueba si el objeto es nulo o de una clase diferente
            return false;
        }
        Libro libro = (Libro) obj; // Convierte el objeto a Libro
        return isbn.equals(libro.isbn);
    }

    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Libro [titulo=" + titulo + ", isbn=" + isbn + ", categoria=" + categoria + ", unidadesDisponibles="
                + unidadesDisponibles + ", anio=" + anio + ", autores=" + autoresComoCadena() + "]";
    }
}