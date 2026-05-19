package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Autor;
import biblioteca.modelo.negocio.mysql.Conexion;
import biblioteca.utilidades.UtilidadesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Autores {
    private static Autores autores;
    private Autores() {}

    public static synchronized Autores getInstancia() {
        if (autores == null) autores = new Autores();
        return autores;
    }

    // Metodos para establecer y cerrar la conexion con la base de datos
    public void comenzar() { 
        try {
            Conexion.getConexion().establecerConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo establecer la conexión: " + e.getMessage());
        }
        leerXML(); // Leemos los autores desde el fichero XML
    }

    public void terminar() { 
        escribirXML(); // Escribimos los autores nuevos en el fichero XML
        try {
            Conexion.getConexion().cerrarConexion(); 
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
        }
    }

    public List<Autor> todos() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT nombre, apellidos, nacionalidad FROM autor";
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Autor(rs.getString("nombre"), rs.getString("apellidos"), rs.getString("nacionalidad")));
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo obtener el listado de autores: " + e.getMessage());
        }
        return lista;
    }

    public void borrarTodos() {
        try (Statement st = Conexion.getConexion().getJdbcConnection().createStatement()) {
            st.executeUpdate("DELETE FROM autor");
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron borrar los autores: " + e.getMessage());
        }
    }

    public void insertar(Autor a) {
        String sql = "INSERT INTO autor (nombre, apellidos, nacionalidad) VALUES (?,?,?)";
        try (PreparedStatement ps = Conexion.getConexion().getJdbcConnection().prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellidos());
            ps.setString(3, a.getNacionalidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Se ignora si ya existe
        }
    }

    // Metodo para convertir un elemento XML a un objeto Autor
    public Autor elementToAutor(Element ea) {
        String nombre = ea.getAttribute("nombre");
        String apellidos = ea.getAttribute("apellidos");
        String nacionalidad = ea.getAttribute("nacionalidad");
        
        return new Autor(nombre, apellidos, nacionalidad);
    }

    // Metodo para leer los autores desde un fichero XML especifico
    public void leerXML(String ruta) {
        Document doc = UtilidadesXML.xmlToDom(ruta); // Cargamos el documento XML
        if (doc != null) { // Si el documento es nulo, devolvemos
            try {
                NodeList nodos = doc.getElementsByTagName("autor"); // Obtenemos los nodos del autor
                for (int i = 0; i < nodos.getLength(); i++) { // Recorremos los nodos
                    Element ea = (Element) nodos.item(i); // Obtenemos el elemento del Autor
                    insertar(elementToAutor(ea)); // Convertimos el elemento XML a un objeto Autor y lo insertamos en la BD
                }
            } catch (Exception e) {
                System.out.println("ERROR: Datos XML incorrectos al leer " + ruta + ": " + e.getMessage());
            }
        }
    }

    // Metodo para leer los autores desde el fichero XML por defecto
    public void leerXML() {
        leerXML("autores.xml");
    }

    // Metodo para convertir un objeto Autor a un elemento XML
    public Element autorToElement(Document dom, Autor a) {
        Element ea = dom.createElement("autor");
        
        ea.setAttribute("nombre", a.getNombre());
        ea.setAttribute("apellidos", a.getApellidos());
        ea.setAttribute("nacionalidad", a.getNacionalidad());
        
        return ea;
    }

    // Metodo para escribir los autores en un fichero XML especifico
    public void escribirXML(String ruta) {
        try {
            Document doc = UtilidadesXML.crearDomVacio("autores"); // Creamos el documento XML
            if (doc == null) return; // Si el documento es nulo, devolvemos
            for (Autor a : todos()) { // Recorremos los autores
                doc.getDocumentElement().appendChild(autorToElement(doc, a)); // Añadimos el autor al documento XML
            }
            UtilidadesXML.domToXml(doc, ruta); // Guardamos el documento XML
        } catch (Exception e) {
            System.out.println("ERROR al guardar " + ruta + ": " + e.getMessage());
        }
    }

    // Metodo para escribir los autores en el fichero XML por defecto
    public void escribirXML() {
        escribirXML("autores.xml");
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