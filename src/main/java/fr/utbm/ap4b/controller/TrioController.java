package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.view.ModeSelectionPage;
import javafx.stage.Stage;

/**
 * Contrôleur initial de l'application.
 * Cette classe agit comme un point d'entrée ou un "wrapper".
 * Son rôle est simplement d'initialiser le premier vrai contrôleur (MenuController)
 * et de fournir la vue de départ à l'application principale (TrioApp).
 */
public class TrioController {

    private final MenuController menuController;

    /**
     * Constructeur du contrôleur initial.
     * Instancie le contrôleur du menu qui va gérer la configuration de la partie.
     *
     * @param primaryStage La fenêtre principale de l'application (Stage JavaFX).
     */
    public TrioController(Stage primaryStage) {
        this.menuController = new MenuController(primaryStage);
    }

    /**
     * Récupère la vue racine à afficher au lancement de l'application.
     * @return La page de sélection du mode de jeu (vue du MenuController).
     */
    public ModeSelectionPage getView() {
        return menuController.getView();
    }
}
