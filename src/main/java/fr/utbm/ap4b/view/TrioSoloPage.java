package fr.utbm.ap4b.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class TrioSoloPage {

    private final int nombreDeJoueurs;
    private BorderPane root;// Conteneur principal
    private Button endBtn;

    public TrioSoloPage(int nombreJoueur){
        this.nombreDeJoueurs = nombreJoueur;
        showScreen();
    }

    private void showScreen()
    {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createEndArea());
        root.setCenter(createPrintArea(nombreDeJoueurs));
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

    /**
     * Affiche le nom des joueurs et leurs trios
     */
    private GridPane createPrintArea(int nbJoueurs) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        //Label d'affiche des caryes
        Label cardLabel = new Label("Trios de chaque joueur");
        cardLabel.setStyle("-fx-font-size: 28px;");
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(Double.MAX_VALUE);
        grid.add(cardLabel, 0, 0, 5, 1);

        int colonnesParLigne = 3; // 3 joueurs par ligne

        for (int joueur = 0; joueur < nbJoueurs; joueur++) {
            HBox box = createPlayerBox(joueur + 1);

            int colonne = joueur % colonnesParLigne;
            int ligne = (joueur / colonnesParLigne) + 1;

            grid.add(box, colonne, ligne);

            // Centrer chaque HBox dans sa cellule
            GridPane.setHalignment(box, HPos.CENTER);
            GridPane.setValignment(box, VPos.CENTER);
        }

        return grid;
    }

    private HBox createPlayerBox(int numeroJoueur) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 10, 30, 10));
        box.setStyle(
                "-fx-background-radius: 10 10 0 0;" +
                "-fx-border-color: #0D1117;" +
                "-fx-border-width: 3;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);");

        // Création de 3 cases pour ce joueur
        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            box.getChildren().add(slot);
        }

        Label label = new Label("Joueur " + numeroJoueur);
        label.setStyle("-fx-font-size: 14px;");
        box.getChildren().add(0, label); // Ajouter au début

        return box;
    }

    public static StackPane createSlot() {
        StackPane slot = new StackPane();

        slot.setPrefSize(70, 100);
        slot.setMinSize(70, 100);
        slot.setMaxSize(70, 100);

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

    public Button getEndBtn() {return endBtn;}

    public BorderPane getRoot() {
        return root;
    }
}
