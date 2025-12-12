package fr.utbm.ap4b.model;

import java.util.List;

/**
 * L'interface Actor représente une entité capable de jouer au jeu Trio.
 * Elle définit les actions et les propriétés communes à tous les joueurs,
 * qu'ils soient humains (Joueur) ou contrôlés par l'ordinateur (IA).
 */
public interface Actor {

    /**
     * @return Le nom de l'acteur.
     */
    String getName();

    /**
     * @return L'index du joueur dans la partie (commençant à 0).
     */
    int getPlayerIndex();

    /**
     * @return La main de l'acteur.
     */
    ActorHand getHand();

    /**
     * Configure la main initiale de l'acteur.
     * @param initialCards Les cartes distribuées au début du jeu.
     */
    void setupHand(List<Card> initialCards);
}
