package biblioteca.modelo.negocio.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String HOST = "localhost";
    private static final String ESQUEMA = "dbbiblioteca";
    private static final String USUARIO = "admin";
    private static final String CONTRASENA = "biblioteca-2026";
    private static final String URL = "jdbc:mysql://" + HOST + "/" + ESQUEMA;
    
    private static Conexion conexion;
    private Connection jdbcConnection;

    private Conexion() {}

    public static synchronized Conexion getConexion() {
        if (conexion == null) {
            conexion = new Conexion();
        }
        return conexion;
    }

    public void establecerConexion() throws SQLException {
        try {
            // Esto fuerza la carga del driver en memoria
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL", e);
        }
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            jdbcConnection = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        }
    }

    public void cerrarConexion() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }

    public Connection getJdbcConnection() {
        return jdbcConnection;
    }
}