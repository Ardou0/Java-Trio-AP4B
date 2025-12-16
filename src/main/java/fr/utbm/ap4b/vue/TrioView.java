package fr.utbm.ap4b.vue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vue principale du jeu Trio avec JavaFX (pattern MVC)
 * Responsabilités : affichage et interaction utilisateur
 */
public class TrioView extends Application {

    // Composants graphiques principaux
    private GridPane plateauGrid;
    private Label labelScore;
    private Label labelMessage;
    private Button btnNouvellePartie;
    private BorderPane root;

    // Référence au contrôleur
    private TrioController controller;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jeu Trio");

        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Création des différentes zones
        root.setCenter(creerZonePlateau());
        root.setBottom(creerZoneControles());
        root.setTop(creerZoneInfo());

        Scene scene = new Scene(root, 800, 600);

        // Optionnel : ajouter un fichier CSS pour le style
        // scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crée la zone du plateau de jeu
     */
    private GridPane creerZonePlateau() {
        plateauGrid = new GridPane();
        plateauGrid.setAlignment(Pos.CENTER);
        plateauGrid.setHgap(10);
        plateauGrid.setVgap(10);
        plateauGrid.setPadding(new Insets(20));

        // Style du plateau
        plateauGrid.setStyle("-fx-background-color: #f0f0f0;");

        return plateauGrid;
    }

    /**
     * Crée la zone d'informations (score, messages)
     */
    private VBox creerZoneInfo() {
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(10));

        labelScore = new Label("Score: 0");
        labelScore.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        labelMessage = new Label("Bienvenue au jeu Trio !");
        labelMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        infoBox.getChildren().addAll(labelScore, labelMessage);

        return infoBox;
    }

    /**
     * Crée la zone des contrôles (boutons)
     */
    private HBox creerZoneControles() {
        HBox controles = new HBox(15);
        controles.setAlignment(Pos.CENTER);
        controles.setPadding(new Insets(10));

        btnNouvellePartie = new Button("Nouvelle Partie");
        btnNouvellePartie.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
        btnNouvellePartie.setOnAction(e -> {
            if (controller != null) {
                controller.nouvellePartie();
            }
        });

        controles.getChildren().add(btnNouvellePartie);

        return controles;
    }

    /**
     * Attache le contrôleur à la vue
     */
    public void setController(TrioController controller) {
        this.controller = controller;
    }

    /**
     * Met à jour l'affichage du plateau
     * Appelée par le contrôleur quand le modèle change
     */
    public void afficherPlateau(/* List<Carte> cartes */) {
        plateauGrid.getChildren().clear();

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

            plateauGrid.add(btnCarte, i % 4, i / 4);
        }
    }

    /**
     * Met à jour le score affiché
     */
    public void mettreAJourScore(int score) {
        labelScore.setText("Score: " + score);
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

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Interface du contrôleur (pour référence)
 * À implémenter dans une classe séparée
 */
interface TrioController {
    void nouvellePartie();
    // void carteSelectionnee(int index);
    // void carteSelectionnee(Carte carte);
    // autres méthodes de contrôle...
}
