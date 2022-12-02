package day2;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;

public class Day2 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");

        var strategies1 = new ArrayList<Strategy1>();
        var strategies2 = new ArrayList<Strategy2>();
        lines.forEach(line -> {
            var chars = line.split(" ");
            strategies1.add(Strategy1.valueOf(chars[0]));
            strategies2.add(Strategy2.valueOf(chars[1]));
        });

        //Part1
        determineScore(strategies1, strategies2);

        //Part 2
        var newStrategies2 = determineNewStrategies2(strategies1, strategies2);
        System.out.println();
        determineScore(strategies1, newStrategies2);
    }

    private static List<Strategy2> determineNewStrategies2(List<Strategy1> strategies1, List<Strategy2> strategies2) {
        var newStrategies2 = new ArrayList<Strategy2>();
        for (int round = 0; round < strategies2.size(); round++) {
            var strategy1 = strategies1.get(round);
            var strategy2 = strategies2.get(round);
            Result expectedResult = switch (strategy2) {
                case X -> Result.LOSE;
                case Y -> Result.DRAW;
                case Z -> Result.WIN;
            };
            var newStrategy2 = determineNewStrategy2(strategy1, expectedResult);
            newStrategies2.add(newStrategy2);
        }
        return newStrategies2;
    }

    private static Strategy2 determineNewStrategy2(Strategy1 strategy1, Result expectedResult) {
        return switch (strategy1) {
            //A = Rock
            case A -> switch (expectedResult) {
                case WIN -> Strategy2.Y;
                case DRAW -> Strategy2.X;
                case LOSE -> Strategy2.Z;
            };
            //B = Paper
            case B -> switch (expectedResult) {
                case WIN -> Strategy2.Z;
                case DRAW -> Strategy2.Y;
                case LOSE -> Strategy2.X;
            };
            //C = Scissors
            case C -> switch (expectedResult) {
                case WIN -> Strategy2.X;
                case DRAW -> Strategy2.Z;
                case LOSE -> Strategy2.Y;
            };
        };
    }

    private static void determineScore(List<Strategy1> strategies1, List<Strategy2> strategies2) {
        long score = 0;
        for (int round = 0; round < strategies1.size(); round++) {
            var strategy1 = strategies1.get(round);
            var strategy2 = strategies2.get(round);
            var result = getStrategy2Result(strategy1, strategy2);

            score += getScoreForResult(result) + getScoreForStrategy(strategy2);
        }
        System.out.println("Total score: " + score);
    }

    private static int getScoreForResult(Result result) {
        return switch (result) {
            case WIN -> 6;
            case DRAW -> 3;
            case LOSE -> 0;
        };
    }

    private static int getScoreForStrategy(Strategy2 strategy2) {
        return switch (strategy2) {
            case X -> 1;
            case Y -> 2;
            case Z -> 3;
        };
    }

    private static Result getStrategy2Result(Strategy1 strategy1, Strategy2 strategy2) {
        return switch (strategy1) {
            //A = rock
            case A -> switch (strategy2) {
                case X -> Result.DRAW;
                case Y -> Result.WIN;
                case Z -> Result.LOSE;
            };
            //B = paper
            case B -> switch (strategy2) {
                case X -> Result.LOSE;
                case Y -> Result.DRAW;
                case Z -> Result.WIN;
            };
            //C = scissors
            case C -> switch (strategy2) {
                case X -> Result.WIN;
                case Y -> Result.LOSE;
                case Z -> Result.DRAW;
            };
        };
    }



}
