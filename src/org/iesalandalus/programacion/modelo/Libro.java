package org.iesalandalus.programacion.modelo;

public class Libro {
    private String titulo;
    private Autor autor;
    private Categoria categoria;
    private int anioPublicacion;

    // Creacion del constructor
    public Libro(String titulo, Autor autor, Categoria categoria, int anioPublicacion) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El título del libro no puede ser nulo o vacío.");
        }
        if (autor == null) {
            throw new IllegalArgumentException("ERROR: El autor del libro no puede ser nulo.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("ERROR: La categoría del libro no puede ser nula.");
        }
        // Asumimos que un año razonable no está en el futuro
        if (anioPublicacion > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("ERROR: El año de publicación no puede ser futuro.");
        }

        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.anioPublicacion = anioPublicacion;
    }

    // Uso este get en ConsolaLibro
    public String getTitulo() {
        return titulo;
    }

    public void mostrarInformacion() {
        System.out.println(this.toString());
    }

    public boolean esAntiguo() {
        return anioPublicacion < 2000; // En caso de que el año sea menor a 2000, el libro es antiguo
    }

    @Override
    public String toString() {
        return "Título: " + titulo +
                "\nAutor: " + autor.toString() +
                "\nCategoría: " + categoria +
                "\nAño: " + anioPublicacion;
    }
}