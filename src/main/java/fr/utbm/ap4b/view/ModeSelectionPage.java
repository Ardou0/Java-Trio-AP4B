package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Page de sélection du mode de jeu (Menu Principal).
 * Permet de choisir le mode (Normal/Piquant), le type de jeu (Individuel/Équipe)
 * et le nombre de joueurs.
 */
public class ModeSelectionPage {

    private BorderPane root;
    private Button rulesButton;
    private Button nextButton;
    private ComboBox<String> modeComboBox;
    private ComboBox<String> teamComboBox;
    private ComboBox<String> playerComboBox;

    public ModeSelectionPage() {
        showScreen();
    }

    /**
     * Initialise l'interface graphique de la page.
     */
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createRulesArea());
        root.setCenter(createSelectionArea());
        root.setBottom(nextArea());
    }

    /**
     * Crée la zone centrale contenant les sélecteurs (ComboBox).
     */
    private VBox createSelectionArea() {
        VBox vBox = new VBox(40);
        vBox.setPadding(new Insets(50, 20, 20, 20));
        vBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Trio");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-font-size: 75px;" +
                "-fx-underline: true;"
        );

        // Container pour le mode de jeu (Normal / Piquant)
        HBox modeContainer = createBoxes();
        Label modeLabel = new Label("Mode de jeu: ");
        modeLabel.setAlignment(Pos.CENTER);
        modeLabel.setStyle("-fx-font-size: 16px;");

        modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Normal", "Piquant");
        modeComboBox.setValue("Normal");

        modeContainer.getChildren().addAll(modeLabel, modeComboBox);

        // Choix des règles (équipe ou individuel)
        HBox teamContainer = createBoxes();
        Label teamLabel = new Label("Choix des règles: ");
        teamLabel.setAlignment(Pos.CENTER);
        teamLabel.setStyle("-fx-font-size: 16px;");

        teamComboBox = new ComboBox<>();
        teamComboBox.getItems().addAll("Individuel", "Equipe");
        teamComboBox.setValue("Individuel");
        teamContainer.getChildren().addAll(teamLabel, teamComboBox);

        // Choix du nombre de joueurs
        HBox playerContainer = createBoxes();
        Label playerLabel = new Label("Nombre de joueurs: ");
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setStyle("-fx-font-size: 16px;");

        playerComboBox = new ComboBox<>();
        playerComboBox.getItems().addAll("3", "4", "5", "6");
        playerComboBox.setValue("6");
        playerContainer.getChildren().addAll(playerLabel, playerComboBox);

        vBox.getChildren().addAll(titleLabel, modeContainer, teamContainer, playerContainer);

        return vBox;
    }

    /**
     * Crée un conteneur stylisé pour les options de sélection.
     */
    private HBox createBoxes(){
        HBox hBox = new HBox(15);
        hBox.setAlignment(Pos.CENTER);
        hBox.setMaxSize(400,100);
        hBox.setPrefSize(400,100);
        hBox.setPadding(new Insets(20,10, 30, 10));
        hBox.setStyle(
                "-fx-background-color: #E2CAA2;" +
                        "-fx-background-radius: 10 10 0 0;" +
                        "-fx-border-color: #0D1117;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);"
        );

        return hBox;
    }

    private HBox createRulesArea() {
        HBox hBox = new HBox(20);
        hBox.setPadding(new Insets(10));

        rulesButton = new Button("Règles");
        rulesButton.setOnMouseEntered(e -> rulesButton.setStyle("-fx-background-color: #5C4C38;"));
        rulesButton.setOnMouseExited(e -> rulesButton.setStyle("-fx-background-color: #8B7355;"));
        hBox.getChildren().addAll(rulesButton);

        return hBox;
    }

    private HBox nextArea(){
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.CENTER);

        nextButton = new Button("Suivant");
        nextButton.setOnMouseEntered(e -> nextButton.setStyle("-fx-background-color: #5C4C38;"));
        nextButton.setOnMouseExited(e -> nextButton.setStyle("-fx-background-color: #8B7355;"));

        hBox.getChildren().add(nextButton);

        return hBox;
    }

    public Button getRulesButton() {return rulesButton;}
    public Button getNextButton() {return nextButton;}
    public ComboBox<String> getModeComboBox() {return modeComboBox;}
    public ComboBox<String> getTeamComboBox() {return teamComboBox;}
    public ComboBox<String> getPlayerComboBox() {return playerComboBox;}
    public BorderPane getRoot() {return root;}
}
