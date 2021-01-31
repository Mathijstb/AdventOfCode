import fileUtils.FileReader;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class Day17 {

    private enum State {
        ACTIVE('#'),
        INACTIVE('.');

        private final char character;

        State(char character) {
            this.character = character;
        }
    }

    @Data
    @AllArgsConstructor
    private static class Cube {
        int w;
        int x;
        int y;
        int z;
    }

    @Data
    private static class Grid {
        Map<Cube, State> cubeToStateMap;

        public int minW() { return Collections.min(cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getW)).getW(); }
        public int maxW() { return Collections.max(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getW)).getW(); }
        public int minX() { return Collections.min(cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getX)).getX(); }
        public int maxX() { return Collections.max(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getX)).getX(); }
        public int minY() { return Collections.min(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getY)).getY(); }
        public int maxY() { return Collections.max(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getY)).getY(); }
        public int minZ() { return Collections.min(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getZ)).getZ(); }
        public int maxZ() { return Collections.max(grid.cubeToStateMap.keySet(), Comparator.comparingInt(Cube::getZ)).getZ(); }

        public void print(String header) {
            System.out.println();
            System.out.println(header);
            int minW = grid.minW();
            int maxW = grid.maxW();
            int minX = grid.minX();
            int maxX = grid.maxX();
            int minY = grid.minY();
            int maxY = grid.maxY();
            int minZ = grid.minZ();
            int maxZ = grid.maxZ();
            for (int w = minW; w <= maxW; w++) {
                for (int z = minZ; z <= maxZ; z++) {
                    System.out.println();
                    System.out.printf("w=%s, z=%s\n", w, z);
                    for (int y = minY; y <= maxY; y++) {
                        StringBuilder line = new StringBuilder();
                        for (int x = minX; x <= maxX; x++) {
                            Cube cube = new Cube(w, x, y, z);
                            if (grid.cubeToStateMap.containsKey(cube)) {
                                line.append(grid.cubeToStateMap.get(cube).character);
                            }
                            ;
                        }
                        System.out.println(line);
                    }
                }
            }
        }
    }

    private static final Grid grid = new Grid();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input17.csv");
        grid.setCubeToStateMap(initializeCubes(lines));

        grid.print("Before any cycles:");
        executeCycle();
        grid.print("After 1 cycle:");

        for (int i = 2; i <= 6; i++) {
            executeCycle();
            grid.print(String.format("After %s cycles:", i));
        }
        countNumberOfActiveCubes();
    }

    private static void executeCycle() {
        addNewCubes();

        Map<Cube, State> newCubeToStateMap = new HashMap<>();
        grid.cubeToStateMap.keySet().forEach(cube -> {
            State newState;
            long numberOfActiveNeighbours = getNeighbours(cube).stream()
                    .filter(n -> grid.cubeToStateMap.getOrDefault(n, State.INACTIVE) == State.ACTIVE).count();
            if (grid.cubeToStateMap.get(cube) == State.ACTIVE) {
                newState = (numberOfActiveNeighbours == 2 || numberOfActiveNeighbours == 3) ? State.ACTIVE : State.INACTIVE;
            }
            else {
                newState = (numberOfActiveNeighbours == 3) ? State.ACTIVE : State.INACTIVE;
            }
            newCubeToStateMap.put(cube, newState);
        });
        grid.cubeToStateMap = newCubeToStateMap;
    }

    private static void countNumberOfActiveCubes() {
        long numberOfActiveCubes = grid.cubeToStateMap.values().stream().filter(state -> state == State.ACTIVE).count();
        System.out.println("Number of active cubes: " + numberOfActiveCubes);
    }

    private static void addNewCubes() {
        Set<Cube> newCubes = new HashSet<>();
        grid.cubeToStateMap.forEach((cube, state) -> getNeighbours(cube).forEach(neighbour -> {
            if (!grid.cubeToStateMap.containsKey(neighbour)) {
                newCubes.add(neighbour);
            }
        }));
        newCubes.forEach(cube -> grid.cubeToStateMap.put(cube, State.INACTIVE));
    }

    private static Set<Cube> getNeighbours(Cube cube) {
        Set<Cube> neighbours = new HashSet<>();
        for (int dw = -1; dw <= 1 ; dw++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dw != 0 || dx != 0 || dy != 0 || dz != 0) {
                            neighbours.add(new Cube(cube.w + dw, cube.x + dx, cube.y + dy, cube.z + dz));
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private static Map<Cube, State> initializeCubes(List<String> lines) {
        Map<Cube, State> cubes = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char character = line.charAt(x);
                State state = Arrays.stream(State.values()).filter(s -> s.character == character).findFirst().orElseThrow();
                cubes.put(new Cube(0, x,y, 0), state);
            }
        }
        return cubes;
    }
}
