package org.iesalandalus.programacion.biblioteca.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Autor;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Categoria;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Direccion;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;
import org.iesalandalus.programacion.utilidades.Entrada;

public class Consola {

    private Consola() {}

    public static void mostrarMenu() {
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("                                   GESTIÓN DE BIBLIOTECA");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("1.- Insertar Usuario");
        System.out.println("2.- Borrar Usuario.");
        System.out.println("3.- Mostrar Usuario");
        System.out.println("4.- Insertar Libro");
        System.out.println("5.- Borrar Libro");
        System.out.println("6.- Mostrar Libros");
        System.out.println("7.- Nuevo Prestamo");
        System.out.println("8.- Devolver Prestamo");
        System.out.println("9.- Mostrar Prestamos");
        System.out.println("10.- Mostrar Prestamos Usuarios");
        System.out.println("");
        System.out.println("0.- Salir.");
        System.out.println("");
    }

    public static Opcion elegirOpcion() {
        int opcion;
        do {
            opcion = leerEntero("Elige una opción: ");
        } while (!Opcion.esValida(opcion));
        return Opcion.get(opcion);
    }

    public static Usuario nuevoUsuario(boolean paraBuscar) {
        System.out.print("Introduce el DNI: ");
        String dni = Entrada.cadena();
        if (paraBuscar) {
            return new Usuario(dni, "Ficticio", "a@a.com", new Direccion("Ficticia", "1", "11111", "Ficticia"));
        }
        System.out.print("Introduce el nombre: ");
        String nombre = Entrada.cadena();
        System.out.print("Introduce el correo: ");
        String correo = Entrada.cadena();
        System.out.print("Introduce la vía: ");
        String via = Entrada.cadena();
        System.out.print("Introduce el número: ");
        String numero = Entrada.cadena();
        System.out.print("Introduce el código postal: ");
        String cp = Entrada.cadena();
        System.out.print("Introduce la localidad: ");
        String localidad = Entrada.cadena();
        return new Usuario(dni, nombre, correo, new Direccion(via, numero, cp, localidad));
    }

    public static Libro nuevoLibro(boolean paraBuscar) {
        System.out.print("Introduce el ISBN: ");
        String isbn = Entrada.cadena();
        if (paraBuscar) {
            return new Libro(isbn, "Ficticio", 1, Categoria.OTROS, 1);
        }
        System.out.print("Introduce el título: ");
        String titulo = Entrada.cadena();
        int anio = leerEntero("Introduce el año de publicación: ");
        Categoria categoria = leerCategoria();
        int unidades = leerEntero("Introduce el número de unidades: ");
        Libro libro = new Libro(isbn, titulo, anio, categoria, unidades);
        
        return libro;
    }

    private static Autor nuevoAutor() {
        System.out.print("Introduce el nombre: ");
        String nombre = Entrada.cadena();
        System.out.print("Introduce los apellidos: ");
        String apellidos = Entrada.cadena();
        System.out.print("Introduce la nacionalidad: ");
        String nacionalidad = Entrada.cadena();
        return new Autor(nombre, apellidos, nacionalidad);
    }

    public static LocalDate leerFecha() {
        return LocalDate.now();
    }

    public static LocalDate leerFecha(String mensaje) {
        LocalDate fecha = null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean fechaCorrecta = false;
        do {
            try {
                System.out.print(mensaje + " (dd/MM/yyyy): ");
                fecha = LocalDate.parse(Entrada.cadena(), formato);
                fechaCorrecta = true;
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: El formato de la fecha no es correcto.");
            }
        } while (!fechaCorrecta);
        return fecha;
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return Entrada.entero();
    }

    private static Categoria leerCategoria() {
        System.out.println("Categorías disponibles:");
        for (Categoria categoria : Categoria.values()) {
            System.out.println(categoria.ordinal() + ".- " + categoria.name());
        }
        int ordinal;
        do {
            ordinal = leerEntero("Elige una categoría: ");
        } while (Categoria.get(ordinal) == null);
        return Categoria.get(ordinal);
    }
}