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
    
    // Atributo privado donde se almacena la unica instancia de la clase permitida
    private static Conexion conexion;
    private Connection jdbcConnection;

    // Constructor privado para evitar instancias fuera de la clase
    private Conexion() {}

    // Metodo para proporcionar el punto de acceso a la instancia de la clase
    public static synchronized Conexion getConexion() {
        if (conexion == null) {
            conexion = new Conexion();
        }
        return conexion;
    }

    public void establecerConexion() throws SQLException {
        try {
            // Incluyo el driver de MySQL, aun que el DriverManager lo detecta automaticamente, para aportar compatibilidad
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("ERROR: No se encontró el driver de MySQL.", e);
        }
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            jdbcConnection = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        }
        System.out.println("Conexión establecida.");
    }

    public void cerrarConexion() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
        System.out.println("Conexión cerrada.");
    }

    public Connection getJdbcConnection() {
        return jdbcConnection;
    }
}