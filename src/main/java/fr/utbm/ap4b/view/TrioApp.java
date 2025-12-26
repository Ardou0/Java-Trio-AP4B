package fr.utbm.ap4b.view;

import fr.utbm.ap4b.controller.TrioController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class TrioApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        //Initialisation de la vue
        TrioController controller = new TrioController(primaryStage);

        //Recupere la vue du controlleur
        BorderPane gameView = controller.getView().getRoot();
        //ModeSelectionPage modeSelection = new ModeSelectionPage();
        //BorderPane gameView = modeSelection.getRoot();

        //Initialisation de la fenêtre
        Scene scene = new Scene(gameView, 1200,800);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        primaryStage.setTitle("Jeu du Trio"); //Nom de la page
        primaryStage.setScene(scene); //Creation de la scene
        primaryStage.setMinWidth(600); //Tille minimum

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
