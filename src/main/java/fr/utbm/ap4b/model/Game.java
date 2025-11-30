package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private DrawPile drawPile;
    private final int numPlayers;
    private final boolean isTeamMode;
    private final boolean isPiquant;
    private int playerTurn;
    private List<Actor> players; // Liste pour gérer tous les joueurs (Joueur, IA, JoueurEquipe)
    private CompletedTrios completedTrios;

    public Game(List<String> playerNames, boolean isTeamMode, boolean isPiquant) {
        if (playerNames == null || playerNames.isEmpty()) {
            throw new IllegalArgumentException("La liste des noms de joueurs ne peut pas être vide.");
        }
        this.numPlayers = playerNames.size();
        this.isTeamMode = isTeamMode;
        this.isPiquant = isPiquant;
        this.drawPile = new DrawPile();
        this.playerTurn = 0;
        this.completedTrios = new CompletedTrios(numPlayers);
        
        initializePlayers(playerNames);
    }

    private void initializePlayers(List<String> playerNames) {
        this.players = new ArrayList<>();
        if (isTeamMode) {
            // Créer des joueurs en équipe
            for (int i = 0; i < numPlayers; i++) {
                players.add(new JoueurEquipe(playerNames.get(i), i));
            }
            // Lier les coéquipiers
            // Pour 4 joueurs, les équipes sont (0,2) and (1,3)
            // Pour 6 joueurs, les équipes sont (0,3), (1,4), (2,5)
            int teamMateOffset = numPlayers / 2;
            for (int i = 0; i < teamMateOffset; i++) {
                JoueurEquipe p1 = (JoueurEquipe) players.get(i);
                JoueurEquipe p2 = (JoueurEquipe) players.get(i + teamMateOffset);
                p1.setTeammate(p2);
                p2.setTeammate(p1);
            }
        } else {
            // Créer des joueurs normaux (on pourrait mixer avec des IA ici)
            for (int i = 0; i < numPlayers; i++) {
                players.add(new Joueur(playerNames.get(i), i));
            }
        }
    }

    public void startGame() {
        // 1. Distribuer les mains
        List<List<Card>> hands = drawPile.dealHands(numPlayers, isTeamMode);

        // 2. Assigner les mains aux joueurs
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setupHand(hands.get(i));
        }

        // Le reste des cartes dans drawPile constitue maintenant le "centre"
        System.out.println("La partie commence ! " + drawPile.getRemainingCardCount() + " cartes au centre.");
    }



    public boolean isPiquant() {
        return isPiquant;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
    
    public Actor getCurrentPlayer() {
        return players.get(playerTurn);
    }

    public void nextPlayer() {
        this.playerTurn = (this.playerTurn + 1) % this.numPlayers;
    }

    public List<Actor> getPlayers() {
        return players;
    }

    public DrawPile getDrawPile() {
        return drawPile;
    }

    public CompletedTrios getCompletedTrios() {
        return completedTrios;
    }
}
