package day4;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;

public class Day4 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");
        List<Integer> draw = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        List<Card> cards = readCards(lines);

        //Determine winning cards
        List<Card> winningCards = cards.stream().filter(card -> card.getOptionalWinningRound(draw).isPresent()).collect(Collectors.toList());

        //Determine first winning card and print score
        int minWinningRound = winningCards.stream().map(card -> card.getOptionalWinningRound(draw).orElseThrow()).mapToInt(v -> v).min().orElseThrow();
        List<Card> firstWinningCards = cards.stream().filter(card -> card.getOptionalWinningRound(draw).orElseThrow().equals(minWinningRound)).collect(Collectors.toList());

        assertEquals(1, firstWinningCards.size());
        Card firstWinningCard = firstWinningCards.get(0);
        printScore(firstWinningCard, minWinningRound, draw);

        //Determine last winning card and print score
        int maxWinningRound = winningCards.stream().map(card -> card.getOptionalWinningRound(draw).orElseThrow()).mapToInt(v -> v).max().orElseThrow();
        List<Card> lastWinningCards = cards.stream().filter(card -> card.getOptionalWinningRound(draw).orElseThrow().equals(maxWinningRound)).collect(Collectors.toList());

        assertEquals(1, lastWinningCards.size());
        Card lastWinningCard = lastWinningCards.get(0);
        printScore(lastWinningCard, maxWinningRound, draw);
    }

    private static void printScore(Card firstWinningCard, int winningRound, List<Integer> draw) {
        List<Integer> winningNumbers = draw.subList(0, winningRound + 1);
        List<Integer> unMarkedNumbers = firstWinningCard.getNumbers();
        unMarkedNumbers.removeAll(winningNumbers);
        int sum = unMarkedNumbers.stream().mapToInt(v -> v).sum();
        int lastWinnningNumber = winningNumbers.get(winningNumbers.size() -1);
        System.out.println("Score: " + sum * lastWinnningNumber);
    }

    private static List<Card> readCards(List<String> lines) {
        int numberOfCards = (lines.size() - 1) / 6;
        int cardIndex = 2;

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            List<List<Integer>> grid = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                String line = lines.get(cardIndex + j).strip();
                List<Integer> gridLine = Arrays.stream(line.split("[\\s]+")).map(Integer::parseInt).collect(Collectors.toList());
                grid.add(gridLine);
            }
            cards.add(new Card(grid));
            cardIndex += 6;
        }
        return cards;
    }
}
