package fr.utbm.ap4b.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe principale gérant la logique et l'état d'une partie de Trio.
 * Elle agit comme le "Cerveau" du jeu, validant les règles, gérant les tours
 * et stockant l'état global (joueurs, pioche, cartes révélées).
 */
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


    /**
     * Constructeur de la partie.
     * Initialise les paramètres de base et valide la configuration (nombre de joueurs, mode de jeu).
     *
     * @param playerNames Liste des noms des joueurs humains.
     * @param numAI Nombre d'intelligences artificielles à ajouter.
     * @param isTeamMode Vrai si le mode équipe est activé.
     * @param isPiquant Vrai si le mode "Piquant" est activé.
     * @throws IllegalArgumentException Si la configuration des joueurs est invalide.
     */
    public Game(List<String> playerNames, int numAI, boolean isTeamMode, boolean isPiquant) {
        if (playerNames == null) {
            throw new IllegalArgumentException("La liste des noms de joueurs ne peut pas être nulle.");
        }

        this.playerNames = playerNames;
        this.numAI = numAI;
        this.numPlayers = playerNames.size() + numAI;

        // Validation des règles de nombre de joueurs
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

        // Initialisation des champs liés aux échanges (Swap)
        this.playersAllowedToSwap = new HashSet<>();
        this.playersWhoHaveSwapped = new HashSet<>();
        this.currentPhase = GamePhase.PLAYING; // Phase par défaut

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
    
    /**
     * Calcule l'ensemble des joueurs qui sont autorisés à échanger mais ne l'ont pas encore fait.
     * @return Un Set contenant les acteurs en attente d'échange.
     */
    public Set<Actor> getPlayersWhoHaveNotSwapped() {
        Set<Actor> pending = new HashSet<>(playersAllowedToSwap);
        pending.removeAll(playersWhoHaveSwapped);
        return pending;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    /**
     * Vérifie si la partie est terminée en interrogeant le gestionnaire de trios (CompletedTrios).
     * Met à jour le gagnant si la partie est finie.
     * @return Vrai si un gagnant a été déterminé.
     */
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

    /**
     * Passe la main au joueur suivant dans la liste de manière cyclique.
     */
    public void nextPlayer() {
        this.playerTurn = (this.playerTurn + 1) % this.players.size();
    }

    /**
     * Tente de révéler une carte spécifique depuis la pioche.
     * Vérifie d'abord si l'action est autorisée.
     * 
     * @param card La carte à révéler.
     */
    public void revealCardFromDrawPile(Card card) {
        if (canRevealCard() && !cardsInPlayThisTurn.contains(card)) {
            this.revealedCards.add(CardLocation.fromDrawPile(card, this.drawPile));
            this.cardsInPlayThisTurn.add(card);
        }
    }

    /**
     * Vérifie si le joueur courant a le droit de révéler une nouvelle carte.
     * Les règles sont :
     * 1. On doit être en phase de jeu (pas d'échange).
     * 2. On ne peut pas révéler plus de 3 cartes.
     * 3. Si 2 cartes sont déjà révélées, elles doivent être identiques pour continuer.
     * 
     * @return Vrai si la révélation est autorisée.
     */
    public boolean canRevealCard() {
        if (currentPhase != GamePhase.PLAYING) return false; // Impossible de jouer pendant un échange
        if (revealedCards.size() >= 3) {
            return false;
        }
        // Règle critique : Si les deux premières cartes sont différentes, le tour s'arrête.
        if (revealedCards.size() == 2) {
            Card card1 = revealedCards.get(0).getCard();
            Card card2 = revealedCards.get(1).getCard();
            if (card1.getValue() != card2.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Traite un trio validé.
     * Retire les cartes concernées de leurs emplacements respectifs (mains des joueurs ou pioche).
     * 
     * @param trio La liste des 3 cartes formant le trio.
     */
    public void processTrio(List<Card> trio) {
        if (trio == null || trio.size() != 3) return;

        for (Card card : trio) {
            // Trouve l'emplacement de la carte révélée et la retire de la source (Main ou Pioche)
            revealedCards.stream()
                    .filter(loc -> loc.getCard().equals(card))
                    .findFirst()
                    .ifPresent(CardLocation::removeFromSource);
        }

        clearRevealedCards();
    }

    /**
     * Révèle la plus grande carte de la main d'un joueur cible.
     * @param playerIndex L'index du joueur ciblé.
     */
    public void revealLargestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getLargestCard();
        if (card != null) {
            revealCardFromPlayer(card, player);
        }
    }

    /**
     * Révèle la plus petite carte de la main d'un joueur cible.
     * @param playerIndex L'index du joueur ciblé.
     */
    public void revealSmallestCardFromPlayer(int playerIndex) {
        Actor player = players.get(playerIndex);
        Card card = player.getHand().getSmallestCard();
        if (card != null) {
            revealCardFromPlayer(card, player);
        }
    }

    /**
     * Démarre officiellement la partie.
     * Distribue les cartes aux joueurs et détermine la première phase de jeu
     * (Phase d'échange si mode équipe, sinon phase de jeu normale).
     */
    public void startGame() {
        if (isGameStarted) return;
        
        // Distribution des cartes
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

    /**
     * Gère l'échange de cartes entre deux coéquipiers.
     * Cette méthode contient toute la logique de validation de l'échange (bonnes cartes, bons joueurs, bonne phase).
     *
     * @param initiatingPlayerIndex L'index du joueur qui initie l'échange.
     * @param cardToGive La carte donnée par l'initiateur.
     * @param cardToReceive La carte reçue du coéquipier.
     * @return Vrai si l'échange a réussi, Faux sinon.
     */
    public boolean exchangeCards(int initiatingPlayerIndex, Card cardToGive, Card cardToReceive) {
        // Vérifications préliminaires sur la phase et le mode
        if (currentPhase == GamePhase.PLAYING || !isTeamMode) {
            return false; 
        }

        Actor initiator = players.get(initiatingPlayerIndex);
        // Vérifie si le joueur a déjà échangé ou n'est pas un joueur d'équipe
        if (!(initiator instanceof JoueurEquipe) || playersWhoHaveSwapped.contains(initiator)) {
            return false; 
        }

        JoueurEquipe teamInitiator = (JoueurEquipe) initiator;
        JoueurEquipe teammate = teamInitiator.getTeammate();

        if (teammate == null || !playersAllowedToSwap.contains(initiator)) {
            return false; // Pas de coéquipier ou équipe non autorisée à échanger
        }

        // Vérifie que les joueurs possèdent bien les cartes qu'ils prétendent échanger
        if (!teamInitiator.getHand().getCards().contains(cardToGive) || !teammate.getHand().getCards().contains(cardToReceive)) {
            return false;
        }

        // Exécution de l'échange physique des cartes
        teamInitiator.getHand().removeCard(cardToGive);
        teamInitiator.getHand().addCard(cardToReceive);
        teammate.getHand().removeCard(cardToReceive);
        teammate.getHand().addCard(cardToGive);

        // Marque les joueurs comme ayant effectué leur action pour cette phase
        playersWhoHaveSwapped.add(teamInitiator);
        playersWhoHaveSwapped.add(teammate);

        System.out.println(teamInitiator.getName() + " et " + teammate.getName() + " ont échangé des cartes.");

        // Vérifie si tous les échanges sont terminés pour changer de phase
        checkAndEndSwapPhase();

        return true;
    }


    /**
     * Gère la fin du tour d'un joueur.
     * Vérifie si un trio a été formé, attribue les points, et gère les transitions de phase (ex: échange après trio).
     * 
     * @return Vrai si le joueur rejoue (car il a fait un trio), Faux si c'est au tour du joueur suivant.
     */
    public boolean nextTurn() {
        if (this.isGameEnded() || !this.isGameStarted) {
            return false;
        }

        // Vérification de la présence d'un trio dans les cartes révélées
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
            
            // Enregistrement du trio et nettoyage du plateau
            completedTrios.addTrio(scoringPlayer.getPlayerIndex(), trio);
            processTrio(trio); 

            // Gestion spécifique au mode équipe : Phase d'échange punitive pour les adversaires
            if (isTeamMode) {
                System.out.println("Un trio a été formé ! Phase d'échange pour les équipes adverses.");
                this.currentPhase = GamePhase.POST_TRIO_SWAP;
                this.playersWhoHaveSwapped.clear();
                this.playersAllowedToSwap.clear();

                JoueurEquipe scoringTeamPlayer = (JoueurEquipe) scoringPlayer;
                // Autorise toutes les équipes SAUF celle qui vient de marquer
                for (Actor player : players) {
                    if (player != scoringTeamPlayer && player != scoringTeamPlayer.getTeammate()) {
                        playersAllowedToSwap.add(player);
                    }
                }
                // S'il n'y a personne pour échanger (ex: partie à 2 équipes), on passe
                if (playersAllowedToSwap.isEmpty()) {
                    checkAndEndSwapPhase();
                }
            }
            
            // Après un trio, fin du tour et passage au joueur suivant
            nextPlayer();
            isGameEnded();
            return true; 
        } else {
            // Pas de trio, fin du tour standard
            clearRevealedCards();
            nextPlayer();
            return false;
        }
    }

    /**
     * Vérifie si la phase d'échange est terminée (tous les joueurs autorisés ont échangé).
     * Si oui, bascule le jeu en phase PLAYING.
     */
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

    /**
     * Réinitialise l'état des cartes révélées (les cache visuellement et vide la liste).
     */
    private void clearRevealedCards() {
        for (CardLocation card : revealedCards) {
            card.getCard().toggleIterable(); // Remet l'état "iterable" (probablement pour l'affichage)
        }
        this.revealedCards.clear();
        this.cardsInPlayThisTurn.clear();
    }

    /**
     * Méthode utilitaire interne pour révéler une carte d'un joueur.
     */
    private void revealCardFromPlayer(Card card, Actor player) {
        if (canRevealCard() && !cardsInPlayThisTurn.contains(card)) {
            this.revealedCards.add(CardLocation.fromPlayer(card, player));
            this.cardsInPlayThisTurn.add(card);
        }
    }

    /**
     * Initialise la liste des joueurs et configure les équipes si nécessaire.
     */
    private void initializePlayers() {
        this.players = new ArrayList<>();
        if (isTeamMode) {
            int playerIndex = 0;
            for (String name : playerNames) {
                players.add(new JoueurEquipe(name, playerIndex++));
            }

            // Appariement des coéquipiers (Joueur i avec Joueur i + offset)
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
