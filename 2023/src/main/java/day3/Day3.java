package day3;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Day3 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        FiniteGrid<Symbol> grid = new FiniteGrid<>();
        lines.forEach(line -> grid.addRow(Chars.asList(line.toCharArray()).stream().map(Symbol::new).toList()));
        grid.draw(s -> String.valueOf(s.character()));
        System.out.println();
        determineAdjacentNumbers(grid);
        determineCogValues(grid);
    }

    private static void determineCogValues(FiniteGrid<Symbol> grid) {
        Map<Point, Set<Point>> starToNumberNeighboursMap = new HashMap<>();
        getPointsWithStartingDigits(grid)
                .forEach(startingDigit ->
                        IntStream.range(0, getNumberSize(grid, startingDigit))
                                .mapToObj(i -> new Point(startingDigit.x + i, startingDigit.y))
                                //find star neighbours
                                .forEach(p -> grid.getNeighbours(p, true, n -> n.character() == '*')
                                        // for each star, add starting digit location of number to set
                                        .forEach(starPoint -> {
                                            var numberNeighbours = starToNumberNeighboursMap.getOrDefault(starPoint, new HashSet<>());
                                            numberNeighbours.add(startingDigit);
                                            starToNumberNeighboursMap.put(starPoint, numberNeighbours);
                                    })
                            )
                );

        var gears = starToNumberNeighboursMap.keySet().stream()
                .filter(point -> starToNumberNeighboursMap.get(point).size() == 2);
        var sumOfGearRatios = gears.map(g ->
            starToNumberNeighboursMap.get(g).stream()
                    .map(n -> (long)(getNumber(grid, n, getNumberSize(grid, n))))
                    .reduce(1L, (a,b) -> (a * b))
        ).reduce(0L, Long::sum);
        System.out.println("Sum of gear ratios: " + sumOfGearRatios);
    }

    private static void determineAdjacentNumbers(FiniteGrid<Symbol> grid) {
        var pointsWithStartingDigits = getPointsWithStartingDigits(grid);
        AtomicInteger total = new AtomicInteger();
        pointsWithStartingDigits.forEach(point -> {
            var digitSize = getNumberSize(grid, point);
            if (isAdjacentToSymbol(grid, point, digitSize)) {
                var number = getNumber(grid, point, digitSize);
                total.addAndGet(number);
            }
        });
        System.out.println("Sum of adjacent numbers: " + total);
    }

    private static List<Point> getPointsWithStartingDigits(FiniteGrid<Symbol> grid) {
        return grid.getAllPoints().stream()
                .filter(p -> grid.getValue(p).isDigit())
                .filter(p -> p.x == 0 || !grid.getValue(new Point(p.x-1, p.y)).isDigit())
                .toList();
    }

    private static int getNumberSize(FiniteGrid<Symbol> grid, Point point) {
        if (!grid.getValue(point).isDigit()) {
            return 0;
        } else if (point.x >= grid.getWidth() - 1) {
            return 1;
        } else {
            return 1 + getNumberSize(grid, new Point(point.x + 1, point.y));
        }
    }

    private static int getNumber(FiniteGrid<Symbol> grid, Point point, int digitSize) {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < digitSize; i++) {
            number.append(grid.getValue(new Point(point.x + i, point.y)).character());
        }
        return Integer.parseInt(number.toString());
    }

    private static boolean isAdjacentToSymbol(FiniteGrid<Symbol> grid, Point point, int digitSize) {
        return IntStream.range(0, digitSize)
                .mapToObj(i -> new Point(point.x + i, point.y))
                .map(p -> grid.getNeighbourValues(p, true))
                .anyMatch(symbols -> symbols.stream()
                        .anyMatch(Symbol::isSymbol));
    }
}
