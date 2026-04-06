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
    public void comenzar() throws SQLException { Conexion.getInstancia().establecerConexion(); }
    public void terminar() throws SQLException { Conexion.getInstancia().cerrarConexion(); }

    // Metodo para dar de alta un nuevo prestamo
    public void prestar(Prestamo p) throws SQLException {
        // Consulta para insertar el prestamo
        String sql = "INSERT INTO prestamo (dni, isbn, fInicio, fLimite, devuelto) VALUES (?, ?, ?, ?, ?)";
        // Preparamos la sentencia con los datos del prestamo (DNI del usuario y ISBN del libro)
        try (PreparedStatement ps = Conexion.getInstancia().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, p.getUsuario().getDni());
            ps.setString(2, p.getLibro().getIsbn());
            // Convertimos las fechas LocalDate a java.sql.Date
            ps.setDate(3, Date.valueOf(p.getfInicio()));
            ps.setDate(4, Date.valueOf(p.getfLimite()));
            ps.setBoolean(5, false); // Por defecto el libro no esta devuelto al prestarse
            // Ejecutamos la insercion
            ps.executeUpdate();
        }
    }

    // Metodo para registrar la devolucion de un libro prestado
    public boolean devolver(Libro l, Usuario u, LocalDate fDev) throws SQLException {
        // Consulta para actualizar el estado del prestamo (solo si no ha sido devuelto ya)
        String sql = "UPDATE prestamo SET devuelto = true, fDevolucion = ? WHERE dni = ? AND isbn = ? AND fDevolucion IS NULL";
        // Ejecutamos la actualizacion con la fecha de devolucion proporcionada
        try (PreparedStatement ps = Conexion.getInstancia().getJdbcConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fDev));
            ps.setString(2, u.getDni());
            ps.setString(3, l.getIsbn());
            // Devolvemos true si se ha actualizado el registro, indicando que la devolucion fue exitosa
            return ps.executeUpdate() > 0;
        }
    }

    // Metodo para obtener todos los prestamos registrados
    public List<Prestamo> todos() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        // Consulta para obtener todos los registros de la tabla prestamo
        String sql = "SELECT * FROM prestamo";
        // Ejecutamos la consulta y recorremos los resultados
        try (Statement st = Conexion.getInstancia().getJdbcConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Buscamos los objetos Usuario y Libro correspondientes mediante sus clases de negocio
                Usuario user = Usuarios.getUsuarios().buscar(new Usuario(rs.getString("dni"), "F", "a@a.com", new Direccion("V", "1", "11111", "L")));
                Libro lib = Libros.getLibros().buscar(new Libro(rs.getString("isbn"), "F", 1, Categoria.OTROS));
                
                // Si ambos objetos existen, reconstruimos el objeto Prestamo
                if (user != null && lib != null) {
                    Prestamo p = new Prestamo(lib, user, rs.getDate("fInicio").toLocalDate());
                    // Si el prestamo figura como devuelto en BD, lo marcamos en el objeto
                    if (rs.getBoolean("devuelto")) p.marcarDevuelto(rs.getDate("fDevolucion").toLocalDate());
                    lista.add(p);
                }
            }
        }
        // Devolvemos la lista de prestamos encontrados
        return lista;
    }

    // Metodo para obtener todos los prestamos de un usuario especifico
    public List<Prestamo> todosPorUsuario(Usuario usuario) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        // Consulta para filtrar los prestamos por el DNI del usuario
        String sql = "SELECT * FROM prestamo WHERE dni = ?";
        // Ejecutamos la consulta preparada
        try (PreparedStatement ps = Conexion.getInstancia().getJdbcConnection().prepareStatement(sql)) {
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
                        lista.add(p);
                    }
                }
            }
        }
        // Devolvemos la lista de prestamos del usuario
        return lista;
    }
}