package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.CardLocation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;

/**
 * Vue principale du jeu Trio avec JavaFX.
 * Cette classe gère l'affichage du plateau de jeu, des mains des joueurs,
 * des cartes révélées au centre et des interactions utilisateur (clics, boutons).
 */
public class GameMainPage {

    // Composants graphiques principaux
    private final int nombreJoueurs;
    private StackPane rootStack; // Conteneur racine pour superposer les couches (jeu + overlays)
    private BorderPane gameRoot; // Conteneur principal du jeu
    private Button rulesButton; // Bouton menant à la page de règles
    private Button trioButton; // Bouton pour voir les trios complétés
    private Button drawPileButton; // Bouton pour accéder à la pioche
    private int actualPlayer; // ID du joueur dont c'est le tour (pour l'affichage)
    
    // Boutons d'actions personnelles
    private Button printButton; // Voir/Cacher ses cartes
    private Button smallestCard; // Révéler sa plus petite carte
    private Button largestCard; // Révéler sa plus grande carte
    
    private Label cardLabel; // Titre de la zone de main ("Cartes de X")
    
    // Maps pour gérer les boutons dynamiques des adversaires
    private Map<Integer, Button> upArrowButtons = new HashMap<>(); // Flèches "Max"
    private Map<Integer, Button> downArrowButtons = new HashMap<>(); // Flèches "Min"
    private Map<Integer, Button> opponentButtons = new HashMap<>(); // Boutons de sélection d'adversaire
    private Map<Integer, VBox> arrowContainers = new HashMap<>(); // Conteneurs des flèches par joueur
    private Map<Integer, HBox> opponentContainers = new HashMap<>(); // Conteneurs globaux par joueur

    // Gestion de l'affichage des cartes
    private List<StackPane> cardSlots = new ArrayList<>(); // Emplacements des cartes révélées au centre
    private Map<Integer, ImageView> cardImages = new HashMap<>(); // Images des cartes au centre

    private List<ImageView> cardViews = new ArrayList<>(); // Références aux images de la main du joueur
    private Map<Card, ImageView> cardToViewMap = new HashMap<>(); // Association carte → vue (non utilisé actuellement mais utile pour futures anims)

    private Set<Card> revealedCards = new HashSet<>(); // Cartes déjà révélées dans ce tour (pour ne pas les réafficher dans la main)
    private List<Card> currentHand = new ArrayList<>(); // Main actuelle du joueur
    private boolean areCardsVisible = false; // État de visibilité des cartes (face cachée/visible)


    /**
     * Constructeur de la page de jeu principale.
     * @param nombreJoueurs Le nombre total de joueurs.
     * @param actualPlayer L'ID du joueur qui commence (pour l'initialisation de l'affichage).
     */
    public GameMainPage(int nombreJoueurs, int actualPlayer) {
        this.nombreJoueurs = nombreJoueurs;
        this.actualPlayer = actualPlayer;
        showScreen();
    }

    /**
     * Initialise et assemble les composants de l'interface graphique.
     */
    private void showScreen(){
        rootStack = new StackPane();
        gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(10));
        
        // Assemblage des différentes zones
        gameRoot.setCenter(createHandArea());
        gameRoot.setTop(createRulesButton());
        gameRoot.setBottom(createBoardContainer());
        gameRoot.setLeft(createOpponentArea());
        gameRoot.setRight(createPersonalArea());
        
