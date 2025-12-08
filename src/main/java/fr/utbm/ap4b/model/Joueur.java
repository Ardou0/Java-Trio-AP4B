package fr.utbm.ap4b.model;

import java.util.List;

/**
 * Représente un joueur humain dans le jeu Trio.
 * Les actions de ce joueur sont supposées être contrôlées via une interface utilisateur.
 */
public class Joueur implements Actor {

    private final String name;
    private final int playerIndex;
    private ActorHand hand;

    public Joueur(String name, int playerIndex) {
        this.name = name;
        this.playerIndex = playerIndex;
        System.out.println("Joueur créé : " + playerIndex + "");
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
     * Gère le tour d'un joueur humain.
     * Cette méthode devra être connectée à l'interface graphique ou textuelle
     * pour permettre au joueur de choisir ses actions (révéler une carte, etc.).
     *
     * @param game L'état actuel du jeu.
     */
    @Override
    public void playTurn(Game game) {
        System.out.println("C'est au tour de " + name + " de jouer.");
    }
}
