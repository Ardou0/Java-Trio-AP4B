package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DrawPile représente la pioche du jeu Trio.
 * Elle est responsable de la création du jeu de cartes complet, de son mélange
 * et de la distribution des cartes aux joueurs en début de partie.
 * Elle gère également les cartes restantes qui forment le "centre" du jeu.
 */
public class DrawPile {

    private List<Card> cards;

    /**
     * Construit une nouvelle pioche.
     * Le jeu est constitué de 36 cartes : 3 exemplaires de chaque valeur de 1 à 12.
     * Le jeu de cartes est automatiquement mélangé à la création.
     */
    public DrawPile() {
        this.cards = new ArrayList<>();
        // Crée 3 cartes pour chaque valeur définie dans l'énumération Trios
        for (Trios trioValue : Trios.values()) {
            for (int i = 0; i < 3; i++) {
                this.cards.add(new Card(trioValue));
            }
        }
        shuffle();
    }

    /**
     * Mélange les cartes restantes dans la pioche.
     */
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    /**
     * Distribue les cartes aux joueurs en fonction du mode de jeu et du nombre de joueurs.
     * Les cartes distribuées sont retirées de la pioche. Le reste forme le "centre".
     *
     * @param numPlayers Le nombre de joueurs dans la partie.
     * @param isTeamMode Vrai si la partie est en mode équipe, faux sinon.
     * @return Une liste de listes de cartes, où chaque liste interne représente la main d'un joueur.
     * @throws IllegalArgumentException si le nombre de joueurs n'est pas supporté.
     */
    public List<List<Card>> dealHands(int numPlayers, boolean isTeamMode) {
        int cardsPerPlayer;
        int totalCardsToDeal;

        // Calcul du nombre de cartes à distribuer selon les règles officielles
        if (isTeamMode) {
            if (numPlayers == 4 || numPlayers == 6) { // 2 ou 3 équipes de 2
                totalCardsToDeal = 36; // Toutes les cartes sont distribuées en équipe
                cardsPerPlayer = totalCardsToDeal / numPlayers;
            } else {
                throw new IllegalArgumentException("Le mode équipe est uniquement disponible pour 4 ou 6 joueurs.");
            }
        } else {
            switch (numPlayers) {
                case 3:
                    cardsPerPlayer = 9;
                    break;
                case 4:
                    cardsPerPlayer = 7;
                    break;
                case 5:
                    cardsPerPlayer = 6;
                    break;
                case 6:
                    cardsPerPlayer = 5;
                    break;
                default:
                    throw new IllegalArgumentException("Nombre de joueurs non supporté : " + numPlayers);
            }
            totalCardsToDeal = cardsPerPlayer * numPlayers;
        }

        if (this.cards.size() < totalCardsToDeal) {
            throw new IllegalStateException("Pas assez de cartes dans la pioche pour distribuer.");
        }

        // Initialisation des mains vides
        List<List<Card>> playersHands = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            playersHands.add(new ArrayList<>());
        }

        // Distribue les cartes une par une à chaque joueur (comme une distribution physique)
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (int j = 0; j < numPlayers; j++) {
                playersHands.get(j).add(draw());
            }
        }

        return playersHands;
    }

    /**
     * Retire et retourne la carte du dessus de la pioche.
     * Utilisé principalement pour la distribution initiale des cartes.
     * @return La carte piochée, ou null si la pioche est vide.
     */
    public Card draw() {
        if (this.cards.isEmpty()) {
            return null;
        }
        return this.cards.remove(0);
    }

    /**
     * Retire une carte spécifique de la pioche (utile pour prendre une carte du "centre").
     * @param card La carte à retirer.
     * @return {@code true} si la carte a été trouvée et retirée, {@code false} sinon.
     */
    public boolean draw(Card card) {
        return this.cards.remove(card);
    }


    /**
     * @return Le nombre de cartes restantes dans la pioche (le "centre").
     */
    public int getRemainingCardCount() {
        return this.cards.size();
    }

    /**
     * @return La liste des cartes restantes dans la pioche.
     */
    public List<Card> getRemainingCards() {
        return Collections.unmodifiableList(this.cards);
    }
}
