package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.CardLocation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vue principale du jeu Trio avec JavaFX
 * Responsabilités : affichage et interaction utilisateur
 */
public class GameMainPage {

    // Composants graphiques principaux
    private final int nombreJoueurs;
    private BorderPane root; // Conteneur principal
    private Button rulesButton; //Bouton menant à la page de règle
    private HBox bottomBoardContainer; //Board montrant cartes retournées
    private Button trioButton;
    private Button drawPileButton;
    private int actualPlayer;
    private Button printButton;
    private Button smallestCard;
    private Button largestCard;
    private Label cardLabel;
    private Map<Integer, Button> upArrowButtons = new HashMap<>();
    private Map<Integer, Button> downArrowButtons = new HashMap<>();

    // Maps pour stocker les références aux boutons
    private Map<Integer, Button> opponentButtons = new HashMap<>();
    private Map<Integer, VBox> arrowContainers = new HashMap<>(); // Conteneur des flèches
    private Map<Integer, HBox> opponentContainers = new HashMap<>();

    private List<StackPane> cardSlots = new ArrayList<>(); // Stocke les emplacements des cartes
    private Map<Integer, ImageView> cardImages = new HashMap<>(); // Stocke les images des cartes


    public GameMainPage(int nombreJoueurs, int actualPlayer) {
        this.nombreJoueurs = nombreJoueurs;
        this.actualPlayer = actualPlayer;
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createHandArea());
        root.setTop(createRulesButton());
        root.setBottom(createBoardContainer());
        root.setLeft(createOpponentArea(nombreJoueurs, actualPlayer));
        root.setRight(createPersonalArea());
    }

    /**
     * Crée la zone des cases en bas de l'écran
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

        bottomBoardContainer = new HBox(15); // 15px d'espace entre les cases
        bottomBoardContainer.setAlignment(Pos.CENTER);
        bottomBoardContainer.setPadding(new Insets(20,10, 30, 10));
        bottomBoardContainer.setStyle(
                "-fx-background-color: #E2CAA2;" +
                "-fx-background-radius: 10 10 0 0;" +
                "-fx-border-color: #0D1117;" +
                "-fx-border-width: 3;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);" // Ombre
        );

        // Création de 3 cases et les stocks dans la liste
        cardSlots.clear();
        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            bottomBoardContainer.getChildren().add(slot);
            cardSlots.add(slot);
        }

        // Lier la hauteur des boutons à celle du HBox
        drawPileButton.prefHeightProperty().bind(bottomBoardContainer.heightProperty());
        trioButton.prefHeightProperty().bind(bottomBoardContainer.heightProperty());

        drawPileButton.setPrefWidth(100);
        trioButton.setPrefWidth(100);

        BorderPane.setMargin(drawPileButton, new Insets(0, 10, 0, 0)); // Marge à droite
        BorderPane.setMargin(trioButton, new Insets(0, 0, 0, 10)); // Marge à gauche

        BorderPane.setAlignment(drawPileButton, Pos.CENTER);
        bottomContainer.setLeft(drawPileButton);

        bottomContainer.setCenter(bottomBoardContainer);

        BorderPane.setAlignment(trioButton, Pos.CENTER);
        bottomContainer.setRight(trioButton);

        return bottomContainer;
    }

    /**
     * Crée une case individuelle (placeholder pour future carte)
     */
    public static StackPane createSlot() {
        StackPane slot = new StackPane();

        slot.setPrefSize(100, 150);
        slot.setMinSize(100, 150);
        slot.setMaxSize(100, 150);

        slot.setStyle(
                "-fx-border-color: #8B7355;" +
                "-fx-border-style: dashed;" + // Bordure en pointillés
                "-fx-border-width: 2;" +
                "-fx-border-radius: 8;" +
                "-fx-background-color: #F5E6D3;" +
                "-fx-background-radius: 8;" +
                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);" // Ombre intérieure
        );

        // Désactiver les interactions
        slot.setMouseTransparent(true);

        return slot;
    }

    /**
     * Affiche une carte dans un emplacement spécifique du board
     * @param slotIndex Index de l'emplacement (0, 1 ou 2)
     * @param card Carte à afficher (objet Card du modèle)
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
        slot.getChildren().clear(); // Nettoie l'emplacement

        try{
            //utilie le chemin depuis l'objet Card
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
                // Affiche la valeur en texte si l'image n'est pas trouvée
                System.err.println("Image non trouvée " +  imagePath);
                Label valueLabel = new Label(String.valueOf(card.getValue()));
                slot.getChildren().add(valueLabel);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            // En cas d'erreur, affiche la valeur en texte
            Label valueLabel = new Label(String.valueOf(card.getValue()));
            valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #5C4C38;");
            slot.getChildren().add(valueLabel);
        }
    }

    /**
     * Met à jour l'affichage du board avec les cartes révélées
     * @param cardLocations Liste des CardLocation du modèle
     */
    public void updateBoard(List<CardLocation> cardLocations) {
        //Vide le board
        clearBoard();

        //Affiche les cartes
        for(int i = 0; i < cardLocations.size() && i < cardSlots.size(); i++){
            CardLocation cardLocation = cardLocations.get(i);
            Card card = cardLocation.getCard();

            displayCardInBoard(i, card);
        }
    }

    /**
     * Crée la zone de pioche
     */
    private GridPane createHandArea() {
        // Grille pour les cartes
        GridPane handGrid = new GridPane();
        handGrid.setAlignment(Pos.CENTER);
        handGrid.setHgap(10); // Espace horizontal de 10 px entre les colonnes de la grille
        handGrid.setVgap(10); // Espace vertical de 10 px entre les lignes de la grille
        handGrid.setPadding(new Insets(20));

        //Label d'affiche des cartes
        cardLabel = new Label("Tes cartes" );
        cardLabel.setStyle("-fx-font-size: 40px; ");
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(Double.MAX_VALUE);
        // Label prend 3 colonnes de large sur 1 ligne de haut (colonne_début, ligne_début, nombre_colonnes, nombre_lignes)
        handGrid.add(cardLabel, 0, 0, 5, 1);

        //Charger l'image verso pour les afficher
        Image imageVerso = null;

        InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
        if (is != null) {
            imageVerso = new Image(is);
        } else {
            System.err.println("Fichier carte_verso.png non trouvé !");
        }

        for (int i = 0; i < 9; i++) {
            ImageView cardView = new ImageView(imageVerso);

            // Définit la taille
            cardView.setFitWidth(100);
            cardView.setFitHeight(150);
            cardView.setPreserveRatio(true);

            int column = i % 5;
            int line = (i / 5) + 1;
            handGrid.add(cardView, column, line);
        }

        return handGrid;
    }

    /**
     * Crée la zone des contrôles (boutons)
     */
    private HBox createRulesButton() {
        HBox help = new HBox(15);
        help.setAlignment(Pos.TOP_RIGHT);
        help.setPadding(new Insets(10));

        // Bouton de contrôle
        rulesButton = new Button("Règles");
        rulesButton.setOnMouseEntered(e -> rulesButton.setStyle("-fx-background-color: #5C4C38;"));
        rulesButton.setOnMouseExited(e -> rulesButton.setStyle("-fx-background-color: #8B7355;"));

        help.getChildren().add(rulesButton);

        return help;
    }

    private VBox createOpponentArea(int nombreJoueurs, int actualPlayer) {
        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));

        //Explication des boutons
        Label explanation = new Label("Choisis un joueur à qui voir une carte");
        explanation.setPrefWidth(200);
        explanation.setStyle("-fx-font-size: 18px; ");
        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(explanation);

        for (int i = 0; i < nombreJoueurs; i++) {
            if ((i + 1) != actualPlayer) {
                // Création d'un conteneur pour un adversaire
                HBox opponentContainer = createOpponentContainer(i + 1);
                vBox.getChildren().add(opponentContainer);
                opponentContainers.put(i + 1, opponentContainer);
            }
        }

        return vBox;
    }

    /**
     * Crée un conteneur avec le bouton de l'adversaire et les boutons de flèches
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
        arrowButtons.setVisible(false);

        // Bouton flèche vers le haut
        Button upButton = new Button("▲");
        upButton.setOnMouseEntered(e -> upButton.setStyle("-fx-background-color: #5C4C38;"));
        upButton.setOnMouseExited(e -> upButton.setStyle("-fx-background-color: #8B7355;"));
        upButton.setMinSize(65,40);
        upArrowButtons.put(adversaireNumero, upButton);

        // Bouton flèche vers le bas
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


    private VBox createPersonalArea(){
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.setPadding(new Insets(5));

        int maxWidht = 145;

        //Explication des boutons
        Label explanation = new Label("Tes propres actions");
        explanation.setPrefWidth(maxWidht);
        explanation.setStyle("-fx-font-size: 18px; ");
        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(explanation);

        //The player looks at all his cards
        printButton = new Button("Voir mes cartes");
        printButton.setPrefWidth(maxWidht);
        printButton.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        printButton.setOnMouseEntered(e -> printButton.setStyle("-fx-background-color: #5C4C38;"));
        printButton.setOnMouseExited(e -> printButton.setStyle("-fx-background-color: #8B7355;"));
        printButton.setAlignment(Pos.CENTER);
        printButton.setWrapText(true);

        //The player shows his smallest card
        smallestCard = new Button("Montrer sa plus petite carte");
        smallestCard.setPrefWidth(maxWidht);
        smallestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        smallestCard.setOnMouseEntered(e -> smallestCard.setStyle("-fx-background-color: #5C4C38;"));
        smallestCard.setOnMouseExited(e -> smallestCard.setStyle("-fx-background-color: #8B7355;"));
        smallestCard.setAlignment(Pos.CENTER);
        smallestCard.setWrapText(true);

        //The player shows his largest card
        largestCard = new Button("Montrer sa plus grande carte");
        largestCard.setPrefWidth(maxWidht);
        largestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        largestCard.setOnMouseEntered(e -> largestCard.setStyle("-fx-background-color: #5C4C38;"));
        largestCard.setOnMouseExited(e -> largestCard.setStyle("-fx-background-color: #8B7355;"));
        largestCard.setAlignment(Pos.CENTER);
        largestCard.setWrapText(true);

        vBox.getChildren().addAll(printButton, smallestCard, largestCard);

        return vBox;
    }

    // Affiche les flèches d'un joueur spécifique
    public void showArrowsForPlayer(int playerId) {
        // Cache toutes les flèches d'abord
        hideAllArrows();

        // Affiche les flèches du joueur sélectionné
        VBox arrows = arrowContainers.get(playerId);
        if (arrows != null) {
            arrows.setVisible(true);
        } else {
            System.out.println("DEBUG: No arrows found for playerId: " + playerId);
            System.out.println("DEBUG: arrowContainers keys: " + arrowContainers.keySet());
        }
    }

    // Cache toutes les flèches
    public void hideAllArrows() {
        for (VBox arrows : arrowContainers.values()) {
            arrows.setVisible(false);
        }
    }

    /**
     * Vide tous les emplacements du board
     */
    public void clearBoard() {
        for (int i = 0; i < cardSlots.size(); i++) {
            clearSlot(i);
        }
    }

    /**
     * Vide un emplacement spécifique
     */
    public void clearSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < cardSlots.size()) {
            StackPane slot = cardSlots.get(slotIndex);
            slot.getChildren().clear();

            // Remettre le style par défaut
            slot.setStyle(
                    "-fx-border-color: #8B7355;" +
                    "-fx-border-style: dashed;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-color: #F5E6D3;" +
                    "-fx-background-radius: 8;" +
                    "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
            );

            // Retirer l'image de la map
            cardImages.remove(slotIndex);
        }
    }

    // Getter pour le bouton du joueur
    public Button getOpponentButton(int playerId) {
        return opponentButtons.get(playerId);
    }

    public BorderPane getRoot() {return root;}

    public Button getRulesButton(){ return rulesButton;}

    /**
     * Renvoie le bouton de la pioche
     */
    public Button getDrawPileButton(){return  drawPileButton;}

    /**
     * Renvoie le bouton de la page des trios
     */
    public Button getTrioButton(){return  trioButton;}

    /**
     * Affiche les cartes du joueur
     */
    public Button getPrintButton(){return  printButton;}

    /**
     * Place dans le board la plus petite carte du joueur
     */
    public Button getSmallestButton(){return  smallestCard;}

    /**
     * Place dans le board la plus grande carte du joueur actif
     */
    public Button getlargestButton(){return  largestCard;}

    /**
     * Renvoie le label titre
     */
    public Label getTitleLabel(){return cardLabel;}

    /**
     * Place dans le board la plus grande carte du joueur en paramètre
     */
    public Button getUpArrowButton(int playerId){return upArrowButtons.get(playerId);}

    /**
     * Place dans le board la plus petite carte du joueur en paramètre
     */
    public Button getDownArrowButton(int playerId){return downArrowButtons.get(playerId);}

}
