package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestionnaire des scores et des conditions de victoire.
 * Cette classe stocke l'ensemble des trios complétés par chaque joueur/équipe
 * et contient la logique pour déterminer le gagnant selon le mode de jeu (Normal ou Piquant).
 */
public class CompletedTrios {

    // Liste de listes de trios, indexée par l'ID du joueur.
    // Structure : Joueur -> [ Trio1, Trio2, ... ] -> [Carte1, Carte2, Carte3]
    private final List<List<List<Card>>> triosByPlayer;

    /**
     * Initialise le gestionnaire de trios pour un nombre donné de joueurs.
     * @param numPlayers Le nombre total de joueurs.
     */
    public CompletedTrios(int numPlayers) {
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être positif.");
        }
        this.triosByPlayer = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            this.triosByPlayer.add(new ArrayList<>());
        }
    }

    /**
     * Enregistre un nouveau trio validé pour un joueur.
     * Effectue une validation basique (taille du trio, valeurs identiques).
     *
     * @param playerIndex L'index du joueur qui a complété le trio.
     * @param trio La liste des 3 cartes formant le trio.
     * @throws IllegalArgumentException Si le trio est invalide.
     */
    public void addTrio(int playerIndex, List<Card> trio) {
        if (trio == null || trio.size() != 3) {
            throw new IllegalArgumentException("Un trio doit être composé de 3 cartes.");
        }
        // Vérification que les 3 cartes ont la même valeur
        if (!(trio.get(0).getValue() == trio.get(1).getValue() && trio.get(1).getValue() == trio.get(2).getValue())) {
            throw new IllegalArgumentException("Les 3 cartes doivent avoir la même valeur pour former un trio.");
        }
        this.triosByPlayer.get(playerIndex).add(new ArrayList<>(trio));
    }

    /**
     * Récupère la liste des trios complétés par un joueur spécifique.
     * @param playerIndex L'index du joueur.
     * @return Une liste non modifiable des trios.
     */
    public List<List<Card>> getTriosForPlayer(int playerIndex) {
        return Collections.unmodifiableList(this.triosByPlayer.get(playerIndex));
    }

    /**
     * Vérifie s'il y a un gagnant en fonction des trios complétés et du mode de jeu.
     * Délègue la logique selon que le jeu est en mode équipe ou individuel.
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

    /**
     * Détermine le gagnant en mode individuel.
     * Conditions de victoire :
     * 1. Avoir le "Trio de 7".
     * 2. Avoir 3 trios quelconques (Mode Normal).
     * 3. Avoir 2 trios liés/voisins (Mode Piquant).
     */
    private Actor getIndividualWinner(Game game) {
        for (Actor player : game.getPlayers()) {
            List<List<Card>> playerTrios = getTriosForPlayer(player.getPlayerIndex());
            if (playerTrios.isEmpty()) continue;

            // Condition de victoire immédiate : Le trio de 7
            if (hasTrioOfSevens(playerTrios)) {
                return player;
            }

            if (game.isPiquant()) {
                // Mode Piquant : 2 trios liés (voisins)
                if (hasNeighboringTrios(playerTrios)) {
                    return player;
                }
            } else {
                // Mode Normal : 3 trios
                if (playerTrios.size() >= 3) {
                    return player;
                }
            }
        }
        return null;
    }

    /**
     * Détermine le gagnant en mode équipe.
     * Combine les trios des deux coéquipiers pour vérifier les conditions de victoire.
     */
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

                // Fusion des trios de l'équipe
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

    /**
     * Vérifie si la liste de trios contient le trio de valeur 7.
     */
    private boolean hasTrioOfSevens(List<List<Card>> trios) {
        return trios.stream().anyMatch(trio -> !trio.isEmpty() && trio.get(0).getValue() == 7);
    }

    /**
     * Vérifie si la liste contient deux trios qui sont "voisins" (liés).
     * Utilise la méthode isNeighbor() de la classe Card.
     */
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
