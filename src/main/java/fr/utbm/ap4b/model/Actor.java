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

    /**
     * Exécute les actions du tour d'un acteur.
     * L'implémentation de cette méthode contiendra la logique de décision
     * (pour une IA) ou les appels à l'interface utilisateur (pour un joueur humain).
     *
     * @param game L'état actuel du jeu, fournissant le contexte nécessaire pour prendre une décision.
     */
    void playTurn(Game game);
}
