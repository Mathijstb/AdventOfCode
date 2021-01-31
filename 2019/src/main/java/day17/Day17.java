package day17;

import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import intCode.IntCodeComputer;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Day17 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input17.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        executeProgram(numbers);
    }

    private static void executeProgram(List<String> numbers) {
        IntCodeComputer.start(numbers);
        initializeGrid();
        findCrossings();
        drawGrid();
        findSumOfAlignmentParameters();
    }

    private static final List<List<State>> grid = new ArrayList<>();
    private static final Map<Point, State> pointMap = new HashMap<>();
    private static Set<Point> crossings;

    private static void findCrossings() {
        crossings = pointMap.keySet().stream().filter(Day17::isCrossing).collect(Collectors.toSet());
        crossings.forEach(p -> {
            pointMap.put(p, State.CROSSING);
            grid.get(p.y).set(p.x, State.CROSSING);
        });
    }

    private static void findSumOfAlignmentParameters() {
        int sumOfAlignments = crossings.stream().map(c -> c.x * c.y).reduce(0, Integer::sum);
        System.out.println("Sum of alignments: " + sumOfAlignments);
    }

    private static boolean isCrossing(Point point) {
        if (pointMap.get(point) != State.SCAFFOLD) return false;
        List<Point> neighbours = pointMap.entrySet().stream()
                .filter(entry -> entry.getValue() == State.SCAFFOLD)
                .map(Map.Entry::getKey)
                .filter(p -> p.equals(new Point(point.x -1, point.y)) ||
                             p.equals(new Point(point.x +1, point.y)) ||
                             p.equals(new Point(point.x, point.y-1)) ||
                             p.equals(new Point(point.x, point.y+1)))
                .collect(Collectors.toList());
        return (neighbours.size() == 4);
    }

    private static void drawGrid() {
//        for (List<State> row: grid) {
//            StringBuilder sb = new StringBuilder();
//            for (State state: row) {
//                sb.append(state.toCharacter());
//            }
//            System.out.println(sb.toString());
//        }
        Map<State, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        paintMap.put(State.SCAFFOLD, (dp) -> dp.getG2d().drawImage(Images.getImage("wall.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.CROSSING, (dp) -> dp.getG2d().drawImage(Images.getImage("crossing.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.UP, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowUp.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.DOWN, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowDown.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.LEFT, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowLeft.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.RIGHT, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowRight.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(State.OFF, (dp) -> dp.getG2d().drawImage(Images.getImage("redCross.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));

        DrawGrid<State> grid = new DrawGrid<>("Scaffolds", State.class, pointMap, State.EMPTY, paintMap);
    }

    private static void initializeGrid() {
        List<State> row = new ArrayList<>();
        grid.add(row);
        while(true) {
            Optional<State> optionalState = getNextState();
            if (optionalState.isEmpty()) {
                break;
            }
            else {
                State state = optionalState.get();
                if (state == State.NEWLINE) {
                    row = new ArrayList<>();
                    grid.add(row);
                }
                else {
                    row.add(state);
                }
            }
        }
        for (int i = 0; i < grid.size(); i++) {
            row = grid.get(i);
            for (int j = 0; j < row.size(); j++) {
                State state = row.get(j);
                pointMap.put(new Point(j, i), state);
            }
        }
    }

    private static Optional<State> getNextState() {
        Optional<Long> optionalOutput = IntCodeComputer.getNextOutputValue();
        return optionalOutput.isEmpty() ? Optional.empty() : Optional.of(State.fromValue(optionalOutput.get().intValue()));
    }

}
