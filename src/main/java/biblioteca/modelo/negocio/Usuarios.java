package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;
import biblioteca.utilidades.UtilidadesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    // Singleton
    private static Usuarios usuarios;
    private Usuarios() {}

    // Metodo para obtener la instancia de la clase Usuarios
    public static synchronized Usuarios getInstancia() {
        if (usuarios == null) {
            usuarios = new Usuarios();
        }
        return usuarios;
    }

    // Metodos para establecer y cerrar la conexion con la base de datos
    public void comenzar() { 
        try {
            Conexion.getConexion().establecerConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo establecer la conexión: " + e.getMessage());
        }
        leerXML(); // Leemos los usuarios desde el fichero XML
    }
    public void terminar() { 
        escribirXML(); // Escribimos los usuarios nuevos en el fichero XML
        try {
            Conexion.getConexion().cerrarConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
        }
    }

    // Metodo para dar de alta un usuario y su direccion
    public void alta(Usuario usuario) {
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
            try {
                // Si ocurre un error, se hace un rollback 
                con.rollback(); 
            } catch (SQLException ex) {
            }
            System.out.println("ERROR: No se pudo dar de alta el usuario: " + e.getMessage());
        } finally { 
            try {
                // Restauramos el modo de auto commit
                con.setAutoCommit(true); 
            } catch (SQLException ex) {
            }
        }
    }

    // Metodo para dar de baja un usuario
    public boolean baja(Usuario usuario) {
        // Consulta para eliminar el usuario por su DNI (la direccion se borra en cascada segun el esquema de la BD)
        String sql = "DELETE FROM usuario WHERE dni = ?";
        // Valido que el usuario no sea nulo
        if (usuario == null) throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        // Ejecutamos la consulta
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, usuario.getDni());
            // Devolvemos true si se ha eliminado el registro, false si no
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo dar de baja el usuario: " + e.getMessage());
            return false;
        }
    }

    // Metodo para buscar un usuario en la base de datos
    public Usuario buscar(Usuario usuario) {
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
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo buscar el usuario: " + e.getMessage());
        }
        return null;
    }

    // Metodo para obtener todos los usuarios registrados
    public List<Usuario> todos() {
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
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo obtener el listado de usuarios: " + e.getMessage());
        }
        // Devolvemos la lista de usuarios encontrados
        return lista;
    }

    // Metodo para convertir un elemento XML a un objeto Usuario
    public Usuario elementToUsuario(Element elemento) {
        String dni = elemento.getAttribute("dni");
        String nombre = getContenidoEtiqueta(elemento, "nombre");
        String email = getContenidoEtiqueta(elemento, "email");
        
        Element ed = (Element) elemento.getElementsByTagName("direccion").item(0);
        Direccion dir = new Direccion(
            ed.getAttribute("via"),
            ed.getAttribute("numero"),
            ed.getAttribute("cp"),
            ed.getAttribute("localidad")
        );
        
        return new Usuario(dni, nombre, email, dir);
    }

    // Metodo para leer los usuarios desde un fichero XML especifico
    public void leerXML(String ruta) {
        Document doc = UtilidadesXML.xmlToDom(ruta); // Cargamos el documento XML
        if (doc != null) {
            try {
                NodeList nodos = doc.getElementsByTagName("usuario"); // Obtenemos los nodos del usuario
                for (int i = 0; i < nodos.getLength(); i++) { // Recorremos los nodos
                    Element eu = (Element) nodos.item(i); // Obtenemos el elemento del usuario
                    Usuario u = elementToUsuario(eu); // Convertimos el elemento XML a un objeto Usuario
                    try { alta(u); } catch (Exception e) { /* Ignorar si ya existe */ } // Si ya existe, lo ignoramos
                }
            } catch (Exception e) {
                System.out.println("ERROR: Datos XML incorrectos al leer " + ruta + ": " + e.getMessage());
            }
        }
    }

    // Metodo para leer los usuarios desde el fichero XML por defecto
    public void leerXML() {
        leerXML("usuarios.xml");
    }

    // Metodo para convertir un objeto Usuario a un elemento XML
    public Element usuarioToElement(Document dom, Usuario usuario) {
        Element eu = dom.createElement("usuario");
        eu.setAttribute("dni", usuario.getDni());
        
        crearHijoTexto(dom, eu, "nombre", usuario.getNombre());
        crearHijoTexto(dom, eu, "email", usuario.getEmail());
        
        Element ed = dom.createElement("direccion");
        Direccion dir = usuario.getDireccion();
        ed.setAttribute("via", dir.getVia());
        ed.setAttribute("numero", dir.getNumero());
        ed.setAttribute("cp", dir.getCp());
        ed.setAttribute("localidad", dir.getLocalidad());
        
        eu.appendChild(ed);
        return eu;
    }

    // Metodo para escribir los usuarios en un fichero XML especifico
    public void escribirXML(String ruta) {
        try {
            Document doc = UtilidadesXML.crearDomVacio("usuarios"); // Creamos el documento XML
            if (doc == null) return; // Si el documento es nulo, devolvemos
            for (Usuario u : todos()) { // Recorremos los usuarios
                doc.getDocumentElement().appendChild(usuarioToElement(doc, u)); // Añadimos el usuario al documento XML
            }
            UtilidadesXML.domToXml(doc, ruta); // Guardamos el documento XML
        } catch (Exception e) {
            System.out.println("ERROR al guardar " + ruta + ": " + e.getMessage());
        }
    }

    // Metodo para escribir los usuarios en el fichero XML por defecto
    public void escribirXML() {
        escribirXML("usuarios.xml");
    }

    public void borrarTodos() {
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement()) {
            st.executeUpdate("DELETE FROM direccion");
            st.executeUpdate("DELETE FROM usuario");
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron borrar los usuarios: " + e.getMessage());
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