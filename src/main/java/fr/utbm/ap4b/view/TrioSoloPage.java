package fr.utbm.ap4b.view;

import fr.utbm.ap4b.model.Card;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page affichant les trios complétés par chaque joueur en mode Solo.
 * Permet de visualiser l'avancement de la partie.
 */
public class TrioSoloPage {

    private final int nombreDeJoueurs;
    private final List<String> playerNames;
    private final Map<Integer, List<Card>> playerTrios; // Map associant l'ID du joueur à la liste de ses trios (représentés par une carte)
    private BorderPane root;// Conteneur principal
    private Button endBtn;
    private Map<Integer, FlowPane> playerTrioContainers = new HashMap<>();

    /**
     * Constructeur de la page des trios solo.
     * @param nombreJoueur Nombre total de joueurs.
     * @param playerNames Liste des noms des joueurs.
     * @param playerTrios Map des trios validés par joueur.
     */
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
        
        // Utiliser un ScrollPane pour permettre le défilement si beaucoup de joueurs/trios
        ScrollPane scrollPane = new ScrollPane(createPrintArea(nombreDeJoueurs));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        StackPane contentWrapper = new StackPane(scrollPane);
        contentWrapper.setAlignment(Pos.CENTER);
        
        root.setCenter(contentWrapper);
    }


    private HBox createEndArea(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(10));

        endBtn = new Button("Retour");
        endBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        endBtn.setOnMouseEntered(e -> endBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;"));
        endBtn.setOnMouseExited(e -> endBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;"));

        hBox.getChildren().add(endBtn);

        return hBox;
    }

    /**
     * Crée la grille affichant les joueurs et leurs trios.
     */
    private GridPane createPrintArea(int nbJoueurs) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label cardLabel = new Label("Trios de chaque joueur");
        cardLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #5C4C38;");
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(Double.MAX_VALUE);
        grid.add(cardLabel, 0, 0, 5, 1);

        int colonnesParLigne = 2; // Affichage sur 2 colonnes

        GridPane.setHalignment(cardLabel, HPos.CENTER);
        GridPane.setColumnSpan(cardLabel, colonnesParLigne);

        // Défini les contraintes de colonne
        for (int i = 0; i < colonnesParLigne; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / colonnesParLigne);
            colConst.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().add(colConst);
        }

        // Crée une boîte pour chaque joueur
        for (int joueur = 0; joueur < nbJoueurs; joueur++) {
            VBox box = createPlayerBox(joueur + 1);

            int colonne = joueur % colonnesParLigne;
            int ligne = (joueur / colonnesParLigne) + 1;

            grid.add(box, colonne, ligne);

            GridPane.setHalignment(box, HPos.CENTER);
            GridPane.setValignment(box, VPos.CENTER);
        }

        return grid;
    }

    private VBox createPlayerBox(int numeroJoueur) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle(
                "-fx-background-color: #E2CAA2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #0D1117;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        String playerLabel = (numeroJoueur - 1 < playerNames.size() ? playerNames.get(numeroJoueur - 1) : "Joueur " + numeroJoueur);

        Label label = new Label(playerLabel);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Conteneur extensible pour les trios
        FlowPane triosContainer = new FlowPane();
        triosContainer.setAlignment(Pos.CENTER);
        triosContainer.setHgap(10);
        triosContainer.setVgap(10);
        triosContainer.setPrefWrapLength(300);

        playerTrioContainers.put(numeroJoueur, triosContainer);

        displayPlayerTrios(numeroJoueur);

        box.getChildren().addAll(label, triosContainer);

        return box;
    }

    public static StackPane createSlot() {
        StackPane slot = new StackPane();

        slot.setPrefSize(70, 100);
        slot.setMinSize(70, 100);
        slot.setMaxSize(70, 100);

        slot.setStyle(
                "-fx-border-color: #8B7355;" +
                        "-fx-border-style: dashed;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-color: #F5E6D3;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        slot.setMouseTransparent(true);

        return slot;
    }

    /**
     * Remplit le conteneur de trios pour un joueur donné.
     */
    private void displayPlayerTrios(int playerId) {
        List<Card> trios =  playerTrios.get(playerId);
        FlowPane container = playerTrioContainers.get(playerId);
        
        if (container == null) return;
        container.getChildren().clear();

        int nbTrios = (trios != null) ? trios.size() : 0;
        int slotsToCreate = Math.max(3, nbTrios); // Affiche au moins 3 slots vides

        for (int i = 0; i < slotsToCreate; i++) {
            StackPane slot = createSlot();
            
            if (trios != null && i < trios.size()) {
                displayCardInSlot(slot, trios.get(i));
            }

            container.getChildren().add(slot);
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
                Label valueLabel = new Label(String.valueOf(card.getValue()));
                valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
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
