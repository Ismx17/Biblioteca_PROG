package biblioteca.modelo;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.Autores;
import biblioteca.modelo.negocio.Libros;
import biblioteca.modelo.negocio.Prestamos;
import biblioteca.modelo.negocio.Usuarios;

import java.time.LocalDate;
import java.util.List;

public class Modelo {

    public Modelo() {}

    public void comenzar() {
        // Iniciamos las conexiones con la base de datos
        Autores.getInstancia().comenzar();
        Libros.getInstancia().comenzar();
        Usuarios.getInstancia().comenzar();
        Prestamos.getInstancia().comenzar();
        System.out.println("Conexiones abiertas. Modelo iniciado");
    }

    public void terminar() {
        // Terminamos las conexiones con la base de datos
        Autores.getInstancia().terminar();
        Libros.getInstancia().terminar();
        Usuarios.getInstancia().terminar();
        Prestamos.getInstancia().terminar();
        System.out.println("Conexiones cerradas. Modelo finalizado");
    }

    // GESTIÓN DE LIBROS
    public void alta(Libro libro) {
        Libros.getInstancia().alta(libro);
    }

    public boolean baja(Libro libro) {
        List<Prestamo> todos = Prestamos.getInstancia().todos();
        for (Prestamo p : todos) {
            if (p.getLibro().getIsbn().equals(libro.getIsbn())) {
                // Eliminamos "if (!p.isDevuelto())" para que salte siempre
                throw new IllegalStateException("ERROR: El libro tiene registros de préstamos asociados.");
            }
        }
        return Libros.getInstancia().baja(libro);
    }

    public Libro buscar(Libro libro) {
        return Libros.getInstancia().buscar(libro);
    }

    public List<Libro> listadoLibros() {
        return Libros.getInstancia().todos();
    }

    // GESTIÓN DE USUARIOS
    public void alta(Usuario usuario) {
        Usuarios.getInstancia().alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        List<Prestamo> todos = Prestamos.getInstancia().todos();
        for (Prestamo p : todos) {
            // Debido a las restricciones de la base de datos, no podemos borrar un usuario en caso de tener historial de prestamos
            if (p.getUsuario().getDni().equals(usuario.getDni())) {
                throw new IllegalStateException("ERROR: No se puede borrar un usuario que tiene historial de préstamos.");
            }
        }
        return Usuarios.getInstancia().baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        return Usuarios.getInstancia().buscar(usuario);
    }

    public List<Usuario> listadoUsuarios() {
        return Usuarios.getInstancia().todos();
    }

    // GESTIÓN DE PRÉSTAMOS
    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        Prestamos.getInstancia().prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        return Prestamos.getInstancia().devolver(libro, usuario, fecha);
    }

    public List<Prestamo> listadoPrestamos() {
        return Prestamos.getInstancia().todos();
    }

    public List<Prestamo> listadoPrestamos(Usuario usuario) {
        return Prestamos.getInstancia().todosPorUsuario(usuario);
    }
}