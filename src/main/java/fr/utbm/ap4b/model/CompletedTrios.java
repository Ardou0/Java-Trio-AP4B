package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente l'ensemble des trios complétés par chaque joueur/équipe au cours de la partie.
 */
public class CompletedTrios {

    private final List<List<List<Card>>> triosByPlayer;

    public CompletedTrios(int numPlayers) {
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être positif.");
        }
        this.triosByPlayer = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            this.triosByPlayer.add(new ArrayList<>());
        }
    }

    public void addTrio(int playerIndex, List<Card> trio) {
        if (trio == null || trio.size() != 3) {
            throw new IllegalArgumentException("Un trio doit être composé de 3 cartes.");
        }
        if (!(trio.get(0).getValue() == trio.get(1).getValue() && trio.get(1).getValue() == trio.get(2).getValue())) {
            throw new IllegalArgumentException("Les 3 cartes doivent avoir la même valeur pour former un trio.");
        }
        this.triosByPlayer.get(playerIndex).add(new ArrayList<>(trio));
    }

    public List<List<Card>> getTriosForPlayer(int playerIndex) {
        return Collections.unmodifiableList(this.triosByPlayer.get(playerIndex));
    }

    /**
     * Vérifie s'il y a un gagnant en fonction des trios complétés et du mode de jeu.
     *
     * @param game L'instance actuelle du jeu pour obtenir le contexte (mode, joueurs).
     * @return L'acteur gagnant, ou null s'il n'y a pas encore de gagnant.
     */
    public Actor getWinner(Game game) {
        if (!game.getPlayers().isEmpty() && game.getPlayers().get(0) instanceof JoueurEquipe && game.isTeamMode()) {
            return getTeamWinner(game);
        }
        return getIndividualWinner(game);
    }

    private Actor getIndividualWinner(Game game) {
        for (Actor player : game.getPlayers()) {
            List<List<Card>> playerTrios = getTriosForPlayer(player.getPlayerIndex());
            if (playerTrios.isEmpty()) continue;

            if (hasTrioOfSevens(playerTrios)) {
                return player;
            }

            if (game.isPiquant()) {
                if (hasNeighboringTrios(playerTrios)) {
                    return player;
                }
            } else {
                if (playerTrios.size() >= 3) {
                    return player;
                }
            }
        }
        return null;
    }

    private Actor getTeamWinner(Game game) {
        List<Actor> checkedPlayers = new ArrayList<>();
        for (Actor player : game.getPlayers()) {
            if (checkedPlayers.contains(player)) {
                continue;
            }

            if (player instanceof JoueurEquipe) {
                JoueurEquipe teamPlayer1 = (JoueurEquipe) player;
                JoueurEquipe teamPlayer2 = teamPlayer1.getTeammate();

                if (teamPlayer2 == null) {
                    continue;
                }

                List<List<Card>> teamTrios = new ArrayList<>(getTriosForPlayer(teamPlayer1.getPlayerIndex()));
                teamTrios.addAll(getTriosForPlayer(teamPlayer2.getPlayerIndex()));

                checkedPlayers.add(teamPlayer1);
                checkedPlayers.add(teamPlayer2);

                if (teamTrios.isEmpty()) {
                    continue;
                }

                if (hasTrioOfSevens(teamTrios)) {
                    return teamPlayer1;
                }

                if (game.isPiquant()) {
                    if (hasNeighboringTrios(teamTrios)) {
                        return teamPlayer1;
                    }
                } else {
                    if (teamTrios.size() >= 3) {
                        return teamPlayer1;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasTrioOfSevens(List<List<Card>> trios) {
        return trios.stream().anyMatch(trio -> !trio.isEmpty() && trio.get(0).getValue() == 7);
    }

    private boolean hasNeighboringTrios(List<List<Card>> trios) {
        if (trios.size() < 2) return false;

        for (int i = 0; i < trios.size(); i++) {
            for (int j = i + 1; j < trios.size(); j++) {
                Card cardFromTrio1 = trios.get(i).get(0);
                Card cardFromTrio2 = trios.get(j).get(0);

                if (cardFromTrio1.isNeighbor(cardFromTrio2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
