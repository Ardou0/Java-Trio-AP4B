package fr.utbm.ap4b.model;

public enum Trios {
    // Les tableaux sont maintenant passés comme une liste d'arguments
    LO21(1, 6, 8),
    LC(2, 5, 9),
    MH40(3, 4, 10),
    WE4(4, 3, 11),
    LR(5, 12),
    LP2(6, 1),
    DEUTEC(7), // Pas de voisins, on ne met rien (le tableau sera vide)
    AP4(8, 1),
    LK(9, 2),
    SI40(10, 3),
    SY34(11, 4),
    LG(12, 5);

    private final int value;
    private final int[] neighbors;

    // Utilisation de 'int... neighbors' (Varargs) au lieu de 'int[] neighbors'
    Trios(int value, int... neighbors) {
        this.value = value;
        this.neighbors = neighbors;
    }

    public int getValue() {
        return value;
    }

    // Il est recommandé d'utiliser 'getNeighbors' par convention,
    // mais 'neighbors' fonctionne aussi.
    public int[] getNeighbors() {
        return this.neighbors;
    }
}