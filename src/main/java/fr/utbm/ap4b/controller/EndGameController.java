package fr.utbm.ap4b.controller;

import fr.utbm.ap4b.model.Actor;
import fr.utbm.ap4b.model.Card;
import fr.utbm.ap4b.model.CompletedTrios;
import fr.utbm.ap4b.model.Game;
import fr.utbm.ap4b.model.JoueurEquipe;
import fr.utbm.ap4b.view.EndGamePage;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EndGameController {

    private final Stage primaryStage;
    private final Game gameModel;

    public EndGameController(Stage primaryStage, Game gameModel) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
    }

    public void showEndGame() {
        Actor winner = gameModel.getWinner();
        boolean isTeamMode = gameModel.isTeamMode();

        // Déterminer la raison de la victoire
        String winReason = "3 Trios";

        if(gameModel.isPiquant()) {
            winReason = "2 Trios liés";
        }

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

        // Construire la map des scores
        Map<String, List<List<Card>>> allScores = buildScoresMap(isTeamMode);

        // Afficher la page de fin
        EndGamePage endGameView = new EndGamePage(winner, isTeamMode, winReason, allScores);
        primaryStage.getScene().setRoot(endGameView.getRoot());
        primaryStage.setTitle("Fin de la partie");

        endGameView.getReplayButton().setOnAction(e -> {
            new MenuController(primaryStage).show();
        });

        endGameView.getQuitButton().setOnAction(e -> {
            primaryStage.close();
        });
    }

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
