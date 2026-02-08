package org.iesalandalus.programacion.biblioteca;

import org.iesalandalus.programacion.biblioteca.controlador.Controlador;
import org.iesalandalus.programacion.biblioteca.modelo.Modelo;
import org.iesalandalus.programacion.biblioteca.vista.Vista;

public class AppBiblioteca {

    public static void main(String[] args) {
        try {
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            Controlador controlador = new Controlador(modelo, vista);
            vista.setControlador(controlador);
            controlador.comenzar();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}