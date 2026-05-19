package biblioteca;

import biblioteca.controlador.MainController;
import biblioteca.modelo.Modelo;
import biblioteca.vista.LocalizadorRecursos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class AppBiblioteca extends Application implements LocalizadorRecursos {

    @Override
    public void start(Stage stage) throws Exception {
        // Instanciar el modelo
        Modelo modelo = new Modelo();
        modelo.comenzar(); // Iniciar conexiones MySQL

        // Cargar el FXML
        FXMLLoader loader = new FXMLLoader(getFxmlResource("MainView.fxml"));
        Scene scene = new Scene(loader.load());

        // Conectar el controlador con el modelo
        MainController controller = loader.getController();
        controller.setModelo(modelo);

        // Configurar ventana
        stage.setTitle("Gestión Biblioteca IES Al-Ándalus");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> modelo.terminar()); // Cierra conexiones al salir
        stage.show();
    }

    @Override
    public URL getFxmlResource(String fxmlFileName) {
        return getClass().getResource("/biblioteca/views/" + fxmlFileName);
    }

    public static void main(String[] args) {
        launch(args);
    }
}