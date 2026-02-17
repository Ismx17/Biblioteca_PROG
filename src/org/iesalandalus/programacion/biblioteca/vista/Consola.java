package org.iesalandalus.programacion.biblioteca.vista;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Audiolibro;
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
        } while (!Opcion.esValida(opcion)); // Mientras que la opcion no sea valida se repite el bucle
        // Devolvemos la opcion elegida
        return Opcion.get(opcion);
    }

    public static Usuario nuevoUsuario(boolean paraBuscar) {
        System.out.print("Introduce el DNI: ");
        String dni = Entrada.cadena();
        if (paraBuscar) {
            return new Usuario(dni, "Ficticio", "a@a.com", new Direccion("Ficticia", "1", "11111", "Ficticia"));
        }
        // Solicitamos e insertamos los datos del usuario
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
            return new Libro(isbn, "Ficticio", 1, Categoria.OTROS);
        }
        // Solicitamos e insertamos los datos del libro
        System.out.print("Introduce el título: ");
        String titulo = Entrada.cadena();
        int anio = leerEntero("Introduce el año de publicación: ");
        Categoria categoria = leerCategoria();
        int unidades = leerEntero("Introduce el número de unidades: ");
        
        // Creamos el libro
        Libro libro = null;
        // Preguntamos si es un audiolibro para crearlo
        System.out.print("¿Es un audiolibro? (S/N): "); 
        if (Entrada.cadena().equalsIgnoreCase("S")) {
            while (libro == null) { // Mientras que el libro no sea creado, se repite el bucle
                Duration duracion = leerDuracion("Introduce la duración"); // Leemos la duracion
                String formato = leerFormato(); // Leemos el formato
                try {
                    libro = new Audiolibro(isbn, titulo, anio, categoria, duracion, formato);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            libro = new Libro(isbn, titulo, anio, categoria);
        }
        
        // Añadimos los autores
        System.out.print("¿Desea añadir un autor? (S/N): ");
        String respuesta = Entrada.cadena();
        while (respuesta.equalsIgnoreCase("S")) {
            try {
                libro.addAutor(nuevoAutor());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            System.out.print("¿Desea añadir otro autor? (S/N): "); // Preguntamos si se quiere añadir otro autor
            respuesta = Entrada.cadena();
        }
        return libro;
    }

    private static Autor nuevoAutor() {
        // Solicitamos e insertamos los datos del autor
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
        // Formato de la fecha
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Pedimos la fecha y validamos que sea correcta
        boolean fechaCorrecta = false;
        do {
            try {
                System.out.print(mensaje + " (dd/MM/yyyy): ");
                // Convertimos la cadena a fecha
                fecha = LocalDate.parse(Entrada.cadena(), formato);
                // Si la fecha es correcta devolvemos true y salimos del bucle
                fechaCorrecta = true;
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: El formato de la fecha no es correcto.");
            }
        } while (!fechaCorrecta); // Mientras que la fecha no sea correcta se repite el bucle
        // Devolvemos la fecha
        return fecha;
    }

    private static Duration leerDuracion(String mensaje) {
        Duration duracion = Duration.ZERO;
        boolean duracionCorrecta = false;
        do {
            try {
                System.out.print(mensaje + " (hh:mm:ss): ");
                String entrada = Entrada.cadena();
                String[] partes = entrada.split(":");
                if (partes.length != 3) {
                    throw new IllegalArgumentException("Formato incorrecto.");
                }
                int horas = Integer.parseInt(partes[0]);
                int minutos = Integer.parseInt(partes[1]);
                int segundos = Integer.parseInt(partes[2]);
                duracion = Duration.ofHours(horas).plusMinutes(minutos).plusSeconds(segundos);
                duracionCorrecta = true;
            } catch (Exception e) {
                System.out.println("ERROR: El formato de la duración no es correcto.");
            }
        } while (!duracionCorrecta);
        return duracion;
    }

    private static String leerFormato() {
        String formato;
        boolean formatoCorrecto = false;
        do {
            System.out.print("Introduce el formato (mp3, mp4B, AA/AAX): ");
            formato = Entrada.cadena();
            if (formato.equalsIgnoreCase("mp3") || formato.equalsIgnoreCase("mp4B") || formato.equalsIgnoreCase("AA/AAX")) {
                formatoCorrecto = true;
            } else {
                System.out.println("ERROR: El formato del audiolibro no es valido.");
            }
        } while (!formatoCorrecto);
        return formato;
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return Entrada.entero();
    }

    private static Categoria leerCategoria() {
        System.out.println("Categorías disponibles:");
        // Mostramos las categorias disponibles
        for (Categoria categoria : Categoria.values()) {
            System.out.println(categoria.ordinal() + ".- " + categoria.name()); // Mostramos el ordinal y el nombre de la categoria
        }
        int ordinal;
        // Pedimos la categoria y validamos que sea correcta
        do {
            ordinal = leerEntero("Elige una categoría: ");
        } while (Categoria.get(ordinal) == null); // Mientras que la categoria no sea correcta se repite el bucle
        // Devolvemos la categoria elegida
        return Categoria.get(ordinal);
    }
}