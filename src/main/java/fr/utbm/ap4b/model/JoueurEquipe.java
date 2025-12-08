package fr.utbm.ap4b.model;

/**
 * Représente un joueur humain qui fait partie d'une équipe.
 * Hérite de Joueur et ajoute des fonctionnalités spécifiques au jeu en équipe,
 * comme l'échange de cartes avec un coéquipier.
 */
public class JoueurEquipe extends Joueur {

    private JoueurEquipe teammate;

    public JoueurEquipe(String name, int playerIndex) {
        super(name, playerIndex);
    }

    /**
     * Définit le coéquipier de ce joueur.
     * @param teammate Le coéquipier (peut être un Joueur, une IA, ou un autre JoueurEquipe).
     */
    public void setTeammate(JoueurEquipe teammate) {
        this.teammate = teammate;
    }

    /**
     * @return Le coéquipier de ce joueur, ou null s'il n'en a pas.
     */
    public JoueurEquipe getTeammate() {
        return teammate;
    }

    /**
     * Échange une carte avec le coéquipier.
     * Cette méthode effectue un échange direct et symétrique. Elle suppose que l'action
     * est valide dans le contexte actuel du jeu.
     *
     * @param cardToGive    La carte que CE joueur donne à son coéquipier.
     * @param cardToReceive La carte que CE joueur reçoit de son coéquipier.
     * @return {@code true} si l'échange a réussi, {@code false} en cas d'erreur
     *         (pas de coéquipier, ou l'un des joueurs ne possède pas la carte à échanger).
     */
    public boolean exchangeCards(Card cardToGive, Card cardToReceive) {
        if (teammate == null) {
            System.err.println("Erreur d'échange : " + getName() + " n'a pas de coéquipier.");
            return false;
        }

        // Vérifier que ce joueur possède bien la carte à donner
        if (!this.getHand().getCards().contains(cardToGive)) {
            System.err.println("Erreur d'échange : " + getName() + " ne possède pas la carte " + cardToGive);
            return false;
        }


        /**
         * TODO
         * Implement the logic to check if players can exchange card + if players want to exchange + if the exchange
         * have been approved
         * */

        // Effectuer l'échange dans les deux sens
        this.getHand().removeCard(cardToGive);
        this.getHand().addCard(cardToReceive);

        teammate.getHand().removeCard(cardToReceive);
        teammate.getHand().addCard(cardToGive);

        System.out.println(getName() + " et " + teammate.getName() + " ont échangé des cartes.");
        return true;
    }
}
