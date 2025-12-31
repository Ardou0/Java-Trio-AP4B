package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.Actor;
import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.Game;
import fr.utbm.ap4b.model.JoueurEquipe;
import fr.utbm.ap4b.view.ExchangePage;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SwapController {

    private final Stage primaryStage;
    private final Game gameModel;
    private final Runnable onSwapPhaseFinished; // Callback pour revenir au jeu

    public SwapController(Stage primaryStage, Game gameModel, Runnable onSwapPhaseFinished) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
        this.onSwapPhaseFinished = onSwapPhaseFinished;
    }

    public void startSwapPhase() {
        // On récupère uniquement ceux qui n'ont PAS encore échangé
        Set<Actor> pendingPlayers = gameModel.getPlayersWhoHaveNotSwapped();

        if (pendingPlayers.isEmpty()) {
            // Fin de la phase d'échange, on appelle le callback
            if (onSwapPhaseFinished != null) {
                onSwapPhaseFinished.run();
            }
            return;
        }

        // Trier pour avoir un ordre déterministe
        List<Actor> sortedPlayers = new ArrayList<>(pendingPlayers);
        sortedPlayers.sort(Comparator.comparingInt(Actor::getPlayerIndex));

        // On prend le premier joueur disponible
        Actor player = sortedPlayers.get(0);
        if (!(player instanceof JoueurEquipe)) return;

        JoueurEquipe teamPlayer = (JoueurEquipe) player;
        JoueurEquipe teammate = teamPlayer.getTeammate();

        ExchangePage exchangeView = new ExchangePage(
                teamPlayer.getName(),
                teammate.getName(),
                teamPlayer.getHand().getCards()
        );

        exchangeView.setOnCardSelected(card -> {
            handleTeammateSelection(teamPlayer, teammate, card);
        });

        primaryStage.getScene().setRoot(exchangeView.getRoot());
        primaryStage.setTitle("Échange - " + teamPlayer.getName());
    }

    private void handleTeammateSelection(JoueurEquipe player1, JoueurEquipe player2, Card cardFromP1) {
        ExchangePage exchangeView = new ExchangePage(
                player2.getName(),
                player1.getName(),
                player2.getHand().getCards()
        );

        exchangeView.setOnCardSelected(cardFromP2 -> {
            // Exécuter l'échange
            boolean success = gameModel.exchangeCards(player1.getPlayerIndex(), cardFromP1, cardFromP2);

            if (success) {
                // Passer à l'équipe suivante (récursion)
                startSwapPhase();
            } else {
                showErrorMessage("Erreur lors de l'échange !");
                startSwapPhase(); // Retry
            }
        });

        primaryStage.getScene().setRoot(exchangeView.getRoot());
        primaryStage.setTitle("Échange - " + player2.getName());
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
