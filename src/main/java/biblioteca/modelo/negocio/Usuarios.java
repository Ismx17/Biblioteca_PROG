package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    // Singleton
    private static Usuarios usuarios;
    private Usuarios() {}

    // Metodo para obtener la instancia de la clase Usuarios
    public static synchronized Usuarios getUsuarios() {
        if (usuarios == null) {
            usuarios = new Usuarios();
        }
        return usuarios;
    }

    // Metodos para establecer y cerrar la conexion con la base de datos
    public void comenzar() throws SQLException { 
        Conexion.getConexion().establecerConexion(); 
    }
    public void terminar() throws SQLException { 
        Conexion.getConexion().cerrarConexion(); 
    }

    // Metodo para dar de alta un usuario y su direccion
    public void alta(Usuario usuario) throws SQLException {
        // Valido que el usuario no sea nulo
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Conexion con la base de datos
        Connection con = Conexion.getConexion().getJdbcConnection();

        try {
            // Desactivamos el auto commit para poder hacer las operaciones de manera atomica
            con.setAutoCommit(false);
            // Insertamos el usuario en la tabla usuario
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO usuario (dni, nombre, email) VALUES (?, ?, ?)")) {
                ps.setString(1, usuario.getDni());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getEmail());
                ps.executeUpdate();
            }
            
            // Insertamos la direccion en la tabla direccion (utiliza el mismo DNI como PK)
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO direccion (dni, via, numero, cp, localidad) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, usuario.getDni());
                ps.setString(2, usuario.getDireccion().getVia());
                ps.setString(3, usuario.getDireccion().getNumero());
                ps.setString(4, usuario.getDireccion().getCp());
                ps.setString(5, usuario.getDireccion().getLocalidad());
                ps.executeUpdate();
            }
            // Commit de las operaciones anteriores 
            con.commit();
        } catch (SQLException e) {
            // Si ocurre un error, se hace un rollback 
            con.rollback(); throw e;
        } finally { 
            // Restauramos el modo de auto commit
            con.setAutoCommit(true); 
        }
    }

    // Metodo para dar de baja un usuario
    public boolean baja(Usuario usuario) throws SQLException {
        // Consulta para eliminar el usuario por su DNI (la direccion se borra en cascada segun el esquema de la BD)
        String sql = "DELETE FROM usuario WHERE dni = ?";
        // Valido que el usuario no sea nulo
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        // Ejecutamos la consulta
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, usuario.getDni());
            // Devolvemos true si se ha eliminado el registro, false si no
            return ps.executeUpdate() > 0;
        }
    }

    // Metodo para buscar un usuario en la base de datos
    public Usuario buscar(Usuario usuario) throws SQLException {
        // Valido que el usuario no sea nulo
        if (usuario == null) {
            return null;
        }
        // Consulta para obtener los datos del usuario y su direccion mediante un JOIN
        String sql = "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad FROM usuario u JOIN direccion d ON u.dni = d.dni WHERE u.dni = ?";
        // Ejecutamos la consulta
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, usuario.getDni());
            try (ResultSet rs = ps.executeQuery()) {
                // Si el usuario existe, creamos el objeto Direccion y el objeto Usuario
                if (rs.next()) {
                    Direccion direccion = new Direccion(rs.getString("via"), rs.getString("numero"), rs.getString("cp"), rs.getString("localidad"));
                    return new Usuario(rs.getString("dni"), rs.getString("nombre"), rs.getString("email"), direccion);
                }
            }
        }
        return null;
    }

    // Metodo para obtener todos los usuarios registrados
    public List<Usuario> todos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        // Consulta para obtener todos los usuarios ordenados por nombre
        String sql = "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad FROM usuario u JOIN direccion d ON u.dni = d.dni ORDER BY u.nombre";
        // Ejecutamos la consulta y recorremos los resultados
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Direccion direccion = new Direccion(rs.getString("via"), rs.getString("numero"), rs.getString("cp"), rs.getString("localidad"));
                // Construimos el objeto Usuario y lo añadimos a la lista
                lista.add(new Usuario(rs.getString("dni"), rs.getString("nombre"), rs.getString("email"), direccion));
            }
        }
        // Devolvemos la lista de usuarios encontrados
        return lista;
    }
}