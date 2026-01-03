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
}
