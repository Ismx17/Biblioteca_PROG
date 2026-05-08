package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.*;
import biblioteca.modelo.negocio.mysql.Conexion;
import biblioteca.utilidades.UtilidadesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {
    // Singleton
    private static Prestamos prestamos;
    private Prestamos() {}

    // Metodo para obtener la instancia de la clase Prestamos
    public static synchronized Prestamos getInstancia() {
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
        leerXML();
    }

    public void terminar() { 
        escribirXML();
        try {
            Conexion.getConexion().cerrarConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
        }
    }

    // Metodo para dar de alta un nuevo prestamo
    public void prestar(Libro libroFicticio, Usuario usuarioFicticio, LocalDate fInicio) {        
        Libro libro = Libros.getInstancia().buscar(libroFicticio);
        if (libro == null) {
            System.out.println("ERROR: El libro no existe.");
            return;
        }
        
        Usuario usuario = Usuarios.getInstancia().buscar(usuarioFicticio);
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
                Usuario usuario = Usuarios.getInstancia().buscar(new Usuario(rs.getString("dni"), "F", "a@a.com", new Direccion("V", "1", "11111", "L")));
                Libro libro = Libros.getInstancia().buscar(new Libro(rs.getString("isbn"), "F", 1, Categoria.OTROS));
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
                    Libro lib = Libros.getInstancia().buscar(new Libro(rs.getString("isbn"), "F", 1, biblioteca.modelo.dominio.Categoria.OTROS));
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

    public Prestamo elementToPrestamo(Element ep) {
        // Extraemos datos usando las etiquetas correctas
        String dni = getContenidoEtiqueta(ep, "usuario"); // dni -> usuario
        String isbn = getContenidoEtiqueta(ep, "libro");  // isbn -> libro
        String fInicioStr = getContenidoEtiqueta(ep, "fechaInicio"); // fInicio -> fechaInicio
    
        // Creamos los objetos de dominio con datos mínimos
        Usuario u = new Usuario(dni, "", "", new Direccion("", "", "", ""));
        Libro l = new Libro(isbn, "", 0, Categoria.OTROS);
        
        // Construimos el objeto Prestamo
        Prestamo p = new Prestamo(l, u, LocalDate.parse(fInicioStr));
        
        // Extraemos la fecha de devolucion si esta presente
        String fDevStr = getContenidoEtiqueta(ep, "fechaDevolucion"); 
        if (!fDevStr.isEmpty()) {
            p.marcarDevuelto(LocalDate.parse(fDevStr));
        }
        
        return p;
    }

    // Metodo para leer los prestamos desde un fichero XML especifico
    public void leerXML(String ruta) {
        Document doc = UtilidadesXML.xmlToDom(ruta);
        if (doc != null) {
            try {
                NodeList nodos = doc.getElementsByTagName("prestamo");
                for (int i = 0; i < nodos.getLength(); i++) {
                    Element ep = (Element) nodos.item(i);
                    try { insertar(elementToPrestamo(ep)); } catch (Exception e) { /* Ignorar si ya existe */ }
                }
            } catch (Exception e) {
                System.out.println("ERROR: Datos XML incorrectos al leer " + ruta + ": " + e.getMessage());
            }
        }
    }

    // Metodo para leer los prestamos desde el fichero XML por defecto
    public void leerXML() {
        leerXML("prestamos.xml");
    }

    // Metodo para convertir un objeto Prestamo a un elemento XML
    public Element prestamoToElement(Document dom, Prestamo p) {
        Element ep = dom.createElement("prestamo");
    
        // Etiquetas de Prestamo
        crearHijoTexto(dom, ep, "usuario", p.getUsuario().getDni()); // dni -> usuario
        crearHijoTexto(dom, ep, "libro", p.getLibro().getIsbn());   // isbn -> libro
        crearHijoTexto(dom, ep, "fechaInicio", p.getfInicio().toString()); // fInicio -> fechaInicio
    
        // Etiqueta opcional en caso de estar devuelto
        if (p.isDevuelto() && p.getfDevolucion() != null) { // Si ya ha sido devuelto creamos la etiqueta
            crearHijoTexto(dom, ep, "fechaDevolucion", p.getfDevolucion().toString()); // fDevolucion -> fechaDevolucion
        }
    
        return ep;
    }

    // Metodo para escribir los prestamos en un fichero XML especifico
    public void escribirXML(String ruta) {
        try {
            Document doc = UtilidadesXML.crearDomVacio("prestamos"); // Creamos el documento XML
            if (doc == null) return; // Si el documento es nulo, devolvemos
            
            for (Prestamo p : todos()) { // Recorremos los prestamos
                doc.getDocumentElement().appendChild(prestamoToElement(doc, p)); // Añadimos el prestamo al documento XML
            }
            
            UtilidadesXML.domToXml(doc, ruta); // Guardamos el documento XML
        } catch (Exception e) {
            System.err.println("Error al exportar " + ruta + ": " + e.getMessage());
        }
    }

    // Metodo para escribir los prestamos en el fichero XML por defecto
    public void escribirXML() {
        escribirXML("prestamos.xml");
    }

    public void borrarTodos() {
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement()) {
            st.executeUpdate("DELETE FROM prestamo");
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron borrar los préstamos: " + e.getMessage());
        }
    }

    public void insertar(Prestamo p) {
        String sql = "INSERT INTO prestamo (dni, isbn, fInicio, fLimite, devuelto, fDevolucion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, p.getUsuario().getDni());
            ps.setString(2, p.getLibro().getIsbn());
            ps.setDate(3, Date.valueOf(p.getfInicio()));
            ps.setDate(4, Date.valueOf(p.getfLimite()));
            ps.setBoolean(5, p.isDevuelto());
            ps.setDate(6, p.isDevuelto() && p.getfDevolucion() != null ? Date.valueOf(p.getfDevolucion()) : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR: No se ha podido restaurar el préstamo: " + e.getMessage());
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