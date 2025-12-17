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

    // Composants graphiques principaux
    private GridPane handGrid;       // Grille pour les cartes
    private Label labelMessage;        // Messages d'information
    private Button btnNewGame;  // Bouton de contrôle
    private BorderPane root;// Conteneur principal

    // Référence au contrôleur
    private TrioController controller;

    public TrioView(){
        //copie et changer nom fonction
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createHandArea());
        root.setTop(createInfoArea());
        root.setBottom(createControlArea());
    }

    /**
     * Crée la zone de pioche
     */
    private GridPane createHandArea() {
        handGrid = new GridPane();
        handGrid.setAlignment(Pos.CENTER);
        handGrid.setHgap(10); // Espace horizontal de 10 px entre les colonnes de la grille
        handGrid.setVgap(10); // Espace vertical de 10 px entre les lignes de la grille
        handGrid.setPadding(new Insets(20));

        //Label d'affiche des caryes
        Label cardLabel = new Label("Tes cartes");
        cardLabel.setStyle("-fx-font-size: 40px;");
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

        // Style du plateau
        handGrid.setStyle("-fx-background-color: #f0f0f0;");

        return handGrid;
    }

    /**
     * Crée la zone d'informations (score, messages)
     */
    private VBox createInfoArea() {
        VBox infoBox = new VBox(10); //Separation de 10px entre chaque composant
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(10));

        labelMessage = new Label("Bienvenue au jeu Trio !");
        labelMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        infoBox.getChildren().addAll(labelMessage);

        return infoBox;
    }

    /**
     * Crée la zone des contrôles (boutons)
     */
    private HBox createControlArea() {
        HBox controles = new HBox(15);
        controles.setAlignment(Pos.CENTER);
        controles.setPadding(new Insets(10));

        btnNewGame = new Button("Nouvelle Partie");
        btnNewGame.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
//        btnNewGame.setOnAction(e -> {
//            if (controller != null) {
//                controller.nouvellePartie();
//            }
//        }); mettre dans le controlleur plus tard

        controles.getChildren().add(btnNewGame);

        return controles;
    }

    public BorderPane getRoot() {
        return root;
    }

    /**
     * Met à jour l'affichage du plateau
     * Appelée par le contrôleur quand le modèle change
     */
    public void afficherPlateau(/* List<Carte> cartes */) {
        handGrid.getChildren().clear();

        // Exemple : afficher 12 cartes en grille 3x4
        int colonne = 0;
        int ligne = 0;

        // for (Carte carte : cartes) {
        //     Button btnCarte = creerBoutonCarte(carte);
        //     plateauGrid.add(btnCarte, colonne, ligne);
        //
        //     colonne++;
        //     if (colonne > 3) {
        //         colonne = 0;
        //         ligne++;
        //     }
        // }

        // Exemple de boutons vides pour visualiser la structure
        for (int i = 0; i < 12; i++) {
            Button btnCarte = new Button("Carte " + (i+1));
            btnCarte.setPrefSize(120, 180);
            btnCarte.setStyle("-fx-font-size: 12px;");

            int index = i;
            btnCarte.setOnAction(e -> {
                if (controller != null) {
                    // controller.carteSelectionnee(index);
                }
            });

            handGrid.add(btnCarte, i % 4, i / 4);
        }
    }

    /**
     * Affiche un message à l'utilisateur
     */
    public void afficherMessage(String message) {
        labelMessage.setText(message);
    }

    /**
     * Crée un bouton pour une carte (exemple)
     */
    private Button creerBoutonCarte(/* Carte carte */) {
        Button btn = new Button(/* carte.toString() */);
        btn.setPrefSize(120, 180);

        // Style selon l'état de la carte
        btn.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #333; " +
                "-fx-border-width: 2px; " +
                "-fx-font-size: 12px;");

        // Effet hover
        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #e0e0e0; " +
                        "-fx-border-color: #333; " +
                        "-fx-border-width: 2px;"));

        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #333; " +
                        "-fx-border-width: 2px;"));

        btn.setOnAction(e -> {
            if (controller != null) {
                // controller.carteSelectionnee(carte);
            }
        });

        return btn;
    }
}
