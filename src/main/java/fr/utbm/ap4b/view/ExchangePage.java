package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExchangePage {

    private BorderPane root;
    private Consumer<Card> onCardSelected;
    private Card selectedCard = null;
    private Button confirmButton;
    private Button revealButton;
    private boolean areCardsVisible = false;
    private Map<Card, ImageView> cardViews = new HashMap<>();
    private Image cardBackImage;

    public ExchangePage(String playerName, String teammateName, List<Card> hand) {
        loadResources();
        createView(playerName, teammateName, hand);
    }

    private void loadResources() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
            if (is != null) {
                cardBackImage = new Image(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createView(String playerName, String teammateName, List<Card> hand) {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5E6D3;");

        // --- En-tête ---
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label titleLabel = new Label("PHASE D'ÉCHANGE");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #5C4C38;");

        Label instructionLabel = new Label(playerName + ", choisis une carte à donner à " + teammateName);
        instructionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #8B7355;");

        // Bouton pour révéler les cartes
        revealButton = new Button("Voir mes cartes");
        revealButton.setStyle("-fx-font-size: 16px;");
        revealButton.setOnAction(e -> toggleCardsVisibility());

        headerBox.getChildren().addAll(titleLabel, instructionLabel, revealButton);
        root.setTop(headerBox);

        // --- Main du joueur (Cartes) ---
        FlowPane handPane = new FlowPane();
        handPane.setAlignment(Pos.CENTER);
        handPane.setHgap(20);
        handPane.setVgap(20);
        handPane.setPadding(new Insets(30));

        for (Card card : hand) {
            ImageView cardView = createCardView(card);
            cardViews.put(card, cardView);
            
            // Gestion de la sélection
            cardView.setOnMouseClicked(e -> {
                if (!areCardsVisible) return; // Impossible de sélectionner si caché

                selectCard(card, cardView, handPane);
            });

            handPane.getChildren().add(cardView);
        }

        root.setCenter(handPane);

        // --- Bas (Bouton Valider) ---
        VBox bottomBox = new VBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        confirmButton = new Button("Valider l'échange");
        confirmButton.setStyle("-fx-font-size: 18px; -fx-background-color: #2E8B57;");
        confirmButton.setDisable(true); // Désactivé tant qu'aucune carte n'est choisie

        confirmButton.setOnAction(e -> {
            if (selectedCard != null && onCardSelected != null) {
                onCardSelected.accept(selectedCard);
            }
        });

        bottomBox.getChildren().add(confirmButton);
        root.setBottom(bottomBox);
    }

    private void toggleCardsVisibility() {
        areCardsVisible = !areCardsVisible;
        revealButton.setText(areCardsVisible ? "Cacher mes cartes" : "Voir mes cartes");

        for (Map.Entry<Card, ImageView> entry : cardViews.entrySet()) {
            Card card = entry.getKey();
            ImageView view = entry.getValue();
            
            if (areCardsVisible) {
                // Afficher la face
                try {
                    InputStream is = getClass().getResourceAsStream(card.getImagePath());
                    if (is != null) {
                        view.setImage(new Image(is));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Afficher le dos
                view.setImage(cardBackImage);
                // Désélectionner si on cache
                view.setEffect(null);
                view.setScaleX(1.0);
                view.setScaleY(1.0);
            }
        }
        
        if (!areCardsVisible) {
            selectedCard = null;
            confirmButton.setDisable(true);
        }
    }

    private void selectCard(Card card, ImageView cardView, FlowPane parent) {
        // Reset styles pour toutes les cartes
        parent.getChildren().forEach(node -> {
            node.setEffect(null);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });

        // Appliquer le style de sélection
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.GOLD);
        borderGlow.setWidth(40);
        borderGlow.setHeight(40);
        borderGlow.setSpread(0.6); // Rend l'ombre plus dense, comme une bordure
        
        cardView.setEffect(borderGlow);
        cardView.setScaleX(1.2); // Agrandir
        cardView.setScaleY(1.2);
        
        selectedCard = card;
        confirmButton.setDisable(false);
    }

    private ImageView createCardView(Card card) {
        ImageView cardView = new ImageView(cardBackImage); // Dos par défaut
        
        cardView.setFitWidth(100);
        cardView.setFitHeight(150);
        cardView.setPreserveRatio(true);
        
        // Effet de survol (seulement si visible)
        cardView.setOnMouseEntered(e -> {
            if (areCardsVisible && selectedCard != card) {
                cardView.setScaleX(1.1);
                cardView.setScaleY(1.1);
            }
        });
        cardView.setOnMouseExited(e -> {
            if (areCardsVisible && selectedCard != card) {
                cardView.setScaleX(1.0);
                cardView.setScaleY(1.0);
            }
        });

        return cardView;
    }

    public void setOnCardSelected(Consumer<Card> onCardSelected) {
        this.onCardSelected = onCardSelected;
    }

    public BorderPane getRoot() {
        return root;
    }
}
