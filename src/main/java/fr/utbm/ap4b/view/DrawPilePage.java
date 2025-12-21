package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class DrawPilePage {

    private BorderPane root;// Conteneur principal
    private HBox hBox;
    private Button endBtn;

    public DrawPilePage(){
        showScreen();
    }

    private void showScreen(){
        root = new BorderPane();
        root.setCenter(createDrawPileArea());
        root.setTop(createEndArea());
    }

    private GridPane createDrawPileArea(){
        GridPane drawPilePane = new GridPane();
        drawPilePane.setHgap(10);
        drawPilePane.setVgap(10);
        drawPilePane.setPadding(new Insets(20));
        drawPilePane.setStyle("-fx-background-color: #E2CAA2;");


        return drawPilePane;
    }

    private HBox createEndArea(){
        hBox = new HBox();
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

    public Button  getEndBtn() {return endBtn;}

    public BorderPane getRoot() {
        return root;
    }
}
