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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrioTeamPage {

    private final int nombreEquipe;
    private final List<String> playerNames;
    private final Map<Integer, List<Card>> teamTrios;
    private BorderPane root;// Conteneur principal
    private Button endBtn;
    private Map<Integer, List<StackPane>> teamSlots = new HashMap<>();

    public TrioTeamPage(int nombreEquipe, List<String> playerNames,
                        Map<Integer, List<Card>> teamTrios) {
        this.nombreEquipe = nombreEquipe;
        this.playerNames = playerNames;
        this.teamTrios = teamTrios != null ? teamTrios : new HashMap<>();
        showScreen();
    }

    private void showScreen()
    {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createEndArea());
        root.setCenter(createPrintArea(nombreEquipe));
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
     * Affiche le nom des équipes et leurs trios
     */
    private GridPane createPrintArea(int nbEquipe) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        //Label d'affiche des caryes
        Label cardLabel = new Label("Trios de chaque équipe");
        cardLabel.setStyle("-fx-font-size: 28px;");
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(Double.MAX_VALUE);
        grid.add(cardLabel, 0, 0, 5, 1);

        int colonnesParLigne = 3; // 3 équipes par ligne

        for (int equipe = 0; equipe < nbEquipe; equipe++) {
            HBox box = createTeamBox(equipe + 1);

            int colonne = equipe % colonnesParLigne;
            int ligne = (equipe / colonnesParLigne) + 1;

            grid.add(box, colonne, ligne);

            // Centre chaque HBox dans sa cellule
            GridPane.setHalignment(box, HPos.CENTER);
            GridPane.setValignment(box, VPos.CENTER);
        }

        return grid;
    }

    private HBox createTeamBox(int numeroEquipe) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 10, 30, 10));
        box.setStyle(
                "-fx-background-radius: 10 10 0 0;" +
                        "-fx-border-color: #0D1117;" +
                        "-fx-border-width: 3;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);");

        // Déterminer les noms des joueurs de l'équipe
        String teamLabelText = getTeamLabel(numeroEquipe);

        Label label = new Label(teamLabelText);
        label.setStyle("-fx-font-size: 14px; -fx-min-width: 150px;");
        label.setWrapText(true);

        // 3 slots pour les trios à droite du label
        HBox slotsContainer = new HBox(10);
        slotsContainer.setAlignment(Pos.CENTER);

        List<StackPane> slots = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot();
            slots.add(slot);
            slotsContainer.getChildren().add(slot);
        }

        teamSlots.put(numeroEquipe, slots);

        displayTeamTrios(numeroEquipe);

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

    private String getTeamLabel(int teamId) {
        // Logique pour les équipes : 4 joueurs : équipe 1 = joueurs 1 et 3, équipe 2 = joueurs 2 et 4
        // 6 joueurs : équipe 1 = joueurs 1 et 4, équipe 2 = joueurs 2 et 5, équipe 3 = joueurs 3 et 6
        if (playerNames != null && playerNames.size() >= 4) {
            int totalPlayers = playerNames.size();
            int teamsCount = totalPlayers / 2;

            //ID des joueurs de l'équipe
            int firstPlayerId = teamId - 1;
            int secondPlayerId = firstPlayerId + teamsCount;

            StringBuilder label = new StringBuilder("Équipe " + teamId + "\n");

            if(firstPlayerId < totalPlayers) {
                label.append(playerNames.get(firstPlayerId));
            }

            if(secondPlayerId < totalPlayers) {
                label.append(" & ").append(playerNames.get(secondPlayerId));
            }

            return label.toString();
        }
        return "Équipe " + teamId;
    }

    private void displayTeamTrios(int teamId) {
        //Récupère la liste des cartes représentatives de l'équipe
        List<Card> trios = teamTrios.get(teamId);

        //Vérifie s'il y a des trios à afficher
        if (trios == null || trios.isEmpty()) {
            System.out.println("DEBUG: Aucun trio pour l'équipe " + teamId);
            return;
        }

        //Récupères les emplacements créés
        List<StackPane> slots = teamSlots.get(teamId);
        if (slots == null){
            System.out.println("DEBUG: Aucun slot pour l'équipe " + teamId);
            return;
        }
        System.out.println("Affichage " + trios.size() +
                " trios pour l'équipe " + teamId);

        // Affiche chaque carte dans un slot jusqu'au nombre minimum entre trios et slots disponibles
        int trioToShow = Math.min(trios.size(), slots.size());
        for(int i =  0; i < trioToShow; i++) {
            Card card = trios.get(i); //Carte représentative du trio
            StackPane slot = slots.get(i); //son emplacement

            displayCardInSlot(slot, card);
        }
    }

    private void displayCardInSlot(StackPane slot, Card card) {
        slot.getChildren().clear();

        //Vérifie que la carte n'est pas nulle
        if(card == null){
            System.err.println("ERROR: Carte null dans displayCardInSlot");
            return;
        }

        try{
            String imagePath = card.getImagePath();
            InputStream is = getClass().getResourceAsStream(imagePath);
            if(is != null){
                Image cardImage = new Image(is);
                ImageView imageView = new ImageView(cardImage);
                imageView.setFitHeight(90);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);

                slot.getChildren().add(imageView);
            }else{
                // Fallback
                Label valueLabel = new Label(String.valueOf(card.getValue()));
                valueLabel.setStyle("-fx-font-size: 20px;");
                slot.getChildren().add(valueLabel);
            }
        } catch (Exception e){
            e.printStackTrace();
            Label valueLabel = new Label(String.valueOf(card.getValue()));
            valueLabel.setStyle("-fx-font-size: 20px;");
            slot.getChildren().add(valueLabel);
        }
    }

    public Button getEndBtn() {return endBtn;}

    public BorderPane getRoot() {
        return root;
    }
}
