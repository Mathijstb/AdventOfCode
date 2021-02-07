package day19;

import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import intCode.IntCodeComputer;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Day19 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        executeProgram(numbers);
    }

    private static void executeProgram(List<String> numbers) {
        findNumberOfCoordinatesAffectedByBeam(numbers);
        Optional<Point> optionalPoint = findFirstPossibleShipCoordinate();
        if (optionalPoint.isPresent()) {
            Point point = optionalPoint.get();
            System.out.printf("Found coordinate! (X: %s, Y: %s)%n", point.x, point.y);
            System.out.println("Value: " + (point.x * 10000 + point.y));
            for (int x = point.x; x < point.x + shipWidth; x++) {
                for (int y = point.y; y < point.y + shipHeight; y++) {
                    beamMap.put(new Point(x, y), 2);
                }
            }
        }
        drawGrid();
    }

    private static final int shipHeight = 100;
    private static final int shipWidth = 100;
    private static final int searchOffsetX = 750;
    private static final int searchOffsetY = 850;


    private static Optional<Point> findFirstPossibleShipCoordinate() {
        for (int y = 0; y < grid.size(); y++) {
            List<Integer> row = grid.get(y);
            int numberOfPositiveX = 0;
            for (int x = 0; x < row.size(); x++) {
                if (row.get(x) == 1) {
                    numberOfPositiveX += 1;
                }
                if (numberOfPositiveX == shipWidth) {
                    int numberOfPositiveY = 0;
                    for (int j = y; j >= 0 ; j--) {
                        if (grid.get(j).get(x) == 1)
                        numberOfPositiveY += 1;
                        if (numberOfPositiveY == shipHeight) {
                            return Optional.of(new Point(x - shipWidth + 1 + searchOffsetX, y - shipHeight + 1 + searchOffsetY));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static Map<Point, Integer> beamMap = new HashMap<>();
    private static List<List<Integer>> grid = new ArrayList<>();

    private static void findNumberOfCoordinatesAffectedByBeam(List<String> numbers) {
        for (int y = searchOffsetY; y < searchOffsetY + 200; y++) {
            List<Integer> row = new ArrayList<>();
            grid.add(row);
            for (int x = searchOffsetX; x < searchOffsetX + 200; x++) {
                Thread thread = IntCodeComputer.start(new ArrayList<>(numbers));
                IntCodeComputer.addInput(x);
                IntCodeComputer.addInput(y);
                int outputValue = IntCodeComputer.getNextOutputValue().orElseThrow().intValue();
                thread.stop();
                beamMap.put(new Point(x, y), outputValue);
                row.add(outputValue);
            }
        }
    }

    private static void drawGrid() {
        Map<Integer, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        paintMap.put(0, dp -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(1, dp -> dp.getG2d().drawImage(Images.getImage("oxygen.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(2, dp -> dp.getG2d().drawImage(Images.getImage("wall.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        DrawGrid<Integer> grid  = new DrawGrid<>("Tractor Beam", Integer.class, beamMap, 0, paintMap);
        System.out.println("Number of points affected: " + beamMap.values().stream().filter(v -> v == 1).count());
    }

}
