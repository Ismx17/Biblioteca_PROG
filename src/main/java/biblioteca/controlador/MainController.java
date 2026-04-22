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

/**
 * Clase controladora de la interfaz gráfica.
 * Actúa como intermediario entre la Vista y el Modelo.
 */
public class MainController {

    // Referencia a la lógica de negocio
    private Modelo modelo;

    // Listas auxiliares para gestionar la creación de libros con múltiples autores antes de guardarlos
    private ObservableList<String> nombresAutoresTemporales = FXCollections.observableArrayList();
    private List<Autor> listaAutoresObjetos = new ArrayList<>();

    // ELEMENTOS FXML: Componentes inyectados desde el archivo principal.fxml

    // Tablas de la interfaz
    @FXML private TableView<Libro> tvLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colCategoria, colInfoExtra;
    @FXML private TableColumn<Libro, Integer> colAnio;

    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colDni, colNombreUsuario, colEmail;

    @FXML private TableView<Prestamo> tvPrestamos;
    @FXML private TableColumn<Prestamo, String> colPrestamoLibro, colPrestamoUsuario, colEstado;
    @FXML private TableColumn<Prestamo, LocalDate> colFInicio, colFLimite;

    // Formulario de inserción de Libros
    @FXML private TextField tfIsbn, tfTitulo, tfAnio, tfDuracion, tfNombreAutor, tfApellidosAutor, tfNacionalidadAutor;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private ComboBox<String> cbFormato;
    @FXML private CheckBox chbEsAudiolibro;
    @FXML private VBox vbCamposAudiolibro; // Contenedor que se oculta/muestra
    @FXML private ListView<String> lvAutoresTemporales;

    // Formulario de inserción de Usuarios
    @FXML private TextField tfDni, tfNombreUsuario, tfEmail, tfVia, tfNumero, tfCp, tfLocalidad;

    // Formulario de gestión de Préstamos
    @FXML private ComboBox<Libro> cbLibrosPrestamo;
    @FXML private ComboBox<Usuario> cbUsuariosPrestamo;
    @FXML private DatePicker dpFechaPrestamo;

    /**
     * Método para establecer el modelo y cargar los datos iniciales de la BD.
     */
    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        refrescarTablas();
    }

    /**
     * Método automático de JavaFX que se ejecuta al cargar el FXML.
     */
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
    }

    /**
     * Define qué atributo de cada objeto se mostrará en cada columna de las tablas.
     */
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
    }

    /**
     * Cambia el color de fondo de las filas de préstamos según su estado.
     */
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

    /**
     * Muestra u oculta los campos específicos de Audiolibro según el CheckBox.
     */
    @FXML
    private void handleCheckAudiolibro() {
        boolean seleccionado = chbEsAudiolibro.isSelected();
        vbCamposAudiolibro.setVisible(seleccionado);
        vbCamposAudiolibro.setManaged(seleccionado); // Ajusta el espacio en el layout
    }

    /**
     * Añade un autor a la lista temporal antes de crear el libro definitivo.
     */
    @FXML
    private void handleAgregarAutorALista() {
        try {
            Autor nuevoAutor = new Autor(tfNombreAutor.getText(), tfApellidosAutor.getText(), tfNacionalidadAutor.getText());
            listaAutoresObjetos.add(nuevoAutor);
            nombresAutoresTemporales.add(nuevoAutor.getNombreCompleto());
            // Limpiar campos del autor para el siguiente
            tfNombreAutor.clear(); tfApellidosAutor.clear(); tfNacionalidadAutor.clear();
        } catch (Exception e) { mostrarError("Autor", e.getMessage()); }
    }

    /**
     * Recoge los datos del formulario y solicita al modelo el alta de un nuevo libro.
     */
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
                // Manejo de excepción de negocio
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

    /**
     * Crea un nuevo registro de préstamo llamando al modelo.
     */
    @FXML
    private void handleNuevoPrestamo() {
        try {
            modelo.prestar(cbLibrosPrestamo.getValue(), cbUsuariosPrestamo.getValue(), dpFechaPrestamo.getValue());
            refrescarTablas();
        } catch (Exception e) { mostrarError("Error Préstamo", e.getMessage()); }
    }

    /**
     * Registra la devolución de un libro seleccionado en la tabla.
     */
    @FXML
    private void handleDevolverPrestamo() {
        Prestamo sel = tvPrestamos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            // Se utiliza la fecha actual del sistema como fecha de devolución
            modelo.devolver(sel.getLibro(), sel.getUsuario(), LocalDate.now());
            refrescarTablas();
        }
    }

    /**
     * Actualiza el contenido de todas las tablas y desplegables con los datos actuales del Modelo.
     */
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

    /**
     * Muestra un cuadro de diálogo de error crítico.
     */
    private void mostrarError(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un aviso sobre reglas de negocio que no se han podido cumplir.
     */
    private void mostrarAdvertencia(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia de Negocio");
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}