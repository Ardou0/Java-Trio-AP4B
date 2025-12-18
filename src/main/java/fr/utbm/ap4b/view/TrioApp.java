package fr.utbm.ap4b.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrioApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        //Initialisation de la vue
        TrioView view = new TrioView();
        //Rules view = new  Rules();

        //Initialisation de la fenêtre
        Scene scene = new Scene(view.getRoot(), 1200,800);

        primaryStage.setTitle("Jeu du Trio"); //Nom de la page
        primaryStage.setScene(scene); //Creation de la scene
        primaryStage.setMinWidth(600); //Tille minimum

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
