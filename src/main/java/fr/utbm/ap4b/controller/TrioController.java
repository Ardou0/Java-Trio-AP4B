package fr.utbm.ap4b.controller;
import fr.utbm.ap4b.view.GameMainPage;
import fr.utbm.ap4b.view.RulesPage;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class TrioController {

    private Scene gameScene;
    private GameMainPage gameView;
    private RulesPage rulesView;
    private Stage primaryStage;

    public TrioController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameView = new GameMainPage();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        gameView.getRulesButton().setOnAction(e -> openRulesPage());
    }

    private void openRulesPage() {
        try{
            //Créer la vue des règles
            rulesView = new RulesPage();

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(rulesView.getRoot());
            primaryStage.setTitle("Règles du jeu du Trio");

            // Connecter le bouton Retour
            rulesView.getEndBtn().setOnAction(e -> {
                // Retourner à la page principale
                primaryStage.getScene().setRoot(gameView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace(); //Affiche erreurs dans la console
        }
    }

    public GameMainPage getView() {
        return gameView;
    }

}
