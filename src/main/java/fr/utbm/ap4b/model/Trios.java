package fr.utbm.ap4b.model;

/**
 * Énumération définissant les différents types de cartes (Trios) du jeu.
 * Chaque élément représente une valeur de carte (de 1 à 12) et ses propriétés associées.
 * Les propriétés incluent la valeur numérique et les "voisins" (utilisés pour le mode Piquant).
 */
public enum Trios {
    // Définition des cartes avec leurs valeurs et leurs voisins respectifs
    LO21(1, 6, 8),
    LC(2, 5, 9),
    MH40(3, 4, 10),
    WE4(4, 3, 11),
    LR(5, 12),
    LP2(6, 1),
    DEUTEC(7), // Le 7 est spécial : il fait gagner la partie mais n'a pas de voisins
    AP4(8, 1),
    LK(9, 2),
    SI40(10, 3),
    SY34(11, 4),
    LG(12, 5);

    private final int value;
    private final int[] neighbors;

    /**
     * Constructeur de l'énumération.
     * @param value La valeur faciale de la carte (1-12).
     * @param neighbors Liste variable d'entiers représentant les valeurs des cartes voisines.
     */
    Trios(int value, int... neighbors) {
        this.value = value;
        this.neighbors = neighbors;
    }

    public int getValue() {
        return value;
    }

    /**
     * Récupère la liste des valeurs voisines de cette carte.
     * Ces voisins sont utilisés pour déterminer les conditions de victoire en mode "Piquant".
     * @return Un tableau d'entiers contenant les valeurs voisines.
     */
    public int[] getNeighbors() {
        return this.neighbors;
    }
}
