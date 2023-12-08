package day4;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.IntStream;

public class Day4 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");
        var cards = lines.stream().map(line -> {
            var nameAndNumbers = line.split(":\\s+");
            var id = Integer.parseInt(nameAndNumbers[0].split("\\s+")[1]);
            var winningAndOwnedNumbers = nameAndNumbers[1].split("\\s+\\|\\s+");
            var winning = Arrays.stream(winningAndOwnedNumbers[0].split("\\s+")).map(Integer::parseInt).toList();
            var owned = Arrays.stream(winningAndOwnedNumbers[1].split("\\s+")).map(Integer::parseInt).toList();
            return new Card(id, winning, owned);
        }).toList();
        var score = determineScore(cards);
        System.out.println("Score: " + score);
        System.out.println();
        processCards(cards);
    }

    private static long determineScore(List<Card> cards) {
        return cards.stream().map(card -> {
            long numberOfMatchingNumbers = findNumberOfMatchingNumbers(card);
            return numberOfMatchingNumbers == 0 ? 0 : (long) Math.pow(2, numberOfMatchingNumbers-1);
        }).reduce(0L, Long::sum);
    }

    private static int findNumberOfMatchingNumbers(Card card) {
        return card.ownedNumbers().stream()
                .filter(ownedNumber -> card.winningNumbers().contains(ownedNumber))
                .toList()
                .size();
    }

    private static void processCards(List<Card> cards) {
        var cardAmountList = new ArrayList<>(IntStream.range(0, cards.size()).mapToObj(i -> 1).toList());
        for (int index = 0; index < cards.size(); index++) {
            var card = cards.get(index);
            int numberOfMatchingNumbers = findNumberOfMatchingNumbers(card);
            if (numberOfMatchingNumbers > 0) {
                int numberOfCopies = cardAmountList.get(index);
                for (int nextCardIndex = index + 1; nextCardIndex <= index + numberOfMatchingNumbers; nextCardIndex++) {
                    cardAmountList.set(nextCardIndex, cardAmountList.get(nextCardIndex) + numberOfCopies);
                }
            }
        }
        var totalNumberOfCopies = cardAmountList.stream().reduce(0, Integer::sum);
        System.out.println("Total number of copies: " + totalNumberOfCopies);
    }
}
