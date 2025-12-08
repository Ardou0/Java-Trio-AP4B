package fr.utbm.ap4b;

import fr.utbm.ap4b.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choisissez un mode de jeu :");
        System.out.println("1: Mode Solo (3 Joueurs)");
        System.out.println("2: Mode Équipe (4 Joueurs, 2v2)");
        System.out.print("Votre choix : ");

        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            runSoloGame(scanner);
        } else if ("2".equals(choice)) {
            runTeamGame(scanner);
        } else {
            System.out.println("Choix invalide.");
        }

        scanner.close();
    }

    private static void runSoloGame(Scanner scanner) {
        List<String> playerNames = Arrays.asList("Joueur 1", "Joueur 2", "Joueur 3");
        Game game = new Game(playerNames, 0, false, false);
        System.out.println("\nLancement d'une partie en Mode Solo.");
        game.startGame();
        
        // The rest of the solo game logic is identical to the playing turn logic
        runGameLoop(game, scanner);
    }

    private static void runTeamGame(Scanner scanner) {
        List<String> playerNames = Arrays.asList("Joueur A1", "Joueur B1", "Joueur A2", "Joueur B2");
        Game game = new Game(playerNames, 0, true, false);
        System.out.println("\nLancement d'une partie en Mode Équipe.");
        System.out.println("Équipe A: Joueur A1 & Joueur A2 | Équipe B: Joueur B1 & Joueur B2");
        game.startGame();

        runGameLoop(game, scanner);
    }

    private static void runGameLoop(Game game, Scanner scanner) {
        int turnCounter = 0;
        while (!game.isGameEnded() && turnCounter < 200) { // Safety break
            turnCounter++;

            if (game.getCurrentPhase() != Game.GamePhase.PLAYING) {
                handleSwapPhase(game, scanner);
                continue; // Restart the loop to re-evaluate the phase
            }

            // --- Playing Phase ---
            Actor currentPlayer = game.getCurrentPlayer();
            System.out.println("\n-----------------------------------");
            System.out.println("--- Tour " + turnCounter + ": C'est au tour de " + currentPlayer.getName() + " ---");

            String hand = currentPlayer.getHand().getCards().stream()
                    .map(card -> String.valueOf(card.getValue()))
                    .collect(Collectors.joining(", "));
            System.out.println("Votre main : [" + hand + "]");

            handlePlayingTurn(game, scanner);

            // Process turn result
            boolean trioFormed = game.nextTurn();
            if (trioFormed) {
                System.out.println("Bravo, un trio a été formé !");
                // In team mode, this triggers a swap phase for opponents. In solo, the player just replays.
                if(!game.isTeamMode()){
                    System.out.println(currentPlayer.getName() + " rejoue.");
                }
            } else {
                System.out.println("Pas de trio ce tour-ci. Passage au joueur suivant.");
            }

            // Display scores
            for (Actor p : game.getPlayers()) {
                System.out.println("Trios de " + p.getName() + ": " + game.getCompletedTrios().getTriosForPlayer(p.getPlayerIndex()));
            }
        }

        // Announce winner
        System.out.println("\n-----------------------------------");
        System.out.println("La partie est terminée !");
        Actor winner = game.getWinner();
        if (winner != null) {
            System.out.println("Le gagnant est " + winner.getName() + "!");
        } else {
            System.out.println("La partie s'est terminée sans gagnant.");
        }
    }

    private static void handlePlayingTurn(Game game, Scanner scanner) {
        while (game.canRevealCard()) {
            System.out.println("\nCartes actuellement révélées : " + getRevealedCardsInfo(game));

            if (game.getRevealedCards().size() == 2) {
                System.out.print("Voulez-vous tenter de révéler une troisième carte ? (o/n) : ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("o")) {
                    System.out.println("Vous avez choisi de vous arrêter.");
                    break;
                }
            }

            int sourceChoice = -1;
            int numPlayers = game.getPlayers().size();
            while (sourceChoice == -1) {
                System.out.println("\nDe quelle source voulez-vous révéler une carte ?");
                for (int i = 0; i < numPlayers; i++) System.out.printf("  %d: %s%n", i + 1, game.getPlayers().get(i).getName());
                if (!game.getDrawPile().getRemainingCards().isEmpty()) System.out.printf("  %d: La Pioche%n", numPlayers + 1);
                System.out.print("Votre choix : ");
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    if (choice > 0 && choice <= numPlayers + 1) sourceChoice = choice;
                    else System.out.println("Choix invalide.");
                } catch (NumberFormatException e) { System.out.println("Entrée invalide."); }
            }

            int revealedBefore = game.getRevealedCards().size();
            if (sourceChoice <= numPlayers) { // From a player
                int playerIndex = sourceChoice - 1;
                String cardChoice = "";
                while (!cardChoice.equals("p") && !cardChoice.equals("g")) {
                    System.out.print("Révéler la plus petite ('p') ou la plus grande ('g') carte ? : ");
                    cardChoice = scanner.nextLine().trim().toLowerCase();
                }
                if (cardChoice.equals("p")) game.revealSmallestCardFromPlayer(playerIndex);
                else game.revealLargestCardFromPlayer(playerIndex);
            } else { // From draw pile
                List<Card> drawPileCards = game.getDrawPile().getRemainingCards();
                int cardIndex = -1;
                while (cardIndex < 1 || cardIndex > drawPileCards.size()) {
                    System.out.println("\nQuelle carte de la pioche voulez-vous révéler ? (1-" + drawPileCards.size() + ")");
                    for (int i = 0; i < drawPileCards.size(); i++) System.out.printf("[%d] ", i + 1);
                    System.out.print("\nVotre choix : ");
                    try {
                        cardIndex = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) { System.out.println("Entrée invalide."); }
                }
                game.revealCardFromDrawPile(drawPileCards.get(cardIndex - 1));
            }

            if (game.getRevealedCards().size() == revealedBefore) {
                System.out.println("Action impossible. Veuillez choisir une autre action.");
                continue;
            }

            CardLocation lastRevealed = game.getRevealedCards().get(game.getRevealedCards().size() - 1);
            String sourceName = lastRevealed.getSourcePlayer() != null ? lastRevealed.getSourcePlayer().getName() : "la pioche";
            System.out.println(">> Vous avez révélé la carte : " + lastRevealed.getCard().getValue() + " (de " + sourceName + ")");

            if (!game.canRevealCard() && game.getRevealedCards().size() < 3) {
                System.out.println("Les cartes ne correspondent pas ! Fin de votre tour.");
                break;
            }
        }
    }

    private static void handleSwapPhase(Game game, Scanner scanner) {
        System.out.println("\n--- PHASE D'ÉCHANGE DE CARTES ---");
        Set<Actor> alreadySwappedTeams = new HashSet<>();
        for (Actor player : game.getPlayers()) {
            if (game.getPlayersAllowedToSwap().contains(player) && !alreadySwappedTeams.contains(player)) {
                JoueurEquipe initiator = (JoueurEquipe) player;
                JoueurEquipe teammate = initiator.getTeammate();

                System.out.println("\nC'est au tour de l'équipe de " + initiator.getName() + " et " + teammate.getName() + " d'échanger.");

                // Display hands
                List<Card> initiatorHand = initiator.getHand().getCards();
                List<Card> teammateHand = teammate.getHand().getCards();
                System.out.println(initiator.getName() + ", votre main : " + getHandInfo(initiatorHand));

                // Get card to give
                Card cardToGive = selectCardFromHand(initiator, initiatorHand, "Quelle carte voulez-vous donner ? (1-" + initiatorHand.size() + ")", scanner);

                System.out.println(teammate.getName() + ", votre main : " + getHandInfo(teammateHand));
                // Get card to receive
                Card cardToReceive = selectCardFromHand(teammate, teammateHand, "Quelle carte voulez-vous donner ? (1-" + teammateHand.size() + ")", scanner);

                game.exchangeCards(initiator.getPlayerIndex(), cardToGive, cardToReceive);

                alreadySwappedTeams.add(initiator);
                alreadySwappedTeams.add(teammate);
            }
        }
    }

    private static Card selectCardFromHand(Actor owner, List<Card> hand, String prompt, Scanner scanner) {
        int cardIndex = -1;
        while (cardIndex < 1 || cardIndex > hand.size()) {
            System.out.println(owner.getName() + ", " + prompt);
            try {
                cardIndex = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide.");
            }
        }
        return hand.get(cardIndex - 1);
    }

    private static String getHandInfo(List<Card> hand) {
        return hand.stream()
                .map(card -> String.valueOf(card.getValue()))
                .collect(Collectors.joining(", "));
    }

    private static String getRevealedCardsInfo(Game game) {
        if (game.getRevealedCards().isEmpty()) return "[]";
        return "[" + game.getRevealedCards().stream()
                .map(loc -> String.valueOf(loc.getCard().getValue()))
                .collect(Collectors.joining(", ")) + "]";
    }
}
