package fr.utbm.ap4b.controller;
import fr.utbm.ap4b.view.DrawPilePage;
import fr.utbm.ap4b.view.GameMainPage;
import fr.utbm.ap4b.view.RulesPage;
import fr.utbm.ap4b.view.TrioSoloPage;
import fr.utbm.ap4b.view.TrioTeamPage;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class TrioController {

    private Scene gameScene;
    private final GameMainPage gameView;
    private final Stage primaryStage;

    public TrioController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameView = new GameMainPage(9);
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        gameView.getRulesButton().setOnAction(e -> openRulesPage());
        gameView.getDrawPileButton().setOnAction(e -> openDrawPilePage());
        gameView.getTrioButton().setOnAction(e -> openTrioSoloPage());
    }

    private void openRulesPage() {
        try{
            //Créer la vue des règles
            RulesPage rulesView = new RulesPage();

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
            DrawPilePage drawPileView = new DrawPilePage(9);

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

    private void openTrioSoloPage() {
        try{
            //Créer la vue des trios
            TrioSoloPage trioView = new TrioSoloPage(6);

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

    private void openTrioTeamPage() {
        try{
            //Créer la vue des trios en equipe
            TrioTeamPage trioView = new TrioTeamPage(3);

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
