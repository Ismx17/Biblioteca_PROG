package org.iesalandalus.programacion.modelo;

public enum Categoria {
    NOVELA,
    ENSAYO,
    HISTORIA,
    POESIA,
    TECNOLOGIA;

    @Override
    public String toString() {
        String nombre = name();
        return nombre;
    }

    // Método estático para obtener la categoria por su índice
    public static Categoria get(int indice) {
        if (indice >= 0 && indice < values().length) {
            return values()[indice];
        }
        return null; // Devuelve null si el número está fuera de rango
    }
}