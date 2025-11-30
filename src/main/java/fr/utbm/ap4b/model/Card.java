package fr.utbm.ap4b.model;

import java.util.Arrays;
import java.util.Objects;

public class Card {
    private final int value;
    private final String name;
    private int[] neighbors; // Laissés pour une utilisation future, non initialisés par le constructeur principal
    private String imagePath; // Laissé pour une utilisation future

    /**
     * Construit une carte à partir d'une valeur de l'énumération Trios.
     * @param trio L'énumération représentant la carte.
     */
    public Card(Trios trio) {
        this.value = trio.getValue();
        this.name = trio.name();
        // Les voisins et l'imagePath ne sont pas définis ici,
        // ils peuvent être définis plus tard si nécessaire.
        this.neighbors = new int[0];
        this.imagePath = "";
    }

    // On peut garder les anciens constructeurs si d'autres parties du code les utilisent
    public Card(int value, String name, int[] neighbors) {
        this.value = value;
        this.name = name;
        this.neighbors = neighbors;
    }

    public Card(int value, String name, int[] neighbors, String imagePath) {
        this.value = value;
        this.name = name;
        this.neighbors = neighbors;
        this.imagePath = imagePath;
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

    @Override
    public String toString() {
        return "Card{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean isNeighbor(Card otherCard) {
        for (int neighborValue : this.neighbors) {
            if (neighborValue == otherCard.getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTrio(Card firstCard, Card secondCard) {
        return this.getValue() == firstCard.getValue() && this.getValue() == secondCard.getValue();
    }
}
