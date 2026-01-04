package org.iesalandalus.programacion.biblioteca.modelo.dominio;

public enum Categoria {
    NOVELA,
    ENSAYO,
    INFANTIL,
    COMIC,
    POESIA,
    TECNICO,
    OTROS;

    @Override
    public String toString() {
        return name();
    }

    // Método estático para obtener la categoria por su índice
    public static Categoria get(int indice) {
        if (indice >= 0 && indice < values().length) {
            return values()[indice];
        }
        return null; // Devuelve null si el número está fuera de rango
    }
}