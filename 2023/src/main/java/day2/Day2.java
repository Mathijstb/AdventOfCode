package day2;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day2 {

    private static final int MAX_RED_CUBES = 12;
    private static final int MAX_GREEN_CUBES = 13;
    private static final int MAX_BLUE_CUBES = 14;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        var games = parseLines(lines);
        var possibleGames = determinePossibleGames(games);
        System.out.println("Number of possible games: " + possibleGames.size());
        var sumOfIds = possibleGames.stream().map(Game::id).reduce(0, Integer::sum);
        System.out.println("Sum of Id's: " + sumOfIds);

        int result = determineSumOfMultiplicationOfMaxNumberOfCubes(games);
        System.out.println("Sum of multiplication of minimum number of cubes: " + result);
    }

    private static int determineSumOfMultiplicationOfMaxNumberOfCubes(List<Game> games) {
        return games.stream().map(game -> {
            int maxRed = game.samples().stream().map(sample -> sample.colorMap().get(Color.RED)).reduce(0, Math::max);
            int maxGreen = game.samples().stream().map(sample -> sample.colorMap().get(Color.GREEN)).reduce(0, Math::max);
            int maxBlue = game.samples().stream().map(sample -> sample.colorMap().get(Color.BLUE)).reduce(0, Math::max);
            return maxRed * maxGreen * maxBlue;
        }).reduce(0, Integer::sum);
    }

    private static List<Game> parseLines(List<String> lines) {
        AtomicInteger id = new AtomicInteger(1);
        return lines.stream().map(line -> {
            var sampleList = new ArrayList<Sample>();
            var samples = line.split(": ")[1].split("; ");
            for (String sample : samples) {
                var colorMap = new HashMap<Color, Integer>();
                colorMap.put(Color.RED, 0);
                colorMap.put(Color.BLUE, 0);
                colorMap.put(Color.GREEN, 0);
                var colors = sample.split(", ");
                for (String colorString : colors) {
                    var amountAndColor = colorString.split(" ");
                    int amount = Integer.parseInt(amountAndColor[0]);
                    var color = Color.of(amountAndColor[1]);
                    colorMap.put(color, amount);
                }
                sampleList.add(new Sample(colorMap));
            }
            return new Game(id.getAndIncrement(), sampleList);
        }).toList();
    }



    private static List<Game> determinePossibleGames(List<Game> games) {
        return games.stream().filter(Day2::isPossible).toList();
    }

    private static boolean isPossible(Game game) {
        return game.samples().stream().allMatch(sample ->
            sample.colorMap().get(Color.RED) <= MAX_RED_CUBES &&
                    sample.colorMap().get(Color.GREEN) <= MAX_GREEN_CUBES &&
                    sample.colorMap().get(Color.BLUE) <= MAX_BLUE_CUBES
        );
    }
}
