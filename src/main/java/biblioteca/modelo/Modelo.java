package biblioteca.modelo;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.Libros;
import biblioteca.modelo.negocio.Prestamos;
import biblioteca.modelo.negocio.Usuarios;

import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;

public class Modelo {

    public Modelo() {}

    public void comenzar() {
        try {
            // Iniciamos las conexiones con la base de datos
            Libros.getLibros().comenzar();
            Usuarios.getUsuarios().comenzar();
            Prestamos.getPrestamos().comenzar();
            System.out.println("Conexiones abiertas. Modelo iniciado");
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: No se pudo iniciar la conexión con la base de datos: " + e.getMessage());
        }
    }

    public void terminar() {
        try {
            // Terminamos las conexiones con la base de datos
            Libros.getLibros().terminar();
            Usuarios.getUsuarios().terminar();
            Prestamos.getPrestamos().terminar();
            System.out.println("Conexiones cerradas. Modelo finalizado");
        } catch (SQLException e) {
            System.out.println("Error al cerrar las conexiones: " + e.getMessage());
        }
    }

    // GESTIÓN DE LIBROS
    public void alta(Libro libro) {
        try {
            Libros.getLibros().alta(libro);
        } catch (SQLException e) {
            throw new IllegalArgumentException("ERROR: No se pudo dar de alta el libro. " + e.getMessage());
        }
    }

    public boolean baja(Libro libro) {
        try {
            // Comprobamos la lógica de préstamos activos
            List<Prestamo> todos = Prestamos.getPrestamos().todos();
            for (Prestamo p : todos) {
                if (p.getLibro().getIsbn().equals(libro.getIsbn())) {
                    if (!p.isDevuelto()) {
                        throw new IllegalStateException("ERROR: El libro tiene préstamos activos.");
                    } else {
                    }
                }
            }
            return Libros.getLibros().baja(libro);
        } catch (SQLException e) {
            // Debido a las restricciones de la base de datos, no podemos borrar un libro en caso de estar en el historial de prestamos
            throw new IllegalArgumentException("ERROR: No se puede borrar el libro porque figura en el historial de préstamos.");
        }
    }

    public Libro buscar(Libro libro) {
        try {
            return Libros.getLibros().buscar(libro);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Libro> listadoLibros() {
        try {
            return Libros.getLibros().todos();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: No se pudo obtener el listado de libros.");
        }
    }

    // GESTIÓN DE USUARIOS
    public void alta(Usuario usuario) {
        try {
            Usuarios.getUsuarios().alta(usuario);
        } catch (SQLException e) {
            throw new IllegalArgumentException("ERROR: No se pudo dar de alta el usuario. " + e.getMessage());
        }
    }

    public boolean baja(Usuario usuario) {
        try {
            List<Prestamo> todos = Prestamos.getPrestamos().todos();
            for (Prestamo p : todos) {
                // Debido a las restricciones de la base de datos, no podemos borrar un usuario en caso de tener historial de prestamos
                if (p.getUsuario().getDni().equals(usuario.getDni())) {
                    throw new IllegalStateException("ERROR: No se puede borrar un usuario que tiene historial de préstamos.");
                }
            }
            return Usuarios.getUsuarios().baja(usuario);
        } catch (SQLException e) {
            throw new IllegalArgumentException("ERROR: " + e.getMessage());
        }
    }

    public Usuario buscar(Usuario usuario) {
        try {
            return Usuarios.getUsuarios().buscar(usuario);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Usuario> listadoUsuarios() {
        try {
            return Usuarios.getUsuarios().todos();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: No se pudo obtener el listado de usuarios.");
        }
    }

    // GESTIÓN DE PRÉSTAMOS
    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        try {
            // Comprobamos si el libro ya está prestado si fDevolucion es null
            List<Prestamo> actuales = Prestamos.getPrestamos().todos();
            for (Prestamo p : actuales) {
                if (p.getLibro().equals(libro) && !p.isDevuelto()) {
                    throw new IllegalArgumentException("ERROR: El libro está prestado actualmente.");
                }
            }
            Prestamo prestamo = new Prestamo(libro, usuario, fecha);
            Prestamos.getPrestamos().prestar(prestamo);
        } catch (SQLException e) {
            throw new IllegalArgumentException("ERROR: No se ha podido registrar el préstamo. " + e.getMessage());
        }
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        try {
            return Prestamos.getPrestamos().devolver(libro, usuario, fecha);
        } catch (SQLException e) {
            throw new IllegalArgumentException("ERROR: No se ha podido procesar la devolución. " + e.getMessage());
        }
    }

    public List<Prestamo> listadoPrestamos() {
        try {
            return Prestamos.getPrestamos().todos();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: No se han podido obtener los préstamos del usuario.");
        }
    }

    public List<Prestamo> listadoPrestamos(Usuario usuario) {
        try {
            return Prestamos.getPrestamos().todosPorUsuario(usuario);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: No se pudo obtener el listado de préstamos.");
        }
    }
}