        rootStack.getChildren().add(gameRoot);
    }

    /**
     * Désactive visuellement et fonctionnellement le bouton Pioche.
     * Utilisé en mode équipe où la pioche n'est pas accessible directement.
     */
    public void disableDrawPileForTeamMode() {
        if (drawPileButton != null) {
            drawPileButton.setText(""); // Enlever le texte
            drawPileButton.setDisable(true); // Désactiver l'interaction
            // Style "décor" : fond sombre, pas de bordure interactive
            drawPileButton.setStyle(
                "-fx-background-color: #3E2C1C;" + // Marron très foncé
                "-fx-opacity: 1.0;" + // Garder l'opacité à 100% même désactivé
                "-fx-border-color: #2A1E12;" +
                "-fx-border-width: 2;"
            );
        }
    }

    /**
     * Affiche un message temporaire en surimpression (Overlay).
     * @param message Le texte à afficher.
     * @param durationMillis La durée d'affichage en millisecondes.
     */
    public void showOverlayMessage(String message, int durationMillis) {
        showOverlayMessage(message, durationMillis, null);
    }

    /**
     * Affiche un message temporaire en overlay avec une action à la fin.
     * @param message Le texte à afficher.
     * @param durationMillis La durée d'affichage.
     * @param onFinished L'action à exécuter après la disparition du message.
     */
    public void showOverlayMessage(String message, int durationMillis, Runnable onFinished) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 10px;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER);

        StackPane overlay = new StackPane(messageLabel);
        overlay.setAlignment(Pos.CENTER);
        
        rootStack.getChildren().add(overlay);

        // Animation d'apparition (Fade In)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Disparition automatique après délai
        PauseTransition delay = new PauseTransition(Duration.millis(durationMillis));
        delay.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), overlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                rootStack.getChildren().remove(overlay);
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            fadeOut.play();
        });
        delay.play();
    }

    /**
     * Affiche un message bloquant qui nécessite une confirmation de l'utilisateur.
     * Utile pour les fins de tour ou les événements importants.
     * @param message Le message à afficher.
     * @param buttonText Le texte du bouton de confirmation.
     * @param onConfirm L'action à exécuter lors du clic sur le bouton.
     */
    public void showBlockingMessage(String message, String buttonText, Runnable onConfirm) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 0 0 20px 0;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER);

        Button confirmButton = new Button(buttonText);
        confirmButton.setStyle(
            "-fx-background-color: #8B7355;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-color: transparent;" +
            "-fx-background-insets: 0;"
        );
        // Effets de survol
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle("-fx-background-color: #5C4C38; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 5px; -fx-border-color: transparent; -fx-background-insets: 0;"));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 5px; -fx-border-color: transparent; -fx-background-insets: 0;"));

        VBox content = new VBox(messageLabel, confirmButton);
        content.setAlignment(Pos.CENTER);
        content.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.85);" +
            "-fx-padding: 30px;" +
            "-fx-background-radius: 15px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane overlay = new StackPane(content);
        overlay.setAlignment(Pos.CENTER);
        
        rootStack.getChildren().add(overlay);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        confirmButton.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), overlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                rootStack.getChildren().remove(overlay);
                if (onConfirm != null) {
                    onConfirm.run();
                }
            });
            fadeOut.play();
        });
    }

    /**
     * Crée la zone inférieure contenant les emplacements de cartes révélées (le "Board").
     */
    private BorderPane createBoardContainer(){
        BorderPane bottomContainer = new BorderPane();

        drawPileButton = new Button("Pioche");
        drawPileButton.setAlignment(Pos.CENTER);
        drawPileButton.setOnMouseEntered(e -> drawPileButton.setStyle("-fx-background-color: #5C4C38;"));
        drawPileButton.setOnMouseExited(e -> drawPileButton.setStyle("-fx-background-color: #8B7355;"));

        trioButton = new Button("Trio");
        trioButton.setAlignment(Pos.CENTER);
        trioButton.setOnMouseEntered(e -> trioButton.setStyle("-fx-background-color: #5C4C38;"));
        trioButton.setOnMouseExited(e -> trioButton.setStyle("-fx-background-color: #8B7355;"));

        // Conteneur horizontal pour les 3 emplacements de cartes
        HBox bottomBoardContainer = new HBox(15);
        bottomBoardContainer.setAlignment(Pos.CENTER);
        bottomBoardContainer.setPadding(new Insets(20,10, 30, 10));
        bottomBoardContainer.setStyle(
                "-fx-background-color: #E2CAA2;" +
                "-fx-background-radius: 10 10 0 0;" +
                "-fx-border-color: #0D1117;" +
                "-fx-border-width: 3;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);"
        );

        // Création des 3 slots vides
        cardSlots.clear();
        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            bottomBoardContainer.getChildren().add(slot);
            cardSlots.add(slot);
        }

        // Mise en page des boutons latéraux
        drawPileButton.prefHeightProperty().bind(bottomBoardContainer.heightProperty());
        trioButton.prefHeightProperty().bind(bottomBoardContainer.heightProperty());

        drawPileButton.setPrefWidth(100);
        trioButton.setPrefWidth(100);

        BorderPane.setMargin(drawPileButton, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(trioButton, new Insets(0, 0, 0, 10));

        BorderPane.setAlignment(drawPileButton, Pos.CENTER);
        bottomContainer.setLeft(drawPileButton);

        bottomContainer.setCenter(bottomBoardContainer);

        BorderPane.setAlignment(trioButton, Pos.CENTER);
        bottomContainer.setRight(trioButton);

        return bottomContainer;
    }

    /**
     * Crée un emplacement de carte vide (Slot) avec un style en pointillés.
     */
    public static StackPane createSlot() {
        StackPane slot = new StackPane();

        slot.setPrefSize(100, 150);
        slot.setMinSize(100, 150);
        slot.setMaxSize(100, 150);

        slot.setStyle(
                "-fx-border-color: #8B7355;" +
                "-fx-border-style: dashed;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 8;" +
                "-fx-background-color: #F5E6D3;" +
                "-fx-background-radius: 8;" +
                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        slot.setMouseTransparent(true); // Pas d'interaction directe sur les slots

        return slot;
    }

    /**
     * Affiche une carte dans un emplacement spécifique du board.
     * @param slotIndex Index de l'emplacement (0, 1 ou 2).
     * @param card La carte à afficher.
     */
    public void displayCardInBoard(int slotIndex, Card card) {
        if(slotIndex < 0 || slotIndex >= cardSlots.size()){
            System.err.println("Index d'emplacement invalide (entre 1 et 3) : " + slotIndex);
            return;
        }

        if (card == null) {
            System.err.println("Carte null pour l'emplacement: " + slotIndex);
            clearSlot(slotIndex);
            return;
        }

        StackPane slot = cardSlots.get(slotIndex);
        slot.getChildren().clear();

        try{
            String imagePath = card.getImagePath();
            InputStream is = getClass().getResourceAsStream(imagePath);

            if(is != null){
                Image cardImage = new Image(is);
                ImageView cardView = new ImageView(cardImage);

                cardView.setFitWidth(90);
                cardView.setFitHeight(135);
                cardView.setPreserveRatio(true);

                cardImages.put(slotIndex, cardView);
                slot.getChildren().add(cardView);
            } else{
                // Fallback texte si l'image manque
                System.err.println("Image non trouvée " +  imagePath);
                Label valueLabel = new Label(String.valueOf(card.getValue()));
                slot.getChildren().add(valueLabel);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Label valueLabel = new Label(String.valueOf(card.getValue()));
            valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #5C4C38;");
            slot.getChildren().add(valueLabel);
        }
    }

    /**
     * Met à jour l'ensemble du board avec la liste des cartes révélées.
     * @param cardLocations Liste des emplacements de cartes provenant du modèle.
     */
    public void updateBoard(List<CardLocation> cardLocations) {
        clearBoard();

        for(int i = 0; i < cardLocations.size() && i < cardSlots.size(); i++){
            CardLocation cardLocation = cardLocations.get(i);
            Card card = cardLocation.getCard();

            displayCardInBoard(i, card);
        }
    }

    /**
     * Crée la zone centrale affichant la main du joueur.
     */
    private GridPane createHandArea() {
        GridPane handGrid = new GridPane();
        handGrid.setAlignment(Pos.CENTER);
        handGrid.setHgap(10);
        handGrid.setVgap(10);
        handGrid.setPadding(new Insets(20));

        cardLabel = new Label("Tes cartes" );
        cardLabel.setStyle("-fx-font-size: 40px; ");
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(Double.MAX_VALUE);
        handGrid.add(cardLabel, 0, 0, 5, 1);

        // Chargement initial des dos de cartes
        Image imageVerso = null;
        InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
        if (is != null) {
            imageVerso = new Image(is);
        } else {
            System.err.println("Fichier carte_verso.png non trouvé !");
        }

        // Création des emplacements (9 max pour une main)
        for (int i = 0; i < 9; i++) {
            ImageView cardView = new ImageView(imageVerso);
            cardView.setFitWidth(100);
            cardView.setFitHeight(150);
            cardView.setPreserveRatio(true);

            int column = i % 5;
            int line = (i / 5) + 1;
            handGrid.add(cardView, column, line);

            cardViews.add(cardView);
        }

        return handGrid;
    }

    /**
     * Marque une carte comme révélée pour qu'elle ne soit plus affichée dans la main du joueur.
     * @param card La carte à masquer.
     */
    public void markCardAsRevealed(Card card) {
        if (card == null) return;

        revealedCards.add(card);
        System.out.println("DEBUG: Carte " + card.getValue() + " marquée comme révélée");

        updateHandDisplay();
    }

    /**
     * Réinitialise la liste des cartes révélées (au début d'un nouveau tour).
     */
    public void resetRevealedCards() {
        revealedCards.clear();
    }

    /**
     * Cache visuellement toutes les cartes de la main.
     */
    public void hideAllHandCards() {
        for (ImageView cardView : cardViews) {
            cardView.setVisible(false);
            cardView.setImage(null);
        }
    }

    /**
     * Met à jour la main du joueur courant.
     * @param hand La liste des cartes de la main.
     */
    public void setCurrentHand(List<Card> hand) {
        this.currentHand = new ArrayList<>(hand);
        updateHandDisplay();
    }

    /**
     * Rafraîchit l'affichage de la main en fonction de l'état de visibilité et des cartes révélées.
     */
    public void updateHandDisplay() {
        GridPane handGrid = (GridPane) gameRoot.getCenter();
        // Nettoie les anciennes images (sauf le titre)
        handGrid.getChildren().removeIf(node ->
                node instanceof ImageView && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        Image imageVerso = loadCardBackImage();
        if (imageVerso == null) {
            System.err.println("Image verso non trouvée !");
            return;
        }

        // Filtre les cartes à afficher (exclut celles qui sont sur le board)
        List<Card> cardsToDisplay = new ArrayList<>();
        for (Card card : currentHand) {
            if (!revealedCards.contains(card)) {
                cardsToDisplay.add(card);
            }
        }
        
        // Affiche les cartes
        for (int i = 0; i < cardsToDisplay.size() && i < 9; i++) {
            Card card = cardsToDisplay.get(i);
            ImageView cardView;

            if (areCardsVisible) {
                // Face visible
                try {
                    InputStream is = getClass().getResourceAsStream(card.getImagePath());
                    if (is != null) {
                        cardView = new ImageView(new Image(is));
                    } else {
                        cardView = new ImageView(imageVerso);
                    }
                } catch (Exception e) {
                    cardView = new ImageView(imageVerso);
                }
            } else {
                // Face cachée
                cardView = new ImageView(imageVerso);
            }

            cardView.setFitWidth(100);
            cardView.setFitHeight(150);
            cardView.setPreserveRatio(true);
            cardView.setUserData(card);

            int column = i % 5;
            int row = (i / 5) + 1;
            handGrid.add(cardView, column, row);
        }
    }

    private Image loadCardBackImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTitle(String playerName) {
        if (cardLabel != null) {
            cardLabel.setText("Cartes de " + playerName);
        }
    }

    /**
     * Crée le bouton d'accès aux règles en haut à droite.
     */
    private HBox createRulesButton() {
        HBox help = new HBox(15);
        help.setAlignment(Pos.TOP_RIGHT);
        help.setPadding(new Insets(10));

        rulesButton = new Button("Règles");
        rulesButton.setOnMouseEntered(e -> rulesButton.setStyle("-fx-background-color: #5C4C38;"));
        rulesButton.setOnMouseExited(e -> rulesButton.setStyle("-fx-background-color: #8B7355;"));

        help.getChildren().add(rulesButton);

        return help;
    }

    /**
     * Crée la zone de gauche contenant la liste des adversaires.
     */
    private VBox createOpponentArea() {
        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));
        vBox.setMinWidth(220);

        Label explanation = new Label("Choisis un joueur à qui voir une carte");
        explanation.setPrefWidth(200);
        explanation.setStyle("-fx-font-size: 16px; ");
        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(explanation);

        for (int i = 1; i <= nombreJoueurs; i++) {
            HBox opponentContainer = createOpponentContainer(i);
            vBox.getChildren().add(opponentContainer);
            opponentContainers.put(i, opponentContainer);
        }

        updateOpponentButtons();

        return vBox;
    }

    /**
     * Crée le conteneur pour un adversaire spécifique (Nom + Flèches d'action).
     */
    private HBox createOpponentContainer(int adversaireNumero) {
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        Button playerName = new Button("Joueur " + adversaireNumero);
        playerName.setOnMouseEntered(e -> playerName.setStyle("-fx-background-color: #5C4C38;"));
        playerName.setOnMouseExited(e -> playerName.setStyle("-fx-background-color: #8B7355;"));
        playerName.setAlignment(Pos.CENTER);
        playerName.setMaxWidth(120);
        playerName.setMinWidth(120);
        opponentButtons.put(adversaireNumero, playerName);

        VBox arrowButtons = new VBox(2);
        arrowButtons.setAlignment(Pos.CENTER);
        arrowButtons.setVisible(false); // Caché par défaut

        Button upButton = new Button("▲");
        upButton.setOnMouseEntered(e -> upButton.setStyle("-fx-background-color: #5C4C38;"));
        upButton.setOnMouseExited(e -> upButton.setStyle("-fx-background-color: #8B7355;"));
        upButton.setMinSize(65,40);
        upArrowButtons.put(adversaireNumero, upButton);

        Button downButton = new Button("▼");
        downButton.setOnMouseEntered(e -> downButton.setStyle("-fx-background-color: #5C4C38;"));
        downButton.setOnMouseExited(e -> downButton.setStyle("-fx-background-color: #8B7355;"));
        downButton.setMinSize(65,40);
        downArrowButtons.put(adversaireNumero, downButton);

        arrowButtons.getChildren().addAll(upButton, downButton);
        arrowContainers.put(adversaireNumero, arrowButtons);

        hBox.getChildren().addAll(playerName, arrowButtons);

        return hBox;
    }


    /**
     * Crée la zone de droite avec les actions personnelles du joueur.
     */
    private VBox createPersonalArea(){
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.setPadding(new Insets(5));

        int maxWidht = 145;

        Label explanation = new Label("Tes propres actions");
        explanation.setPrefWidth(maxWidht);
        explanation.setStyle("-fx-font-size: 18px; ");
        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(explanation);

        printButton = new Button("Voir mes cartes");
        printButton.setPrefWidth(maxWidht);
        printButton.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        printButton.setOnMouseEntered(e -> printButton.setStyle("-fx-background-color: #5C4C38;"));
        printButton.setOnMouseExited(e -> printButton.setStyle("-fx-background-color: #8B7355;"));
        printButton.setAlignment(Pos.CENTER);
        printButton.setWrapText(true);

        smallestCard = new Button("Montrer sa plus petite carte");
        smallestCard.setPrefWidth(maxWidht);
        smallestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        smallestCard.setOnMouseEntered(e -> smallestCard.setStyle("-fx-background-color: #5C4C38;"));
        smallestCard.setOnMouseExited(e -> smallestCard.setStyle("-fx-background-color: #8B7355;"));
        smallestCard.setAlignment(Pos.CENTER);
        smallestCard.setWrapText(true);

        largestCard = new Button("Montrer sa plus grande carte");
        largestCard.setPrefWidth(maxWidht);
        largestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        largestCard.setOnMouseEntered(e -> largestCard.setStyle("-fx-background-color: #5C4C38;"));
        largestCard.setOnMouseExited(e -> largestCard.setStyle("-fx-background-color: #8B7355;"));
        largestCard.setAlignment(Pos.CENTER);
        largestCard.setWrapText(true);

        vBox.getChildren().addAll(printButton, largestCard, smallestCard);

        return vBox;
    }

    /**
     * Affiche les flèches d'action pour un joueur spécifique.
     */
    public void showArrowsForPlayer(int playerId) {
        hideAllArrows();
        VBox arrows = arrowContainers.get(playerId);
        if (arrows != null) {
            arrows.setVisible(true);
        }
    }

    public void hideAllArrows() {
        for (VBox arrows : arrowContainers.values()) {
            arrows.setVisible(false);
        }
    }

    public void clearBoard() {
        for (int i = 0; i < cardSlots.size(); i++) {
            clearSlot(i);
        }
    }

    public void clearSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < cardSlots.size()) {
            StackPane slot = cardSlots.get(slotIndex);
            slot.getChildren().clear();
            // Reset style
            slot.setStyle(
                    "-fx-border-color: #8B7355;" +
                    "-fx-border-style: dashed;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-color: #F5E6D3;" +
                    "-fx-background-radius: 8;" +
                    "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
            );
            cardImages.remove(slotIndex);
        }
    }

    public void updatePlayerName(int playerId, String newName) {
        Button btn = opponentButtons.get(playerId);
        if (btn != null) {
            btn.setText(newName);
        }
    }

    public void updateAllPlayerNames(Map<Integer, String> playerNames) {
        for (Map.Entry<Integer, String> entry : playerNames.entrySet()) {
            updatePlayerName(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Met à jour la visibilité des boutons adverses (cache le joueur courant).
     */
    private void updateOpponentButtons() {
        hideAllArrows();
        for (int playerId = 0; playerId <= nombreJoueurs; playerId++) {
            HBox container = opponentContainers.get(playerId);
            if(container != null) {
                if (playerId == actualPlayer) {
                    container.setVisible(false);
                    container.setManaged(false);
                } else{
                    container.setVisible(true);
                    container.setManaged(true);
                }
            }
        }
    }

    public void setActualPlayer(int newActualPlayer){
        if (newActualPlayer < 1 || newActualPlayer > nombreJoueurs) {
            System.err.println("ERREUR: Joueur invalide: " + newActualPlayer);
            return;
        }
        this.actualPlayer = newActualPlayer;
        updateOpponentButtons();
    }

    public void setCardsVisible(boolean visible) {
        this.areCardsVisible = visible;
        updateHandDisplay();
    }

    public boolean areCardsVisible() {
        return areCardsVisible;
    }

    // --- Getters pour les contrôleurs ---

    public Button getOpponentButton(int playerId) {
        return opponentButtons.get(playerId);
    }

    public StackPane getRoot() {return rootStack;}
    public Button getRulesButton(){ return rulesButton;}
    public Button getDrawPileButton(){return  drawPileButton;}
    public Button getTrioButton(){return  trioButton;}
    public Button getPrintButton(){return  printButton;}
    public Button getSmallestButton(){return  smallestCard;}
    public Button getlargestButton(){return  largestCard;}
    public Label getTitleLabel(){return cardLabel;}
    public Button getUpArrowButton(int playerId){return upArrowButtons.get(playerId);}
    public Button getDownArrowButton(int playerId){return downArrowButtons.get(playerId);}

}
