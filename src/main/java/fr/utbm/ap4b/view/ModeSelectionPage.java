package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class ModeSelectionPage {

    private BorderPane root;
    private Button rulesButton;
    private Button nextButton;

    public ModeSelectionPage() {
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createRulesArea());
        root.setCenter(createSelectionArea());
        root.setBottom(nextArea());
    }

    private VBox createSelectionArea() {
        VBox vBox = new VBox(40);
        vBox.setPadding(new Insets(50, 20, 20, 20));
        vBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Trio");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-font-size: 75px;" +
//                "-fx-padding: 100px;" +
//                "-fx-border-insets: 50px;" +
//                "fx-background-insets: 50px;" +
                "-fx-underline: true;"
        );

        // Container pour le mode de jeu
        HBox modeContainer = createBoxes();

        //Label du mode de jeu
        Label modeLabel = new Label("Mode de jeu: ");
        modeLabel.setAlignment(Pos.CENTER);
        modeLabel.setStyle("-fx-font-size: 16px;");

        // Création de la ComboBox
        ComboBox<String> modeComboBox = new ComboBox<>();
        // Ajout des options
        modeComboBox.getItems().addAll("Normal", "Piquant");
        // Valeur par défaut
        modeComboBox.setValue("Normal");

        modeContainer.getChildren().addAll(modeLabel, modeComboBox);

        // Choix des règles (équipe ou individuel)
        HBox teamContainer = createBoxes();
        Label teamLabel = new Label("Choix des règles: ");
        teamLabel.setAlignment(Pos.CENTER);
        teamLabel.setStyle("-fx-font-size: 16px;");

        // Création de la ComboBox
        ComboBox<String> teamComboBox = new ComboBox<>();
        teamComboBox.getItems().addAll("Individuel", "Equipe");
        teamComboBox.setValue("Individuel");
        teamContainer.getChildren().addAll(teamLabel, teamComboBox);

        // Choix du nombre de joueurs
        HBox playerContainer = createBoxes();
        Label playerLabel = new Label("Nombre de joueurs: ");
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setStyle("-fx-font-size: 16px;");

        ComboBox<String> playerComboBox = new ComboBox<>();
        playerComboBox.getItems().addAll("3", "4", "5", "6");
        playerComboBox.setValue("6");
        playerContainer.getChildren().addAll(playerLabel, playerComboBox);

        vBox.getChildren().addAll(titleLabel, modeContainer, teamContainer, playerContainer);

        return vBox;
    }

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
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, -3);" // Ombre
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

    public BorderPane getRoot() {return root;}
}
