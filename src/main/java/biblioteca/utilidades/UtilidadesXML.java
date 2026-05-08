package biblioteca.utilidades;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;

public class UtilidadesXML {

    public static Document xmlToDom(String ruta) {
        try {
            File fichero = new File(ruta);
            if (!fichero.exists()) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(fichero);
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo convertir XML a DOM: " + e.getMessage());
            return null;
        }
    }

    public static void domToXml(Document dom, String ruta) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
            DOMSource source = new DOMSource(dom);
            StreamResult result = new StreamResult(new File(ruta));
            
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.err.println("ERROR: No se pudo convertir DOM a XML: " + e.getMessage());
        }
    }

    public static Document crearDomVacio(String etiquetaRaiz) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element rootElement = document.createElement(etiquetaRaiz);
            document.appendChild(rootElement);
            
            return document;
        } catch (ParserConfigurationException e) {
            System.err.println("ERROR: No se pudo crear el DOM vacío: " + e.getMessage());
            return null;
        }
    }
}