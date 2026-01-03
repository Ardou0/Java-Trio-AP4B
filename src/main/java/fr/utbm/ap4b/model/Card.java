package fr.utbm.ap4b.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Représente une carte unique du jeu Trio.
 * Chaque carte possède une valeur faciale (ex: 7), un nom, et une identité unique (UUID).
 * L'identité unique est cruciale car il existe 3 exemplaires physiques de chaque valeur dans le jeu.
 */
public class Card {
    private final UUID id; // Identifiant unique pour distinguer deux cartes de même valeur
    private final int value;
    private final String name;
    private int[] neighbors;
    private String imagePath;
    private boolean iterable;

    /**
     * Crée une nouvelle instance de carte basée sur un type de Trio.
     * @param trio L'énumération définissant les propriétés de la carte (valeur, nom).
     */
    public Card(Trios trio) {
        this.id = UUID.randomUUID(); // Génère un ID unique à la création
        this.value = trio.getValue();
        this.name = trio.name();
        this.neighbors = trio.getNeighbors();
        this.imagePath = "/images/carte_" + trio.getValue() + ".png";
        this.iterable = true;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(int[] neighbors) {
        this.neighbors = neighbors;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isIterable() {
        return iterable;
    }

    /**
     * Bascule l'état "iterable" de la carte.
     * Cet état est utilisé pour savoir si la carte peut être parcourue ou affichée dans certains contextes.
     */
    public void toggleIterable() {
        this.iterable = !this.iterable;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", value=" + value +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Vérifie l'égalité entre deux objets Carte.
     * ATTENTION : L'égalité est basée sur l'identité unique (UUID) et non sur la valeur faciale.
     * Deux cartes "7" différentes physiquement ne seront PAS égales.
     * 
     * @param o L'objet à comparer.
     * @return Vrai si c'est exactement la même instance physique de carte.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Vérifie si une autre carte est considérée comme "voisine" selon les règles du jeu.
     * @param otherCard L'autre carte à tester.
     * @return Vrai si la valeur de l'autre carte est dans la liste des voisins.
     */
    public boolean isNeighbor(Card otherCard) {
        for (int neighborValue : this.neighbors) {
            if (neighborValue == otherCard.getValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si cette carte forme un trio valide avec deux autres cartes.
     * Un trio est valide si les trois cartes ont la même valeur faciale.
     * 
     * @param firstCard La première carte à comparer.
     * @param secondCard La deuxième carte à comparer.
     * @return Vrai si this.value == firstCard.value == secondCard.value.
     */
    public boolean isTrio(Card firstCard, Card secondCard) {
        return this.getValue() == firstCard.getValue() && this.getValue() == secondCard.getValue();
    }
}
