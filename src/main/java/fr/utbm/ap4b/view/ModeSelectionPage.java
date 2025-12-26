package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);

        // Création de la ComboBox
        ComboBox<String> modeComboBox = new ComboBox<>();
        // Ajout des options
        modeComboBox.getItems().addAll("Normal", "Piquant");
        // Valeur par défaut
        modeComboBox.setValue("Normal");

        vBox.getChildren().addAll(modeComboBox);

        return vBox;
    }

    private HBox createRulesArea() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.TOP_LEFT);

        rulesButton = new Button("Règles");

        rulesButton.setOnMouseEntered(e -> rulesButton.setStyle("-fx-background-color: #5C4C38;"));
        rulesButton.setOnMouseExited(e -> rulesButton.setStyle("-fx-background-color: #8B7355;"));
        hBox.getChildren().add(rulesButton);

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
