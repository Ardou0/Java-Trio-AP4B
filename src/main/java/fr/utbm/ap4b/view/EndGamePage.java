package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Actor;
import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.JoueurEquipe;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class EndGamePage {

    private BorderPane root;
    private Button replayButton;
    private Button quitButton;

    public EndGamePage(Actor winner, boolean isTeamMode, String winReason, Map<String, List<List<Card>>> allScores) {
        createView(winner, isTeamMode, winReason, allScores);
    }

    private void createView(Actor winner, boolean isTeamMode, String winReason, Map<String, List<List<Card>>> allScores) {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        // Fond beige global
        root.setStyle("-fx-background-color: #F5E6D3;");

        // --- En-tête (Vainqueur) ---
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20, 20, 10, 20));

        Label titleLabel = new Label("FIN DE LA PARTIE");
        titleLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #5C4C38; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        String winnerText;
        if (winner != null) {
            if (isTeamMode && winner instanceof JoueurEquipe) {
                JoueurEquipe teamPlayer = (JoueurEquipe) winner;
                // Format demandé : "Joueur 1 et Joueur 2 ont gagné"
                winnerText = teamPlayer.getName() + " et " + teamPlayer.getTeammate().getName() + " ont gagné !";
            } else {
                winnerText = winner.getName() + " a gagné !";
            }
        } else {
            winnerText = "Match nul !";
        }

        Label winnerLabel = new Label(winnerText);
        winnerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;"); // Vert victoire
        winnerLabel.setWrapText(true);
        winnerLabel.setTextAlignment(TextAlignment.CENTER);

        Label reasonLabel = new Label("Victoire par : " + winReason);
        reasonLabel.setStyle("-fx-font-size: 20px; -fx-font-style: italic; -fx-text-fill: #8B7355;");

        headerBox.getChildren().addAll(titleLabel, winnerLabel, reasonLabel);
        root.setTop(headerBox);

        // --- Centre (Scores avec cartes) ---
        VBox scoresContainer = new VBox(20);
        scoresContainer.setAlignment(Pos.TOP_CENTER);
        scoresContainer.setPadding(new Insets(10));

        for (Map.Entry<String, List<List<Card>>> entry : allScores.entrySet()) {
            String participantName = entry.getKey();
            List<List<Card>> trios = entry.getValue();

            // Conteneur pour un joueur/équipe
            VBox participantBox = new VBox(10);
            participantBox.setPadding(new Insets(15));
            participantBox.setStyle(
                    "-fx-background-color: #E2CAA2;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #0D1117;" +
                    "-fx-border-width: 2;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
            );
            participantBox.setMaxWidth(800);

            // Nom et nombre de trios
            Label nameLabel = new Label(participantName + " : " + trios.size() + " trio(s)");
            nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0D1117;");
            participantBox.getChildren().add(nameLabel);

            // Zone des trios (FlowPane pour que ça passe à la ligne si beaucoup de trios)
            FlowPane triosFlow = new FlowPane();
            triosFlow.setHgap(20);
            triosFlow.setVgap(10);
            triosFlow.setAlignment(Pos.CENTER_LEFT);

            if (trios.isEmpty()) {
                Label noTrioLabel = new Label("Aucun trio réalisé");
                noTrioLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #5C4C38; -fx-font-size: 16px;");
                triosFlow.getChildren().add(noTrioLabel);
            } else {
                for (List<Card> trio : trios) {
                    if (!trio.isEmpty()) {
                        // Créer un conteneur visuel pour UN trio (3 cartes collées)
                        HBox trioBox = new HBox(2); // Très peu d'espace entre les cartes d'un même trio
                        trioBox.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0, 2, 2);");
                        
                        for (Card card : trio) {
                            ImageView cardView = createCardView(card);
                            trioBox.getChildren().add(cardView);
                        }
                        triosFlow.getChildren().add(trioBox);
                    }
                }
            }

            participantBox.getChildren().add(triosFlow);
            scoresContainer.getChildren().add(participantBox);
        }

        // ScrollPane pour faire défiler si beaucoup de joueurs
        ScrollPane scrollPane = new ScrollPane(scoresContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5E6D3; -fx-background-color: #F5E6D3;");
        // Astuce pour enlever la bordure du ScrollPane
        scrollPane.getStyleClass().add("edge-to-edge"); 
        
        // Centrer le contenu du ScrollPane
        StackPane contentWrapper = new StackPane(scoresContainer);
        contentWrapper.setStyle("-fx-background-color: #F5E6D3;");
        scrollPane.setContent(contentWrapper);

        root.setCenter(scrollPane);

        // --- Bas (Boutons) ---
        HBox buttonsBox = new HBox(30);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20));

        replayButton = createStyledButton("Rejouer", "#2E8B57", "#3CB371");
        quitButton = createStyledButton("Quitter", "#CD5C5C", "#F08080");

        buttonsBox.getChildren().addAll(replayButton, quitButton);
        root.setBottom(buttonsBox);
    }

    private ImageView createCardView(Card card) {
        ImageView cardView = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream(card.getImagePath());
            if (is != null) {
                cardView.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + card.getImagePath());
        }
        
        // Taille réduite pour l'affichage des scores
        cardView.setFitWidth(60);
        cardView.setFitHeight(90);
        cardView.setPreserveRatio(true);
        
        return cardView;
    }

    private Button createStyledButton(String text, String colorBase, String colorHover) {
        Button btn = new Button(text);
        String baseStyle = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8; -fx-background-color: " + colorBase + ";";
        String hoverStyle = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8; -fx-background-color: " + colorHover + ";";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        // Ombre sur le bouton
        btn.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.3)));
        
        return btn;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getReplayButton() {
        return replayButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}
