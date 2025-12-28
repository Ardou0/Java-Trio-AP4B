package fr.utbm.ap4b.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class PlayerPage {
    private final int nbPlayers;
    private BorderPane root;
    private Button previousButton;
    private Button nextButton;
    private CheckBox teamExampleCheckBox;
    private Label teamExampleLabel;

    public PlayerPage(int nbPlayers) {
        this.nbPlayers = nbPlayers;
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createPreviousArea());
        root.setCenter(createPlayerArea(nbPlayers));
        root.setBottom(nextArea());
    }

    private GridPane createPlayerArea(int nbPlayers){
        GridPane playerPane = new GridPane();
        playerPane.setAlignment(Pos.CENTER);
        playerPane.setHgap(20);
        playerPane.setVgap(10);
        playerPane.setPadding(new Insets(20));

        Label titleLabel = new Label("Veuillez saisir le nom des joueurs :");
        titleLabel.setStyle("-fx-font-size: 30px;");
        playerPane.add(titleLabel, 0, 0, 2, 1); // Prend 2 colonnes, 1 ligne
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setMargin(titleLabel, new Insets(0, 0, 40, 0));

        for(int i = 0; i < nbPlayers ; i++){

            HBox hBox = new HBox(5);
            hBox.setAlignment(Pos.CENTER_LEFT);
            Label playerName = new Label("Joueur " + (i + 1) + ": ");
            playerName.setAlignment(Pos.CENTER_LEFT);
            playerName.setStyle("-fx-font-size: 20; -fx-min-width: 100px;");

            TextField playerNameField = new TextField();
            playerNameField.setEditable(true);
            playerNameField.setPrefWidth(150);
            playerNameField.setMaxWidth(150);

            hBox.getChildren().addAll(playerName,  playerNameField);

            int column = i % 2;
            int line = (i / 2) + 1;

            playerPane.add(hBox, column, line);
        }

        HBox checkBoxContainer = new HBox(10);
        checkBoxContainer.setAlignment(Pos.CENTER);
        teamExampleCheckBox = new CheckBox("Voir comment sont répartis les joueurs en équipe");
        teamExampleCheckBox.setStyle("-fx-font-size: 16px;");
        checkBoxContainer.getChildren().add(teamExampleCheckBox);

        playerPane.add(checkBoxContainer, 0, nbPlayers/2 + 2, 2, 1);
        GridPane.setHalignment(checkBoxContainer, HPos.CENTER);
        GridPane.setMargin(checkBoxContainer, new Insets(20, 0, 0, 0));

        // Label d'exemple (initialement invisible)
        teamExampleLabel = new Label("""
                Pour 4 joueurs:
                    Equipe 1: joueur 1 et joueur 3
                    Equipe 2: joueur 2 et joueur 4
                
                Pour 6 joueurs:
                    Equipe 1: joueur 1 et joueur 4
                    Equipe 2: joueur 2 et joueur 5
                    Equipe 3: joueur 3 et joueur 6""");

        teamExampleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; " +
                "-fx-background-color: #8B7355; -fx-padding: 10; " +
                "-fx-border-color: black ; -fx-border-radius: 20px; -fx-background-radius: 20px;");
        teamExampleLabel.setWrapText(true);
        teamExampleLabel.setMaxWidth(600);
        teamExampleLabel.setVisible(false); // Caché par défaut

        playerPane.add(teamExampleLabel, 0, nbPlayers/2 + 3, 2, 1);
        GridPane.setHalignment(teamExampleLabel, HPos.CENTER);
        GridPane.setMargin(teamExampleLabel, new Insets(10, 0, 0, 0));

        return  playerPane;
    }

    private HBox createPreviousArea(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_LEFT);
        hBox.setPadding(new Insets(10));

        previousButton = new Button("Précédent");
        previousButton.setOnMouseEntered(e -> previousButton.setStyle("-fx-background-color: #5C4C38;"));
        previousButton.setOnMouseExited(e -> previousButton.setStyle("-fx-background-color: #8B7355;"));
        hBox.getChildren().addAll(previousButton);

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

    public Button getPreviousButton() {return previousButton;}

    public Button getNextButton() {return nextButton;}

    public CheckBox getExampleCheck() {return teamExampleCheckBox;}

    public Label getExampleLabel() {return teamExampleLabel;}

    public BorderPane getRoot() {return root;}
}
