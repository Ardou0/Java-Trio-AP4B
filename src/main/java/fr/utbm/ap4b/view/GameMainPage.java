package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;

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


    public GameMainPage(int nombreJoueurs){
        this.nombreJoueurs = nombreJoueurs;
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createHandArea());
        root.setTop(createRulesButton());
        root.setBottom(createBoardContainer());
        root.setLeft(createOpponentArea(nombreJoueurs));
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

        // Création de 3 cases
        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            bottomBoardContainer.getChildren().add(slot);
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

    //Retourne la box entière
    public HBox getBottomBoardContainer() {
        return bottomBoardContainer;
    }

    /**
     * Permet d'acceder à l'index d'une case pour lui ajouter une carte
     */
    public StackPane getSlotAt(int index) {
        if (index >= 0 && index < bottomBoardContainer.getChildren().size()) {
            return (StackPane) bottomBoardContainer.getChildren().get(index);
        }
        return null;
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
        Label cardLabel = new Label("Tes cartes");
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

    private VBox createOpponentArea(int nombreJoueurs) {
        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));

        //Explication des boutons
        Label explanation = new Label("Choisis un joueur à qui voir une carte");
        explanation.setPrefWidth(130);
        explanation.setStyle("-fx-font-size: 18px; ");
        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(explanation);

        // Calcul du nombre d'adversaires
        int nombreAdversaires = nombreJoueurs - 1;

        for (int i = 0; i < nombreAdversaires; i++) {
            // Création d'un conteneur pour un adversaire
            Button opponentButton = createOpponentButton(i + 1);
            vBox.getChildren().add(opponentButton);
        }

        return vBox;
    }

    /**
     * Crée un panneau pour un adversaire spécifique
     */
    private Button createOpponentButton(int adversaireNumero) {

        // Nom du joueur
        Button playerName = new Button("Joueur " + adversaireNumero);
        playerName.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        playerName.setOnMouseEntered(e -> playerName.setStyle("-fx-background-color: #5C4C38;"));
        playerName.setOnMouseExited(e -> playerName.setStyle("-fx-background-color: #8B7355;"));
        playerName.setAlignment(Pos.CENTER);
        playerName.setMaxWidth(Double.MAX_VALUE);
        playerName.setWrapText(true); // Permet au texte de passer à la ligne si trop long

        return playerName;
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
        Button printButton = new Button("Voir mes cartes");
        printButton.setPrefWidth(maxWidht);
        printButton.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        printButton.setOnMouseEntered(e -> printButton.setStyle("-fx-background-color: #5C4C38;"));
        printButton.setOnMouseExited(e -> printButton.setStyle("-fx-background-color: #8B7355;"));
        printButton.setAlignment(Pos.CENTER);
        printButton.setWrapText(true);

        //The player shows his smallest card
        Button smallestCard = new Button("Montrer sa plus petite carte");
        smallestCard.setPrefWidth(maxWidht);
        smallestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        smallestCard.setOnMouseEntered(e -> smallestCard.setStyle("-fx-background-color: #5C4C38;"));
        smallestCard.setOnMouseExited(e -> smallestCard.setStyle("-fx-background-color: #8B7355;"));
        smallestCard.setAlignment(Pos.CENTER);
        smallestCard.setWrapText(true);

        //The player shows his largest card
        Button largestCard = new Button("Montrer sa plus grande carte");
        largestCard.setPrefWidth(maxWidht);
        largestCard.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        largestCard.setOnMouseEntered(e -> largestCard.setStyle("-fx-background-color: #5C4C38;"));
        largestCard.setOnMouseExited(e -> largestCard.setStyle("-fx-background-color: #8B7355;"));
        largestCard.setAlignment(Pos.CENTER);
        largestCard.setWrapText(true);

        vBox.getChildren().addAll(printButton, smallestCard, largestCard);

        return vBox;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getRulesButton(){ return rulesButton;}

    /**
     * Renvoie le bouton de la pioche
     */
    public Button getDrawPileButton(){return  drawPileButton;}

    /**
     * Renvoie le bouton de la page des trios
     */
    public Button getTrioButton(){return  trioButton;}
}
