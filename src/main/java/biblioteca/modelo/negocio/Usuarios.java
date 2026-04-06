package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class Usuarios {
    private static Usuarios usuarios;

    private Usuarios() {}

    public static Usuarios getUsuarios() {
        if (usuarios == null) {
            usuarios = new Usuarios();
        }
        return usuarios;
    }

    public void comenzar() {
        try {
            Conexion.getConexion().establecerConexion();
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión: " + e.getMessage());
        }
    }

    public void terminar() {
        try {
            Conexion.getConexion().cerrarConexion();
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public void alta(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        
        Connection con = Conexion.getConexion().getJdbcConnection();
        String insUsu = "INSERT INTO usuario (dni, nombre, email) VALUES (?, ?, ?)";
        String insDir = "INSERT INTO direccion (via, numero, cp, localidad, usuario_dni) VALUES (?, ?, ?, ?, ?)";
        
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false); // Iniciamos transacción
            
            try (PreparedStatement psUsu = con.prepareStatement(insUsu);
                 PreparedStatement psDir = con.prepareStatement(insDir)) {
                
                psUsu.setString(1, usuario.getDni());
                psUsu.setString(2, usuario.getNombre());
                psUsu.setString(3, usuario.getEmail());
                psUsu.executeUpdate();
                
                Direccion dir = usuario.getDireccion();
                psDir.setString(1, dir.getVia());
                psDir.setString(2, dir.getNumero());
                psDir.setString(3, dir.getCp());
                psDir.setString(4, dir.getLocalidad());
                psDir.setString(5, usuario.getDni());
                psDir.executeUpdate();
            }
            con.commit(); // Confirmamos los cambios de golpe
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) {} // Revertimos si falla algo a medias
            throw new IllegalArgumentException("ERROR: Ya existe un usuario con ese DNI o error en BD.");
        } finally {
            try { con.setAutoCommit(autoCommitOriginal); } catch (SQLException ex) {} // Restauramos la conexión
        }
    }

    public boolean baja(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        
        Connection con = Conexion.getConexion().getJdbcConnection();
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false); // Iniciamos transacción
            
            try (PreparedStatement psDir = con.prepareStatement("DELETE FROM direccion WHERE usuario_dni = ?")) {
                psDir.setString(1, usuario.getDni());
                psDir.executeUpdate();
            }
            
            int filasAfectadas = 0;
            try (PreparedStatement psUsu = con.prepareStatement("DELETE FROM usuario WHERE dni = ?")) {
                psUsu.setString(1, usuario.getDni());
                filasAfectadas = psUsu.executeUpdate();
            }
            
            con.commit();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { con.setAutoCommit(autoCommitOriginal); } catch (SQLException ex) {}
        }
    }

    public Usuario buscar(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        
        Connection con = Conexion.getConexion().getJdbcConnection();
        String q = "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad FROM usuario u JOIN direccion d ON u.dni = d.usuario_dni WHERE u.dni = ?";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, usuario.getDni());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Direccion dir = new Direccion(rs.getString("via"), rs.getString("numero"), rs.getString("cp"), rs.getString("localidad"));
                    return new Usuario(rs.getString("dni"), rs.getString("nombre"), rs.getString("email"), dir);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el usuario: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> todos() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection con = Conexion.getConexion().getJdbcConnection();
        String q = "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad FROM usuario u JOIN direccion d ON u.dni = d.usuario_dni ORDER BY u.nombre ASC, u.dni ASC";
        try (PreparedStatement ps = con.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Direccion dir = new Direccion(rs.getString("via"), rs.getString("numero"), rs.getString("cp"), rs.getString("localidad"));
                usuarios.add(new Usuario(rs.getString("dni"), rs.getString("nombre"), rs.getString("email"), dir));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }
}