package org.iesalandalus.programacion.biblioteca.vista;

public enum Opcion {

    SALIR(0, "Salir"),
    INSERTAR_USUARIO(1, "Insertar Usuario"),
    BORRAR_USUARIO(2, "Borrar Usuario"),
    MOSTRAR_USUARIO(3, "Mostrar Usuario"),
    INSERTAR_LIBRO(4, "Insertar Libro"),
    BORRAR_LIBRO(5, "Borrar Libro"),
    MOSTRAR_LIBROS(6, "Mostrar Libros"),
    NUEVO_PRESTAMO(7, "Nuevo Prestamo"),
    DEVOLVER_PRESTAMO(8, "Devolver Prestamo"),
    MOSTRAR_PRESTAMOS(9, "Mostrar Prestamos"),
    MOSTRAR_PRESTAMOS_USUARIOS(10, "Mostrar Prestamos Usuarios");

    private final int valor;
    private final String tipo;

    private Opcion(int valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }
}