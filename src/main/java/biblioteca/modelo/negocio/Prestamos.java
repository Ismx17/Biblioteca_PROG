package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.*;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {
    // Singleton
    private static Prestamos prestamos;
    private Prestamos() {}

    // Metodo para obtener la instancia de la clase Prestamos
    public static synchronized Prestamos getPrestamos() {
        if (prestamos == null) prestamos = new Prestamos();
        return prestamos;
    }

    // Metodos para establecer y cerrar la conexion con la base de datos
    public void comenzar() { 
        try {
            Conexion.getConexion().establecerConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo establecer la conexión: " + e.getMessage());
        }
    }

    public void terminar() { 
        try {
            Conexion.getConexion().cerrarConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
        }
    }

    // Metodo para dar de alta un nuevo prestamo
    public void prestar(Libro libroFicticio, Usuario usuarioFicticio, LocalDate fInicio) {        
        Libro libro = Libros.getLibros().buscar(libroFicticio);
        if (libro == null) {
            System.out.println("ERROR: El libro no existe.");
            return;
        }
        
        Usuario usuario = Usuarios.getUsuarios().buscar(usuarioFicticio);
        if (usuario == null) {
            System.out.println("ERROR: El usuario no existe.");
            return;
        }

        Connection con = Conexion.getConexion().getJdbcConnection();
        String sqlCheck = "SELECT COUNT(*) FROM prestamo WHERE isbn = ? AND devuelto = false";
        String sqlInsert = "INSERT INTO prestamo (dni, isbn, fInicio, fLimite, devuelto) VALUES (?, ?, ?, ?, ?)";
        try {
            // Iniciamos la transacción
            con.setAutoCommit(false);
            // Validación para saber si el libro esta disponible
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setString(1, libro.getIsbn());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("ERROR: El libro con ISBN " + libro.getIsbn() + " ya está prestado.");
                    return;
                }
            }
            
            Prestamo p = new Prestamo(libro, usuario, fInicio);
            // Inserción del préstamo
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setString(1, p.getUsuario().getDni());
                psInsert.setString(2, p.getLibro().getIsbn());
                psInsert.setDate(3, Date.valueOf(p.getfInicio()));
                psInsert.setDate(4, Date.valueOf(p.getfLimite()));
                psInsert.setBoolean(5, false);
                // Ejecutamos la insercion
                psInsert.executeUpdate();
            }
            // Confirmamos los cambios
            con.commit();
        } catch (SQLException e) {
            try {
                // Si algo falla deshacemos todo
                con.rollback();
            } catch (SQLException ex) {
            }
            System.out.println("ERROR: No se ha podido registrar el préstamo: " + e.getMessage());
        } finally {
            try {
                // Restauramos el estado de la conexión
                con.setAutoCommit(true);
            } catch (SQLException ex) {
            }
        }
    }

    // Metodo para registrar la devolucion de un libro prestado
    public boolean devolver(Libro l, Usuario u, LocalDate fDev) {
        // Consulta para actualizar el estado del prestamo (solo si no ha sido devuelto ya)
        String sql = "UPDATE prestamo SET devuelto = true, fDevolucion = ? WHERE dni = ? AND isbn = ? AND fDevolucion IS NULL";
        // Ejecutamos la actualizacion con la fecha de devolucion proporcionada
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fDev));
            ps.setString(2, u.getDni());
            ps.setString(3, l.getIsbn());
            // Devolvemos true si se ha actualizado el registro, indicando que la devolucion fue exitosa
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo procesar la devolución: " + e.getMessage());
            return false;
        }
    }

    // Metodo para obtener todos los prestamos registrados
    public List<Prestamo> todos() {
        List<Prestamo> lista = new ArrayList<>();
        // Consulta para obtener todos los registros de la tabla prestamo ordenados por fecha de inicio descendente
        String sql = "SELECT * FROM prestamo ORDER BY fInicio DESC";
        // Ejecutamos la consulta y recorremos los resultados
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Buscamos los objetos Usuario y Libro correspondientes
                Usuario usuario = Usuarios.getUsuarios().buscar(new Usuario(rs.getString("dni"), "F", "a@a.com", new Direccion("V", "1", "11111", "L")));
                Libro libro = Libros.getLibros().buscar(new Libro(rs.getString("isbn"), "F", 1, Categoria.OTROS));
                // Si ambos objetos existen, reconstruimos el objeto Prestamo
                // Garantiza que siempre existan los objetos Usuario y Libro
                if (usuario != null && libro != null) {
                    Prestamo p = new Prestamo(libro, usuario, rs.getDate("fInicio").toLocalDate());
                    // Si el prestamo figura como devuelto en BD, lo marcamos en el objeto
                    if (rs.getBoolean("devuelto")) {
                        p.marcarDevuelto(rs.getDate("fDevolucion").toLocalDate());
                    }
                    // Lo añadimos a la lista
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo obtener el listado de préstamos: " + e.getMessage());
        }
        // Devolvemos la lista de prestamos encontrados
        return lista;
    }

    // Metodo para obtener todos los prestamos de un usuario especifico
    public List<Prestamo> todosPorUsuario(Usuario usuario) {
        List<Prestamo> lista = new ArrayList<>();
        // Consulta para filtrar los prestamos por el DNI del usuario en orden de fecha de inicio descendente
        String sql = "SELECT * FROM prestamo WHERE dni = ? ORDER BY fInicio DESC";
        // Ejecutamos la consulta
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, usuario.getDni());
            try (ResultSet rs = ps.executeQuery()) {
                // Mientras haya resultados, reconstruimos los prestamos
                while (rs.next()) {
                    Libro lib = Libros.getLibros().buscar(new Libro(rs.getString("isbn"), "F", 1, biblioteca.modelo.dominio.Categoria.OTROS));
                    if (lib != null) {
                        Prestamo p = new Prestamo(lib, usuario, rs.getDate("fInicio").toLocalDate());
                        // Si ya ha sido devuelto, actualizamos el objeto
                        if (rs.getBoolean("devuelto")) {
                            p.marcarDevuelto(rs.getDate("fDevolucion").toLocalDate());
                        }
                        // Lo añadimos a la lista
                        lista.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron obtener los préstamos del usuario: " + e.getMessage());
        }
        // Devolvemos la lista de prestamos del usuario
        return lista;
    }
}