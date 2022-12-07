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
        try {
            executeProgram(numbers);
        }
        catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted!");
        }
    }

    private static void executeProgram(List<String> numbers) throws InterruptedException {
        IntCodeComputer.start(numbers);

        List<Long> mainInput = Lists.newArrayList(65L, 44L, 66L, 44L, 65L, 44L, 67L, 44L, 66L, 44L, 67L, 44L, 66L, 44L, 65L, 44L, 67L, 44L, 66L, 10L);
        List<Long> function1 = Lists.newArrayList(76L, 44L, 49L, 48L, 44L, 76L, 44L, 54L, 44L, 82L, 44L, 49L, 48L, 10L);
        List<Long> function2 = Lists.newArrayList(82L, 44L, 54L, 44L, 82L, 44L, 56L, 44L, 82L, 44L, 56L, 44L, 76L, 44L, 54L, 44L, 82L, 44L, 56L, 10L);
        List<Long> function3 = Lists.newArrayList(76L, 44L, 49L, 48L, 44L, 82L, 44L, 56L, 44L, 82L, 44L, 56L, 44L, 76L, 44L, 49L, 48L, 10L);
        List<Long> wantVideoFeed = Lists.newArrayList(121L, 10L);//Lists.newArrayList(110L, 10L);
        mainInput.forEach(IntCodeComputer::addInput);
        function1.forEach(IntCodeComputer::addInput);
        function2.forEach(IntCodeComputer::addInput);
        function3.forEach(IntCodeComputer::addInput);
        wantVideoFeed.forEach(IntCodeComputer::addInput);

        initializePointMap(getVideoFeed());
        drawGrid();
        processInput();

        while (!IntCodeComputer.isFinished() || IntCodeComputer.getOutputSize() > 2) {
            synchronized (Thread.currentThread()) {
                Thread.currentThread().wait(1);
            }
            initializePointMap(getVideoFeed());
            findCrossings();
            drawGrid.repaint();
        }

        State lastState = getNextState().orElseThrow();
        System.out.println("Result: " + lastState.otherValue);
    }

    private static List<State> getVideoFeed() {
        State previousState = State.EMPTY;
        List<State> videoFeed = new ArrayList<>();
        while (true) {
            State state = getNextState().orElseThrow();
            if(state == State.NEWLINE && previousState == State.NEWLINE) break;
            videoFeed.add(state);
            previousState = state;
        }
        return videoFeed;
    }

    private static void processInput() {
        State previousState = State.EMPTY;
        StringBuilder sb = new StringBuilder();
        while (true) {
            State state = getNextTextState().orElseThrow();
            if(state == State.NEWLINE) {
                String string = sb.toString();
                sb = new StringBuilder();
                System.out.println(string);
                if (previousState == State.NEWLINE) break;
            }
            else {
                sb.append((char) state.otherValue);
            }
            previousState = state;
        }
    }

    private static List<List<State>> grid;
    private static final Map<Point, State> pointMap = new HashMap<>();
    private static Set<Point> crossings;
    private static DrawGrid<State> drawGrid;

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

    private static Map<State, Consumer<DrawGrid.DrawParameters>> paintMap;

    private static void drawGrid() {
        if (paintMap == null) {
            paintMap = new HashMap<>();
            paintMap.put(State.SCAFFOLD, (dp) -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.CROSSING, (dp) -> dp.getG2d().drawImage(Images.getImage("crossing.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.UP, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowUp.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.DOWN, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowDown.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.LEFT, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowLeft.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.RIGHT, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowRight.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(State.OFF, (dp) -> dp.getG2d().drawImage(Images.getImage("redCross.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        }
        drawGrid = new DrawGrid<>("Scaffolds", State.class, pointMap, State.EMPTY, paintMap);
    }

    private static void initializePointMap(List<State> input) {
        grid = new ArrayList<>();
        List<State> row = new ArrayList<>();
        grid.add(row);
        for (State state: input) {
            if (state == State.NEWLINE) {
                row = new ArrayList<>();
                grid.add(row);
            }
            else {
                row.add(state);
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

    private static Optional<State> getNextTextState() {
        Optional<Long> optionalOutput = IntCodeComputer.getNextOutputValue();
        return optionalOutput.isEmpty() ? Optional.empty() : Optional.of(State.fromTextValue(optionalOutput.get().intValue()));
    }

    private static Optional<State> getNextState() {
        Optional<Long> optionalOutput = IntCodeComputer.getNextOutputValue();
        return optionalOutput.isEmpty() ? Optional.empty() : Optional.of(State.fromValue(optionalOutput.get().intValue()));
    }

}
