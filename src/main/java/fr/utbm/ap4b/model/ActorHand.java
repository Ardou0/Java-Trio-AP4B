package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Représente la main d'un joueur, contenant un ensemble de cartes.
 */
public class ActorHand {

    private List<Card> cards;

    /**
     * Construit une main vide.
     */
    public ActorHand() {
        this.cards = new ArrayList<>();
    }

    /**
     * Construit une main à partir d'une liste de cartes initiale (distribuée par DrawPile).
     * La main est automatiquement triée à la création.
     *
     * @param initialCards La liste des cartes à ajouter à la main.
     */
    public ActorHand(List<Card> initialCards) {
        this.cards = new ArrayList<>(initialCards);
        sortHand();
    }

    /**
     * Trie les cartes dans la main par ordre croissant de leur valeur.
     */
    public void sortHand() {
        this.cards.sort(Comparator.comparingInt(Card::getValue));
    }

    /**
     * Supprime une carte spécifique de la main.
     *
     * @param card La carte à retirer.
     * @return {@code true} si la carte a été trouvée et retirée, {@code false} sinon.
     */
    public boolean removeCard(Card card) {
        return this.cards.remove(card);
    }

    /**
     * Ajoute une carte à la main et la retrie.
     * @param card La carte à ajouter.
     */
    public void addCard(Card card) {
        this.cards.add(card);
        sortHand();
    }

    /**
     * Renvoie la carte avec la plus petite valeur dans la main.
     * La main doit être triée pour que cette méthode soit fiable.
     *
     * @return La carte la plus petite.
     * @throws NoSuchElementException si la main est vide.
     */
    public Card getSmallestCard() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("La main est vide.");
        }
        // Comme la main est triée, la plus petite carte est la première.
        return cards.get(0);
    }

    /**
     * Renvoie la carte avec la plus grande valeur dans la main.
     * La main doit être triée pour que cette méthode soit fiable.
     *
     * @return La carte la plus grande.
     * @throws NoSuchElementException si la main est vide.
     */
    public Card getLargestCard() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("La main est vide.");
        }
        // Comme la main est triée, la plus grande carte est la dernière.
        return cards.get(cards.size() - 1);
    }


    /**
     * @return La liste complète des cartes dans la main.
     */
    public List<Card> getCards() {
        return this.cards;
    }

    /**
     * @return Le nombre de cartes dans la main.
     */
    public int getHandSize() {
        return this.cards.size();
    }

    @Override
    public String toString() {
        return "ActorHand{" +
                "cards=" + cards +
                '}';
    }
}
