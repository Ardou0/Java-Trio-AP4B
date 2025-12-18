package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;

public class RulesPage {

    private Button endBtn;  // Bouton de contrôle
    private BorderPane root;// Conteneur principal
    private HBox hBox;

    public RulesPage(){
        //copie et changer nom fonction
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createRulesArea());
        root.setTop(createEndArea());
    }

    private Label createRulesArea(){
        // Messages d'information
        Label ruleLabel = new Label("METTRE LES REGLES");
        ruleLabel.setStyle("-fx-font-size: 20px;");
        ruleLabel.setPadding(new Insets(10));
        ruleLabel.setAlignment(Pos.CENTER);
        return ruleLabel;
    }

    private HBox createEndArea(){
        hBox = new HBox();
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(10));

        endBtn = new Button("X");
        endBtn.setStyle("-fx-font-size: 20px;");
        endBtn.setStyle("-fx-text-fill: #F00020");
        hBox.getChildren().add(endBtn);

        return hBox;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getEndBtn() {
        return endBtn;
    }
}
