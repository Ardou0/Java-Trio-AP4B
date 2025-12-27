package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class PlayerPage {
    private final int nbPlayers;
    private BorderPane root;
    private Button previousButton;
    private Button nextButton;

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
        playerPane.setVgap(20);
        playerPane.setPadding(new Insets(20));

        for(int i = 0; i < nbPlayers ; i++){

            HBox hBox = new HBox(10);
            hBox.setAlignment(Pos.CENTER);
            Label playerName = new Label("Joueur " + (i + 1) + ": ");
            playerName.setAlignment(Pos.CENTER);
            playerName.setStyle("-fx-font-size: 15;");
            TextArea playerNameField = new TextArea();
            playerNameField.setEditable(true);
            playerNameField.setPrefRowCount(1);

            hBox.getChildren().addAll(playerName,  playerNameField);

            int column = i % 2;
            int line = (i / 2) + 1;

            playerPane.add(hBox, column, line);
        }

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

    public BorderPane getRoot() {return root;}
}
