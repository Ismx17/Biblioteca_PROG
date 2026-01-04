package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.time.LocalDate;

public class Prestamo {

    private LocalDate fInicio;
    private LocalDate fLimite;
    private boolean devuelto;
    private LocalDate fDevolucion;
    private Usuario usuario;
    private Libro libro;

    public Prestamo(Libro libro, Usuario usuario, LocalDate fInicio) {
        if (libro == null) {
            throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        if (fInicio == null) {
            throw new IllegalArgumentException("ERROR: La fecha de inicio no puede ser nula.");
        }
        this.libro = libro;
        this.usuario = usuario;
        this.fInicio = fInicio;
        this.fLimite = fInicio.plusDays(30);
        this.devuelto = false;
        this.fDevolucion = null;
    }

    public Libro getLibro() {
        return libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getfInicio() {
        return fInicio;
    }

    public LocalDate getfLimite() {
        return fLimite;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public LocalDate getfDevolucion() {
        return fDevolucion;
    }

    public int diasDeRetraso() {
        return fDevolucion.compareTo(fLimite);
    }

    public boolean diasRetraso() {
        
    }
}