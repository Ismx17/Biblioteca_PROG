package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;
import biblioteca.modelo.dominio.Categoria;
import biblioteca.modelo.dominio.Direccion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class Prestamos {
    private static Prestamos prestamos;

    private Prestamos() {}

    public static Prestamos getPrestamos() {
        if (prestamos == null) {
            prestamos = new Prestamos();
        }
        return prestamos;
    }

    public void comenzar() {
        try {
            Conexion.getConexion().establecerConexion();
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }

    public void terminar() {
        try {
            Conexion.getConexion().cerrarConexion();
        } catch (SQLException e) {
            System.out.println("Error al desconectar: " + e.getMessage());
        }
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null || usuario == null || fecha == null) throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        if (fecha.isBefore(LocalDate.now())) throw new IllegalArgumentException("ERROR: La fecha no puede ser anterior a la actual.");

        Connection con = Conexion.getConexion().getJdbcConnection();
        
        try (PreparedStatement psCheck = con.prepareStatement("SELECT COUNT(*) FROM prestamo WHERE libro_isbn = ? AND usuario_dni = ? AND fDevolucion IS NULL")) {
            psCheck.setString(1, libro.getIsbn());
            psCheck.setString(2, usuario.getDni());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("ERROR: El usuario ya tiene un prestamo activo de este libro.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de BD: " + e.getMessage());
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO prestamo (libro_isbn, usuario_dni, fInicio) VALUES (?, ?, ?)")) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, usuario.getDni());
            ps.setDate(3, Date.valueOf(fecha));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar préstamo: " + e.getMessage());
        }
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null || usuario == null || fecha == null) throw new IllegalArgumentException("ERROR: Los parametros no pueden ser nulos.");
        Connection con = Conexion.getConexion().getJdbcConnection();
        
        try (PreparedStatement psCheck = con.prepareStatement("SELECT fInicio FROM prestamo WHERE libro_isbn = ? AND usuario_dni = ? AND fDevolucion IS NULL")) {
            psCheck.setString(1, libro.getIsbn());
            psCheck.setString(2, usuario.getDni());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    LocalDate fInicio = rs.getDate("fInicio").toLocalDate();
                    if (fecha.isBefore(fInicio)) {
                        throw new IllegalArgumentException("ERROR: La fecha de devolución no puede ser anterior a la fecha de inicio.");
                    }
                } else {
                    return false; // No hay préstamo activo
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al comprobar préstamo: " + e.getMessage());
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement("UPDATE prestamo SET fDevolucion = ? WHERE libro_isbn = ? AND usuario_dni = ? AND fDevolucion IS NULL")) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setString(2, libro.getIsbn());
            ps.setString(3, usuario.getDni());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al devolver préstamo: " + e.getMessage());
            return false;
        }
    }

    public List<Prestamo> todos(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        return obtenerPrestamos("SELECT * FROM prestamo WHERE usuario_dni = ?", usuario.getDni());
    }

    public List<Prestamo> todos() {
        return obtenerPrestamos("SELECT * FROM prestamo", null);
    }

    private List<Prestamo> obtenerPrestamos(String query, String dniFiltro) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection con = Conexion.getConexion().getJdbcConnection();

        try (PreparedStatement ps = con.prepareStatement(query)) {
            if (dniFiltro != null) ps.setString(1, dniFiltro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("libro_isbn");
                    String dni = rs.getString("usuario_dni");
                    LocalDate fInicio = rs.getDate("fInicio").toLocalDate();
                    Date fDevolucion = rs.getDate("fDevolucion");

                    Libro lReal = Libros.getLibros().buscar(new Libro(isbn, "Ficticio", 1, Categoria.OTROS));
                    Usuario uReal = Usuarios.getUsuarios().buscar(new Usuario(dni, "Ficticio", "a@a.com", new Direccion("V", "1", "11111", "L")));

                    if (lReal != null && uReal != null) {
                        Prestamo p = new Prestamo(lReal, uReal, fInicio);
                        if (fDevolucion != null) p.marcarDevuelto(fDevolucion.toLocalDate());
                        prestamos.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener préstamos: " + e.getMessage());
        }
        return prestamos;
    }
}