package fr.utbm.ap4b.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RulesPage {

    private Button endBtn;  // Bouton de contrôle
    private BorderPane root;// Conteneur principal
    private HBox hBox;

    public RulesPage(){
        showScreen();
    }

    //Affiche la page javaFX
    private void showScreen(){
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(createRulesArea());
        root.setTop(createEndArea());
    }

    private GridPane createRulesArea(){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(15); // Espace horizontal entre les cellules
        grid.setVgap(15); // Espace vertical entre les cellules

        // --- Contenu des règles ---

        // Partie 1 : Comment jouer
        String title1 = "1. Comment jouer";
        String content1 = "L’ACCÈS AUX CARTES :\n" +
                "    Au tour par tour, révélez soit la plus grande, soit la plus petite carte de vos adversaires ou de votre propre main. " +
                "Vous pouvez aussi révéler une carte de la pioche (face cachée).\n\n" +
                "PHASE D’ACTION :\n" +
                "    Dès que vous révélez une carte, montrez-la aux autres joueurs. " +
                "Si les deux premières cartes sont identiques, tentez de compléter le trio avec une troisième.\n\n" +
                "VALIDATION :\n" +
                "    Si un trio est formé (3 cartes identiques), il est validé !\n\n" +
                "FIN DU TOUR :\n" +
                "    Si les deux ou trois cartes révélées sont différentes ou qu'un trio est formé, le tour s'arrête immédiatement.";

        // Partie 2 : Conditions de victoire
        String title2 = "2. Conditions de victoire";
        String content2 = "Le but est de valider vos Unités d'Enseignement (UE) :\n\n" +
                "EN MODE NORMAL :\n" +
                "    • Complétez 3 Trios quelconques.\n" +
                "    • OU réussissez le Trio de 7 (Diplôme DEUTEC).\n\n" +
                "EN MODE PIQUANT :\n" +
                "    • Complétez 2 Trios liés (compétences connectées).\n" +
                "    • OU réussissez le Trio de 7.";

        // Partie 3 : Mode en Équipe
        String title3 = "3. Mode en Équipe";
        String content3 = "PRINCIPE :\n" +
                "    Système identique au solo, mais en coopération avec un partenaire.\n\n" +
                "ÉCHANGES :\n" +
                "    Possibles au début de la partie et quand l’équipe adverse valide un trio.\n\n" +
                "RÈGLES D'ÉCHANGE :\n" +
                "    • Échangez n’importe quelle carte.\n" +
                "    • Communication interdite pour préparer l'échange !";


        // Création des cellules avec du style
        // Cellule 1 (Gauche)
        VBox cell1 = createRuleCell(title1, content1, "#E3F2FD"); // Bleu très clair

        // Cellule 2 (Droite Haut)
        VBox cell2 = createRuleCell(title2, content2, "#E8F5E9"); // Vert très clair

        // Cellule 3 (Droite Bas)
        VBox cell3 = createRuleCell(title3, content3, "#FFF3E0"); // Orange très clair

        // Ajout au GridPane : grid.add(node, columnIndex, rowIndex, columnSpan, rowSpan)
        // La cellule 1 occupe la colonne 0, ligne 0, s'étend sur 1 colonne et 2 lignes
        grid.add(cell1, 0, 0, 1, 2);

        // Les deux autres occupent la colonne 1 sur une seule ligne chacune
        grid.add(cell2, 1, 0);
        grid.add(cell3, 1, 1);

        // Contraintes de colonnes (50% / 50%)
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // Contraintes de lignes (50% / 50%)
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        grid.getRowConstraints().addAll(row1, row2);

        return grid;
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

    private VBox createRuleCell(String title, String content, String bgColor) {
        VBox box = new VBox(10); // Espacement vertical de 10
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;" +
                " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-border-color: #0D1117; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10;");
        box.setAlignment(Pos.TOP_LEFT);

        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20)); // Titre un peu plus gros
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.LEFT);
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Contenu
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("System", 14));
        contentLabel.setWrapText(true);
        // Alignement gauche souvent plus lisible pour les listes que JUSTIFY
        contentLabel.setTextAlignment(TextAlignment.LEFT);
        contentLabel.setStyle("-fx-text-fill: #34495e;");

        // ScrollPane pour garantir la lisibilité
        ScrollPane scroll = new ScrollPane(contentLabel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // On force le ScrollPane à grandir
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(titleLabel, scroll);
        return box;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getEndBtn() {
        return endBtn;
    }
}
