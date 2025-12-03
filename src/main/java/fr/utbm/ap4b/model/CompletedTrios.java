package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente l'ensemble des trios complétés par chaque joueur au cours de la partie.
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
