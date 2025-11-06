package org.iesalandalus.programacion.aplicacion;

import org.iesalandalus.programacion.modelo.Libro;
import org.iesalandalus.programacion.modelo.Autor;
import org.iesalandalus.programacion.modelo.Categoria;
import org.iesalandalus.programacion.utilidades.Entrada;
import java.util.InputMismatchException;

public class ConsolaLibro {

    private static Libro libroActual = null;

    public static void ejecutar() {
        OpcionMenu opcion;
        int opcionSeleccionada;

        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            mostrarMenu();

            System.out.print("Seleccione una opción (1-" + OpcionMenu.values().length + "): "); // Muestra el número de opciones disponibles

            try {
                opcionSeleccionada = Entrada.entero();

                // Uso del método OpcionMenu.get() para obtener y validar la opción
                opcion = OpcionMenu.get(opcionSeleccionada - 1); // Obtiene la opción seleccionada

                if (opcion == null) { // Si la opcion no es válida lanza un mensaje de error
                    System.out.println("Error: Opción no válida. Por favor, inténtelo de nuevo.");
                }

                switch (opcion) {
                    case CREAR_LIBRO:
                        crearLibro();
                        break;

                    case MOSTRAR_LIBRO:
                        mostrarLibro();
                        break;

                    case COMPROBAR_ANTIGUEDAD:
                        comprobarAntiguedad();
                        break;

                    case SALIR:
                        System.out.println("¡Adiós!");
                        break;
                }

            } catch (NumberFormatException e) { // En caso de que el número no sea entero
                System.out.println("Error: Debe introducir un número entero.");
                opcion = null;
            } catch (Exception e) { //En caso de que ocurra otro tipo de error
                System.out.println("Error inesperado en el menú: " + e.getMessage());
                opcion = null;
            }

        } while (opcion == null || opcion != OpcionMenu.SALIR); // Mientras la opción sea null o distinta de salir, el bucle se repite
    }

    // Método auxiliar para mostrar el menú completo
    private static void mostrarMenu() {
        OpcionMenu[] opciones = OpcionMenu.values(); // Obtiene todas las opciones del enum OpcionMenu y las almacena en un array
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]); // Muestra las opciones disponibles
        }
    }

    //Crear nuevo libro
    private static void crearLibro() {
        Libro nuevoLibro = null; // Creamos un libro y lo inicializamos en null, para darle valores a continuación

        try {
            System.out.println("\n--- CREAR NUEVO LIBRO ---");

            System.out.print("Título: ");
            String titulo = Entrada.cadena();

            System.out.print("Nombre del autor: ");
            String nombreAutor = Entrada.cadena();

            System.out.print("Nacionalidad del autor: ");
            String nacionalidad = Entrada.cadena();

            Autor autor = new Autor(nombreAutor, nacionalidad); // Creamos un autor inicializandolo con los valores dados por el usuario

            System.out.println("\nSeleccione una categoría (nº del índice): ");
            Categoria[] categorias = Categoria.values(); // Obtiene todas las opciones del enum Categoria y las almacena en un array
            for (int i = 0; i < categorias.length; i++) {
                System.out.println((i + 1) + ". " + categorias[i]); // Muestra las categorías disponibles
            }

            System.out.print("Opción: ");
            int entradaCategoria = Entrada.entero();

            Categoria categoria = Categoria.get(entradaCategoria - 1); //Selección de la categoria mediante el índice del array

            if (categoria == null) {
                throw new ArrayIndexOutOfBoundsException(); // Opción fuera de rango
            }

            System.out.print("Año de publicación: ");
            int anio = Entrada.entero();

            nuevoLibro = new Libro(titulo, autor, categoria, anio); // Creamos un libro inicializandolo con los valores dados por el usuario

            libroActual = nuevoLibro; // Asignamos el nuevo libro creado a la variable libroActual
            System.out.println("\n¡Libro creado con éxito!");

        } catch (ArrayIndexOutOfBoundsException e) { // En caso de que la opción se encuentre fuera de rango
            System.out.println("Error: Categoría no válida. Libro no creado.");
        } catch (NumberFormatException e) { // En caso de que el año no sea un número entero
            System.out.println("Error: El año o la opción de categoría deben ser números enteros. Libro no creado.");
        } catch (IllegalArgumentException e) { // En caso de que el año sea negativo
            System.out.println("Error de validación: " + e.getMessage() + " Libro no creado.");
        } catch (Exception e) { // En caso de que ocurra otro tipo de error
            System.out.println("Error inesperado al crear el libro: " + e.getMessage());
        }
    }

    //Información del libro
    private static void mostrarLibro() {
        if (libroActual != null) {
            System.out.println("\n--- INFORMACIÓN DEL LIBRO ---");
            libroActual.mostrarInformacion(); // Mostramos la información del libro
        } else {
            System.out.println("\nNo hay ningún libro creado. Por favor, cree uno primero.");
        }
    }

    //Comprobación de antiguedad
    private static void comprobarAntiguedad() {
        if (libroActual != null) {
            System.out.println("\n--- COMPROBACIÓN DE ANTIGÜEDAD ---");

            String mensaje = (libroActual.esAntiguo()) //Usamos un condicional ternario
                    ? " es antiguo (publicado antes del año 2000)."
                    : " no es antiguo (publicado en el año 2000 o posterior).";

            System.out.println("El libro \"" + libroActual.getTitulo() + "\"" + mensaje);

        } else {
            System.out.println("\nNo hay ningún libro creado. Por favor, introduzca un libro primero.");
        }
    }
}