package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.*;
import biblioteca.modelo.negocio.mysql.Conexion;
import biblioteca.utilidades.UtilidadesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Libros {
    // Singleton
    private static Libros libros;
    private Libros() {}

    // Metodo para obtener la instancia de la clase Libros
    public static synchronized Libros getInstancia() {
        if (libros == null) libros = new Libros();
        return libros;
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

    // Metodo para dar de alta un libro
    public void alta(Libro libro) {
        // Conexion con la base de datos
        Connection con = Conexion.getConexion().getJdbcConnection();
        try {
            // Desactivamos el auto commit para poder hacer las operaciones de manera atomica
            con.setAutoCommit(false);
            // Insertamos el libro en la tabla libro
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO libro (isbn, titulo, anio, categoria) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, libro.getIsbn());
                ps.setString(2, libro.getTitulo());
                ps.setInt(3, libro.getAnio());
                ps.setString(4, libro.getCategoria().name());
                ps.executeUpdate();
            }
            // Si el libro es de tipo Audiolibro, insertamos sus datos especificos en la tabla audiolibro
            if (libro instanceof Audiolibro) {
                // Casteamos el objeto Libro a Audiolibro
                Audiolibro a = (Audiolibro) libro;
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO audiolibro (isbn, duracion_segundos, formato) VALUES (?, ?, ?)")) {
                    ps.setString(1, a.getIsbn());
                    ps.setLong(2, a.getDuracion().getSeconds());
                    ps.setString(3, a.getFormato());
                    ps.executeUpdate();
                }
            }
            // Gestionamos los autores: obtener su ID o insertarlos si no existen, y crear la relacion N:M
            for (Autor autor : libro.getAutores()) {
                int idAutor = obtenerOInsertarAutor(autor, con);
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO libro_autor (isbn, idAutor) VALUES (?, ?)")) {
                    ps.setString(1, libro.getIsbn());
                    ps.setInt(2, idAutor);
                    ps.executeUpdate();
                }
            }
            // Confirmamos todas las operaciones de la transaccion
            con.commit();
        } catch (SQLException e) {
            try {
                // Si ocurre un error, deshacemos los cambios para evitar datos inconsistentes
                con.rollback(); 
            } catch (SQLException ex) {
                System.out.println("ERROR: No se pudo deshacer la transacción: " + ex.getMessage());
            }
            System.out.println("ERROR: No se pudo dar de alta el libro: " + e.getMessage());
        } finally { 
            try {
                // Restauramos el modo de auto commit
                con.setAutoCommit(true); 
            } catch (SQLException ex) {
            }
        }
    }

    // Metodo auxiliar para obtener el ID de un autor o insertarlo si es nuevo
    private int obtenerOInsertarAutor(Autor a, Connection con) throws SQLException {
        // Consulta para verificar si el autor ya existe por sus datos unicos
        String sql = "SELECT idAutor FROM autor WHERE nombre=? AND apellidos=? AND nacionalidad=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellidos());
            ps.setString(3, a.getNacionalidad());
            ResultSet rs = ps.executeQuery();
            // Si el autor ya esta en la BD, devolvemos su ID
            if (rs.next()) return rs.getInt(1);
        }
        // Si no existe, lo insertamos 
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO autor (nombre, apellidos, nacionalidad) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellidos());
            ps.setString(3, a.getNacionalidad());
            ps.executeUpdate();
            // Recuperamos la clave generada
            ResultSet rs = ps.getGeneratedKeys();
            // Devolvemos el ID del autor
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    // Metodo para buscar un libro por su ISBN, incluyendo audiolibro
    public Libro buscar(Libro libro) {
        // Consulta que une libro con audiolibro mediante un LEFT JOIN
        String sql = "SELECT l.*, a.duracion_segundos, a.formato FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn WHERE l.isbn = ?";
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Libro encontrado;
                String isbn = rs.getString("isbn");
                String titulo = rs.getString("titulo");
                int anio = rs.getInt("anio");
                Categoria cat = Categoria.valueOf(rs.getString("categoria"));
                // Si tiene el campo formato, significa que es un audiolibro
                if (rs.getString("formato") != null) {
                    encontrado = new Audiolibro(isbn, titulo, anio, cat, Duration.ofSeconds(rs.getLong("duracion_segundos")), rs.getString("formato"));
                } else {
                    encontrado = new Libro(isbn, titulo, anio, cat);
                }
                // Cargamos la lista de autores asociada al libro desde la tabla intermedia
                cargarAutores(encontrado);
                return encontrado;
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo buscar el libro: " + e.getMessage());
        }
        return null;
    }

    // Metodo auxiliar para rellenar la lista de autores de un objeto Libro y resolver la relacion N:M
    private void cargarAutores(Libro l) throws SQLException {
        // Consulta para obtener los autores vinculados al ISBN del libro
        String sql = "SELECT a.* FROM autor a JOIN libro_autor la ON a.idAutor = la.idAutor WHERE la.isbn = ?";
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, l.getIsbn());
            ResultSet rs = ps.executeQuery();
            // Usamos while para recorrer todos los autores que pueda tener un libro
            while (rs.next()) {
                l.addAutor(new Autor(rs.getString("nombre"), rs.getString("apellidos"), rs.getString("nacionalidad")));
            }
        }
    }

    // Metodo para dar de baja un libro de la base de datos
    public boolean baja(Libro libro) {
        // Consulta para eliminar el registro 
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement("DELETE FROM libro WHERE isbn = ?")) {
            ps.setString(1, libro.getIsbn());
            // Devolvemos true si se ha eliminado el registro, false si no existia
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo dar de baja el libro: " + e.getMessage());
            return false;
        }
    }

    // Metodo para obtener el listado completo de libros
    public List<Libro> todos() {
        List<Libro> lista = new ArrayList<>();
        // Consulta para obtener todos los libros ordenados por titulo
        String sql = "SELECT l.*, a.duracion_segundos, a.formato FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn ORDER BY l.titulo";
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Libro encontrado;
                Categoria cat = Categoria.valueOf(rs.getString("categoria"));
                if (rs.getString("formato") != null) {
                    encontrado = new Audiolibro(rs.getString("isbn"), rs.getString("titulo"), rs.getInt("anio"), cat, Duration.ofSeconds(rs.getLong("duracion_segundos")), rs.getString("formato"));
                } else {
                    encontrado = new Libro(rs.getString("isbn"), rs.getString("titulo"), rs.getInt("anio"), cat);
                }
                cargarAutores(encontrado);
                lista.add(encontrado);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo obtener el listado de libros: " + e.getMessage());
        }
        return lista;
    }

    public Libro elementToLibro(Element el) {
        String isbn = getContenidoEtiqueta(el, "isbn");
        String titulo = getContenidoEtiqueta(el, "titulo");
        int anio = Integer.parseInt(getContenidoEtiqueta(el, "anio"));
        Categoria cat = Categoria.valueOf(getContenidoEtiqueta(el, "categoria"));

        Libro l;
        Element tipoElement = (Element) el.getElementsByTagName("tipo").item(0);
        if (tipoElement.getAttribute("tipo").equals("audiolibro")) {
            long duracion = Long.parseLong(tipoElement.getAttribute("duracion"));
            String formato = tipoElement.getAttribute("formato");
            l = new Audiolibro(isbn, titulo, anio, cat, Duration.ofSeconds(duracion), formato);
        } else {
            l = new Libro(isbn, titulo, anio, cat);
        }

        NodeList nlAutores = el.getElementsByTagName("autor");
        for (int j = 0; j < nlAutores.getLength(); j++) {
            Element autorElement = (Element) nlAutores.item(j);
            String nombreCompleto = getContenidoEtiqueta(autorElement, "nombre");
            String nombre;
            String apellidos;

            int primerEspacio = nombreCompleto.indexOf(" ");
            if (primerEspacio != -1) {
                nombre = nombreCompleto.substring(0, primerEspacio);
                apellidos = nombreCompleto.substring(primerEspacio + 1);
            } else {
                nombre = nombreCompleto;
                apellidos = "";
            }
            l.addAutor(new Autor(nombre, apellidos, ""));
        }
        return l;
    }

    // Metodo para leer los libros desde un fichero XML especifico
    public void leerXML(String ruta) {
        Document doc = UtilidadesXML.xmlToDom(ruta);
        if (doc != null) {
            try {
                NodeList nodos = doc.getElementsByTagName("libro");
                for (int i = 0; i < nodos.getLength(); i++) {
                    Element el = (Element) nodos.item(i);
                    Libro l = elementToLibro(el);
                    try {
                        alta(l);
                    } catch (Exception e) {
                        System.out.println("ERROR al insertar libro del XML: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: Datos XML incorrectos al leer " + ruta + ": " + e.getMessage());
            }
        }
    }

    // Metodo para convertir un objeto Libro a un elemento XML
    public Element libroToElement(Document dom, Libro l) {
        Element el = dom.createElement("libro");

        crearHijoTexto(dom, el, "isbn", l.getIsbn());
        crearHijoTexto(dom, el, "titulo", l.getTitulo());
        crearHijoTexto(dom, el, "anio", String.valueOf(l.getAnio()));
        crearHijoTexto(dom, el, "categoria", l.getCategoria().name());

        Element autores = dom.createElement("autores");
        for (Autor a : l.getAutores()) {
            Element ea = dom.createElement("autor");
            crearHijoTexto(dom, ea, "nombre", a.getNombre() + " " + a.getApellidos());
            autores.appendChild(ea);
        }
        el.appendChild(autores);

        Element tipo = dom.createElement("tipo");
        if (l instanceof Audiolibro) {
            Audiolibro al = (Audiolibro) l;
            tipo.setAttribute("tipo", "audiolibro");
            tipo.setAttribute("duracion", String.valueOf(al.getDuracion().getSeconds()));
            tipo.setAttribute("formato", al.getFormato());
        } else {
            tipo.setAttribute("tipo", "libro");
        }
        el.appendChild(tipo);

        return el;
    }

    // Metodo para escribir los libros en un fichero XML especifico
    public void escribirXML(String ruta) {
        try {
            Document doc = UtilidadesXML.crearDomVacio("libros");
            if (doc == null) return;
            for (Libro l : todos()) {
                doc.getDocumentElement().appendChild(libroToElement(doc, l));
            }
            UtilidadesXML.domToXml(doc, ruta);
        } catch (Exception e) {
            System.out.println("ERROR al guardar " + ruta + ": " + e.getMessage());
        }
    }

    public void borrarTodos() {
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement()) {
            st.executeUpdate("DELETE FROM audiolibro");
            st.executeUpdate("DELETE FROM libro_autor");
            st.executeUpdate("DELETE FROM libro");
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron borrar los libros: " + e.getMessage());
        }
    }

    // Metodos auxiliares para simplificar la creacion de nodos de texto
    // Metodo para crear un nodo de texto
    private void crearHijoTexto(Document doc, Element padre, String etiqueta, String valor) {
        Element hijo = doc.createElement(etiqueta);
        hijo.setTextContent(valor != null ? valor : ""); 
        padre.appendChild(hijo); 
    }
    // Metodo para obtener el texto de una etiqueta
    private String getContenidoEtiqueta(Element padre, String etiqueta) {
        NodeList lista = padre.getElementsByTagName(etiqueta);
        if (lista != null && lista.getLength() > 0) {
            return lista.item(0).getTextContent(); 
        }
        return "";
    }
}