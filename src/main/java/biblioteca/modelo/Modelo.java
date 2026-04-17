package biblioteca.modelo;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.Libros;
import biblioteca.modelo.negocio.Prestamos;
import biblioteca.modelo.negocio.Usuarios;

import java.time.LocalDate;
import java.util.List;

public class Modelo {

    public Modelo() {}

    public void comenzar() {
        // Iniciamos las conexiones con la base de datos
        Libros.getLibros().comenzar();
        Usuarios.getUsuarios().comenzar();
        Prestamos.getPrestamos().comenzar();
        System.out.println("Conexiones abiertas. Modelo iniciado");
    }

    public void terminar() {
        // Terminamos las conexiones con la base de datos
        Libros.getLibros().terminar();
        Usuarios.getUsuarios().terminar();
        Prestamos.getPrestamos().terminar();
        System.out.println("Conexiones cerradas. Modelo finalizado");
    }

    // GESTIÓN DE LIBROS
    public void alta(Libro libro) {
        Libros.getLibros().alta(libro);
    }

    public boolean baja(Libro libro) {
        // Comprobamos la lógica de préstamos activos
        List<Prestamo> todos = Prestamos.getPrestamos().todos();
        for (Prestamo p : todos) {
            if (p.getLibro().getIsbn().equals(libro.getIsbn())) {
                if (!p.isDevuelto()) {
                    throw new IllegalStateException("ERROR: El libro tiene préstamos activos.");
                }
            }
        }
        return Libros.getLibros().baja(libro);
    }

    public Libro buscar(Libro libro) {
        return Libros.getLibros().buscar(libro);
    }

    public List<Libro> listadoLibros() {
        return Libros.getLibros().todos();
    }

    // GESTIÓN DE USUARIOS
    public void alta(Usuario usuario) {
        Usuarios.getUsuarios().alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        List<Prestamo> todos = Prestamos.getPrestamos().todos();
        for (Prestamo p : todos) {
            // Debido a las restricciones de la base de datos, no podemos borrar un usuario en caso de tener historial de prestamos
            if (p.getUsuario().getDni().equals(usuario.getDni())) {
                throw new IllegalStateException("ERROR: No se puede borrar un usuario que tiene historial de préstamos.");
            }
        }
        return Usuarios.getUsuarios().baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        return Usuarios.getUsuarios().buscar(usuario);
    }

    public List<Usuario> listadoUsuarios() {
        return Usuarios.getUsuarios().todos();
    }

    // GESTIÓN DE PRÉSTAMOS
    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        Prestamos.getPrestamos().prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        return Prestamos.getPrestamos().devolver(libro, usuario, fecha);
    }

    public List<Prestamo> listadoPrestamos() {
        return Prestamos.getPrestamos().todos();
    }

    public List<Prestamo> listadoPrestamos(Usuario usuario) {
        return Prestamos.getPrestamos().todosPorUsuario(usuario);
    }
}