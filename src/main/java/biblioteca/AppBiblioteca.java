package biblioteca;

import biblioteca.controlador.MainController;
import biblioteca.modelo.Modelo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppBiblioteca extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Instanciar el modelo
        Modelo modelo = new Modelo();
        modelo.comenzar(); // Inicia conexiones MySQL

        // Cargar el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/vista/principal.fxml"));
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

    public static void main(String[] args) {
        launch(args);
    }
}