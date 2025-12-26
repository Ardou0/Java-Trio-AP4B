package fr.utbm.ap4b.controller;
import fr.utbm.ap4b.view.DrawPilePage;
import fr.utbm.ap4b.view.GameMainPage;
import fr.utbm.ap4b.view.RulesPage;
import fr.utbm.ap4b.view.TrioSoloPage;
import fr.utbm.ap4b.view.TrioTeamPage;
import fr.utbm.ap4b.view.ModeSelectionPage;
import javafx.stage.Stage;

public class TrioController {

    private final ModeSelectionPage selectionView;
    private final GameMainPage gameView;
    private final Stage primaryStage;

    public TrioController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameView = new GameMainPage(6);
        this.selectionView = new ModeSelectionPage();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        selectionView.getRulesButton().setOnAction(e -> openRulesPageFromSelection());
        selectionView.getNextButton().setOnAction(e -> openGamePage());
    }

    private void openGamePage(){
        primaryStage.getScene().setRoot(gameView.getRoot());
        primaryStage.setTitle("Jeu du Trio");

        gameView.getDrawPileButton().setOnAction(e -> openDrawPilePage());
        gameView.getRulesButton().setOnAction(e -> openRulesPageFromGame());
        gameView.getTrioButton().setOnAction(e -> openTrioSoloPage());
    }

    private void openRulesPageFromSelection() {
        try{
            //Créer la vue des règles
            RulesPage rulesView = new RulesPage();

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(rulesView.getRoot());
            primaryStage.setTitle("Règles du jeu du Trio");

            // Connecte le bouton Retour
            rulesView.getEndBtn().setOnAction(e -> {
                // Retourner à la page principale
                primaryStage.getScene().setRoot(selectionView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace(); //Affiche erreurs dans la console
        }
    }

    private void openRulesPageFromGame(){
        try{
            RulesPage rulesView = new RulesPage();

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(rulesView.getRoot());
            primaryStage.setTitle("Règles du jeu du Trio");

            // Connecte le bouton Retour
            rulesView.getEndBtn().setOnAction(e -> {
                // Retourner à la page principale
                primaryStage.getScene().setRoot(gameView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void openDrawPilePage() {
        try{
            //Créer la vue des règles
            DrawPilePage drawPileView = new DrawPilePage(9);

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(drawPileView.getRoot());
            primaryStage.setTitle("Pioche du Trio");

            // Connecte le bouton Retour
            drawPileView.getEndBtn().setOnAction(e -> {
                // Retourne à la page principale
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

            // Connecte le bouton Retour
            trioView.getEndBtn().setOnAction(e -> {
                // Retourne à la page principale
                primaryStage.getScene().setRoot(gameView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace(); //Affiche erreurs dans la console
        }
    }

    private void openTrioTeamPage() {
        try{
            //Créer la vue des trios en équipe
            TrioTeamPage trioView = new TrioTeamPage(3);

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(trioView.getRoot());
            primaryStage.setTitle("Trios obtenus");

            // Connecte le bouton Retour
            trioView.getEndBtn().setOnAction(e -> {
                // Retourne à la page principale
                primaryStage.getScene().setRoot(gameView.getRoot());
            });
        }
        catch(Exception e){
            e.printStackTrace(); //Affiche erreurs dans la console
        }
    }

    public ModeSelectionPage getView() {
        return selectionView;
    }

}
