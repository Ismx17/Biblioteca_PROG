package biblioteca.vista;

import biblioteca.controlador.Controlador;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;



public class Vista {

    private Opcion opcion;
    private Controlador controlador;
    
    public Vista() {}

     public void setControlador(Controlador controlador) {
        if(controlador == null) {
            throw new IllegalArgumentException("ERROR: El controlador no puede ser nulo.");
        }
        this.controlador = controlador;
     }

     public void comenzar() {
        do {
            Consola.mostrarMenu();
            opcion = Consola.elegirOpcion();
            ejecutarOpcion(opcion);
        } while (opcion != Opcion.SALIR);
    }

    public void terminar() {
        System.out.println("¡Hasta pronto!");
    }

    private void ejecutarOpcion(Opcion opcion) {
        if (opcion == null) {
            throw new IllegalArgumentException("ERROR: La opcion no puede ser nula.");
        }
        switch (opcion) {
            case INSERTAR_USUARIO:
                insertarUsuario();
                break;
            case BORRAR_USUARIO:
                borrarUsuario();
                break;
            case MOSTRAR_USUARIO:
                mostrarUsuarios();
                break;
            case INSERTAR_LIBRO:
                insertarLibro();
                break;
            case BORRAR_LIBRO:
                borrarLibro();
                break;
            case MOSTRAR_LIBROS:
                mostrarLibros();
                break;
            case NUEVO_PRESTAMO:
                nuevoPrestamo();
                break;
            case DEVOLVER_PRESTAMO:
                devolverPrestamo();
                break;
            case MOSTRAR_PRESTAMOS:
                mostrarPrestamos();
                break;
            case MOSTRAR_PRESTAMOS_USUARIOS:
                mostrarPrestamosUsuario();
                break;
            case SALIR:
                controlador.terminar();
                break;
        }
    }

    private void insertarUsuario() {
        try {
            Usuario usuario = Consola.nuevoUsuario(false);
            controlador.alta(usuario); // Intento dar de alta
            System.out.println("Usuario insertado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Muestro el error si falla
        }
    }

    private void borrarUsuario() {
        try {
            // Creamos un usuario ficticio solo con el DNI para la búsqueda
            Usuario usuario = Consola.nuevoUsuario(true);
            // Intento dar de baja
            if (controlador.baja(usuario)) {
                System.out.println("Usuario borrado correctamente.");
            } else {
                System.out.println("ERROR: No se encontró el usuario.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarUsuarios() {
        try {
            // Recorro la lista de usuarios yla muestro
            List<Usuario> usuarios = controlador.listadoUsuarios(); // Lista ya ordenada alfabeticamente por nombre en el modelo

            for (Usuario usuario : usuarios) {
                System.out.println(usuario);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertarLibro() {
        try {
            // Intento dar de alta el libro
            Libro libro = Consola.nuevoLibro(false);
            controlador.alta(libro);
            System.out.println("Libro dado de alta correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void borrarLibro() {
        try {
            // Creamos un libro ficticio solo con el ISBN para la búsqueda
            Libro libro = Consola.nuevoLibro(true);
            // Intento dar de baja el libro
            if (controlador.baja(libro)) {
                System.out.println("Libro borrado correctamente.");
            } else {
                System.out.println("ERROR: No se encontró el libro.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarLibros() {
        try {
            // Recorro la lista de libros y la muestro
            List<Libro> libros = controlador.listadoLibros(); // Lista ya ordenada alfabeticamente por titulo en el modelo

            for (Libro libro : libros) {
                System.out.println(libro);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void nuevoPrestamo() {
        try {
            // Buscamos el libro por ISBN
            Libro libro = controlador.buscar(Consola.nuevoLibro(true));
            // Si el libro no existe mostramos mensaje de error y salimos del metodo
            if (libro == null) {
                System.out.println("ERROR: No se encuentra el libro.");
                return;
            }
            // Buscamos el usuario por DNI
            Usuario usuario = controlador.buscar(Consola.nuevoUsuario(true));
            // Si el usuario no existe mostramos mensaje de error y salimos del metodo
            if (usuario == null) {
                System.out.println("ERROR: No se encuentra el usuario.");
                return;
            }
            LocalDate fecha = Consola.leerFecha("Introduce la fecha de préstamo"); // Pedimos la fecha de prestamo
            // Intento dar de alta el prestamo
            controlador.prestar(libro, usuario, fecha);
            System.out.println("Libro prestado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void devolverPrestamo() {
        try {
            // Creamos objetos ficticios para pasar al controlador
            Libro libro = Consola.nuevoLibro(true);
            Usuario usuario = Consola.nuevoUsuario(true);
            LocalDate fecha = Consola.leerFecha("Introduce la fecha de devolución");
            
            // Intento dar de baja elprestamo
            if (controlador.devolver(libro, usuario, fecha)) {
                System.out.println("Libro devuelto correctamente.");
            } else {
                System.out.println("ERROR: No se pudo devolver el libro (no existe préstamo o ya devuelto).");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarPrestamos() {
        // Recorro la lista de prestamos y la muestro
        try {
            List<Prestamo> prestamos = controlador.listadoPrestamos();
            prestamos.sort(Comparator.comparing(Prestamo::getfInicio).reversed().thenComparing(p -> p.getUsuario().getNombre()));
            for (Prestamo prestamo : prestamos) {
                System.out.println(prestamo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarPrestamosUsuario() {
        // Buscamos el usuario por DNI
        try {
            Usuario usuario = controlador.buscar(Consola.nuevoUsuario(true));
            // Si el usuario no existe mostramos el mensaje de error y salimos del metodo
            if (usuario == null) {
                System.out.println("ERROR: No se encuentra el usuario.");
                // Salimos del metodo
                return;
            }
            // Recorro la lista de prestamos y la muestro
            List<Prestamo> prestamos = controlador.listadoPrestamos(usuario);
            prestamos.sort(Comparator.comparing(Prestamo::getfInicio).reversed().thenComparing(p -> p.getUsuario().getNombre()));
            for (Prestamo prestamo : prestamos) {
                System.out.println(prestamo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}