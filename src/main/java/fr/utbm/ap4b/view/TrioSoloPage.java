package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrioSoloPage {

    private final int nombreDeJoueurs;
    private final List<String> playerNames;
    private final Map<Integer, List<Card>> playerTrios; //Joueur ID -> liste de trios
    private BorderPane root;// Conteneur principal
    private Button endBtn;
    private Map<Integer,List<StackPane>> playerSlots = new HashMap<>();

    public TrioSoloPage(int nombreJoueur, List<String> playerNames, Map<Integer, List<Card>> playerTrios) {
        this.nombreDeJoueurs = nombreJoueur;
        this.playerNames = playerNames;
        this.playerTrios = playerTrios != null ? playerTrios : new HashMap<>();
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

        String playerLabel = (numeroJoueur - 1 < playerNames.size() ? playerNames.get(numeroJoueur - 1) : "Joueur " + numeroJoueur);

        Label label = new Label(playerLabel);
        label.setStyle("-fx-font-size: 14px;");
        label.setMaxWidth(80);

        // Création de 3 cases pour les trios à droite du label
        HBox slotsContainer = new HBox(10);
        slotsContainer.setAlignment(Pos.CENTER);

        List<StackPane> slots = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            slots.add(slot);
            slotsContainer.getChildren().add(slot);
        }

        playerSlots.put(numeroJoueur, slots);

        //Affiche les trios
        displayPlayerTrios(numeroJoueur);

        box.getChildren().addAll(label, slotsContainer); // Ajouter au début

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

    private void displayPlayerTrios(int playerId) {
        List<Card> trios =  playerTrios.get(playerId);
        if (trios == null || trios.isEmpty()) return;

        List<StackPane> slots = playerSlots.get(playerId);
        if (slots == null || slots.isEmpty()) return;

        // Afficher chaque carte représentative dans un slot
        for (int i = 0; i < Math.min(trios.size(), 3); i++){
            Card card = trios.get(i);
            displayCardInSlot(slots.get(i), card);
        }
    }

    private void displayCardInSlot(StackPane slot, Card card) {
        slot.getChildren().clear();

        try{
            InputStream is = getClass().getResourceAsStream(card.getImagePath());
            if(is != null){
                Image cardImage = new Image(is);
                ImageView cardView = new ImageView(cardImage);
                cardView.setFitHeight(90);
                cardView.setFitWidth(60);
                cardView.setPreserveRatio(true);

                slot.getChildren().add(cardView);
            }else{
                // Fallback
                Label valueLabel = new Label(String.valueOf(card.getValue()));
                valueLabel.setStyle("-fx-font-size: 20px;");
                slot.getChildren().add(valueLabel);
            }
        }catch (Exception e){
            e.printStackTrace();
            Label valueLabel = new Label(String.valueOf(card.getValue()));
            valueLabel.setStyle("-fx-font-size: 16px;");
            slot.getChildren().add(valueLabel);
        }
    }

    public Button getEndBtn() {return endBtn;}

    public BorderPane getRoot() {
        return root;
    }
}
