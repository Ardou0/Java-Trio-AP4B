package fr.utbm.ap4b.view;

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

public class DrawPilePage {

    private BorderPane root;// Conteneur principal
    private Button endBtn;
    private final int nombreCartes;

    public DrawPilePage(int  nombreCartes){
        this.nombreCartes = nombreCartes;
        showScreen();
    }

    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createDrawPileArea(nombreCartes));
        root.setTop(createEndArea());
    }

    private GridPane createDrawPileArea(int nombreCartes){
        GridPane drawPilePane = new GridPane();
        drawPilePane.setAlignment(Pos.CENTER);
        drawPilePane.setHgap(10);
        drawPilePane.setVgap(10);
        drawPilePane.setPadding(new Insets(20));
        VBox vBox = new VBox(30);

        //Label d'affiche des cartes
        Label drawPileLabel = new Label("Pioche");
        drawPileLabel.setStyle("-fx-font-size: 40px;");
        drawPileLabel.setAlignment(Pos.CENTER);
        drawPileLabel.setMaxWidth(Double.MAX_VALUE);

        Label explanationLabel = new Label("Clique sur la carte que tu veux dévoiler");
        explanationLabel.setStyle("-fx-font-size: 25px;");
        explanationLabel.setAlignment(Pos.CENTER);
        explanationLabel.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().addAll(drawPileLabel, explanationLabel);
        drawPilePane.add(vBox, 0, 0, 5, 1);

        //Charger l'image verso pour les afficher
        Image imageVerso = null;

        InputStream is = getClass().getResourceAsStream("/images/carte_verso.png");
        if (is != null) {
            imageVerso = new Image(is);
        } else {
            System.err.println("Fichier carte_verso.png non trouvé !");
        }

        for (int i = 0; i < nombreCartes; i++) {
            ImageView cardView = new ImageView(imageVerso);

            // Définit la taille
            cardView.setFitWidth(100);
            cardView.setFitHeight(150);
            cardView.setPreserveRatio(true);

            int column = i % 5;
            int line = (i / 5) + 1;
            drawPilePane.add(cardView, column, line);
        }


        return drawPilePane;
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
