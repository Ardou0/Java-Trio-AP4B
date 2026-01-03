package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.*;
import fr.utbm.ap4b.view.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.*;

/**
 * Contrôleur principal du jeu.
 * Il fait le lien entre le modèle (Game) et la vue principale (GameMainPage).
 * Il gère les interactions utilisateur (clics boutons), met à jour l'affichage
 * et orchestre le déroulement des tours.
 */
public class GameController {

    private final Stage primaryStage;
    private final Game gameModel;
    private GameMainPage gameView;
    
    // État du jeu local au contrôleur
    private int turnCounter = 0;
    private Integer selectedPlayer = null;

    /**
     * Constructeur du contrôleur de jeu.
     * @param primaryStage La fenêtre principale de l'application.
     * @param gameModel L'instance du jeu déjà configurée.
     */
    public GameController(Stage primaryStage, Game gameModel) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
    }

    /**
     * Lance la boucle de jeu.
     * Détermine si le jeu doit commencer par une phase d'échange ou directement par le jeu,
     * et initialise l'interface en conséquence.
     */
    public void startGame() {
        gameModel.startGame();
        turnCounter = 0;

        String modeMessage = gameModel.isTeamMode() ? "Mode Équipe" : "Mode Solo";
        
        if (gameModel.getCurrentPhase() == Game.GamePhase.INITIAL_SWAP) {
            startSwapPhase();
        } else {
            openGamePage();
            gameView.showOverlayMessage("Début de la partie - " + modeMessage, 2000);
        }
    }

    /**
     * Initialise et lance la phase d'échange de cartes (pour le mode équipe).
     * Utilise un sous-contrôleur dédié (SwapController).
     */
    private void startSwapPhase() {
        // Déléguer au SwapController pour ne pas surcharger cette classe
        SwapController swapController = new SwapController(primaryStage, gameModel, () -> {
            // Callback exécuté quand l'échange est fini : on revient au jeu principal
            openGamePage();
            gameView.showOverlayMessage("Échanges terminés ! À vous de jouer.", 2000);
        });
        swapController.startSwapPhase();
    }

    /**
     * Configure et affiche la vue principale du jeu (le plateau).
     * Connecte tous les écouteurs d'événements (boutons, cartes) aux méthodes du contrôleur.
     */
    private void openGamePage() {
        // Stocker l'ID du joueur pour lequel la vue est créée
        int initialPlayerId = gameModel.getCurrentPlayer().getPlayerIndex() + 1;
        int nbPlayers = gameModel.getPlayers().size();

        gameView = new GameMainPage(nbPlayers, initialPlayerId);
        
        // Désactiver la pioche si mode équipe (règle spécifique ?)
        if (gameModel.isTeamMode()) {
            gameView.disableDrawPileForTeamMode();
        }
        
        primaryStage.getScene().setRoot(gameView.getRoot());
        primaryStage.setTitle("Jeu du Trio");

        // Configuration des événements globaux
        gameView.getDrawPileButton().setOnAction(e -> {
            hidePlayerHand(); // Cache les cartes si on fait une autre action pour éviter la triche visuelle
            openDrawPilePage();
        });
        gameView.getRulesButton().setOnAction(e -> {
            hidePlayerHand();
            openRulesPageFromGame();
        });
        gameView.getTrioButton().setOnAction(e -> {
            hidePlayerHand();
            openTrioPage();
        });
        
        // Gestion du bouton pour voir/cacher ses propres cartes
        gameView.getPrintButton().setOnAction(e -> togglePlayerHand());
        
        // Boutons pour révéler ses propres cartes extrêmes
        gameView.getSmallestButton().setOnAction(e -> {
            hidePlayerHand();
            revealSmallestCard();
        });
        gameView.getlargestButton().setOnAction(e -> {
            hidePlayerHand();
            revealLargestCard();
        });

        gameView.getTitleLabel().setText("Cartes de " + gameModel.getCurrentPlayer().getName());

        setupOpponentButtons();
        updateGameDisplay();
    }

    /**
     * Configure les boutons d'interaction avec les adversaires (flèches haut/bas).
     * Permet de choisir un adversaire puis de demander sa plus petite ou plus grande carte.
     */
    private void setupOpponentButtons() {
        List<Actor> players = gameModel.getPlayers();

        for (Actor player : players) {
            final int playerId = player.getPlayerIndex();
            final int viewPlayerId = playerId + 1;

            Button playerBtn = gameView.getOpponentButton(viewPlayerId);
            if (playerBtn != null) {
                // Sélection d'un adversaire
                playerBtn.setOnAction(e -> {
                    hidePlayerHand();
                    handlePlayerSelection(viewPlayerId);
                });

                Button upArrow = gameView.getUpArrowButton(viewPlayerId);
                Button downArrow = gameView.getDownArrowButton(viewPlayerId);

                // Demande de la plus grande carte
                if (upArrow != null) {
                    upArrow.setOnAction(e -> {
                        hidePlayerHand();
                        revealLargestCardFromPlayer(viewPlayerId);
                    });
                }
                // Demande de la plus petite carte
                if (downArrow != null) {
                    downArrow.setOnAction(e -> {
                        hidePlayerHand();
                        revealSmallestCardFromPlayer(viewPlayerId);
                    });
                }
            }
        }
    }

    /**
     * Gère la logique de sélection visuelle d'un joueur adverse.
     * Affiche ou cache les flèches d'action (Min/Max) associées à ce joueur.
     * @param playerId L'identifiant visuel du joueur sélectionné.
     */
    private void handlePlayerSelection(int playerId) {
        if (selectedPlayer != null && selectedPlayer == playerId) {
            // Désélection si on clique sur le même joueur
            gameView.hideAllArrows();
            selectedPlayer = null;
        } else {
            // Nouvelle sélection
            gameView.hideAllArrows();
            gameView.showArrowsForPlayer(playerId);
            selectedPlayer = playerId;
        }
    }

    /**
     * Met à jour l'ensemble de l'affichage en fonction de l'état actuel du modèle.
     * Rafraîchit les noms, les cartes révélées au centre, et la main du joueur courant.
     */
    private void updateGameDisplay() {
        Actor currentPlayer = gameModel.getCurrentPlayer();
        int currentPlayerId = currentPlayer.getPlayerIndex() + 1;
        List<CardLocation> revealedCards = gameModel.getRevealedCards();

        gameView.setActualPlayer(currentPlayerId);

        for (Actor player : gameModel.getPlayers()) {
            int viewId = player.getPlayerIndex() + 1;
            gameView.updatePlayerName(viewId, player.getName());
        }

        gameView.updateTitle(currentPlayer.getName());
        gameView.setCurrentHand(currentPlayer.getHand().getCards());
        gameView.updateBoard(revealedCards);

        if (revealedCards.isEmpty()) {
            gameView.clearBoard();
        }

        updateArrowButtons(revealedCards);
        
        // Log console pour le débogage
        System.out.println("Tour " + turnCounter + " - " + currentPlayer.getName() + " joue");
    }

    /**
     * Met à jour la visibilité des flèches d'action en fonction du nombre de cartes déjà révélées.
     * Si 3 cartes sont révélées (tour fini ou trio), on cache les flèches.
     */
    private void updateArrowButtons(List<CardLocation> revealedCards) {
        if (selectedPlayer != null && revealedCards.size() < 3) {
            gameView.showArrowsForPlayer(selectedPlayer);
        } else {
            gameView.hideAllArrows();
            selectedPlayer = null;
        }
    }

    // --- Actions de jeu ---

    /**
     * Action : Révéler la plus petite carte du joueur courant.
     */
    private void revealSmallestCard() {
        revealCardAction(() -> gameModel.getCurrentPlayer().getHand().getSmallestCard(),
                () -> gameModel.revealSmallestCardFromPlayer(gameModel.getCurrentPlayer().getPlayerIndex()));
    }

    /**
     * Action : Révéler la plus grande carte du joueur courant.
     */
    private void revealLargestCard() {
        revealCardAction(() -> gameModel.getCurrentPlayer().getHand().getLargestCard(),
                () -> gameModel.revealLargestCardFromPlayer(gameModel.getCurrentPlayer().getPlayerIndex()));
    }

    /**
     * Action : Révéler la plus petite carte d'un adversaire.
     */
    private void revealSmallestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1;
        revealCardAction(null, () -> gameModel.revealSmallestCardFromPlayer(modelPlayerId));
    }

    /**
     * Action : Révéler la plus grande carte d'un adversaire.
     */
    private void revealLargestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1;
        revealCardAction(null, () -> gameModel.revealLargestCardFromPlayer(modelPlayerId));
    }

    /**
     * Méthode générique pour exécuter une action de révélation de carte.
     * Gère les vérifications d'usage, l'exécution de l'action, la mise à jour de l'affichage
     * et la gestion des erreurs.
     *
     * @param cardSupplier Fonction pour récupérer la carte (optionnel, pour vérification locale).
     * @param revealAction L'action du modèle à exécuter.
     */
    private void revealCardAction(java.util.function.Supplier<Card> cardSupplier, Runnable revealAction) {
        if (!gameModel.canRevealCard()) {
            showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
            return;
        }

        try {
            if (cardSupplier != null) {
                Card card = cardSupplier.get();
                if (card == null) {
                    showErrorMessage("Aucune carte disponible !");
                    return;
                }
                // Optimisation visuelle : marquer la carte si c'est celle du joueur courant
                if (cardSupplier.get().equals(gameModel.getCurrentPlayer().getHand().getSmallestCard()) || 
                    cardSupplier.get().equals(gameModel.getCurrentPlayer().getHand().getLargestCard())) {
                     gameView.markCardAsRevealed(card);
                }
            }

            revealAction.run();
            updateGameDisplay();
            handleAfterReveal();
            
            // Réinitialisation de l'interface après action
            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de la révélation de la carte");
        }
    }

    /**
     * Analyse l'état du jeu après qu'une carte ait été révélée.
     * Détermine si le tour doit s'arrêter (cartes différentes) ou si un trio est potentiel.
     */
    private void handleAfterReveal() {
        List<CardLocation> revealedCards = gameModel.getRevealedCards();

        if (revealedCards.size() == 2) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();

            if (card1.getValue() != card2.getValue()) {
                // Cartes différentes : Fin du tour forcée avec message bloquant
                gameView.showBlockingMessage(
                    "Cartes différentes (" + card1.getValue() + " et " + card2.getValue() + ")",
                    "Fin du tour",
                    this::nextTurn
                );
                
            } else {
                // Cartes identiques : Encouragement
                gameView.showOverlayMessage("Cartes identiques (" + card1.getValue() + ") ! Continuez...", 1500);
            }
        } else if (revealedCards.size() == 3) {
            // 3 cartes révélées : Vérification finale du trio
            checkTrioAndEndTurn();
        }
    }

    /**
     * Vérifie si les 3 cartes révélées forment un trio et termine le tour.
     * Affiche un message approprié (Succès ou Échec).
     */
    private void checkTrioAndEndTurn() {
        List<CardLocation> revealedCards = gameModel.getRevealedCards();
        boolean isTrio = false;
        
        if (revealedCards.size() == 3) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();
            Card card3 = revealedCards.get(2).getCard();
            if (card1.getValue() == card2.getValue() && card2.getValue() == card3.getValue()) {
                isTrio = true;
            }
        }

        if (isTrio) {
            gameView.showBlockingMessage(
                "TRIO FORMÉ !",
                "Continuer",
                this::nextTurn
            );
        } else {
            gameView.showBlockingMessage(
                "Pas de trio...",
                "Fin du tour",
                this::nextTurn
            );
        }
    }

    /**
     * Passe au tour suivant dans le modèle et met à jour l'interface.
     * Vérifie également les conditions de fin de partie ou de changement de phase (ex: échange après trio).
     */
    private void nextTurn() {
        boolean trioFormed = gameModel.nextTurn();
        turnCounter++;

        gameView.resetRevealedCards();
        hidePlayerHand();
        updateGameDisplay();

        // PRIORITÉ ABSOLUE : Vérifier si la partie est finie
        if (gameModel.isGameEnded()) {
            System.out.println("--------------------------------------------------");
            System.out.println("Fin de partie détectée.");
            System.out.println("--------------------------------------------------");
            endGame();
            return; // Arrêt immédiat
        }

        // Vérifier si une phase d'échange a été déclenchée (Trio en mode équipe)
        if (gameModel.getCurrentPhase() == Game.GamePhase.POST_TRIO_SWAP) {
            startSwapPhase();
            return;
        }

        // Sinon, continuation normale
        gameView.showOverlayMessage("Tour de " + gameModel.getCurrentPlayer().getName(), 2000);
    }

    // --- Navigation et Pages Annexes ---

    /**
     * Ouvre la page de la pioche.
     * Filtre les cartes pour ne montrer que celles disponibles.
     */
    private void openDrawPilePage() {
        try {
            List<Card> allCards = gameModel.getDrawPile().getRemainingCards();
            List<Card> availableCards = filterCardsNotInUse(allCards);

            if (availableCards.isEmpty()) {
                showErrorMessage("La pioche est vide !");
                return;
            }

            DrawPilePage drawPileView = new DrawPilePage(availableCards);
            drawPileView.setCardSelectionHandler(card -> {
                if (gameModel.canRevealCard()) {
                    gameModel.revealCardFromDrawPile(card);
                    updateGameDisplay();
                    primaryStage.getScene().setRoot(gameView.getRoot());
                    handleAfterReveal();
                } else {
                    showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
                }
            });

            primaryStage.getScene().setRoot(drawPileView.getRoot());
            primaryStage.setTitle("Pioche du Trio");
            drawPileView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de l'ouverture de la pioche");
        }
    }

    /**
     * Ouvre la page récapitulative des trios formés.
     * Adapte l'affichage selon le mode de jeu (Solo ou Équipe).
     */
    private void openTrioPage() {
        if (gameModel.isTeamMode()) {
            openTrioTeamPage();
        } else {
            openTrioSoloPage();
        }
    }

    /**
     * Affiche les trios en mode Solo.
     */
    private void openTrioSoloPage() {
        Map<Integer, List<Card>> playerTrios = getPlayerTriosCards();
        TrioSoloPage trioView = new TrioSoloPage(gameModel.getPlayers().size(), getPlayerNames(), playerTrios);
        primaryStage.getScene().setRoot(trioView.getRoot());
        trioView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    /**
     * Affiche les trios en mode Équipe.
     */
    private void openTrioTeamPage() {
        Map<Integer, List<Card>> teamTrios = getTeamTriosCards();
        TrioTeamPage trioView = new TrioTeamPage(gameModel.getPlayers().size() / 2, getPlayerNames(), teamTrios);
        primaryStage.getScene().setRoot(trioView.getRoot());
        trioView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    /**
     * Ouvre la page des règles sans quitter la partie en cours.
     */
    private void openRulesPageFromGame() {
        RulesPage rulesView = new RulesPage();
        primaryStage.getScene().setRoot(rulesView.getRoot());
        rulesView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    /**
     * Bascule l'affichage des cartes du joueur (Visible <-> Caché).
     */
    private void togglePlayerHand() {
        boolean isVisible = gameView.areCardsVisible();
        if (isVisible) {
            hidePlayerHand();
        } else {
            gameView.setCardsVisible(true);
            gameView.getPrintButton().setText("Cacher mes cartes");
        }
    }
    
    /**
     * Force le masquage des cartes du joueur (sécurité visuelle).
     */
    private void hidePlayerHand() {
        if (gameView.areCardsVisible()) {
            gameView.setCardsVisible(false);
            gameView.getPrintButton().setText("Voir mes cartes");
        }
    }

    /**
     * Déclenche la fin de partie et affiche l'écran de victoire.
     */
    private void endGame() {
        EndGameController endGameController = new EndGameController(primaryStage, gameModel);
        endGameController.showEndGame();
    }

    // --- Utilitaires ---

    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for(Actor a : gameModel.getPlayers()) {
            names.add(a.getName());
        }
        return names;
    }

    /**
     * Filtre une liste de cartes pour retirer celles qui sont actuellement sur le plateau ou dans des trios validés.
     * Utile pour l'affichage de la pioche.
     */
    private List<Card> filterCardsNotInUse(List<Card> allCards) {
        List<Card> availableCards = new ArrayList<>();
        for (Card card : allCards) {
            if (!isCardCurrentlyInUse(card)) {
                availableCards.add(card);
            }
        }
        return availableCards;
    }

    /**
     * Vérifie si une carte est "utilisée" (révélée sur le plateau ou dans un trio validé).
     */
    private boolean isCardCurrentlyInUse(Card card) {
        for (CardLocation location : gameModel.getRevealedCards()) {
            if (location.getCard().equals(card)) return true;
        }
        CompletedTrios completedTrios = gameModel.getCompletedTrios();
        if (completedTrios != null) {
            for (int playerId = 0; playerId < gameModel.getPlayers().size(); playerId++) {
                List<List<Card>> playerTrios = completedTrios.getTriosForPlayer(playerId);
                if (playerTrios != null) {
                    for (List<Card> trio : playerTrios) {
                        if (trio != null) {
                            for (Card trioCard : trio) {
                                if (trioCard.equals(card)) return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Récupère une représentation simplifiée des trios par joueur pour l'affichage.
     * @return Une map associant l'ID du joueur à une liste de cartes représentatives de ses trios.
     */
    private Map<Integer, List<Card>> getPlayerTriosCards() {
        Map<Integer, List<Card>> playerTrios = new HashMap<>();
        if (gameModel != null && gameModel.getCompletedTrios() != null) {
            CompletedTrios completedTrios = gameModel.getCompletedTrios();
            for (int playerId = 0; playerId < gameModel.getPlayers().size(); playerId++) {
                List<List<Card>> trioLists = completedTrios.getTriosForPlayer(playerId);
                if (trioLists != null && !trioLists.isEmpty()) {
                    List<Card> representativeCards = new ArrayList<>();
                    for (List<Card> trio : trioLists) {
                        if (trio != null && !trio.isEmpty()) representativeCards.add(trio.getFirst());
                    }
                    playerTrios.put(playerId + 1, representativeCards);
                }
            }
        }
        return playerTrios;
    }

    /**
     * Récupère une représentation simplifiée des trios par équipe.
     * Combine les trios des deux joueurs de l'équipe.
     */
    private Map<Integer, List<Card>> getTeamTriosCards() {
        Map<Integer, List<Card>> teamTrios = new HashMap<>();
        if (gameModel != null && gameModel.isTeamMode() && gameModel.getCompletedTrios() != null) {
            CompletedTrios completedTrios = gameModel.getCompletedTrios();
            int teamsCount = gameModel.getPlayers().size() / 2;

            for (int teamId = 1; teamId <= teamsCount; teamId++) {
                int firstPlayerId = (teamId - 1);
                int secondPlayerId = firstPlayerId + teamsCount;
                List<Card> combinedTrios = new ArrayList<>();

                addTriosToTeamList(completedTrios, firstPlayerId, combinedTrios);
                addTriosToTeamList(completedTrios, secondPlayerId, combinedTrios);

                combinedTrios.sort(Comparator.comparing(Card::getValue));
                if (!combinedTrios.isEmpty()) {
                    teamTrios.put(teamId, combinedTrios);
                }
            }
        }
        return teamTrios;
    }

    private void addTriosToTeamList(CompletedTrios completedTrios, int playerId, List<Card> combinedTrios) {
        List<List<Card>> trios = completedTrios.getTriosForPlayer(playerId);
        if (trios != null) {
            for (List<Card> trio : trios) {
                if (trio != null && !trio.isEmpty()) combinedTrios.add(trio.getFirst());
            }
        }
    }

    /**
     * Affiche une popup d'erreur standard.
     */
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
