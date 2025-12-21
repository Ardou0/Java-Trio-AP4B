package fr.utbm.ap4b.controller;
import fr.utbm.ap4b.view.DrawPilePage;
import fr.utbm.ap4b.view.GameMainPage;
import fr.utbm.ap4b.view.RulesPage;
import fr.utbm.ap4b.view.TrioPage;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class TrioController {

    private Scene gameScene;
    private final GameMainPage gameView;
    private RulesPage rulesView;
    private final Stage primaryStage;
    private DrawPilePage drawPileView;
    private TrioPage trioView;

    public TrioController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameView = new GameMainPage();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        gameView.getRulesButton().setOnAction(e -> openRulesPage());
        gameView.getDrawPileButton().setOnAction(e -> openDrawPilePage());
        gameView.getTrioButton().setOnAction(e -> openTrioPage());
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

    private void openDrawPilePage() {
        try{
            //Créer la vue des règles
            drawPileView = new DrawPilePage();

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(drawPileView.getRoot());
            primaryStage.setTitle("Pioche du Trio");

            // Connecter le bouton Retour
            drawPileView.getEndBtn().setOnAction(e -> {
                // Retourner à la page principale
                primaryStage.getScene().setRoot(gameView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace(); //Affiche erreurs dans la console
        }
    }

    private void openTrioPage() {
        try{
            //Créer la vue des règles
            trioView = new TrioPage();

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(trioView.getRoot());
            primaryStage.setTitle("Trios obtenus");

            // Connecter le bouton Retour
            trioView.getEndBtn().setOnAction(e -> {
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
