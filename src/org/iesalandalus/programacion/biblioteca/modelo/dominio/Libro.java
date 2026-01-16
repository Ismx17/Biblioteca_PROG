package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;

public class Libro {
    private static final String ISBN_PATTERN = "^\\d{13}$";
    private static final int MAX_AUTORES = 3;
    private String titulo;
    private String isbn;
    private Categoria categoria;
    private int unidadesDisponibles;
    private int anio;
    private Autor[] autores;
    private int numAutores;

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

        this.isbn = getIsbn();
        this.titulo = getTitulo();
        this.categoria = getCategoria();
        this.anio = getAnio();
        this.unidadesDisponibles = getUnidadesDisponibles();
        this.autores = new Autor[MAX_AUTORES];
        this.numAutores = 0;
    }

    // Constructor copia
    public Libro(Libro libro) { 
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        this.isbn = libro.getIsbn();
        this.titulo = libro.getTitulo();
        this.categoria = libro.getCategoria();
        this.anio = libro.getAnio();
        this.unidadesDisponibles = libro.getUnidadesDisponibles();
        this.autores = new Autor[MAX_AUTORES]; 
        this.numAutores = libro.numAutores;
        for (int i = 0; i < libro.numAutores; i++) { // Recorre el array dee autores
            this.autores[i] = new Autor(libro.autores[i]); // Crea una copia del autor
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
        if (numAutores >= MAX_AUTORES) {
            throw new IllegalArgumentException("ERROR: El libro no puede tener más de 2 autores.");
        }
        autores[numAutores] = autor;
        numAutores++;
    }

    public Autor[] getAutores() {
        Autor[] copiaAutores = new Autor[numAutores]; // Crea un array de autores con el tamaño correcto
        for (int i = 0; i < numAutores; i++) {
            copiaAutores[i] = new Autor(autores[i]); // Crea una copia del autor
        }
        return copiaAutores; // Devuelve una copia del array de autores
    }

    public void setAutores(Autor[] autores) {
        if (autores == null) {
            throw new IllegalArgumentException("ERROR: Los autores no pueden ser nulos.");
        }
        if (autores.length > MAX_AUTORES) {
            throw new IllegalArgumentException("ERROR: El libro no puede tener más de 2 autores.");
        }
        this.autores = new Autor[MAX_AUTORES]; // Crea un array de autores con el tamaño correcto
        this.numAutores = 0; // Reinicia el numero de autores a 0
        for (Autor autor : autores) { // Recorre el array dee autores
            if (autor == null) {
                throw new IllegalArgumentException("ERROR: El autor no puede ser nulo.");
            }
            this.autores[numAutores] = autor; // Copia los autores al array
            numAutores++; // Incrementa el numero de autores 
        }
    }

    public String autoresComoCadena() {
        StringBuilder sb = new StringBuilder(); // Crea un objeto StringBuilder para construir la cadena
        for (int i = 0; i < numAutores; i++) { // Recorre el array de autores
            if (i > 0) {
                sb.append(", "); // Agrega una coma y un espacio si no es el primer autor
            }
            sb.append(autores[i].getNombreCompleto()); // Agrega el nombre completo del autor a la cadena
        }
        return sb.toString(); // Devuelve la cadena resuultante
    }

    public void tomarPrestado() {
        if (unidadesDisponibles > 0) {
            unidadesDisponibles--; // Decrementa el numero de unidades disponibles
        } else {
            throw new IllegalArgumentException("ERROR: No hay unidades disponibles del libro.");
        }
    }

    public void devolverUnidad() {
        unidadesDisponibles++; // Incrementa el numero de unidades disponibles
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
        boolean autoresIguales = (numAutores == libro.numAutores); // Comprueba si tienen el mismo número de autores
        if (autoresIguales) { 
            for (int i = 0; i < numAutores; i++) { 
                if (!autores[i].equals(libro.autores[i])) { // Compara los autores para ver si son iguales
                    autoresIguales = false;
                    break;
                }
            }
        }
        return isbn.equals(libro.isbn) && titulo.equals(libro.titulo) && categoria.equals(libro.categoria) && anio == libro.anio && unidadesDisponibles == libro.unidadesDisponibles && autoresIguales;
    }

    public int hashCode() {
        return Objects.hash(isbn, titulo, categoria, anio, unidadesDisponibles, autores[0], autores[1]);
    }

    @Override
    public String toString() {
        return "Libro [isbn=" + isbn + ", titulo=" + titulo + ", categoria=" + categoria + ", anio=" + anio + ", unidadesDisponibles=" + unidadesDisponibles + "]";
    }
}