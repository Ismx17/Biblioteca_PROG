package biblioteca;


import biblioteca.controlador.Controlador;
import biblioteca.modelo.Modelo;
import biblioteca.vista.Vista;

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