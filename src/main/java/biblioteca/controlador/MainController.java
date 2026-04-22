package biblioteca.controlador;

import biblioteca.modelo.Modelo;
import biblioteca.modelo.dominio.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private Modelo modelo;
    private ObservableList<String> nombresAutoresTemporales = FXCollections.observableArrayList();
    private List<Autor> listaAutoresObjetos = new ArrayList<>();

    // --- ELEMENTOS FXML: TABLAS ---
    @FXML private TableView<Libro> tvLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colCategoria, colInfoExtra;
    @FXML private TableColumn<Libro, Integer> colAnio;

    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colDni, colNombreUsuario, colEmail;

    @FXML private TableView<Prestamo> tvPrestamos;
    @FXML private TableColumn<Prestamo, String> colPrestamoLibro, colPrestamoUsuario, colEstado;
    @FXML private TableColumn<Prestamo, LocalDate> colFInicio, colFLimite;

    // --- ELEMENTOS FXML: FORMULARIO LIBROS ---
    @FXML private TextField tfIsbn, tfTitulo, tfAnio, tfDuracion, tfNombreAutor, tfApellidosAutor, tfNacionalidadAutor;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private ComboBox<String> cbFormato;
    @FXML private CheckBox chbEsAudiolibro;
    @FXML private VBox vbCamposAudiolibro;
    @FXML private ListView<String> lvAutoresTemporales;

    // --- ELEMENTOS FXML: FORMULARIO USUARIOS ---
    @FXML private TextField tfDni, tfNombreUsuario, tfEmail, tfVia, tfNumero, tfCp, tfLocalidad;

    // --- ELEMENTOS FXML: FORMULARIO PRÉSTAMOS ---
    @FXML private ComboBox<Libro> cbLibrosPrestamo;
    @FXML private ComboBox<Usuario> cbUsuariosPrestamo;
    @FXML private DatePicker dpFechaPrestamo;

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        refrescarTablas();
    }

    @FXML
    private void initialize() {
        configurarColumnas();

        // Inicializar ComboBoxes
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));
        cbFormato.setItems(FXCollections.observableArrayList("mp3", "mp4B", "AA/AAX"));
        lvAutoresTemporales.setItems(nombresAutoresTemporales);
        dpFechaPrestamo.setValue(LocalDate.now());

        configurarColoreadoFilas();
    }

    private void configurarColumnas() {
        // Libros
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colInfoExtra.setCellValueFactory(fila -> {
            if (fila.getValue() instanceof Audiolibro) return new SimpleStringProperty("Audiolibro");
            return new SimpleStringProperty("Físico");
        });

        // Usuarios
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Préstamos
        colPrestamoLibro.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getLibro().getTitulo()));
        colPrestamoUsuario.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getUsuario().getNombre()));
        colFInicio.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getfInicio()));
        colFLimite.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getfLimite()));

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
    }

    private void configurarColoreadoFilas() {
        tvPrestamos.setRowFactory(tv -> new TableRow<Prestamo>() {
            @Override
            protected void updateItem(Prestamo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.estaVencido() && !item.isDevuelto()) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else if (item.isDevuelto()) {
                    setStyle("-fx-opacity: 0.7;");
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
            listaAutoresObjetos.add(nuevoAutor);
            nombresAutoresTemporales.add(nuevoAutor.getNombreCompleto());
            tfNombreAutor.clear(); tfApellidosAutor.clear(); tfNacionalidadAutor.clear();
        } catch (Exception e) { mostrarError("Autor", e.getMessage()); }
    }

    @FXML
    private void handleAltaLibro() {
        try {
            Libro nuevo;
            if (chbEsAudiolibro.isSelected()) {
                nuevo = new Audiolibro(tfIsbn.getText(), tfTitulo.getText(), Integer.parseInt(tfAnio.getText()), cbCategoria.getValue(), Duration.ofSeconds(Long.parseLong(tfDuracion.getText())), cbFormato.getValue());
            } else {
                nuevo = new Libro(tfIsbn.getText(), tfTitulo.getText(), Integer.parseInt(tfAnio.getText()), cbCategoria.getValue());
            }
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
                // Captura la restricción de negocio: el libro tiene préstamos activos
                mostrarAdvertencia("Restricción de Borrado", e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAltaUsuario() {
        try {
            Usuario u = new Usuario(tfDni.getText(), tfNombreUsuario.getText(), tfEmail.getText(), new Direccion(tfVia.getText(), tfNumero.getText(), tfCp.getText(), tfLocalidad.getText()));
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
                // Captura la restricción de negocio: el usuario tiene historial de préstamos
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
            modelo.devolver(sel.getLibro(), sel.getUsuario(), LocalDate.now());
            refrescarTablas();
        }
    }

    private void refrescarTablas() {
        tvLibros.setItems(FXCollections.observableArrayList(modelo.listadoLibros()));
        tvUsuarios.setItems(FXCollections.observableArrayList(modelo.listadoUsuarios()));
        tvPrestamos.setItems(FXCollections.observableArrayList(modelo.listadoPrestamos()));
        cbLibrosPrestamo.setItems(FXCollections.observableArrayList(modelo.listadoLibros()));
        cbUsuariosPrestamo.setItems(FXCollections.observableArrayList(modelo.listadoUsuarios()));
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

    // Nueva ventana de advertencia para reglas de negocio
    private void mostrarAdvertencia(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia de Negocio");
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}