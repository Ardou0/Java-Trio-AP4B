package fr.utbm.ap4b.controller;
import fr.utbm.ap4b.model.Actor;
import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.Game;
import fr.utbm.ap4b.model.CardLocation;
import fr.utbm.ap4b.model.JoueurEquipe;
import fr.utbm.ap4b.view.DrawPilePage;
import fr.utbm.ap4b.view.GameMainPage;
import fr.utbm.ap4b.view.RulesPage;
import fr.utbm.ap4b.view.TrioSoloPage;
import fr.utbm.ap4b.view.TrioTeamPage;
import fr.utbm.ap4b.view.ModeSelectionPage;
import fr.utbm.ap4b.view.PlayerPage;
import fr.utbm.ap4b.view.ExchangePage;
import javafx.animation.FadeTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TrioController {

    private final ModeSelectionPage selectionView;
    private GameMainPage gameView;
    private final Stage primaryStage;
    private String teamMode;
    private String gameMode;
    private int nbPlayers;
    private Game gameModel;
    private Integer selectedPlayer = null;
    private List<String> playerNames = new ArrayList<>();

    // Pour suivre l'état du jeu
    private int turnCounter = 0;
    private boolean isInSwapPhase = false;

    public TrioController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.nbPlayers = 6;
        this.selectionView = new ModeSelectionPage();
        this.teamMode = "Individuel";
        this.gameMode = "Normal";
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        selectionView.getRulesButton().setOnAction(e -> openRulesPageFromSelection());
        selectionView.getNextButton().setOnAction(e -> openPlayerPage());
        //Changement de la ComboBox de la team
        selectionView.getTeamComboBox().valueProperty().addListener((observable, oldValue, newValue) ->
                this.teamMode = newValue
        );
        selectionView.getModeComboBox().valueProperty().addListener((observable, oldValue, newValue) ->
                this.gameMode = newValue
        );
        selectionView.getPlayerComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                try {
                    this.nbPlayers = Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    // Afficher l'erreur
                    System.err.println("Erreur fatale : Impossible de convertir '" + newValue + "' en entier.");
                    e.printStackTrace();

                    // Arrêter complètement l'application
                    System.exit(1);
                }
            } else {
                // Si la valeur est null ou vide, arrêter aussi
                System.err.println("Erreur fatale : Valeur null ou vide dans le ComboBox.");
                System.exit(2);
            }
        });

    }

    private void openPlayerPage(){
        try{
            PlayerPage playerView = new PlayerPage(nbPlayers);
            primaryStage.getScene().setRoot(playerView.getRoot());
            primaryStage.setTitle("Trio - Nom des joueurs");

            playerView.getPreviousButton().setOnAction(e ->
                    primaryStage.getScene().setRoot(selectionView.getRoot())
            );

            playerView.getNextButton().setOnAction(e -> {
                // Récupère et valide les noms avant d'ouvrir la page de jeu
                if (retrievePlayerNames(playerView)) {
                    // Si les noms sont valides, ouvre la page de jeu
                    initiatingGameModel();
                    openGamePage();
                }
            });

            playerView.getExampleCheck().setOnAction(event -> {
                boolean isSelected = playerView.getExampleCheck().isSelected();
                playerView.getExampleLabel().setVisible(isSelected);

                // Animation de transition
                playerView.getExampleLabel().setOpacity(isSelected ? 0 : 1);

                if (isSelected) {
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), playerView.getExampleLabel());
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean retrievePlayerNames(PlayerPage playerView) {
        try {
            // Récupére la liste des noms depuis PlayerPage
            playerNames = playerView.getPlayerNames();

            // Vérifie que tous les noms sont remplis
            for (int i = 0; i < playerNames.size(); i++) {
                String name = playerNames.get(i);

                if (name == null || name.trim().isEmpty()) {
                    showErrorMessage("Veuillez entrer un nom pour le Joueur " + (i + 1));
                    return false;
                }

                // Enlève les espaces superflus
                playerNames.set(i, name.trim());
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de la récupération des noms des joueurs");
            return false;
        }
    }

    private void initiatingGameModel(){
        boolean isTeamMode = !Objects.equals(teamMode, "Individuel");
        boolean isPiquant = !Objects.equals(gameMode, "Normal");
        gameModel = new Game(playerNames, 0, isTeamMode, isPiquant);

        startGame();
    }

    private void openGamePage(){
        gameView = new GameMainPage(nbPlayers, gameModel.getCurrentPlayer().getPlayerIndex() + 1);
        primaryStage.getScene().setRoot(gameView.getRoot());
        primaryStage.setTitle("Jeu du Trio");

        gameView.getDrawPileButton().setOnAction(e -> openDrawPilePage());
        gameView.getRulesButton().setOnAction(e -> openRulesPageFromGame());
        gameView.getTrioButton().setOnAction(e -> openTrioPage());
        gameView.getPrintButton().setOnAction(e -> showPlayerHand());
        gameView.getSmallestButton().setOnAction(e -> revealSmallestCard());
        gameView.getlargestButton().setOnAction(e -> revealLargestCard());

        if(gameModel == null){
            boolean isTeamMode = !Objects.equals(teamMode, "Individuel");
            boolean isPiquant = !Objects.equals(gameMode, "Normal");
            gameModel = new Game(playerNames, 0, isTeamMode, isPiquant);
        }

        // Debug
        System.out.println("DEBUG: Nombre de joueurs: " + nbPlayers);
        System.out.println("DEBUG: Actual player: " + gameModel.getCurrentPlayer().getName());
        System.out.println("DEBUG: Player names: " + playerNames);
        gameView.getTitleLabel().setText("Cartes de " + gameModel.getCurrentPlayer().getName());

        setupOpponentButtons();

        updateGameDisplay();
    }

    private void handlePlayerSelection(int playerId){
        if (selectedPlayer != null && selectedPlayer == playerId) {
            gameView.hideAllArrows();
            selectedPlayer = null;
        } else {
            gameView.hideAllArrows();
            gameView.showArrowsForPlayer(playerId);
            selectedPlayer = playerId;

        }
    }

    private void openTrioPage() {
        if ("Equipe".equals(teamMode)) {
            openTrioTeamPage();
        } else {
            openTrioSoloPage();
        }
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
            TrioSoloPage trioView = new TrioSoloPage(nbPlayers);

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(trioView.getRoot());
            primaryStage.setTitle("Trios obtenus (Mode Individuel)");

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
            TrioTeamPage trioView = new TrioTeamPage(nbPlayers/2);

            //Créer une scene avec cette vue
            primaryStage.getScene().setRoot(trioView.getRoot());
            primaryStage.setTitle("Trios obtenus (Mode Équipe)");

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

    private void startGame() {
        gameModel.startGame();
        turnCounter = 0;
        isInSwapPhase = false;

        // Afficher message de début
        String modeMessage = gameModel.isTeamMode() ?
                "Mode Équipe" : "Mode Solo";
        showInfoMessage("Début de la partie - " + modeMessage);
    }

    private void setupOpponentButtons() {
        // Récupère la liste des joueurs depuis le modèle
        List<Actor> players = gameModel.getPlayers();

        for (Actor player : players) {
            final int playerId = player.getPlayerIndex();
            final int viewPlayerId = playerId + 1;

            Button playerBtn = gameView.getOpponentButton(viewPlayerId);
            if(playerBtn != null){
                playerBtn.setOnAction(e ->
                        handlePlayerSelection(viewPlayerId));

                // Configurer les boutons flèches
                Button upArrow = gameView.getUpArrowButton(viewPlayerId);
                Button downArrow = gameView.getDownArrowButton(viewPlayerId);

                if (upArrow != null) {
                    // Flèche haut révèle la plus grande carte
                    upArrow.setOnAction(e -> revealLargestCardFromPlayer(viewPlayerId));
                }

                if (downArrow != null) {
                    // Flèche bas révèle la plus petite carte
                    downArrow.setOnAction(e -> revealSmallestCardFromPlayer(viewPlayerId));
                }

//                // Configurer l'action
//                playerBtn.setOnAction(e -> {
//                    if (isInSwapPhase) {
//                        handleCardExchange(viewPlayerId);
//                    } else {
//                        handlePlayerSelection(viewPlayerId);
//                    }
//                });
            }
        }
    }

    private void revealSmallestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1; // Convertir en ID modèle

        // Vérifie si on peut encore révéler des cartes
        if (!gameModel.canRevealCard()) {
            showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
            return;
        }

        try{
            //Révèle la carte
            gameModel.revealSmallestCardFromPlayer(modelPlayerId);
            updateGameDisplay();

            //Vérifie ce qu'il faut faire après la révélation
            handleAfterReveal();

            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la révélation de la carte");
        }
    }

    private void revealLargestCardFromPlayer(int viewPlayerId) {
        int modelPlayerId = viewPlayerId - 1;
        // Vérifie si on peut encore révéler des cartes
        if (!gameModel.canRevealCard()) {
            showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
            return;
        }

        try{
            //Révèle la carte
            gameModel.revealLargestCardFromPlayer(modelPlayerId);
            updateGameDisplay();

            //Vérifie ce qu'il faut faire après la révélation
            handleAfterReveal();

            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la révélation de la carte");
        }
    }

    private void handleCardExchange(int viewPlayerId) {
        // Logique pour l'échange de cartes en mode équipe
        showInfoMessage("Échange de cartes");
        //Afficher page d'échanges
    }

    private void showPlayerHand() {
        Actor currentPlayer = gameModel.getCurrentPlayer();
        List<Card> hand = currentPlayer.getHand().getCards();

        StringBuilder handInfo = new StringBuilder("Votre main : ");
        for (int i = 0; i < hand.size(); i++) {
            handInfo.append(hand.get(i).getValue());
            if (i < hand.size() - 1) {
                handInfo.append(", ");
            }
        }

        showInfoMessage(handInfo.toString());
    }

    //Révèle la plus petite carte du joueur actuel
    private void revealSmallestCard() {
        int currentPlayer = gameModel.getCurrentPlayer().getPlayerIndex();

        // Vérifie si on peut encore révéler des cartes
        if (!gameModel.canRevealCard()) {
            showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
            return;
        }

        try{
            //Révèle la carte
            gameModel.revealSmallestCardFromPlayer(currentPlayer);
            updateGameDisplay();

            //Vérifie ce qu'il faut faire après la révélation
            handleAfterReveal();

            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la révélation de la carte");
        }

    }

    //Révèle la plus grande carte du jour actuel
    private void revealLargestCard() {
        int currentPlayer = gameModel.getCurrentPlayer().getPlayerIndex();

        // Vérifie si on peut encore révéler des cartes
        if (!gameModel.canRevealCard()) {
            showErrorMessage("Vous ne pouvez pas révéler plus de cartes !");
            return;
        }

        try{
            //Révèle la carte
            gameModel.revealLargestCardFromPlayer(currentPlayer);
            updateGameDisplay();

            //Vérifie ce qu'il faut faire après la révélation
            handleAfterReveal();

            gameView.hideAllArrows();
            selectedPlayer = null;
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la révélation de la carte");
        }
    }

    private void handleAfterReveal() {
        List<CardLocation> revealedCards = gameModel.getRevealedCards();

        if (revealedCards.size() == 2) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();

            if (card1.getValue() != card2.getValue()) {
                showInfoMessage("Cartes différentes - Fin du tour");
                nextTurn(); // Le modèle gère toute la logique
            }
        } else if (revealedCards.size() == 3) {
            showInfoMessage("3 cartes révélées. Vérification du trio");
            nextTurn(); // Le modèle vérifie le trio et gère le tour
        }
    }

    private void nextTurn() {
        boolean trioFormed = gameModel.nextTurn();
        turnCounter++;

        // Affiche un message en fonction du résultat
        if (trioFormed) {
            showInfoMessage("TRIO FORMÉ !");
        } else {
            showInfoMessage("Tour terminé");
        }

        // Met à jour l'interface
        updateGameDisplay();

        // Gère la fin de partie
        if (gameModel.isGameEnded()) {
            endGame();
        }
    }

    private void handleSwapPhase() {
        if (gameModel.isTeamMode() && !gameModel.getPlayersAllowedToSwap().isEmpty()) {
            showInfoMessage("--- PHASE D'ÉCHANGE DE CARTES ---");
            // Ici, vous devriez ouvrir une fenêtre d'échange ou activer un mode d'échange
            // Pour l'instant, on simule avec un message
            for (Actor player : gameModel.getPlayersAllowedToSwap()) {
                showInfoMessage(player.getName() + " peut échanger une carte avec son coéquipier");
            }

            // Après l'échange, repasser en phase normale
            isInSwapPhase = false;
        }
    }

    private void updateGameDisplay() {
        // Mettre à jour les informations affichées
        Actor currentPlayer = gameModel.getCurrentPlayer();

        // Mettre à jour les noms des joueurs dans la vue
        for (Actor player : gameModel.getPlayers()) {
            int viewId = player.getPlayerIndex() + 1;
            if (viewId != gameModel.getCurrentPlayer().getPlayerIndex() + 1) {
                Button btn = gameView.getOpponentButton(viewId);
                if (btn != null) {
                    btn.setText(player.getName());
                }
            }
        }

        // Mettre à jour le titre
        gameView.getTitleLabel().setText("Cartes de " + gameModel.getCurrentPlayer().getName());

        // Met à jour le board avec les cartes révélées
        List<CardLocation> revealedCards = gameModel.getRevealedCards();
        gameView.updateBoard(revealedCards);

        // Afficher les cartes révélées
        if (!revealedCards.isEmpty()) {
            StringBuilder cardsInfo = new StringBuilder("Cartes révélées : ");
            for (CardLocation loc : revealedCards) {
                String source = loc.getSourcePlayer() != null ?
                        loc.getSourcePlayer().getName() : "Pioche";
                cardsInfo.append(loc.getCard().getValue())
                        .append(" (")
                        .append(source)
                        .append("), ");
            }
            // Retirer la dernière virgule
            if (cardsInfo.length() > 2) {
                cardsInfo.setLength(cardsInfo.length() - 2);
            }
//            showInfoMessage(cardsInfo.toString());
        }
        // Nettoie le board si aucune carte n'est révélée
        if (revealedCards.isEmpty()) {
            gameView.clearBoard();
        }

        // Met à jour l'état des boutons flèches
        if (selectedPlayer != null && gameModel.getRevealedCards().size() < 3) {
            // Si un joueur est sélectionné et on peut encore révéler des cartes
            gameView.showArrowsForPlayer(selectedPlayer);
        } else {
            // Sinon cacher toutes les flèches
            gameView.hideAllArrows();
            selectedPlayer = null;
        }

        // Mettre à jour le tour actuel
        showInfoMessage("Tour " + turnCounter + " - " + currentPlayer.getName() + " joue");
    }

    private void endGame() {
        Actor winner = gameModel.getWinner();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partie");
        alert.setHeaderText("La partie est terminée !");

        if (winner != null) {
            if (gameModel.isTeamMode() && winner instanceof JoueurEquipe) {
                JoueurEquipe winningPlayer = (JoueurEquipe) winner;
                alert.setContentText("L'équipe gagnante est celle de " +
                        winningPlayer.getName() + " et " +
                        winningPlayer.getTeammate().getName() + " !");
            } else {
                alert.setContentText("Le gagnant est " + winner.getName() + " !");
            }
        } else {
            alert.setContentText("La partie s'est terminée sans gagnant.");
        }

        alert.showAndWait();

        // Proposer de rejouer
        Alert replayAlert = new Alert(Alert.AlertType.CONFIRMATION);
        replayAlert.setTitle("Nouvelle partie");
        replayAlert.setHeaderText("Voulez-vous rejouer ?");
        replayAlert.setContentText("Choisissez votre option :");

        if (replayAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Retourner à la sélection du mode
            primaryStage.getScene().setRoot(selectionView.getRoot());
        } else {
            // Quitter l'application
            primaryStage.close();
        }
    }

    private void showInfoMessage(String message) {
        System.out.println("INFO: " + message); // Pour debug
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Infomation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
