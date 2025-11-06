package org.iesalandalus.programacion.modelo;

public class Autor {

    private String nombre;
    private String nacionalidad;

    //Creación del constructor
    public Autor(String nombre, String nacionalidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El nombre del autor no puede ser nulo o vacío.");
        }
        if (nacionalidad == null || nacionalidad.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: la nacionalidad del autor no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    //Creacion del método toString
    @Override
    public String toString() {
        return nombre + "\nNacionalidad: " + nacionalidad;
    }
}