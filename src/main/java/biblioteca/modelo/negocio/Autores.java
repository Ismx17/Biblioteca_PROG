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
        leerXML("src/main/java/biblioteca/fichero/autores.xml");
    }

    public void terminar() {
        escribirXML("src/main/java/biblioteca/fichero/autores.xml");
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
        String nombreCompleto = getContenidoEtiqueta(ea, "nombre"); // Obtener el contenido de la etiqueta <nombre>
        String nombre;
        String apellidos;

        int primerEspacio = nombreCompleto.indexOf("");
        if (primerEspacio != -1) {
            nombre = nombreCompleto.substring(0, primerEspacio);
            apellidos = nombreCompleto.substring(primerEspacio + 1);
        } else {
            nombre = nombreCompleto;
            apellidos = "";
        }
        return new Autor(nombre, apellidos, "");
    }

    // Metodo para leer los autores desde un fichero XML especifico
    public void leerXML(String ruta) {
        Document doc = UtilidadesXML.xmlToDom(ruta);
        if (doc != null) {
            try {
                NodeList nodos = doc.getElementsByTagName("autor");
                for (int i = 0; i < nodos.getLength(); i++) {
                    Element ea = (Element) nodos.item(i);
                    Autor a = elementToAutor(ea);
                    try {
                        insertar(a);
                    } catch (Exception e) {
                        System.out.println("ERROR al insertar autor del XML: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: Datos XML incorrectos al leer " + ruta + ": " + e.getMessage());
            }
        }
    }

    // Metodo para convertir un objeto Autor a un elemento XML
    public Element autorToElement(Document dom, Autor a) {
        Element ea = dom.createElement("autor");
        crearHijoTexto(dom, ea, "nombre", a.getNombre() + " " + a.getApellidos());
        return ea;
    }

    // Metodo para escribir los autores en un fichero XML especifico
    public void escribirXML(String ruta) {
        try {
            Document doc = UtilidadesXML.crearDomVacio("autores");
            if (doc == null) return;
            for (Autor a : todos()) {
                doc.getDocumentElement().appendChild(autorToElement(doc, a));
            }
            UtilidadesXML.domToXml(doc, ruta);
        } catch (Exception e) {
            System.out.println("ERROR al guardar " + ruta + ": " + e.getMessage());
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