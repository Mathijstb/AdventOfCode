package day7;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day7 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input7.csv");
        var handAndBids = parseLines(lines);
        var sortedByStrength = sortByStrength(handAndBids, Day7::determineStrength, card -> card.rank);
        var score = getScore(sortedByStrength);
        System.out.println("Score: " + score);

        System.out.println();
        var sortedByStrength2 = sortByStrength(handAndBids, Day7::determineStrength2, Card::alternativeRank);
        var score2 = getScore(sortedByStrength2);
        System.out.println("Score 2: " + score2);
    }

    private static List<HandAndBid> parseLines(List<String> lines) {
        return lines.stream().map(line -> {
            var handAndBid = line.split(" ");
            var ranks = Chars.asList(handAndBid[0].toCharArray()).stream().map(Card::of).toList();
            var bid = Integer.parseInt(handAndBid[1]);
            return new HandAndBid(ranks, bid);
        }).toList();
    }

    private static List<HandAndBid> sortByStrength(List<HandAndBid> handAndBids, Function<HandAndBid, Strength> strengthFunction,
                                                   Function<Card, Integer> rankFunction) {
        var result = new ArrayList<>(handAndBids);
        result.sort(Comparator.comparing(strengthFunction)
                .thenComparing(handAndBid -> -rankFunction.apply(handAndBid.hand().get(0)))
                .thenComparing(handAndBid -> -rankFunction.apply(handAndBid.hand().get(1)))
                .thenComparing(handAndBid -> -rankFunction.apply(handAndBid.hand().get(2)))
                .thenComparing(handAndBid -> -rankFunction.apply(handAndBid.hand().get(3)))
                .thenComparing(handAndBid -> -rankFunction.apply(handAndBid.hand().get(4))));
        return result;
    }

    private static long getScore(List<HandAndBid> handAndBids) {
        return IntStream.range(0, handAndBids.size())
                .mapToObj(i -> (long) handAndBids.get(i).bid() * (handAndBids.size() - i))
                .reduce(0L, Long::sum);
    }

    private static Strength determineStrength(HandAndBid handAndBid) {
        var hand = handAndBid.hand();
        Map<Card, Integer> cardCount = new HashMap<>();
        hand.forEach(card -> cardCount.put(card, cardCount.getOrDefault(card, 0) + 1));
        if (cardCount.values().stream().anyMatch(v -> v.equals(5))) {
            return Strength.FIVE_OF_A_KIND;
        } else if (cardCount.values().stream().anyMatch(v -> v.equals(4))) {
            return Strength.FOUR_OF_A_KIND;
        } else if (cardCount.values().stream().anyMatch(v -> v.equals(3)) &&
                  (cardCount.values().stream().anyMatch(v -> v.equals(2)))) {
            return Strength.FULL_HOUSE;
        } else if (cardCount.values().stream().anyMatch(v -> v.equals(3))) {
            return Strength.THREE_OF_A_KIND;
        } else if (cardCount.values().stream().filter(v -> v.equals(2)).count() == 2) {
            return Strength.TWO_PAIR;
        } else if (cardCount.values().stream().filter(v -> v.equals(2)).count() == 1) {
            return Strength.ONE_PAIR;
        } else {
            return Strength.HIGH_CARD;
        }
    }

    private static Strength determineStrength2(HandAndBid handAndBid) {
        var hand = handAndBid.hand();
        Map<Card, Integer> cardCount = new HashMap<>();
        hand.forEach(card -> cardCount.put(card, cardCount.getOrDefault(card, 0) + 1));
        var numberOfJokers = cardCount.getOrDefault(Card.J, 0);
        cardCount.put(Card.J, 0);
        if (cardCount.values().stream().anyMatch(v -> v + numberOfJokers == 5)) {
            return Strength.FIVE_OF_A_KIND;
        } else if (cardCount.values().stream().anyMatch(v -> v + numberOfJokers == 4)) {
            return Strength.FOUR_OF_A_KIND;
        } else if (
                switch (numberOfJokers) {
                    case 0: yield cardCount.values().stream().anyMatch(v -> v.equals(3)) &&
                            cardCount.values().stream().anyMatch(v -> v.equals(2));
                    case 1: yield cardCount.values().stream().filter(v -> v == 2).count() == 2;
                    case 2: yield cardCount.values().stream().anyMatch(v -> v.equals(2));
                    case 3: yield true;
                    default: throw new IllegalStateException("Can not have more than 3 jokers at this point");}) {
            return Strength.FULL_HOUSE;
        } else if (cardCount.values().stream().anyMatch(v -> v + numberOfJokers == 3))  {
            return Strength.THREE_OF_A_KIND;
        } else if (
                switch (numberOfJokers) {
                    case 0: yield cardCount.values().stream().filter(v -> v.equals(2)).count() == 2;
                    case 1: yield cardCount.values().stream().anyMatch(v -> v == 2);
                    default: throw new IllegalStateException("Can not have more than 1 joker at this point");}) {
            return Strength.TWO_PAIR;
        } else if (cardCount.values().stream().anyMatch(v -> v + numberOfJokers == 2)) {
            return Strength.ONE_PAIR;
        } else {
            assert cardCount.values().stream().allMatch(v -> v <= 1);
            assert numberOfJokers == 0;
            return Strength.HIGH_CARD;
        }
    }
}
