package org.iesalandalus.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Categoria;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Libros;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Prestamos;
import org.iesalandalus.programacion.biblioteca.modelo.negocio.Usuarios;

public class Consola {

    private static final Scanner entrada = new Scanner(System.in);

    private Consola() {}

    public static void mostrarMenu() {
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("                                   GESTIÓN DE BIBLIOTECA");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("1.- Alta de libro.");
        System.out.println("2.- Baja de libro.");
        System.out.println("3.- Listado de libros.");
        System.out.println("4.- Alta de usuario.");
        System.out.println("5.- Listado de usuarios.");
        System.out.println("6.- Prestar libro.");
        System.out.println("7.- Devolver libro.");
        System.out.println("8.- Listado de préstamos de un usuario.");
        System.out.println("9.- Listado de préstamos (Histórico).");
        System.out.println("");
        System.out.println("0.- Salir.");
        System.out.println("");
    }

    public static int elegirOpcion() {
        int opcion;
        do {
            opcion = leerEntero("Elige una opción: ");
        } while (opcion < 0 || opcion > 9);
        return opcion;
    }

    public static Libro leerLibro() {
        System.out.print("Introduce el título: ");
        String titulo = entrada.nextLine();
        System.out.print("Introduce el ISBN: ");
        String isbn = entrada.nextLine();
        int anio = leerEntero("Introduce el año de publicación: ");
        Categoria categoria = leerCategoria();
        int unidades = leerEntero("Introduce el número de unidades: ");
        
        return new Libro(isbn, titulo, anio, categoria, unidades);
    }
    
    public static Usuario leerUsuario() {
        System.out.print("Introduce el nombre: ");
        String nombre = entrada.nextLine();
        System.out.print("Introduce el ID: ");
        String id = entrada.nextLine();
        System.out.print("Introduce el correo: ");
        String correo = entrada.nextLine();
        
        return new Usuario(id, nombre, correo);
    }

    public static LocalDate leerFecha(String mensaje) {
        LocalDate fecha = null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean fechaCorrecta = false;
        do {
            try {
                System.out.print(mensaje + " (dd/MM/yyyy): ");
                fecha = LocalDate.parse(entrada.nextLine(), formato);
                fechaCorrecta = true;
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: El formato de la fecha no es correcto.");
            }
        } while (!fechaCorrecta);
        return fecha;
    }

    private static int leerEntero(String mensaje) {
        int entero = 0;
        boolean enteroCorrecto = false;
        do {
            try {
                System.out.print(mensaje);
                entero = Integer.parseInt(entrada.nextLine());
                enteroCorrecto = true;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Debes introducir un número entero.");
            }
        } while (!enteroCorrecto);
        return entero;
    }

    private static Categoria leerCategoria() {
        System.out.println("Categorías disponibles:");
        for (Categoria categoria : Categoria.values()) {
            System.out.println(categoria.ordinal() + ".- " + categoria.name());
        }
        int ordinal;
        Categoria categoria = null;
        do {
            ordinal = leerEntero("Elige una categoría: ");
            categoria = Categoria.get(ordinal);
            if (categoria == null) {
                System.out.println("ERROR: Categoría no válida.");
            }
        } while (categoria == null);
        return categoria;
    }

