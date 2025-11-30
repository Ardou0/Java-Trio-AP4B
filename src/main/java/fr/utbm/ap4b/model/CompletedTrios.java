package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Représente l'ensemble des trios complétés par chaque joueur au cours de la partie.
 */
public class CompletedTrios {

    // Une liste où chaque index correspond à un joueur.
    // Chaque joueur a une liste de ses trios. Un trio est une liste de 3 cartes.
    private final List<List<List<Card>>> triosByPlayer;
    private final int numPlayers;

    /**
     * Construit un gestionnaire de trios pour un nombre de joueurs donné.
     *
     * @param numPlayers Le nombre de joueurs dans la partie.
     */
    public CompletedTrios(int numPlayers) {
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être positif.");
        }
        this.numPlayers = numPlayers;
        this.triosByPlayer = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            // Initialise une liste de trios vide pour chaque joueur.
            this.triosByPlayer.add(new ArrayList<>());
        }
    }

    /**
     * Ajoute un trio complété à la liste d'un joueur spécifique.
     *
     * @param playerIndex L'index du joueur (commençant à 0).
     * @param trio        La liste de cartes constituant le trio. Doit contenir 3 cartes.
     * @throws IllegalArgumentException si la liste de cartes n'est pas un trio valide.
     * @throws IndexOutOfBoundsException si l'index du joueur est invalide.
     */
    public void addTrio(int playerIndex, List<Card> trio) {
        if (trio == null || trio.size() != 3) {
            throw new IllegalArgumentException("Un trio doit être composé de 3 cartes.");
        }
        // On s'assure que c'est bien un trio (les 3 cartes ont la même valeur)
        if (!(trio.get(0).getValue() == trio.get(1).getValue() && trio.get(1).getValue() == trio.get(2).getValue())) {
            throw new IllegalArgumentException("Les 3 cartes doivent avoir la même valeur pour former un trio.");
        }

        this.triosByPlayer.get(playerIndex).add(new ArrayList<>(trio)); // Ajoute une copie
    }

    /**
     * Récupère la liste de tous les trios complétés par un joueur.
     *
     * @param playerIndex L'index du joueur.
     * @return Une liste non modifiable des trios du joueur.
     * @throws IndexOutOfBoundsException si l'index du joueur est invalide.
     */
    public List<List<Card>> getTriosForPlayer(int playerIndex) {
        return Collections.unmodifiableList(this.triosByPlayer.get(playerIndex));
    }

    /**
     * Compte le nombre de trios complétés par un joueur.
     *
     * @param playerIndex L'index du joueur.
     * @return Le nombre de trios.
     */
    public int getNumberOfTriosForPlayer(int playerIndex) {
        return this.triosByPlayer.get(playerIndex).size();
    }

    /**
     * @return Le nombre total de joueurs gérés.
     */
    public int getNumPlayers() {
        return numPlayers;
    }
}
