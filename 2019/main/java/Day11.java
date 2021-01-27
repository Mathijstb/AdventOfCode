import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Day11 {

    @Data
    @AllArgsConstructor
    private static class ExecuteProgramTask implements Runnable {

        List<String> numbers;
        LinkedBlockingDeque<Long> inputQueue;
        LinkedBlockingDeque<Long> outputQueue;

        @Override
        public void run() {
            Day5.executeProgram(numbers, inputQueue, outputQueue);
            outputQueue.add(99L);
        }
    }

    private enum State {
        BLACK,
        WHITE;

        public Integer toValue() {
            switch (this) {
                case BLACK: return 0;
                case WHITE: return 1;
                default: throw new IllegalArgumentException("Invalid state");
            }
        }

        public static State fromValue(int color) {
            switch (color) {
                case 0: return BLACK;
                case 1: return WHITE;
                default: throw new IllegalArgumentException("Invalid color");
            }
        }
    }

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    @Data
    @AllArgsConstructor
    private static class Robot {
        Direction direction;
        Point position;

        public void move() {
            switch (direction) {
                case NORTH: position.translate(0, -1); break;
                case EAST: position.translate(1, 0); break;
                case SOUTH: position.translate(0, 1); break;
                case WEST: position.translate(-1, 0); break;
            }
        }

        public void turn(int leftOrRight) {
            switch (leftOrRight) {
                case 0: {
                    switch (direction) {
                        case NORTH: direction = Direction.WEST; break;
                        case EAST: direction = Direction.NORTH; break;
                        case SOUTH: direction = Direction.EAST; break;
                        case WEST: direction = Direction.SOUTH; break;
                    }
                } break;
                case 1: {
                    switch (direction) {
                        case NORTH: direction = Direction.EAST; break;
                        case EAST: direction = Direction.SOUTH; break;
                        case SOUTH: direction = Direction.WEST; break;
                        case WEST: direction = Direction.NORTH; break;
                    }
                } break;
                default: throw new IllegalArgumentException("Invalid leftOrRight");
            }
        }

        @Override
        public String toString() {
            return String.format("Robot position: (%s,%s), direction %s", position.x, position.y, direction);
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input11.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        startProgram(numbers);
        Map<Point, State> stateMap = runRobot();
        System.out.println("Number of colored positions: " + stateMap.size());
        System.out.println("Number black: " + stateMap.values().stream().filter(s -> s == State.BLACK).count());
        System.out.println("Number white: " + stateMap.values().stream().filter(s -> s == State.WHITE).count());
        System.out.println();
        printStateMap(stateMap);
    }

    private static void printStateMap(Map<Point, State> stateMap){
        int minX = stateMap.keySet().stream().map(p -> p.x).min(Comparator.comparing(x -> x)).orElseThrow();
        int maxX = stateMap.keySet().stream().map(p -> p.x).max(Comparator.comparing(x -> x)).orElseThrow();
        int minY = stateMap.keySet().stream().map(p -> p.y).min(Comparator.comparing(y -> y)).orElseThrow();
        int maxY = stateMap.keySet().stream().map(p -> p.y).max(Comparator.comparing(y -> y)).orElseThrow();
        State[][] grid = new State[maxY - minY + 1][maxX - minX + 1];
        stateMap.forEach((key, value) -> grid[key.y - minY][key.x - minX] = value);
        for (State[] row : grid) {
            StringBuilder sb = new StringBuilder();
            for (State state : row) {
                sb.append(state == State.BLACK ? "." : "#");
            }
            System.out.println(sb.toString());
        }
    }

    private static LinkedBlockingDeque<Long> inputQueue = new LinkedBlockingDeque<>();
    private static LinkedBlockingDeque<Long> outputQueue = new LinkedBlockingDeque<>();

    private static void startProgram(List<String> numbers) {
        ExecuteProgramTask executeProgramTask = new ExecuteProgramTask(new ArrayList<>(numbers), inputQueue, outputQueue);
        Thread thread = new Thread(executeProgramTask);
        thread.start();
    }

    //2319 too high, someone else's answer
    private static Map<Point, State> runRobot() {
        Map<Point, State> stateMap = new HashMap<>();
        Robot robot = new Robot(Direction.NORTH, new Point(0,0));
        stateMap.put(new Point(0,0), State.WHITE);
        int move = 1;
        while (true) {
            System.out.printf("---------- Move %s ---------%n", move);
            System.out.println();
            //Print robot state
            System.out.println(robot);

            //Determine input
            State state = getColor(stateMap, robot.position);
            long input = state.toValue().longValue();
            System.out.println("Hull state: " + state.name() + " (input: " + input + ")");
            inputQueue.add(input);
            System.out.println();

            //Get paint color and paint
            Optional<Long> output = getNextOutputValue();
            if (output.isEmpty()) break;
            State paintState = State.fromValue(output.get().intValue());
            System.out.println("Paint color: " + paintState.name());
            stateMap.put(new Point(robot.position), paintState);

            //Get turn direction and turn
            Optional<Long> output2 = getNextOutputValue();
            if (output2.isEmpty()) break;
            int turnDirection = output2.get().intValue();
            System.out.println("Turn direction: " + turnDirection);
            robot.turn(turnDirection);

            //Move
            robot.move();
            System.out.println(robot);
            System.out.println();
            move += 1;
        }
        System.out.println("Program finished");
        return stateMap;
    }

    private static State getColor(Map<Point, State> stateMap, Point point) {
        return stateMap.getOrDefault(point, State.BLACK);
    }

    private static Optional<Long> getNextOutputValue() {
        try {
            Long output = outputQueue.takeFirst();
            return output == 99L ? Optional.empty() : Optional.of(output);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }
    }


}
