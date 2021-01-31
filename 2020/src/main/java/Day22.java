import fileUtils.FileReader;
import lombok.Data;
import lombok.Value;

import java.util.*;

public class Day22 {

    @Value
    private static class Decks {
        Deque<Integer> queue1;
        Deque<Integer> queue2;

        @ Override
        public String toString() {
            return queue1.toString() + queue2.toString();
        }
    }

    private static int numberOfInstantWins = 0;

    private static int numberOfGamesStarted = 0;

    @Data
    private static class Game {
        int gameNumber;
        Decks currentDecks;
        Set<String> previousDecks = new HashSet<>();
        Integer cardInPlay1 = null;
        Integer cardInPlay2 = null;

        public Game(Decks currentDecks) {
            this.gameNumber = numberOfGamesStarted + 1;
            numberOfGamesStarted += 1;
            this.currentDecks = currentDecks;
        }

            int playGame() {
            System.out.printf("=== Game %s ===%n", gameNumber);
            int roundnumber = 1;
            int gameWinner;
            while(true) {
                System.out.println();
                System.out.printf("-- Round %s (Game %s) --%n", roundnumber, gameNumber);

                //if same configuration, player 1 wins
                if (previousDecks.contains(currentDecks.toString())) {
                    numberOfInstantWins += 1;
                    gameWinner = 1;
                    break;
                }
                previousDecks.add(currentDecks.toString());

                //get decks
                Deque<Integer> queue1 = currentDecks.queue1;
                Deque<Integer> queue2 = currentDecks.queue2;
                System.out.println("Player 1's deck: " + queue1.toString());
                System.out.println("Player 2's deck: " + queue2.toString());

                //play cards
                setCardInPlay1(queue1.pop());
                setCardInPlay2(queue2.pop());
                System.out.println("Player 1 plays: " + cardInPlay1);
                System.out.println("Player 2 plays: " + cardInPlay2);

                //determine winner of the round
                int roundWinner;
                if (queue1.size() >= cardInPlay1 && queue2.size() >= cardInPlay2) {
                    System.out.println("Playing a sub-game to determine the winner...");
                    System.out.println();
                    Deque<Integer> subQueue1 = new ArrayDeque<>(queue1);
                    for (int i = 0; i < queue1.size() - cardInPlay1; i++) {
                        subQueue1.removeLast();
                    }
                    Deque<Integer> subQueue2 = new ArrayDeque<>(queue2);
                    for (int i = 0; i < queue2.size() - cardInPlay2; i++) {
                        subQueue2.removeLast();
                    }
                    roundWinner = new Game(new Decks(subQueue1, subQueue2)).playGame();
                    System.out.printf("...anyway, back to game %s.%n", gameNumber);
                }
                else if (cardInPlay1 > cardInPlay2) {
                    roundWinner = 1;
                } else {
                    roundWinner = 2;
                }
                switch (roundWinner) {
                    case 1: {
                        queue1.addLast(cardInPlay1);
                        queue1.addLast(cardInPlay2);
                        System.out.printf("Player 1 wins round %s of game %s!", roundnumber, gameNumber);
                        System.out.println();
                    } break;
                    case 2: {
                        queue2.addLast(cardInPlay2);
                        queue2.addLast(cardInPlay1);
                        System.out.printf("Player 2 wins round %s of game %s!", roundnumber, gameNumber);
                        System.out.println();
                    } break;
                }

                // if a player has all the cards, he wins
                if (queue2.isEmpty()) {
                    gameWinner = 1;
                    break;
                }
                if (queue1.isEmpty()) {
                    gameWinner = 2;
                    break;
                }

                // prepare next round
                currentDecks = new Decks(new ArrayDeque<>(currentDecks.queue1), new ArrayDeque<>(currentDecks.queue2));
                roundnumber += 1;
            }
            System.out.printf("The winner of game %s is player %s!%n", gameNumber, gameWinner);
            System.out.println();
            return gameWinner;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input22.csv");
        Game mainGame = getInitialGame(lines);
        int winner = mainGame.playGame();
        determineScore(mainGame, winner);
    }

    private static Game getInitialGame(List<String> lines ) {
        int splitIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty()) {
                splitIndex = i;
                break;
            }
        }
        Decks firstDecks = new Decks(new ArrayDeque<>(), new ArrayDeque<>());
        lines.subList(1, splitIndex).forEach(line -> firstDecks.queue1.addLast(Integer.parseInt(line)));
        lines.subList(splitIndex + 2, lines.size()).forEach(line -> firstDecks.queue2.addLast(Integer.parseInt(line)));
        return new Game(firstDecks);
    }

    private static void determineScore(Game game, int winner) {
        System.out.println();
        System.out.println();
        System.out.println("== Post-game results ==");
        System.out.println("Player 1's deck: " + game.currentDecks.queue1.toString());
        System.out.println("Player 2's deck: " + game.currentDecks.queue2.toString());
        Deque<Integer> winningQueue = winner == 1 ? game.currentDecks.queue1 : game.currentDecks.queue2;

        int score = 0;
        List<Integer> winningNumbers = new ArrayList<>(winningQueue);
        for (int i = 0; i < winningNumbers.size(); i++) {
            score += winningNumbers.get(i) * (winningNumbers.size() - i);
        }
        System.out.println("Winning score: " + score);
        System.out.println("Number of instant wins: " + numberOfInstantWins);
    }
}
