package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.*;
import fr.utbm.ap4b.view.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.*;

public class GameController {

    private final Stage primaryStage;
    private final Game gameModel;
    private GameMainPage gameView;
    
    // État du jeu
    private int turnCounter = 0;
    private Integer selectedPlayer = null;

    public GameController(Stage primaryStage, Game gameModel) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
    }

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

    private void startSwapPhase() {
        // Déléguer au SwapController
        SwapController swapController = new SwapController(primaryStage, gameModel, () -> {
            // Callback quand l'échange est fini
            openGamePage();
            gameView.showOverlayMessage("Échanges terminés ! À vous de jouer.", 2000);
        });
        swapController.startSwapPhase();
    }

    private void openGamePage() {
        // Stocker l'ID du joueur pour lequel la vue est créée
        int initialPlayerId = gameModel.getCurrentPlayer().getPlayerIndex() + 1;
        int nbPlayers = gameModel.getPlayers().size();

        gameView = new GameMainPage(nbPlayers, initialPlayerId);
        
        // Désactiver la pioche si mode équipe
        if (gameModel.isTeamMode()) {
            gameView.disableDrawPileForTeamMode();
        }
        
        primaryStage.getScene().setRoot(gameView.getRoot());
        primaryStage.setTitle("Jeu du Trio");

        // Configuration des événements
        gameView.getDrawPileButton().setOnAction(e -> {
            hidePlayerHand(); // Cache les cartes si on fait une autre action
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
        
        // Gestion du toggle pour l'affichage des cartes
        gameView.getPrintButton().setOnAction(e -> togglePlayerHand());
        
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

    private void setupOpponentButtons() {
        List<Actor> players = gameModel.getPlayers();

        for (Actor player : players) {
            final int playerId = player.getPlayerIndex();
            final int viewPlayerId = playerId + 1;

            Button playerBtn = gameView.getOpponentButton(viewPlayerId);
            if (playerBtn != null) {
                playerBtn.setOnAction(e -> {
                    hidePlayerHand();
                    handlePlayerSelection(viewPlayerId);
                });

                Button upArrow = gameView.getUpArrowButton(viewPlayerId);
                Button downArrow = gameView.getDownArrowButton(viewPlayerId);

                if (upArrow != null) {
                    upArrow.setOnAction(e -> {
                        hidePlayerHand();
                        revealLargestCardFromPlayer(viewPlayerId);
                    });
                }
                if (downArrow != null) {
                    downArrow.setOnAction(e -> {
                        hidePlayerHand();
                        revealSmallestCardFromPlayer(viewPlayerId);
                    });
                }
            }
        }
    }

    private void handlePlayerSelection(int playerId) {
        if (selectedPlayer != null && selectedPlayer == playerId) {
            gameView.hideAllArrows();
            selectedPlayer = null;
        } else {
            gameView.hideAllArrows();
            gameView.showArrowsForPlayer(playerId);
            selectedPlayer = playerId;
        }
    }

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
        
        // Log console
        System.out.println("Tour " + turnCounter + " - " + currentPlayer.getName() + " joue");
    }

    private void updateArrowButtons(List<CardLocation> revealedCards) {
        if (selectedPlayer != null && revealedCards.size() < 3) {
            gameView.showArrowsForPlayer(selectedPlayer);
        } else {
            gameView.hideAllArrows();
            selectedPlayer = null;
        }
    }

    // --- Actions de jeu ---

    private void revealSmallestCard() {
        revealCardAction(() -> gameModel.getCurrentPlayer().getHand().getSmallestCard(),
                () -> gameModel.revealSmallestCardFromPlayer(gameModel.getCurrentPlayer().getPlayerIndex()));
    }

    private void revealLargestCard() {
        revealCardAction(() -> gameModel.getCurrentPlayer().getHand().getLargestCard(),
                () -> gameModel.revealLargestCardFromPlayer(gameModel.getCurrentPlayer().getPlayerIndex()));
    }

    private void revealSmallestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1;
        revealCardAction(null, () -> gameModel.revealSmallestCardFromPlayer(modelPlayerId));
    }

    private void revealLargestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1;
        revealCardAction(null, () -> gameModel.revealLargestCardFromPlayer(modelPlayerId));
    }

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
                if (cardSupplier.get().equals(gameModel.getCurrentPlayer().getHand().getSmallestCard()) || 
                    cardSupplier.get().equals(gameModel.getCurrentPlayer().getHand().getLargestCard())) {
                     // Marquer visuellement si c'est le joueur courant (optimisation possible)
                     gameView.markCardAsRevealed(card);
                }
            }

            revealAction.run();
            updateGameDisplay();
            handleAfterReveal();
            
            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de la révélation de la carte");
        }
    }

    private void handleAfterReveal() {
        List<CardLocation> revealedCards = gameModel.getRevealedCards();

        if (revealedCards.size() == 2) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();

            if (card1.getValue() != card2.getValue()) {
                // Cartes différentes : Fin du tour avec confirmation
                gameView.showBlockingMessage(
                    "Cartes différentes (" + card1.getValue() + " et " + card2.getValue() + ")",
                    "Fin du tour",
                    this::nextTurn
                );
                
            } else {
                // Cartes identiques : Continue
                gameView.showOverlayMessage("Cartes identiques (" + card1.getValue() + ") ! Continuez...", 1500);
            }
        } else if (revealedCards.size() == 3) {
            // 3 cartes révélées : Vérification du trio
            checkTrioAndEndTurn();
        }
    }

    private void checkTrioAndEndTurn() {
        // Vérifie manuellement si c'est un trio avant de passer le tour
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

    private void nextTurn() {
        boolean trioFormed = gameModel.nextTurn();
        turnCounter++;

        gameView.resetRevealedCards();
        hidePlayerHand();
        updateGameDisplay();

        // PRIORITÉ ABSOLUE : Vérifier si la partie est finie
        if (gameModel.isGameEnded()) {
            endGame();
            return; // On arrête tout ici
        }

        // Ensuite, vérifier si une phase d'échange a été déclenchée (Trio en mode équipe)
        if (gameModel.getCurrentPhase() == Game.GamePhase.POST_TRIO_SWAP) {
            startSwapPhase();
            return;
        }

        // Sinon, on continue le jeu
        gameView.showOverlayMessage("Tour de " + gameModel.getCurrentPlayer().getName(), 2000);
    }

    // --- Navigation et Pages Annexes ---

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
                    // Pas de popup ici, juste l'overlay si nécessaire ou rien
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

    private void openTrioPage() {
        if (gameModel.isTeamMode()) {
            openTrioTeamPage();
        } else {
            openTrioSoloPage();
        }
    }

    private void openTrioSoloPage() {
        Map<Integer, List<Card>> playerTrios = getPlayerTriosCards();
        TrioSoloPage trioView = new TrioSoloPage(gameModel.getPlayers().size(), getPlayerNames(), playerTrios);
        primaryStage.getScene().setRoot(trioView.getRoot());
        trioView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    private void openTrioTeamPage() {
        Map<Integer, List<Card>> teamTrios = getTeamTriosCards();
        TrioTeamPage trioView = new TrioTeamPage(gameModel.getPlayers().size() / 2, getPlayerNames(), teamTrios);
        primaryStage.getScene().setRoot(trioView.getRoot());
        trioView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    private void openRulesPageFromGame() {
        RulesPage rulesView = new RulesPage();
        primaryStage.getScene().setRoot(rulesView.getRoot());
        rulesView.getEndBtn().setOnAction(e -> primaryStage.getScene().setRoot(gameView.getRoot()));
    }

    private void togglePlayerHand() {
        boolean isVisible = gameView.areCardsVisible();
        if (isVisible) {
            hidePlayerHand();
        } else {
            gameView.setCardsVisible(true);
            gameView.getPrintButton().setText("Cacher mes cartes");
        }
    }
    
    private void hidePlayerHand() {
        if (gameView.areCardsVisible()) {
            gameView.setCardsVisible(false);
            gameView.getPrintButton().setText("Voir mes cartes");
        }
    }

    private void endGame() {
        // Déléguer au EndGameController
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

    private List<Card> filterCardsNotInUse(List<Card> allCards) {
        List<Card> availableCards = new ArrayList<>();
        for (Card card : allCards) {
            if (!isCardCurrentlyInUse(card)) {
                availableCards.add(card);
            }
        }
        return availableCards;
    }

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

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
