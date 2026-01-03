package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.*;
import fr.utbm.ap4b.view.EndGamePage;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur gérant la fin de la partie.
 * Il est responsable de calculer les scores finaux, de déterminer la raison de la victoire
 * et d'afficher la page de résultats (EndGamePage).
 */
public class EndGameController {

    private final Stage primaryStage;
    private final Game gameModel;

    public EndGameController(Stage primaryStage, Game gameModel) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
    }

    /**
     * Affiche l'écran de fin de partie.
     * Récupère toutes les informations nécessaires (gagnant, scores, raison) depuis le modèle
     * et configure la vue.
     */
    public void showEndGame() {
        Actor winner = gameModel.getWinner();
        boolean isTeamMode = gameModel.isTeamMode();

        // Déterminer la raison textuelle de la victoire pour l'affichage
        String winReason = "3 Trios"; // Raison par défaut (Mode Normal)

        if(gameModel.isPiquant()) {
            winReason = "2 Trios liés";
        }

        // Vérification spécifique pour la victoire "Trio de 7"
        if (winner != null) {
            CompletedTrios completedTrios = gameModel.getCompletedTrios();
            List<List<Card>> winnerTrios = completedTrios.getTriosForPlayer(winner.getPlayerIndex());
            if (winnerTrios != null) {
                for (List<Card> trio : winnerTrios) {
                    if (!trio.isEmpty() && trio.get(0).getValue() == 7) {
                        winReason = "Trio de 7 !";
                        break;
                    }
                }
            }
        }

        // Construire la map des scores pour l'affichage détaillé
        Map<String, List<List<Card>>> allScores = buildScoresMap(isTeamMode);

        // --- DEBUG CONSOLE ---
        printDebugReport(winner, winReason, allScores);
        // ---------------------

        // Afficher la page de fin
        EndGamePage endGameView = new EndGamePage(winner, isTeamMode, winReason, allScores);
        primaryStage.getScene().setRoot(endGameView.getRoot());
        primaryStage.setTitle("Fin de la partie");

        // Configuration des boutons Rejouer / Quitter
        endGameView.getReplayButton().setOnAction(e -> {
            new MenuController(primaryStage).show();
        });

        endGameView.getQuitButton().setOnAction(e -> {
            primaryStage.close();
        });
    }

    /**
     * Affiche un rapport complet de fin de partie dans la console pour le débogage.
     */
    private void printDebugReport(Actor winner, String winReason, Map<String, List<List<Card>>> allScores) {
        System.out.println("==================================================");
        System.out.println("              RAPPORT DE FIN DE PARTIE            ");
        System.out.println("==================================================");
        
        if (winner != null) {
            if(winner instanceof JoueurEquipe) {
                System.out.println("ÉQUIPE GAGNANTE : " + ((JoueurEquipe) winner).getName() + " et " + ((JoueurEquipe) winner).getTeammate().getName());
            }
            else {
                System.out.println("VAINQUEUR : " + winner.getName());
            }
            System.out.println("RAISON    : " + winReason);
        } else {
            System.out.println("RÉSULTAT  : Match Nul / Pas de vainqueur");
        }
        
        System.out.println("--------------------------------------------------");
        System.out.println("DÉTAIL DES SCORES :");
        
        for (Map.Entry<String, List<List<Card>>> entry : allScores.entrySet()) {
            String participant = entry.getKey();
            List<List<Card>> trios = entry.getValue();
            
            System.out.print(String.format("%-20s : %d trio(s) -> ", participant, trios.size()));
            
            if (trios.isEmpty()) {
                System.out.println("Aucun");
            } else {
                List<String> trioValues = new ArrayList<>();
                for (List<Card> trio : trios) {
                    if (!trio.isEmpty()) {
                        trioValues.add("[" + trio.get(0).getValue() + "]");
                    }
                }
                System.out.println(String.join(", ", trioValues));
            }
        }
        System.out.println("==================================================");
    }

    /**
     * Construit une structure de données contenant les scores de tous les joueurs ou équipes.
     * Cette structure est utilisée par la vue pour afficher le tableau récapitulatif.
     *
     * @param isTeamMode Vrai si les scores doivent être agrégés par équipe.
     * @return Une Map ordonnée (Nom -> Liste de Trios).
     */
    private Map<String, List<List<Card>>> buildScoresMap(boolean isTeamMode) {
        Map<String, List<List<Card>>> allScores = new LinkedHashMap<>();
        if (isTeamMode) {
            int teamsCount = gameModel.getPlayers().size() / 2;
            for (int teamId = 1; teamId <= teamsCount; teamId++) {
                int p1Index = teamId - 1;
                int p2Index = p1Index + teamsCount;

                List<List<Card>> teamTrios = new ArrayList<>();
                List<List<Card>> t1 = gameModel.getCompletedTrios().getTriosForPlayer(p1Index);
                List<List<Card>> t2 = gameModel.getCompletedTrios().getTriosForPlayer(p2Index);

                if (t1 != null) teamTrios.addAll(t1);
                if (t2 != null) teamTrios.addAll(t2);

                String teamName = "Équipe " + teamId + " (" + gameModel.getPlayers().get(p1Index).getName() + " & " + gameModel.getPlayers().get(p2Index).getName() + ")";
                allScores.put(teamName, teamTrios);
            }
        } else {
            for (Actor player : gameModel.getPlayers()) {
                List<List<Card>> trios = gameModel.getCompletedTrios().getTriosForPlayer(player.getPlayerIndex());
                if (trios == null) trios = new ArrayList<>();
                allScores.put(player.getName(), trios);
            }
        }
        return allScores;
    }
}
