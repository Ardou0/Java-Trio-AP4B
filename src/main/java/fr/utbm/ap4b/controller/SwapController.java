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

/**
 * Contrôleur dédié à la gestion de la phase d'échange de cartes (Swap Phase).
 * Cette phase survient en mode équipe au début de la partie ou après un trio.
 * Le contrôleur gère la séquence d'échanges pour chaque équipe concernée.
 */
public class SwapController {

    private final Stage primaryStage;
    private final Game gameModel;
    private final Runnable onSwapPhaseFinished; // Callback pour revenir au jeu principal

    /**
     * Initialise le contrôleur d'échange.
     * @param primaryStage La fenêtre principale.
     * @param gameModel Le modèle de jeu.
     * @param onSwapPhaseFinished Action à exécuter une fois que tous les échanges sont terminés.
     */
    public SwapController(Stage primaryStage, Game gameModel, Runnable onSwapPhaseFinished) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
        this.onSwapPhaseFinished = onSwapPhaseFinished;
    }

    /**
     * Lance ou continue la phase d'échange.
     * Cette méthode est récursive : elle identifie le prochain joueur devant échanger,
     * affiche l'interface pour lui, et se rappelle elle-même une fois l'échange fait.
     */
    public void startSwapPhase() {
        // On récupère uniquement ceux qui n'ont PAS encore échangé
        Set<Actor> pendingPlayers = gameModel.getPlayersWhoHaveNotSwapped();

        if (pendingPlayers.isEmpty()) {
            // Fin de la phase d'échange, on appelle le callback pour retourner au jeu
            if (onSwapPhaseFinished != null) {
                onSwapPhaseFinished.run();
            }
            return;
        }

        // Trier pour avoir un ordre déterministe (par index de joueur)
        List<Actor> sortedPlayers = new ArrayList<>(pendingPlayers);
        sortedPlayers.sort(Comparator.comparingInt(Actor::getPlayerIndex));

        // On prend le premier joueur disponible
        Actor player = sortedPlayers.get(0);
        if (!(player instanceof JoueurEquipe)) return;

        JoueurEquipe teamPlayer = (JoueurEquipe) player;
        JoueurEquipe teammate = teamPlayer.getTeammate();

        // Affichage de la vue pour le premier joueur de l'équipe (sélection de sa carte à donner)
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

    /**
     * Gère la seconde partie de l'échange : le coéquipier choisit sa carte.
     * @param player1 Le joueur qui a initié l'échange.
     * @param player2 Le coéquipier qui doit répondre.
     * @param cardFromP1 La carte sélectionnée par le premier joueur.
     */
    private void handleTeammateSelection(JoueurEquipe player1, JoueurEquipe player2, Card cardFromP1) {
        ExchangePage exchangeView = new ExchangePage(
                player2.getName(),
                player1.getName(),
                player2.getHand().getCards()
        );

        exchangeView.setOnCardSelected(cardFromP2 -> {
            // Exécuter l'échange dans le modèle
            boolean success = gameModel.exchangeCards(player1.getPlayerIndex(), cardFromP1, cardFromP2);

            if (success) {
                // Passer à l'équipe suivante (appel récursif)
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
