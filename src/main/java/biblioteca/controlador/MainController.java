package biblioteca.controlador;

import biblioteca.modelo.Modelo;
import biblioteca.modelo.dominio.*;
import biblioteca.modelo.negocio.Autores;
import biblioteca.modelo.negocio.Libros;
import biblioteca.modelo.negocio.Prestamos;
import biblioteca.modelo.negocio.Usuarios;
import biblioteca.utilidades.UtilidadesXML;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.w3c.dom.Document;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    // Referencia a la lógica de negocio
    private Modelo modelo;

    // Listas auxiliares para gestionar la creación de libros con múltiples autores antes de guardarlos
    private ObservableList<String> nombresAutoresTemporales = FXCollections.observableArrayList();
    private List<Autor> listaAutoresObjetos = new ArrayList<>();
    
    // Tablas de la interfaz
    @FXML private TableView<Libro> tvLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colCategoria, colInfoExtra;
    @FXML private TableColumn<Libro, Integer> colAnio;

    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colDni, colNombreUsuario, colEmail;

    @FXML private TableView<Prestamo> tvPrestamos;
    @FXML private TableColumn<Prestamo, String> colPrestamoLibro, colPrestamoUsuario, colEstado;
    @FXML private TableColumn<Prestamo, LocalDate> colFInicio, colFLimite;

    // Tabla y columnas para Autores
    @FXML private TableView<Autor> tvAutores;
    @FXML private TableColumn<Autor, String> colAutorNombre, colAutorApellidos, colAutorNacionalidad;

    // Formulario de inserción de Libros
    @FXML private TextField tfIsbn, tfTitulo, tfAnio, tfDuracion, tfNombreAutor, tfApellidosAutor, tfNacionalidadAutor;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private ComboBox<String> cbFormato;
    @FXML private ComboBox<Autor> cbAutoresExistentes;
    @FXML private CheckBox chbEsAudiolibro;
    @FXML private VBox vbCamposAudiolibro; // Contenedor que se oculta/muestra
    @FXML private ListView<String> lvAutoresTemporales;

    // Formulario de inserción de Usuarios
    @FXML private TextField tfDni, tfNombreUsuario, tfEmail, tfVia, tfNumero, tfCp, tfLocalidad;

    // Formulario de gestión de Préstamos
    @FXML private ComboBox<Libro> cbLibrosPrestamo;
    @FXML private ComboBox<Usuario> cbUsuariosPrestamo;
    @FXML private DatePicker dpFechaPrestamo; // Especifico para fechas

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        refrescarTablas();
    }

    @FXML
    private void initialize() {
        configurarColumnas();

        // Carga de opciones fijas en ComboBoxes
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));
        cbFormato.setItems(FXCollections.observableArrayList("mp3", "mp4B", "AA/AAX"));

        // Vinculación de la lista de autores con el componente visual
        lvAutoresTemporales.setItems(nombresAutoresTemporales);

        // Valor por defecto para la fecha de préstamo
        dpFechaPrestamo.setValue(LocalDate.now());

        // Aplicar estilos visuales dinámicos a las filas
        configurarColoreadoFilas();

        // Configurar cómo se muestran los autores en el ComboBox
        cbAutoresExistentes.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreCompleto());
                }
            }
        });
        cbAutoresExistentes.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreCompleto());
                }
            }
        });
    }

    private void configurarColumnas() {
        // Mapeo simple usando los nombres de los atributos de la clase Libro
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

        // Mapeo personalizado para diferenciar visualmente tipo de libro
        colInfoExtra.setCellValueFactory(fila -> {
            if (fila.getValue() instanceof Audiolibro) return new SimpleStringProperty("Audiolibro");
            return new SimpleStringProperty("Físico");
        });

        // Configuración de columnas de Usuarios
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configuración de columnas de Préstamos
        colPrestamoLibro.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getLibro().getTitulo()));
        colPrestamoUsuario.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getUsuario().getNombre()));
        colFInicio.setCellValueFactory(fila ->
                new SimpleObjectProperty<>(fila.getValue().getfInicio()));
        colFLimite.setCellValueFactory(fila ->
                new SimpleObjectProperty<>(fila.getValue().getfLimite()));

        // Lógica para mostrar el estado del préstamo de forma descriptiva
        colEstado.setCellValueFactory(fila -> {
            Prestamo p = fila.getValue();
            if (p.isDevuelto()) {
                return new SimpleStringProperty("Devuelto (" + p.getfDevolucion() + ")");
            } else if (p.estaVencido()) {
                return new SimpleStringProperty("¡VENCIDO!");
            } else {
                return new SimpleStringProperty("En curso");
            }
        });

        // Configuración de columnas de Autores
        colAutorNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAutorApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colAutorNacionalidad.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));
    }

    private void configurarColoreadoFilas() {
        tvPrestamos.setRowFactory(tv -> new TableRow<Prestamo>() {
            @Override
            protected void updateItem(Prestamo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.estaVencido() && !item.isDevuelto()) {
                    setStyle("-fx-background-color: #ffcccc;"); // Rojo si está vencido
                } else if (item.isDevuelto()) {
                    setStyle("-fx-opacity: 0.7;"); // Opacidad reducida si ya se entregó
                } else {
                    setStyle("");
                }
            }
        });
    }

    @FXML
    private void handleCheckAudiolibro() {
        boolean seleccionado = chbEsAudiolibro.isSelected();
        vbCamposAudiolibro.setVisible(seleccionado);
        vbCamposAudiolibro.setManaged(seleccionado);
    }

    @FXML
    private void handleAgregarAutorALista() {
        try {
            Autor nuevoAutor = new Autor(tfNombreAutor.getText(), tfApellidosAutor.getText(), tfNacionalidadAutor.getText());
            if (!listaAutoresObjetos.contains(nuevoAutor)) {
                listaAutoresObjetos.add(nuevoAutor);
                nombresAutoresTemporales.add(nuevoAutor.getNombreCompleto());
                // Limpiar campos del autor para el siguiente
                tfNombreAutor.clear(); tfApellidosAutor.clear(); tfNacionalidadAutor.clear();
            } else {
                mostrarAdvertencia("Autor Duplicado", "Este autor ya ha sido añadido a la lista o ya existe un autor idéntico.");
            }
        } catch (Exception e) { mostrarError("Autor", e.getMessage()); }
    }

    @FXML
    private void handleAgregarAutorExistente() {
        Autor autorSeleccionado = cbAutoresExistentes.getValue();
        if (autorSeleccionado != null) {
            if (!listaAutoresObjetos.contains(autorSeleccionado)) {
                listaAutoresObjetos.add(autorSeleccionado);
                nombresAutoresTemporales.add(autorSeleccionado.getNombreCompleto());
            } else {
                mostrarAdvertencia("Autor Duplicado", "Este autor ya ha sido añadido a la lista.");
            }
        } else {
            mostrarAdvertencia("Selección Vacía", "Por favor, selecciona un autor de la lista.");
        }
    }

    @FXML
    private void handleEliminarTodosLosAutores() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("Eliminar todos los autores");
        confirm.setContentText("¿Está seguro de que desea eliminar TODOS los autores de la base de datos? Esta acción no se puede deshacer y podría fallar si hay libros asociados.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Autores.getInstancia().borrarTodos();
                refrescarTablas();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Éxito");
                alert.setContentText("Todos los autores han sido eliminados correctamente.");
                alert.showAndWait();
            } catch (Exception e) {
                mostrarError("Error al eliminar autores", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAltaLibro() {
        try {
            Libro nuevo;
            // Creamos Libro o Audiolibro según la selección
            if (chbEsAudiolibro.isSelected()) {
                nuevo = new Audiolibro(tfIsbn.getText(), tfTitulo.getText(), Integer.parseInt(tfAnio.getText()), cbCategoria.getValue(), Duration.ofSeconds(Long.parseLong(tfDuracion.getText())), cbFormato.getValue());
            } else {
                nuevo = new Libro(tfIsbn.getText(), tfTitulo.getText(), Integer.parseInt(tfAnio.getText()), cbCategoria.getValue());
            }

            // Adjuntar todos los autores añadidos previamente
            for (Autor a : listaAutoresObjetos) { nuevo.addAutor(a); }

            modelo.alta(nuevo);
            refrescarTablas();
            limpiarFormLibro();
        } catch (Exception e) { mostrarError("Error Libro", e.getMessage()); }
    }

    @FXML
    private void handleEliminarLibro() {
        Libro sel = tvLibros.getSelectionModel().getSelectedItem(); 
        if (sel != null) {
            try {
                if (modelo.baja(sel)) {
                    refrescarTablas();
                }
            } catch (IllegalStateException e) {
                mostrarAdvertencia("Restricción de Borrado", e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAltaUsuario() {
        try {
            Usuario u = new Usuario(tfDni.getText(), tfNombreUsuario.getText(), tfEmail.getText(),
                    new Direccion(tfVia.getText(), tfNumero.getText(), tfCp.getText(), tfLocalidad.getText()));
            modelo.alta(u);
            refrescarTablas();
            limpiarFormUsuario();
        } catch (Exception e) { mostrarError("Error Usuario", e.getMessage()); }
    }

    @FXML
    private void handleEliminarUsuario() {
        Usuario sel = tvUsuarios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                if (modelo.baja(sel)) {
                    refrescarTablas();
                }
            } catch (IllegalStateException e) {
                mostrarAdvertencia("Restricción de Borrado", e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleNuevoPrestamo() {
        try {
            modelo.prestar(cbLibrosPrestamo.getValue(), cbUsuariosPrestamo.getValue(), dpFechaPrestamo.getValue());
            refrescarTablas();
        } catch (Exception e) { mostrarError("Error Préstamo", e.getMessage()); }
    }

    @FXML
    private void handleDevolverPrestamo() {
        Prestamo sel = tvPrestamos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            // Se utiliza la fecha actual del sistema como fecha de devolución
            modelo.devolver(sel.getLibro(), sel.getUsuario(), LocalDate.now());
            refrescarTablas();
        }
    }

    private void refrescarTablas() {
        tvLibros.setItems(FXCollections.observableArrayList(modelo.listadoLibros()));
        tvUsuarios.setItems(FXCollections.observableArrayList(modelo.listadoUsuarios()));
        tvPrestamos.setItems(FXCollections.observableArrayList(modelo.listadoPrestamos()));
        tvAutores.setItems(FXCollections.observableArrayList(Autores.getInstancia().todos()));
        cbLibrosPrestamo.setItems(FXCollections.observableArrayList(modelo.listadoLibros()));
        cbUsuariosPrestamo.setItems(FXCollections.observableArrayList(modelo.listadoUsuarios()));
        cbAutoresExistentes.setItems(FXCollections.observableArrayList(Autores.getInstancia().todos()));
    }

    private void limpiarFormLibro() {
        tfIsbn.clear(); tfTitulo.clear(); tfAnio.clear(); tfDuracion.clear();
        listaAutoresObjetos.clear(); nombresAutoresTemporales.clear();
    }

    private void limpiarFormUsuario() {
        tfDni.clear(); tfNombreUsuario.clear(); tfEmail.clear();
        tfVia.clear(); tfNumero.clear(); tfCp.clear(); tfLocalidad.clear();
    }

    private void mostrarError(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia de Negocio");
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleHacerCopiaSeguridad() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Copia de Seguridad");
        confirm.setHeaderText("Hacer copia de seguridad");
        confirm.setContentText("Los datos se guardarán automáticamente en la carpeta fichero del proyecto. " +
                "\n¿Desea continuar?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            File ficheroDir = new File("src/main/java/biblioteca/fichero");
            if (!ficheroDir.exists()) {
                ficheroDir.mkdirs();
            }

            String path = ficheroDir.getAbsolutePath() + File.separator;
            try {
                Autores.getInstancia().escribirXML(path + "autores.xml");
                Libros.getInstancia().escribirXML(path + "libros.xml");
                Usuarios.getInstancia().escribirXML(path + "usuarios.xml");
                Prestamos.getInstancia().escribirXML(path + "prestamos.xml");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Copia de Seguridad");
                alert.setContentText("Copia de seguridad realizada correctamente en:\n" + ficheroDir.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception e) {
                mostrarError("Error en Copia de Seguridad", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCargarCopiaSeguridad() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Restauración");
        confirm.setHeaderText("Cargar copia de seguridad");
        confirm.setContentText("Se eliminarán los datos actuales y se reemplazarán por los de la copia de seguridad.\n¿Desea continuar?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Uso DirectoryChooser para poder seleccionar la carpeta de destino
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Cargar copia de seguridad (Seleccione la carpeta fichero)");
            
            // Por defecto, abrir en el directorio fichero si existe
            File directorioDefault = new File("src/main/java/biblioteca/fichero");
            if (directorioDefault.exists() && directorioDefault.isDirectory()) {
                directoryChooser.setInitialDirectory(directorioDefault);
            }

            File selectedDirectory = directoryChooser.showDialog(tvLibros.getScene().getWindow());

            if (selectedDirectory != null) {
                String path = selectedDirectory.getAbsolutePath() + File.separator;

                try {
                    // Comprobamos si los documentos existen y están bien formados
                    Document docAutores = UtilidadesXML.xmlToDom(path + "autores.xml");
                    Document docLibros = UtilidadesXML.xmlToDom(path + "libros.xml");
                    Document docUsuarios = UtilidadesXML.xmlToDom(path + "usuarios.xml");
                    Document docPrestamos = UtilidadesXML.xmlToDom(path + "prestamos.xml");

                    if (docAutores == null || docLibros == null || docUsuarios == null || docPrestamos == null) {
                        throw new Exception("Error: La carpeta seleccionada no contiene todos los archivos de la copia de seguridad");
                    }

                    java.sql.Connection con = biblioteca.modelo.negocio.mysql.Conexion.getConexion().getJdbcConnection();
                    try (java.sql.Statement st = con.createStatement()) {
                        st.execute("SET FOREIGN_KEY_CHECKS = 0");
                    }

                    // Borramos en el orden inverso
                    Prestamos.getInstancia().borrarTodos();
                    Libros.getInstancia().borrarTodos();
                    Usuarios.getInstancia().borrarTodos();
                    Autores.getInstancia().borrarTodos();

                    try (java.sql.Statement st = con.createStatement()) {
                        st.execute("SET FOREIGN_KEY_CHECKS = 1");
                    }

                    // Cargar en orden de jerarquía establecido para evitar errores de restricción
                    Autores.getInstancia().leerXML(path + "autores.xml");
                    Usuarios.getInstancia().leerXML(path + "usuarios.xml");
                    Libros.getInstancia().leerXML(path + "libros.xml");
                    Prestamos.getInstancia().leerXML(path + "prestamos.xml");

                    refrescarTablas();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Restauración");
                    alert.setContentText("Copia de seguridad cargada correctamente desde:\n" + selectedDirectory.getAbsolutePath());
                    alert.showAndWait();
                } catch (Exception e) {
                    mostrarError("Error al restaurar", e.getMessage());
                }
            }
        }
    }
}