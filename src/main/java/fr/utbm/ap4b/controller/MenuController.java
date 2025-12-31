package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.Game;
import fr.utbm.ap4b.view.ModeSelectionPage;
import fr.utbm.ap4b.view.PlayerPage;
import fr.utbm.ap4b.view.RulesPage;
import javafx.animation.FadeTransition;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuController {

    private final Stage primaryStage;
    private final ModeSelectionPage selectionView;
    
    private String teamMode = "Individuel";
    private String gameMode = "Normal";
    private int nbPlayers = 6;
    private List<String> playerNames = new ArrayList<>();

    public MenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.selectionView = new ModeSelectionPage();
        setupEventHandlers();
    }

    public void show() {
        primaryStage.getScene().setRoot(selectionView.getRoot());
        primaryStage.setTitle("Trio - Sélection du mode");
    }

    private void setupEventHandlers() {
        selectionView.getRulesButton().setOnAction(e -> openRulesPage());
        selectionView.getNextButton().setOnAction(e -> openPlayerPage());
        
        selectionView.getTeamComboBox().valueProperty().addListener((obs, oldVal, newVal) -> this.teamMode = newVal);
        selectionView.getModeComboBox().valueProperty().addListener((obs, oldVal, newVal) -> this.gameMode = newVal);
        
        selectionView.getPlayerComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                try {
                    this.nbPlayers = Integer.parseInt(newVal);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur fatale : Impossible de convertir '" + newVal + "' en entier.");
                    System.exit(1);
                }
            } else {
                System.err.println("Erreur fatale : Valeur null ou vide dans le ComboBox.");
                System.exit(2);
            }
        });
    }

    private void openPlayerPage() {
        boolean isTeamMode = !Objects.equals(teamMode, "Individuel");

        // Vérification du nombre de joueurs pour le mode équipe
        if (isTeamMode && (nbPlayers != 4 && nbPlayers != 6)) {
            showErrorMessage("Le mode équipe n'est disponible qu'avec 4 ou 6 joueurs.");
            return;
        }

        try {
            PlayerPage playerView = new PlayerPage(nbPlayers);
            primaryStage.getScene().setRoot(playerView.getRoot());
            primaryStage.setTitle("Trio - Nom des joueurs");

            playerView.getPreviousButton().setOnAction(e -> 
                primaryStage.getScene().setRoot(selectionView.getRoot())
            );

            playerView.getNextButton().setOnAction(e -> {
                if (retrievePlayerNames(playerView)) {
                    startGame();
                }
            });

            playerView.getExampleCheck().setOnAction(event -> {
                boolean isSelected = playerView.getExampleCheck().isSelected();
                playerView.getExampleLabel().setVisible(isSelected);
                playerView.getExampleLabel().setOpacity(isSelected ? 0 : 1);

                if (isSelected) {
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), playerView.getExampleLabel());
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean retrievePlayerNames(PlayerPage playerView) {
        try {
            playerNames = playerView.getPlayerNames();
            for (int i = 0; i < playerNames.size(); i++) {
                String name = playerNames.get(i);
                if (name == null || name.trim().isEmpty()) {
                    showErrorMessage("Veuillez entrer un nom pour le Joueur " + (i + 1));
                    return false;
                }
                playerNames.set(i, name.trim());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de la récupération des noms des joueurs");
            return false;
        }
    }

    private void startGame() {
        boolean isTeamMode = !Objects.equals(teamMode, "Individuel");
        boolean isPiquant = !Objects.equals(gameMode, "Normal");
        
        Game gameModel = new Game(playerNames, 0, isTeamMode, isPiquant);
        
        // Passer le relais au GameController
        GameController gameController = new GameController(primaryStage, gameModel);
        gameController.startGame();
    }

    private void openRulesPage() {
        try {
            RulesPage rulesView = new RulesPage();
            primaryStage.getScene().setRoot(rulesView.getRoot());
            primaryStage.setTitle("Règles du jeu du Trio");
            rulesView.getEndBtn().setOnAction(e -> 
                primaryStage.getScene().setRoot(selectionView.getRoot())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public ModeSelectionPage getView() {
        return selectionView;
    }
}
