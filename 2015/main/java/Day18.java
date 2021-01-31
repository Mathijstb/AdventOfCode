import fileUtils.FileReader;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day18 {

    private enum State {
        ON,
        OFF
    }

    @Data
    private static class Light {
        State state;
        final List<Point> neighbourCoordinates = new ArrayList<>();

        public Light(State state) {
            this.state = state;
        }

        private List<Light> getNeighbours() {
            return neighbourCoordinates.stream().map(coordinate -> grid.get(coordinate.y).get(coordinate.x)).collect(Collectors.toList());
        }
    }

    private static List<List<Light>> grid = new ArrayList<>();

    private static void printGrid() {
        for (List<Light> row : grid) {
            StringBuilder sb = new StringBuilder();
            for (Light light : row) {
                sb.append(light.state == State.ON ? "#" : ".");
            }
            System.out.println(sb.toString());
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input18.csv");
        intializeGrid(lines);
        printGrid();
        executeAnimation(100);
        printNumberOfLightsOn();
    }

    private static void printNumberOfLightsOn() {
        int numberOfLightsOn = 0;
        for (List<Light> row : grid) {
            for (Light light : row) {
                numberOfLightsOn += light.state == State.ON ? 1 : 0;
            }
        }
        System.out.println("Number of lights on: " + numberOfLightsOn);
    }

    private static void executeAnimation(int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++) {
            printNumberOfLightsOn();
            executeStep();
        }
    }

    private static void executeStep() {
        List<List<State>> newStates = new ArrayList<>();
        for (int y = 0; y < grid.size(); y++) {
            List<Light> row = grid.get(y);
            List<State> newStatesRow = new ArrayList<>();
            newStates.add(newStatesRow);
            for (int x = 0; x < row.size(); x++) {
                Light light = row.get(x);
                List<Light> neighbours = light.getNeighbours();
                long numberOfNeighboursOn = neighbours.stream().filter(n -> n.state == State.ON).count();
                State newState;
                int maxX = grid.size() - 1;
                int maxY = grid.get(y).size() - 1;
                if (light.state == State.ON) {
                    if (x == 0 && y == 0 || x == 0 && y == maxY || x == maxX && y == 0 || x== maxX && y == maxY) {
                        newState = State.ON;
                    } else {
                        newState = numberOfNeighboursOn == 2 || numberOfNeighboursOn == 3 ? State.ON : State.OFF;
                    }
                } else {
                    newState = numberOfNeighboursOn == 3 ? State.ON : State.OFF;
                }
                newStatesRow.add(newState);
            }
        }

        for (int y = 0; y < grid.size(); y++) {
            List<Light> row = grid.get(y);
            for (int x = 0; x < row.size(); x++) {
                Light light = row.get(x);
                light.setState(newStates.get(y).get(x));
            }
        }
    }

    private static void intializeGrid(List<String> lines) {
        for (String line : lines) {
            List<Light> row = new ArrayList<>();
            grid.add(row);
            for (int j = 0; j < line.length(); j++) {
                row.add(new Light(line.charAt(j) == '#' ? State.ON : State.OFF));
            }
        }

        for (int y = 0; y < grid.size(); y++) {
            List<Light> row = grid.get(y);
            for (int x = 0; x < row.size(); x++) {
                Light light = row.get(x);
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dy == 0 && dx == 0) continue;
                        int otherY = y + dy;
                        int otherX = x + dx;
                        if (otherY < 0 ||otherY >= grid.size() || otherX < 0 || otherX >= grid.get(otherY).size()) continue;
                        light.neighbourCoordinates.add(new Point(otherX, otherY));
                    }
                }
            }
        }
    }

}
