package org.iesalandalus.programacion.biblioteca.modelo.dominio;

import java.time.Duration;

public class Audiolibro extends Libro{
    private Duration duracion;
    private String formato;

    public Audiolibro(String isbn, String titulo, int anio, Categoria categoria, Duration duracion, String formato) {
        super(isbn, titulo, anio, categoria); // Llamo al constructor de la clase padre
        setDuracion(duracion); 
        setFormato(formato);
    }

    public Audiolibro(Audiolibro audiolibro) {
        // Al llamar al constructor de la clase padre, se crea una copia del libro y se asigna a la clase Audiolibro
        // En caso de que sea nulo, se lanzara la excepcion del constructor de la clase padre
        super(audiolibro);
        setDuracion(audiolibro.getDuracion());
        setFormato(audiolibro.getFormato());
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        // Valido que la duracion no sea nula
        if (duracion == null) {
            throw new IllegalArgumentException("ERROR: La duracion del audiolibro no puede ser nula.");
        }
        // Valido que la duracion no sea negativa
        if (duracion.isNegative()) {
            throw new IllegalArgumentException("ERROR: La duracion del audilobro no puede ser negativa.");
        }
        // Valido que la duracion no sea cero
        if (duracion.isZero()) {
            throw new IllegalArgumentException("ERROR: La duracion del audiolibro no puede ser cero.");
        }
        this.duracion = duracion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        // Valido que el formato no sea nulo o este vacio
        if (formato == null || formato.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El formato del audiolibro no puede ser nulo o estar vacio.");
        }
        // Valido que el formato sea valido
        if (!formato.equalsIgnoreCase("mp3") && !formato.equalsIgnoreCase("mp4B") && !formato.equalsIgnoreCase("AA/AAX")) {
            throw new IllegalArgumentException("ERROR: El formato del audiolibro no es valido.");
        }
        this.formato = formato;
    }

    @Override
    public String toString() {
        return "Audiolibro [duracion=" + duracion + ", formato=" + formato + ", " + super.toString() + "]";
    }
}