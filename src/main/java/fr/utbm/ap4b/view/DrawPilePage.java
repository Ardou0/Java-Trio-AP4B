package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class DrawPilePage {

    private BorderPane root;// Conteneur principal
    private Button endBtn;
    private Consumer<Card> cardSelectionHandler; //Permet de communiquer avec le contrôleur
    private List<Card> availableCards;
    private List<Card> cards;
    private Card selectedCard = null;
    private Button selectButton = null;

    public DrawPilePage(List<Card> availableCards) {
        this.availableCards = availableCards;
        showScreen();
    }

    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createDrawPileArea());
        root.setTop(createEndArea());
    }

    private VBox createDrawPileArea(){
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(10));

        //Label d'affiche des cartes
        Label drawPileLabel = new Label("Pioche");
        drawPileLabel.setStyle("-fx-font-size: 40px;");
        drawPileLabel.setAlignment(Pos.CENTER);

        Label explanationLabel = new Label("Clique sur la carte que tu veux dévoiler");
        explanationLabel.setStyle("-fx-font-size: 25px;");
        explanationLabel.setAlignment(Pos.CENTER);

        //Grille pour les cartes verso
        GridPane drawPilePane = createCardsGrid();

        //Zone pour le bouton de selection (initialement vide)
        HBox selectionZone = new HBox();
        selectionZone.setAlignment(Pos.CENTER);
        selectionZone.setPadding(new Insets(20,0,0,0));

        mainContainer.getChildren().addAll(drawPileLabel, explanationLabel, drawPilePane, selectionZone);

        return mainContainer;
    }

    private GridPane createCardsGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        //Charge l'image verso
        Image imageVerso = loadCardBackImage();
        if(imageVerso == null){
            Label errorLabel = new Label("Image verso non trouvée");
            errorLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
            gridPane.add(errorLabel, 0, 0);
            return gridPane;
        }

        //Crée une carte verso pour chaque carte disponible
        for(int i = 0; i < availableCards.size(); i++){
            Card card = availableCards.get(i);

            //Crée l'image verso
            ImageView cardView = new ImageView(imageVerso);
            cardView.setFitWidth(100);
            cardView.setFitHeight(150);
            cardView.setPreserveRatio(true);

            // Rend cliquable
            final int cardIndex = i;
            cardView.setOnMouseClicked(event -> handleCardBackClick(card, cardView, cardIndex));

            //Style au survol
            cardView.setOnMouseEntered(event -> {
                if(selectedCard == null || selectedCard != card){
                    cardView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 0);");
                    cardView.setScaleX(1.05);
                    cardView.setScaleY(1.05);
                }
            });

            cardView.setOnMouseExited(event -> {
                if (selectedCard == null || selectedCard != card) {
                    cardView.setStyle("");
                    cardView.setScaleX(1.0);
                    cardView.setScaleY(1.0);
                }
            });

            int column = i % 5;
            int line = (i / 5) + 1;
            gridPane.add(cardView, column, line);
        }
        return gridPane;
    }

    private Image loadCardBackImage(){
        try{
            InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
            if (is != null){
                return new Image(is);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void handleCardBackClick(Card card, ImageView cardView, int cardIndex){
        System.out.println("Carte verso cliquée (index " + cardIndex + ")");

        // Réinitialise la sélection précédente
        resetPreviousSelection();

        //Marque cette carte comme sélectionnée
        selectedCard = card;

        // Mettre en évidence la carte sélectionnée
        cardView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,100,255,0.8), 15, 0, 0, 0);");
        cardView.setScaleX(1.1);
        cardView.setScaleY(1.1);

        // Affiche le bouton de sélection
        showSelectionButton();
    }

    private void resetPreviousSelection(){
        //Réinitialise tous les styles de cartes
        GridPane gridPane = (GridPane) ((VBox) root.getCenter()).getChildren().get(2);

        for (int i = 0; i < availableCards.size(); i++){
            ImageView cardView = (ImageView) gridPane.getChildren().get(i);
            cardView.setStyle("");
            cardView.setScaleX(1.0);
            cardView.setScaleY(1.0);
        }
        selectedCard = null;

        // Cache le bouton de sélection
        HBox selectionZone = (HBox) ((VBox) root.getCenter()).getChildren().get(3);
        selectionZone.getChildren().clear();
    }

    private void showSelectionButton(){
        HBox selectionZone = (HBox) ((VBox) root.getCenter()).getChildren().get(3);
        selectionZone.getChildren().clear();
        if(selectedCard != null){
            //Bouton pour choisir la carte
            selectButton = new Button("Choisir cette carte");
            selectButton.setOnMouseEntered(event -> {
                selectButton.setStyle("-fx-background-color: #5C4C38; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 15 30; " +
                        "-fx-background-radius: 10;");
            });
            selectButton.setOnMouseExited(e -> selectButton.setStyle("-fx-background-color: #8B7355;"));

            selectButton.setOnAction(e -> {
                if (cardSelectionHandler != null) {
                    System.out.println("Carte choisie: " + selectedCard.getValue());

                    // Cache visuellement la carte dans la pioche
                    hideSelectedCard();

                    cardSelectionHandler.accept(selectedCard);
                }
            });

            selectionZone.getChildren().add(selectButton);
        }
    }

    private void hideSelectedCard(){
        GridPane gridPane = (GridPane) ((VBox) root.getCenter()).getChildren().get(2);

        for(int i =  0; i < availableCards.size(); i++){
            if(availableCards.get(i) == selectedCard){
                ImageView cardView = (ImageView) gridPane.getChildren().get(i);

                cardView.setVisible(false);
                cardView.setManaged(false);
                break;
            }
        }
    }

    public void setCardSelectionHandler(Consumer<Card> handler) {
        this.cardSelectionHandler = handler;
    }

    private HBox createEndArea(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(10));

        endBtn = new Button("Retour");
        // Style du bouton de fermeture
        endBtn.setStyle("-fx-background-color: #e74c3c;");
        endBtn.setOnMouseEntered(e -> endBtn.setStyle("-fx-background-color: #c0392b;"));
        endBtn.setOnMouseExited(e -> endBtn.setStyle("-fx-background-color: #e74c3c;"));

        hBox.getChildren().add(endBtn);

        return hBox;
    }

    public Button  getEndBtn() {return endBtn;}

    public BorderPane getRoot() {
        return root;
    }
}
