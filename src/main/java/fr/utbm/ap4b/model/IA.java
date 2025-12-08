package fr.utbm.ap4b.model;

import java.util.List;

/**
 * Représente un joueur contrôlé par l'ordinateur (IA).
 * Ses actions sont automatisées.
 */
public class IA implements Actor {

    private final String name;
    private final int playerIndex;
    private ActorHand hand;

    public IA(String name, int playerIndex) {
        this.name = name;
        this.playerIndex = playerIndex;
        this.hand = new ActorHand(); // Initialise avec une main vide
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public ActorHand getHand() {
        return hand;
    }

    @Override
    public void setupHand(List<Card> initialCards) {
        this.hand = new ActorHand(initialCards);
    }

    /**
     * Gère le tour d'un joueur IA.
     * Cette méthode contiendra la logique de décision pour que l'IA joue automatiquement.
     *
     * @param game L'état actuel du jeu.
     */
    @Override
    public void playTurn(Game game) {
        // TODO: Implémenter la logique de décision de l'IA.
        // Stratégie possible :
        // 1. Analyser les cartes connues (sa propre main, les cartes révélées).
        // 2. Déterminer la meilleure action pour former un trio (révéler une carte, etc.).
        // 3. Exécuter l'action en mettant à jour l'état du jeu.
        System.out.println("L'IA " + name + " réfléchit à son coup...");

        // Pour l'instant, l'IA passe simplement son tour.
        game.nextPlayer();
    }
}