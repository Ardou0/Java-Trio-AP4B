package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private DrawPile drawPile;
    private final int numPlayers;
    private final boolean isTeamMode;
    private final boolean isPiquant;
    private final List<String> playerNames;
    private final int numAI;
    private int playerTurn;
    private List<Actor> players;
    private CompletedTrios completedTrios;
    private boolean isGameStarted;
    private Actor winner = null;
    
    private List<CardLocation> revealedCards;

    public Game(List<String> playerNames, int numAI, boolean isTeamMode, boolean isPiquant) {
        if (playerNames == null) {
            throw new IllegalArgumentException("La liste des noms de joueurs ne peut pas être nulle.");
        }
        
        this.playerNames = playerNames;
        this.numAI = numAI;
        this.numPlayers = playerNames.size() + numAI;

        if (numPlayers > 6) {
            throw new IllegalArgumentException("Le nombre total de joueurs (humains + IA) ne peut pas dépasser 6.");
        }
        if (numPlayers < 3 && !isTeamMode) {
             throw new IllegalArgumentException("Le mode normal requiert au moins 3 joueurs au total.");
        }
        if (isTeamMode && numAI > 0) {
            throw new IllegalArgumentException("Le mode équipe avec des IA n'est pas supporté.");
        }

        this.isTeamMode = isTeamMode;
        this.isPiquant = isPiquant;
        this.drawPile = new DrawPile();
        this.playerTurn = 0;
        this.completedTrios = new CompletedTrios(numPlayers);
        this.revealedCards = new ArrayList<>();
        this.isGameStarted = false;
        
        initializePlayers();
    }

    private void initializePlayers() {
        this.players = new ArrayList<>();
        if (isTeamMode) {
            int playerIndex = 0;
            for (String name : playerNames) {
                players.add(new JoueurEquipe(name, playerIndex++));
            }
            
            int teamMateOffset = players.size() / 2;
            for (int i = 0; i < teamMateOffset; i++) {
                JoueurEquipe p1 = (JoueurEquipe) players.get(i);
                JoueurEquipe p2 = (JoueurEquipe) players.get(i + teamMateOffset);
                p1.setTeammate(p2);
                p2.setTeammate(p1);
            }
        } else {
            int playerIndex = 0;
            for (String name : playerNames) {
                players.add(new Joueur(name, playerIndex++));
            }
            for (int i = 0; i < numAI; i++) {
                players.add(new IA("IA " + (i + 1), playerIndex++));
            }
        }
    }

    public void startGame() {
        if (isGameStarted) return;
        List<List<Card>> hands = drawPile.dealHands(numPlayers, isTeamMode);
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setupHand(hands.get(i));
        }
        System.out.println("La partie commence ! " + drawPile.getRemainingCardCount() + " cartes dans la pioche.");
        isGameStarted = true;
    }

    public void revealLargestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getLargestCard();
        revealCardFromPlayer(card, player);
    }

    public void revealSmallestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getSmallestCard();
        revealCardFromPlayer(card, player);
    }

    private void revealCardFromPlayer(Card card, Actor player) {
        this.revealedCards.add(CardLocation.fromPlayer(card, player));
    }

    public void revealCardFromDrawPile(Card card) {
        this.revealedCards.add(CardLocation.fromDrawPile(card, this.drawPile));
    }

    public void processTrio(List<Card> trio) {
        if (trio == null || trio.size() != 3) return;

        for (Card card : trio) {
            revealedCards.stream()
                .filter(loc -> loc.getCard().equals(card))
                .findFirst()
                .ifPresent(CardLocation::removeFromSource);
        }
        
        clearRevealedCards();
    }
    
    private void clearRevealedCards() {
        this.revealedCards.clear();
    }

    public List<CardLocation> getRevealedCards() {
        return revealedCards;
    }

    public boolean isPiquant() {
        return isPiquant;
    }

    public boolean isTeamMode() {
        return isTeamMode;
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

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public Actor getWinner() {
        return this.winner;
    }

    public boolean isGameEnded() {
        this.winner = completedTrios.getWinner(this);
        return this.winner != null;
    }
}
