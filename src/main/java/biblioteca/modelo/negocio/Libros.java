package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Audiolibro;
import biblioteca.modelo.dominio.Autor;
import biblioteca.modelo.dominio.Categoria;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class Libros {
    private static Libros libros;

    private Libros() {}

    public static Libros getLibros() {
        if (libros == null) {
            libros = new Libros();
        }
        return libros;
    }

    public void comenzar() {
        try {
            Conexion.getConexion().establecerConexion();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la BD: " + e.getMessage());
        }
    }

    public void terminar() {
        try {
            Conexion.getConexion().cerrarConexion();
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    public void alta(Libro libro) {
        if (libro == null) throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        Connection con = Conexion.getConexion().getJdbcConnection();
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false); // Transacción iniciada
            
            try (PreparedStatement psLibro = con.prepareStatement("INSERT INTO libro (isbn, titulo, anio, categoria) VALUES (?, ?, ?, ?)")) {
                psLibro.setString(1, libro.getIsbn());
                psLibro.setString(2, libro.getTitulo());
                psLibro.setInt(3, libro.getAnio());
                psLibro.setString(4, libro.getCategoria().name());
                psLibro.executeUpdate();
            }

            if (libro instanceof Audiolibro) {
                Audiolibro audio = (Audiolibro) libro;
                try (PreparedStatement psAudio = con.prepareStatement("INSERT INTO audiolibro (isbn, duracion, formato) VALUES (?, ?, ?)")) {
                    psAudio.setString(1, audio.getIsbn());
                    psAudio.setLong(2, audio.getDuracion().getSeconds());
                    psAudio.setString(3, audio.getFormato());
                    psAudio.executeUpdate();
                }
            }

            for (Autor autor : libro.getAutores()) {
                int autorId = -1;
                try (PreparedStatement psSel = con.prepareStatement("SELECT id FROM autor WHERE nombre = ? AND apellidos = ? AND nacionalidad = ?")) {
                    psSel.setString(1, autor.getNombre());
                    psSel.setString(2, autor.getApellidos());
                    psSel.setString(3, autor.getNacionalidad());
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) autorId = rs.getInt("id");
                    }
                }

                if (autorId == -1) {
                    try (PreparedStatement psIns = con.prepareStatement("INSERT INTO autor (nombre, apellidos, nacionalidad) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        psIns.setString(1, autor.getNombre());
                        psIns.setString(2, autor.getApellidos());
                        psIns.setString(3, autor.getNacionalidad());
                        psIns.executeUpdate();
                        try (ResultSet rsKeys = psIns.getGeneratedKeys()) {
                            if (rsKeys.next()) autorId = rsKeys.getInt(1);
                        }
                    }
                }

                try (PreparedStatement psRel = con.prepareStatement("INSERT INTO libro_autor (libro_isbn, autor_id) VALUES (?, ?)")) {
                    psRel.setString(1, libro.getIsbn());
                    psRel.setInt(2, autorId);
                    psRel.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) {}
            throw new IllegalArgumentException("ERROR: Ya existe un libro con ese ISBN o ha ocurrido un error.");
        } finally {
            try { con.setAutoCommit(autoCommitOriginal); } catch (SQLException ex) {}
        }
    }

    public boolean baja(Libro libro) {
        if (libro == null) throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        Connection con = Conexion.getConexion().getJdbcConnection();
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);
            
            try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM libro_autor WHERE libro_isbn = ?")) {
                ps1.setString(1, libro.getIsbn());
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM audiolibro WHERE isbn = ?")) {
                ps2.setString(1, libro.getIsbn());
                ps2.executeUpdate();
            }
            int filasAfectadas = 0;
            try (PreparedStatement ps3 = con.prepareStatement("DELETE FROM libro WHERE isbn = ?")) {
                ps3.setString(1, libro.getIsbn());
                filasAfectadas = ps3.executeUpdate();
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

    public Libro buscar(Libro libro) {
        if (libro == null) throw new IllegalArgumentException("ERROR: El libro no puede ser nulo.");
        Connection con = Conexion.getConexion().getJdbcConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT l.isbn, l.titulo, l.anio, l.categoria, a.duracion, a.formato FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn WHERE l.isbn = ?")) {
            ps.setString(1, libro.getIsbn());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construirLibro(rs, con);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Libro> todos() {
        List<Libro> lista = new ArrayList<>();
        Connection con = Conexion.getConexion().getJdbcConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT l.isbn, l.titulo, l.anio, l.categoria, a.duracion, a.formato FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn ORDER BY l.titulo ASC, l.isbn ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(construirLibro(rs, con));
        } catch (SQLException e) {
            System.out.println("Error al obtener libros: " + e.getMessage());
        }
        return lista;
    }

    private Libro construirLibro(ResultSet rs, Connection con) throws SQLException {
        String isbn = rs.getString("isbn");
        Categoria cat = Categoria.valueOf(rs.getString("categoria"));
        Libro encontrado = rs.getString("formato") != null ? 
            new Audiolibro(isbn, rs.getString("titulo"), rs.getInt("anio"), cat, Duration.ofSeconds(rs.getLong("duracion")), rs.getString("formato")) : 
            new Libro(isbn, rs.getString("titulo"), rs.getInt("anio"), cat);

        try (PreparedStatement psAutores = con.prepareStatement("SELECT a.nombre, a.apellidos, a.nacionalidad FROM autor a JOIN libro_autor la ON a.id = la.autor_id WHERE la.libro_isbn = ?")) {
            psAutores.setString(1, isbn);
            try (ResultSet rsAutores = psAutores.executeQuery()) {
                while (rsAutores.next()) encontrado.addAutor(new Autor(rsAutores.getString("nombre"), rsAutores.getString("apellidos"), rsAutores.getString("nacionalidad")));
            }
        }
        return encontrado;
    }
}