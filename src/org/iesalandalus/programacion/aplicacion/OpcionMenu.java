package org.iesalandalus.programacion.aplicacion;

public enum OpcionMenu {
    CREAR_LIBRO("Crear Libro"),
    MOSTRAR_LIBRO("Mostrar Libro"),
    COMPROBAR_ANTIGUEDAD("Comprobar Antigüedad"),
    SALIR("Salir");

    // Atributo para almacenar el texto que se mostrará al usuario
    private final String textoMostrar;

    // Constructor privado para inicializar la descripción
    private OpcionMenu(String textoMostrar) {
        this.textoMostrar = textoMostrar;
    }

    @Override
    public String toString() {
        return textoMostrar;
    }

    // Método estático para obtener la opción por su índice
    public static OpcionMenu get(int indice) {
        if (indice >= 0 && indice < values().length) {
            return values()[indice];
        }
        return null; // Devuelve null si el número está fuera de rango
    }
}