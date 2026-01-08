package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        if (fDevolucion == null) {
            throw new IllegalArgumentException("ERROR: La fecha de devolución no puede ser nula.");
        }
        if (fDevolucion.isAfter(fLimite)) {
            long dias = ChronoUnit.DAYS.between(fLimite, fDevolucion);
            return (int) Math.max(0, dias);
        } else {
            return 0;
        }
    }

    public boolean estaVencido() {
        return !devuelto && LocalDate.now().isAfter(fLimite);
    }

    public void marcarDevuelto(LocalDate fecha, boolean ejecutarDevolucionLibro) {
        if (devuelto) {
            throw new IllegalArgumentException("El libro ya ha sido devuelto");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("ERROR: La fecha de devolucion no puede ser nula.");
        }
        if (fecha.isBefore(fInicio)) {
            throw new IllegalArgumentException("ERROR: La fecha de devolucion no puede ser anterior a la fecha de inicio.");
        }
        devuelto = true;
        fDevolucion = fecha;
        if (ejecutarDevolucionLibro && libro != null) {
            libro.devolverUnidad();
            System.out.println("El libro ha sido devuelto correctamente.");
        }
    }

    @Override
    public String toString() {
        return "Prestamo [fInicio=" + fInicio + ", fLimite=" + fLimite + ", devuelto=" + devuelto + ", fDevolucion="
                + fDevolucion + ", usuario=" + usuario + ", libro=" + libro + "]";
    }
}