package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    public enum GamePhase {
        INITIAL_SWAP,
        PLAYING,
        POST_TRIO_SWAP
    }

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
    private List<Card> cardsInPlayThisTurn;
    private GamePhase currentPhase;
    private Set<Actor> playersAllowedToSwap;
    private Set<Actor> playersWhoHaveSwapped;


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
        this.cardsInPlayThisTurn = new ArrayList<>();
        this.isGameStarted = false;

        // Initialize swap-related fields
        this.playersAllowedToSwap = new HashSet<>();
        this.playersWhoHaveSwapped = new HashSet<>();
        this.currentPhase = GamePhase.PLAYING; // Default phase

        initializePlayers();
    }


    public List<CardLocation> getRevealedCards() {
        return revealedCards;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public Actor getCurrentPlayer() {
        return players.get(playerTurn);
    }

    public List<Actor> getPlayers() {return players;}

    public DrawPile getDrawPile() {
        return drawPile;
    }

    public CompletedTrios getCompletedTrios() {
        return completedTrios;
    }

    public Actor getWinner() {
        return this.winner;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public Set<Actor> getPlayersAllowedToSwap() {
        return playersAllowedToSwap;
    }
    
    public Set<Actor> getPlayersWhoHaveNotSwapped() {
        Set<Actor> pending = new HashSet<>(playersAllowedToSwap);
        pending.removeAll(playersWhoHaveSwapped);
        return pending;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public boolean isGameEnded() {
        this.winner = completedTrios.getWinner(this);
        return this.winner != null;
    }

    public boolean isPiquant() {
        return isPiquant;
    }

    public boolean isTeamMode() {
        return isTeamMode;
    }

    public void nextPlayer() {
        this.playerTurn = (this.playerTurn + 1) % this.players.size();
    }

    public void revealCardFromDrawPile(Card card) {
        if (canRevealCard() && !cardsInPlayThisTurn.contains(card)) {
            this.revealedCards.add(CardLocation.fromDrawPile(card, this.drawPile));
            this.cardsInPlayThisTurn.add(card);
        }
    }

    public boolean canRevealCard() {
        if (currentPhase != GamePhase.PLAYING) return false; // Can't reveal during swap
        if (revealedCards.size() >= 3) {
            return false;
        }
        if (revealedCards.size() == 2) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();
            if (card1.getValue() != card2.getValue()) {
                return false;
            }
        }
        return true;
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

    public void revealLargestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getLargestCard();
        if (card != null) {
            revealCardFromPlayer(card, player);
        }
    }

    public void revealSmallestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getSmallestCard();
        if (card != null) {
            revealCardFromPlayer(card, player);
        }
    }

    public void startGame() {
        if (isGameStarted) return;
        List<List<Card>> hands = drawPile.dealHands(numPlayers, isTeamMode);
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setupHand(hands.get(i));
        }
        isGameStarted = true;

        if (isTeamMode) {
            System.out.println("Début de la phase d'échange initiale.");
            this.currentPhase = GamePhase.INITIAL_SWAP;
            this.playersAllowedToSwap.addAll(players);
        } else {
            System.out.println("La partie commence ! " + drawPile.getRemainingCardCount() + " cartes dans la pioche.");
        }
    }

    public boolean exchangeCards(int initiatingPlayerIndex, Card cardToGive, Card cardToReceive) {
        if (currentPhase == GamePhase.PLAYING || !isTeamMode) {
            return false; // Not a swap phase or not team mode
        }

        Actor initiator = players.get(initiatingPlayerIndex);
        if (!(initiator instanceof JoueurEquipe) || playersWhoHaveSwapped.contains(initiator)) {
            return false; // Not a team player or already swapped this phase
        }

        JoueurEquipe teamInitiator = (JoueurEquipe) initiator;
        JoueurEquipe teammate = teamInitiator.getTeammate();

        if (teammate == null || !playersAllowedToSwap.contains(initiator)) {
            return false; // No teammate or this team is not allowed to swap now
        }

        // Verify cards exist in the correct hands
        if (!teamInitiator.getHand().getCards().contains(cardToGive) || !teammate.getHand().getCards().contains(cardToReceive)) {
            return false;
        }

        // Perform the swap
        teamInitiator.getHand().removeCard(cardToGive);
        teamInitiator.getHand().addCard(cardToReceive);
        teammate.getHand().removeCard(cardToReceive);
        teammate.getHand().addCard(cardToGive);

        // Mark both players as having swapped for this phase
        playersWhoHaveSwapped.add(teamInitiator);
        playersWhoHaveSwapped.add(teammate);

        System.out.println(teamInitiator.getName() + " et " + teammate.getName() + " ont échangé des cartes.");

        // Check if the current swap phase is over
        checkAndEndSwapPhase();

        return true;
    }


    public boolean nextTurn() {
        if (this.isGameEnded() || !this.isGameStarted) {
            return false;
        }

        boolean isTrio = false;
        if (revealedCards.size() == 3) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();
            Card card3 = revealedCards.get(2).getCard();
            if (card1.getValue() == card2.getValue() && card2.getValue() == card3.getValue()) {
                isTrio = true;
            }
        }

        if (isTrio) {
            Actor scoringPlayer = getCurrentPlayer();
            List<Card> trio = new ArrayList<>();
            for (CardLocation loc : revealedCards) {
                trio.add(loc.getCard());
            }
            completedTrios.addTrio(scoringPlayer.getPlayerIndex(), trio);
            processTrio(trio); // This clears revealedCards and cardsInPlayThisTurn

            if (isTeamMode) {
                System.out.println("Un trio a été formé ! Phase d'échange pour les équipes adverses.");
                this.currentPhase = GamePhase.POST_TRIO_SWAP;
                this.playersWhoHaveSwapped.clear();
                this.playersAllowedToSwap.clear();

                JoueurEquipe scoringTeamPlayer = (JoueurEquipe) scoringPlayer;
                for (Actor player : players) {
                    if (player != scoringTeamPlayer && player != scoringTeamPlayer.getTeammate()) {
                        playersAllowedToSwap.add(player);
                    }
                }
                // If there are no other teams to swap, end the phase immediately.
                if (playersAllowedToSwap.isEmpty()) {
                    checkAndEndSwapPhase();
                }
            }
            nextPlayer();
            isGameEnded();
            return true; // Player plays again
        } else {
            // No trio, turn is over for the next player
            clearRevealedCards();
            nextPlayer();
            return false;
        }
    }

    private void checkAndEndSwapPhase() {
        if (playersWhoHaveSwapped.size() >= playersAllowedToSwap.size()) {
            if (currentPhase == GamePhase.INITIAL_SWAP) {
                System.out.println("Phase d'échange initiale terminée. La partie commence !");
            } else { // POST_TRIO_SWAP
                System.out.println("Phase d'échange terminée. Le jeu reprend.");
            }
            this.currentPhase = GamePhase.PLAYING;
            this.playersAllowedToSwap.clear();
            this.playersWhoHaveSwapped.clear();
        }
    }

    private void clearRevealedCards() {
        for (CardLocation card : revealedCards) {
            card.getCard().toggleIterable();
        }
        this.revealedCards.clear();
        this.cardsInPlayThisTurn.clear();
    }

    private void revealCardFromPlayer(Card card, Actor player) {
        if (canRevealCard() && !cardsInPlayThisTurn.contains(card)) {
            this.revealedCards.add(CardLocation.fromPlayer(card, player));
            this.cardsInPlayThisTurn.add(card);
        }
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
}
