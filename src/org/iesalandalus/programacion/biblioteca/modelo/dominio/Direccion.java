package org.iesalandalus.programacion.biblioteca.modelo.dominio;
import java.util.Objects;

public class Direccion {
    public static final String CP_PATTERN = "^\\d{5}$";
    private String via;
    private String numero;
    private String cp;
    private String localidad;

    // Constructor
    public Direccion(String via, String numero, String cp, String localidad) {
        setVia(via);
        setNumero(numero);
        setCp(cp);
        setLocalidad(localidad);
    }

    // Constructor copia para tener una direccion independiente
    public Direccion(Direccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("ERROR: La dirección no puede ser nula.");
        }
        setVia(direccion.getVia());
        setNumero(direccion.getNumero());
        setCp(direccion.getCp());
        setLocalidad(direccion.getLocalidad());
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        if (via == null || via.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: La via no puede ser nula o vacía.");
        }
        this.via = via;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El número no puede ser nulo o vacío.");
        }
        this.numero = numero;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        if (cp == null || cp.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El código postal no puede ser nulo o vacío.");
        }
        if (!cp.matches(CP_PATTERN)) {
            throw new IllegalArgumentException("ERROR: El código postal no es válido.");
        }
        this.cp = cp;
    }

    public String getLocalidad() {
        return localidad;   
    }

    public void setLocalidad(String localidad) {
        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: La localidad no puede ser nula o vacía.");
        }
        this.localidad = localidad;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cp, localidad, numero, via);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Direccion direccion = (Direccion) obj;
        return Objects.equals(cp, direccion.cp) && Objects.equals(localidad, direccion.localidad)
                && Objects.equals(numero, direccion.numero) && Objects.equals(via, direccion.via);
    }

    @Override
    public String toString() {
        return "Direccion [via=" + via + ", numero=" + numero + ", cp=" + cp + ", localidad=" + localidad + "]";
    }
}