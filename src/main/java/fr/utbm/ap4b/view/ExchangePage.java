package fr.utbm.ap4b.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

public class ExchangePage {

    private final int nombreCartes;
    private final int numeroJoueur;
    private final String nomJoueur;
    private Button validButton;
    private BorderPane root;

    public ExchangePage(int  nombreCartes, int numeroJoueur, String nomJoueur){
        this.nombreCartes = nombreCartes;
        this.numeroJoueur = numeroJoueur;
        this.nomJoueur = nomJoueur;
        showScreen();
    }

    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createCardsArea(nombreCartes));
        root.setTop(titleArea(numeroJoueur, nomJoueur));
    }

    private VBox titleArea(int numeroJoueur, String nomJoueur){
        VBox titleVBox = new VBox(40);
        titleVBox.setAlignment(Pos.CENTER);
        titleVBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Joueur " +  numeroJoueur + "(" + nomJoueur + ")");
        titleLabel.setStyle("-fx-font-size: 40px;");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label explanationLabel = new Label("Clique sur la carte que tu veux échanger");
        explanationLabel.setStyle("-fx-font-size: 25px;");
        explanationLabel.setAlignment(Pos.CENTER);
        explanationLabel.setMaxWidth(Double.MAX_VALUE);

        titleVBox.getChildren().addAll(titleLabel, explanationLabel);

        return titleVBox;
    }

    private GridPane createCardsArea(int nombreCartes){
        GridPane cardsPane = new GridPane();
        cardsPane.setAlignment(Pos.CENTER);
        cardsPane.setHgap(10);
        cardsPane.setVgap(10);
        cardsPane.setPadding(new Insets(20));

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
            cardsPane.add(cardView, column, line);
        }

        validButton = new Button("Valider");
        validButton.setVisible(true);
        validButton.setOnMouseEntered(e -> validButton.setStyle("-fx-background-color: #5C4C38;"));
        validButton.setOnMouseExited(e -> validButton.setStyle("-fx-background-color: #8B7355;"));
        GridPane.setColumnSpan(validButton, 5);
        GridPane.setHalignment(validButton, HPos.CENTER);
        cardsPane.add(validButton, 0, 3);

        return cardsPane;
    }

    public BorderPane getRoot(){return root;}
}
