package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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
        if (libro.getUnidadesDisponibles() <= 0) {
            throw new IllegalArgumentException("ERROR: No hay unidades disponibles del libro.");
        }
        
        this.libro = new Libro(libro);
        this.usuario = new Usuario(usuario);
        this.fInicio = fInicio;
        this.fLimite = fInicio.plusDays(30);
        this.devuelto = false;
        this.fDevolucion = null;
        
        // Descontar unidad al crear el préstamo
        this.libro.tomarPrestado();
    }

    public Prestamo(Prestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("ERROR: El préstamo no puede ser nulo.");
        }
        this.libro = new Libro(prestamo.libro);
        this.usuario = new Usuario(prestamo.usuario);
        this.fInicio = prestamo.fInicio;
        this.fLimite = prestamo.fLimite;
        this.devuelto = prestamo.devuelto;
        this.fDevolucion = prestamo.fDevolucion;
    }

    public Libro getLibro() {
        return new Libro(libro);  
    }

    public Usuario getUsuario() {
        return new Usuario(usuario);  
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
        LocalDate fechaReferencia = (devuelto) ? fDevolucion : LocalDate.now();
        
        if (fechaReferencia.isAfter(fLimite)) {
            return (int) ChronoUnit.DAYS.between(fLimite, fechaReferencia);
        }
        return 0;
    }

    public boolean estaVencido() {
        return !devuelto && LocalDate.now().isAfter(fLimite);
    }

    public void marcarDevuelto(LocalDate fecha) {
        if (devuelto) {
            throw new IllegalArgumentException("ERROR: El libro ya ha sido devuelto.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("ERROR: La fecha de devolución no puede ser nula.");
        }
        if (fecha.isBefore(fInicio)) {
            throw new IllegalArgumentException("ERROR: La fecha de devolución no puede ser anterior a la fecha de inicio.");
        }
        
        devuelto = true;
        fDevolucion = fecha;
        libro.devolverUnidad();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Prestamo prestamo = (Prestamo) obj;
        return Objects.equals(libro.getIsbn(), prestamo.libro.getIsbn()) &&
               Objects.equals(usuario.getId(), prestamo.usuario.getId()) &&
               Objects.equals(fInicio, prestamo.fInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libro.getIsbn(), usuario.getId(), fInicio);
    }

    @Override
    public String toString() {
        String estadoDevolucion = devuelto ? 
            ", devuelto el " + fDevolucion : 
            ", no devuelto"; 
        
        return "Prestamo [" +
               "libro=" + libro.getTitulo() + 
               ", usuario=" + usuario.getNombre() +
               ", inicio=" + fInicio +
               ", límite=" + fLimite +
               estadoDevolucion +
               "]";
    }
}