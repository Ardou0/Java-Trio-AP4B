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
}