package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.view.ModeSelectionPage;
import javafx.stage.Stage;

public class TrioController {

    private final MenuController menuController;

    public TrioController(Stage primaryStage) {
        this.menuController = new MenuController(primaryStage);
    }

    public ModeSelectionPage getView() {
        return menuController.getView();
    }
}
