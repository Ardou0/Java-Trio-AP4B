package fr.utbm.ap4b.model;

import java.util.Objects;

/**
 * Représente une carte qui a été révélée, en conservant une trace de son emplacement d'origine.
 * Cela permet de savoir d'où vient une carte (main d'un joueur, pioche/centre)
 * afin de pouvoir la retirer correctement si un trio est formé.
 */
public class CardLocation {

    private final Card card;
    private final Actor owner; // Le joueur propriétaire, peut être null
    private final DrawPile drawPile; // La pioche, peut être null

    // Constructeur privé pour forcer l'utilisation des méthodes factory
    private CardLocation(Card card, Actor owner, DrawPile drawPile) {
        this.card = card;
        this.owner = owner;
        this.drawPile = drawPile;
    }

    /**
     * Crée une localisation pour une carte provenant de la main d'un joueur.
     */
    public static CardLocation fromPlayer(Card card, Actor player) {
        Objects.requireNonNull(card, "La carte ne peut pas être nulle");
        Objects.requireNonNull(player, "Le joueur ne peut pas être nul");
        return new CardLocation(card, player, null);
    }

    /**
     * Crée une localisation pour une carte provenant de la pioche (le "centre").
     */
    public static CardLocation fromDrawPile(Card card, DrawPile drawPile) {
        Objects.requireNonNull(card, "La carte ne peut pas être nulle");
        Objects.requireNonNull(drawPile, "La pioche ne peut pas être nulle");
        return new CardLocation(card, null, drawPile);
    }

    /**
     * Retire la carte de son emplacement d'origine.
     * Appelle la méthode de suppression appropriée sur le propriétaire (joueur ou pioche).
     */
    public void removeFromSource() {
        if (owner != null) {
            owner.getHand().removeCard(card);
            System.out.println("Carte " + card.getValue() + " retirée de la main de " + owner.getName());
        } else if (drawPile != null) {
            drawPile.draw(card);
            System.out.println("Carte " + card.getValue() + " retirée du centre.");
        }
    }

    public Card getCard() {
        return card;
    }

    public Actor getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        String source = (owner != null) ? "Joueur: " + owner.getName() : "Centre";
        return "CardLocation{" +
                "card=" + card.getValue() +
                ", source=" + source +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardLocation that = (CardLocation) o;
        return Objects.equals(card, that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card);
    }
}
