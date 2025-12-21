package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Vue principale du jeu Trio avec JavaFX
 * Responsabilités : affichage et interaction utilisateur
 */
public class GameMainPage {

    // Composants graphiques principaux
    private Label labelMessage; // Messages d'information
    private BorderPane root; // Conteneur principal
    private Button rulesButton; //Bouton menant à la page de règle
    private HBox bottomBoardContainer; //Board montrant cartes retournées
    private BorderPane bottomContainer;
    private Button trioButton;
    private Button drawPileButton;


    public GameMainPage(){
        //copie et changer nom fonction
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createHandArea());
        root.setTop(createRulesButton());
        root.setBottom(createBoardContainer());
    }

    /**
     * Crée la zone des cases en bas de l'écran
     */
    private BorderPane createBoardContainer(){
        bottomContainer = new BorderPane();

        drawPileButton = new Button("Draw Pile");
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
            StackPane slot = createSlot(i);
            bottomBoardContainer.getChildren().add(slot);
        }

        BorderPane.setAlignment(drawPileButton, Pos.CENTER);
        bottomContainer.setLeft(drawPileButton);
        BorderPane.setAlignment(trioButton, Pos.CENTER);
        bottomContainer.setCenter(bottomBoardContainer);
        bottomContainer.setRight(trioButton);

        return bottomContainer;
    }

    /**
     * Renvoie le bouton de la pioche
     */
    public Button getDrawPileButton(){return  drawPileButton;}

    /**
     * Renvoie le bouton de la page des trios
     */
    public Button getTrioButton(){return  trioButton;}

    /**
     * Crée une case individuelle (placeholder pour future carte)
     */
    private StackPane createSlot(int index) {
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
        handGrid.setStyle("-fx-background-color: #E2CAA2;");

        //Label d'affiche des caryes
        Label cardLabel = new Label("Tes cartes");
        cardLabel.setStyle("-fx-font-size: 40px; -fx-background-color: #E2CAA2;");
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

//    /**
//     * Crée la zone d'informations (score, messages)
//     */
//    private VBox createInfoArea() {
//        VBox infoBox = new VBox(10); //Separation de 10px entre chaque composant
//        infoBox.setAlignment(Pos.CENTER);
//        infoBox.setPadding(new Insets(10));
//
//        labelMessage = new Label("Bienvenue au jeu Trio !");
//        labelMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
//
//        infoBox.getChildren().addAll(labelMessage);
//
//        return infoBox;
//    }

    /**
     * Crée la zone des contrôles (boutons)
     */
    private HBox createRulesButton() {
        HBox help = new HBox(15);
        help.setAlignment(Pos.TOP_RIGHT);
        help.setPadding(new Insets(10));

        // Bouton de contrôle
        rulesButton = new Button("Rules");

        rulesButton.setOnMouseEntered(e -> rulesButton.setStyle("-fx-background-color: #5C4C38;"));
        rulesButton.setOnMouseExited(e -> rulesButton.setStyle("-fx-background-color: #8B7355;"));

        help.getChildren().add(rulesButton);

        return help;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getRulesButton(){ return rulesButton;}

    /**
     * Affiche un message à l'utilisateur
     */
    public void afficherMessage(String message) {
        labelMessage.setText(message);
    }

}