    public static void altaLibro(Libros libros) {
        try {
            Libro libro = leerLibro();
            libros.alta(libro);
            System.out.println("Libro dado de alta correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void bajaLibro(Libros libros) {
        try {
            System.out.print("Introduce el ISBN del libro a borrar: ");
            String isbn = entrada.nextLine();
            // Creamos un libro ficticio solo con el ISBN para la búsqueda
            Libro libro = new Libro(isbn, "Ficticio", 1, Categoria.OTROS, 1);
            if (libros.baja(libro)) {
                System.out.println("Libro borrado correctamente.");
            } else {
                System.out.println("ERROR: No se encontró el libro.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listarLibros(Libros libros) {
        try {
            for (Libro libro : libros.todos()) {
                System.out.println(libro);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void altaUsuario(Usuarios usuarios) {
        try {
            Usuario usuario = leerUsuario();
            usuarios.alta(usuario);
            System.out.println("Usuario dado de alta correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void bajaUsuario(Usuarios usuarios) {
        try {
            System.out.print("Introduce el ID del usuario a borrar: ");
            String id = entrada.nextLine();
            // Creamos un usuario ficticio solo con el ID para la búsqueda
            Usuario usuario = new Usuario(id, "Ficticio", "a@a.com");
            if (usuarios.baja(usuario)) {
                System.out.println("Usuario borrado correctamente.");
            } else {
                System.out.println("ERROR: No se encontró el usuario.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listarUsuarios(Usuarios usuarios) {
        try {
            for (Usuario usuario : usuarios.todos()) {
                System.out.println(usuario);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void prestarLibro(Prestamos prestamos, Libros libros, Usuarios usuarios) {
        try {
            System.out.print("Introduce el ISBN del libro: ");
            String isbn = entrada.nextLine();
            Libro libro = libros.buscar(new Libro(isbn, "Ficticio", 1, Categoria.OTROS, 1));
            if (libro == null) {
                System.out.println("ERROR: No se encuentra el libro.");
                return;
            }
            System.out.print("Introduce el ID del usuario: ");
            String id = entrada.nextLine();
            Usuario usuario = usuarios.buscar(new Usuario(id, "Ficticio", "a@a.com"));
            if (usuario == null) {
                System.out.println("ERROR: No se encuentra el usuario.");
                return;
            }
            LocalDate fecha = leerFecha("Introduce la fecha de préstamo");
            prestamos.prestar(libro, usuario, fecha);
            System.out.println("Libro prestado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void devolverLibro(Prestamos prestamos) {
        try {
            System.out.print("Introduce el ISBN del libro: ");
            String isbn = entrada.nextLine();
            System.out.print("Introduce el ID del usuario: ");
            String id = entrada.nextLine();
            LocalDate fecha = leerFecha("Introduce la fecha de devolución");
            if (prestamos.devolver(isbn, id, fecha)) {
                System.out.println("Libro devuelto correctamente.");
            } else {
                System.out.println("ERROR: No se pudo devolver el libro (no existe préstamo o ya devuelto).");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listarPrestamosUsuario(Prestamos prestamos, Usuarios usuarios) {
        try {
            System.out.print("Introduce el ID del usuario: ");
            String id = entrada.nextLine();
            Usuario usuario = usuarios.buscar(new Usuario(id, "Ficticio", "a@a.com"));
            if (usuario == null) {
                System.out.println("ERROR: No se encuentra el usuario.");
                return;
            }
            for (Prestamo prestamo : prestamos.prestamosUsuario(usuario)) {
                System.out.println(prestamo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listarPrestamos(Prestamos prestamos) {
        try {
            for (Prestamo prestamo : prestamos.historico()) {
                System.out.println(prestamo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void iniciar(Libros libros, Usuarios usuarios, Prestamos prestamos) {
        int opcion;
        do {
            mostrarMenu();
            opcion = elegirOpcion();
            switch (opcion) {
                case 1:
                    altaLibro(libros);
                    break;
                case 2:
                    bajaLibro(libros);
                    break;
                case 3:
                    listarLibros(libros);
                    break;
                case 4:
                    altaUsuario(usuarios);
                    break;
                case 5:
                    listarUsuarios(usuarios);
                    break;
                case 6:
                    prestarLibro(prestamos, libros, usuarios);
                    break;
                case 7:
                    devolverLibro(prestamos);
                    break;
                case 8:
                    listarPrestamosUsuario(prestamos, usuarios);
                    break;
                case 9:
                    listarPrestamos(prestamos);
                    break;
                case 0:
                    System.out.println("¡Adios!");
                    break;
            }
        } while (opcion != 0);
    }
}
