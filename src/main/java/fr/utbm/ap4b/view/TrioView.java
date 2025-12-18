package fr.utbm.ap4b.view;
import fr.utbm.ap4b.controller.*;

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
public class TrioView  {

    private Label labelMessage;        // Messages d'information
    private BorderPane root;// Conteneur principal


    public TrioView(){
        //copie et changer nom fonction
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createHandArea());
        root.setTop(createRulesButton());
        root.setStyle("-fx-background-color: #E2CAA2;");
    }

    /**
     * Crée la zone de pioche
     */
    private GridPane createHandArea() {
        // Composants graphiques principaux
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
        Button btnNewGame = new Button("Rules");
        btnNewGame.setStyle("-fx-padding: 10px 20px;" +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-radius: 10px;" +
                "-fx-border-color:black; " +
                "-fx-font-size:14;");
//        btnNewGame.setOnAction(e -> {
//            if (controller != null) {
//                controller.rulesPage();
//            }
//        }); mettre dans le controlleur plus tard

        help.getChildren().add(btnNewGame);

        return help;
    }

    public BorderPane getRoot() {
        return root;
    }

    /**
     * Affiche un message à l'utilisateur
     */
    public void afficherMessage(String message) {
        labelMessage.setText(message);
    }

}
