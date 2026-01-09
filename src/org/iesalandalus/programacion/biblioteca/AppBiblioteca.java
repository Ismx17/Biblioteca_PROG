package org.iesalandalus.programacion.biblioteca;

import org.iesalandalus.programacion.biblioteca.modelo.negocio.Libros;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Prestamos;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Usuarios;
import org.iesalandalus.programacion.biblioteca.vista.Consola;

public class AppBiblioteca {

    private static final int CAPACIDAD = 50;

    public static void main(String[] args) {
        Libros libros = new Libros(CAPACIDAD);
        Usuarios usuarios = new Usuarios(CAPACIDAD);
        Prestamos prestamos = new Prestamos(CAPACIDAD);
        
        Consola.iniciar(libros, usuarios, prestamos);
    }
}