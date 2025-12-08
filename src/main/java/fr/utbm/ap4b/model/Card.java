package fr.utbm.ap4b.model;

import java.util.Objects;
import java.util.UUID;

public class Card {
    private final UUID id; // Unique identifier for each card object
    private final int value;
    private final String name;
    private int[] neighbors;
    private String imagePath;
    private boolean iterable;

    public Card(Trios trio) {
        this.id = UUID.randomUUID(); // Assign a unique ID on creation
        this.value = trio.getValue();
        this.name = trio.name();
        this.neighbors = trio.getNeighbors();
        this.imagePath = "";
        this.iterable = true;
    }

    // Keep old constructors but add the ID initialization
    public Card(int value, String name, int[] neighbors) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.name = name;
        this.neighbors = neighbors;
        this.iterable = true;
    }

    public Card(int value, String name, int[] neighbors, String imagePath) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.name = name;
        this.neighbors = neighbors;
        this.imagePath = imagePath;
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

    // --- CRITICAL FIX ---
    // The equals and hashCode methods now use the unique ID, not the value.
    // This ensures that two different card objects with the same face value are NOT considered equal.
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
