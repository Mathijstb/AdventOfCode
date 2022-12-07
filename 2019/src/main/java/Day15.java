import fileUtils.FileReader;
import intCode.IntCodeComputer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input15.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        try {
            Thread.sleep(10000);
            executeProgram(numbers);
            Thread.sleep(4000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }
        System.exit(0);
    }

    private enum MovementCommand {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        public MovementCommand opposite() {
            switch (this) {
                case NORTH: return SOUTH;
                case EAST: return WEST;
                case WEST: return EAST;
                case SOUTH: return NORTH;
                default: throw new IllegalArgumentException();
            }
        }

        public long toInputValue() {
            switch (this) {
                case NORTH: return 1;
                case SOUTH: return 2;
                case WEST: return 3;
                case EAST: return 4;
                default: throw new IllegalArgumentException();
            }
        }
    }

    private enum MovementResponse {
        WALL,
        MOVED,
        GOAL;

        public static MovementResponse fromOutputValue(Long outputValue) {
            switch (outputValue.intValue()) {
                case 0: return WALL;
                case 1: return MOVED;
                case 2: return GOAL;
                default: throw new IllegalArgumentException();
            }
        }
    }

    private enum PointType {
        EMPTY,
        WALL,
        GOAL,
        DROID,
        UNEXPLORED,
        OXYGEN
    }

    private static Point position;
    private static Deque<MovementCommand> movementHistory = new ArrayDeque<>();
    private static Map<Point, Set<MovementCommand>> pointDirectionMap = new HashMap<>();
    private static Map<Point, PointType> pointTypeMap = new HashMap<>();

    private static void addPoint(Point point, PointType pointType) {
        if (!pointTypeMap.containsKey(point)) {
            pointTypeMap.put(point, pointType);
            if (pointType.equals(PointType.EMPTY) || pointType.equals(PointType.GOAL)) {
                pointDirectionMap.put(point, Sets.newHashSet(MovementCommand.NORTH, MovementCommand.EAST, MovementCommand.SOUTH, MovementCommand.WEST));
            }
        }
    }

    private static Grid grid = new Grid(new HashMap<>(pointTypeMap));

    private static void executeProgram(List<String> numbers) throws InterruptedException {
        IntCodeComputer.start(numbers);
        position = new Point(0, 0);
        MovementCommand movementCommand = MovementCommand.NORTH;
        addPoint(new Point(position), PointType.EMPTY);
        while(true) {
            MovementResponse response = move(movementCommand).orElseThrow();
            Optional<MovementCommand> optionalNextMovementCommand = getNextMovementCommand(response);
            //drawMap(true);
            drawGrid(true);

            if (!pointDirectionMap.get(position).isEmpty() && optionalNextMovementCommand.isPresent()) {
                movementCommand = optionalNextMovementCommand.get();
            }
            else {
                break;
            }
        }
        fillWithOxygen();
    }

    private static void drawGrid(boolean drawPosition) throws InterruptedException {
        Map<Point, PointType> drawMap = new HashMap<>(pointTypeMap);
        if (drawPosition) drawMap.put(position, PointType.DROID);
        grid.setPointTypeMap(drawMap);
        Thread.sleep(100);
        grid.repaint();
    }

    private static void fillWithOxygen() throws InterruptedException {
        System.out.println();
        System.out.println("Filling with oxygen:");
        System.out.println();
        Point oxygen = pointTypeMap.entrySet().stream().filter(entry -> entry.getValue() == PointType.GOAL).findFirst().orElseThrow().getKey();
        pointTypeMap.put(oxygen, PointType.OXYGEN);
        Set<Point> points = Sets.newHashSet(oxygen);
        int numberOfMinutes = 0;
        while (true) {
            //System.out.printf("Minute: %s%n", numberOfMinutes + 1);
            points = points.stream().map(Day15::getNextPointsToFillWithOxygen).reduce(new HashSet<>(), Sets::union);
            if (points.isEmpty()) break;
            points.forEach(p -> {
                pointTypeMap.put(p, PointType.OXYGEN);
            });
            numberOfMinutes += 1;
            //drawMap(false);
            drawGrid(false);
            System.out.println();
        }
        drawGrid(false);
        System.out.printf("Filled with oxygen after %s minutes!%n", numberOfMinutes);
    }


    private static Set<Point> getNextPointsToFillWithOxygen(Point point) {
        List<MovementCommand> directions = Lists.newArrayList(MovementCommand.NORTH, MovementCommand.EAST, MovementCommand.SOUTH, MovementCommand.WEST);
        return directions.stream().map(c -> getPointAfterMove(c, point)).filter(p -> pointTypeMap.get(p).equals(PointType.EMPTY)).collect(Collectors.toSet());
    }

    private static void drawMap(boolean drawPosition) {
        int minX = pointTypeMap.keySet().stream().map(p -> p.x).min(Comparator.comparing(x -> x)).orElseThrow();
        int maxX = pointTypeMap.keySet().stream().map(p -> p.x).max(Comparator.comparing(x -> x)).orElseThrow();
        int minY = pointTypeMap.keySet().stream().map(p -> p.y).min(Comparator.comparing(y -> y)).orElseThrow();
        int maxY = pointTypeMap.keySet().stream().map(p -> p.y).max(Comparator.comparing(y -> y)).orElseThrow();
        PointType[][] grid = new PointType[maxY - minY + 1][maxX - minX + 1];
        for (int i = 0; i < grid.length; i++) {
            PointType[] row = grid[i];
            for (int j = 0; j < row.length; j++) {
                grid[i][j] = PointType.UNEXPLORED;
            }
        }
        pointTypeMap.forEach((key, value) -> {
            grid[key.y - minY][key.x - minX] = value;
        });
        if (drawPosition) grid[position.y - minY][position.x - minX] = PointType.DROID;

        System.out.println("----------------------------------");
        for (PointType[] row : grid) {
            StringBuilder sb = new StringBuilder();
            for (PointType pointType : row) {
                switch (pointType) {
                    case WALL: sb.append("#"); break;
                    case EMPTY: sb.append("."); break;
                    case DROID: sb.append("x"); break;
                    case GOAL: sb.append("!"); break;
                    case UNEXPLORED: sb.append(" "); break;
                    case OXYGEN: sb.append("O"); break;
                }
            }
            System.out.println(sb.toString());
        }
        System.out.println("----------------------------------");
        System.out.println();
    }

    private static Optional<MovementCommand> getNextMovementCommand(MovementResponse response) {
        if (movementHistory.isEmpty()) return Optional.empty();
        MovementCommand previousCommand = movementHistory.peek();

        Point newPoint = getPointAfterMove(previousCommand, position);
        switch (response) {
            case WALL: {
                System.out.println("Hit wall!");
                addPoint(newPoint, PointType.WALL);
                movementHistory.pop();
                return Optional.of(getNextDirection());
            }
            case GOAL: {
                position = newPoint;
                if (pointTypeMap.containsKey(newPoint)) {
                    System.out.println("At goal again!");
                    if (pointDirectionMap.get(position).contains(previousCommand.opposite())) {
                        return Optional.of(previousCommand.opposite());
                    }
                    else {
                        return Optional.of(getNextDirection());
                    }
                }
                else {
                    System.out.println("Reached goal!");
                    addPoint(newPoint, PointType.GOAL);
                    return Optional.of(getNextDirection());
                }
            }
            case MOVED: {
                position = newPoint;
                if (pointTypeMap.containsKey(newPoint)) {
                    System.out.println("Been here before!");
                    if (pointDirectionMap.get(position).contains(previousCommand.opposite())) {
                        return Optional.of(previousCommand.opposite());
                    }
                    else {
                        return Optional.of(getNextDirection());
                    }
                }
                else {
                    System.out.println("Moved!");
                    addPoint(newPoint, PointType.EMPTY);
                    return Optional.of(getNextDirection());
                }
            }
            default: throw new IllegalArgumentException();
        }
    }

    private static MovementCommand getNextDirection() {
        Set<MovementCommand> movementCommands = new HashSet<>(pointDirectionMap.get(position));
        if (movementHistory.isEmpty()) {
            return movementCommands.stream().findFirst().orElseThrow(() -> new IllegalStateException("No movements left"));
        }
        MovementCommand previousCommand = movementHistory.peek();

        //Remove moves that would hit a wall
        movementCommands.forEach(c -> {
            Point newPoint = getPointAfterMove(c, position);
            if (pointTypeMap.containsKey(newPoint) && pointTypeMap.get(newPoint) == PointType.WALL) {
                pointDirectionMap.get(position).remove(c);
            }
        });
        movementCommands = new HashSet<>(pointDirectionMap.get(position));

        //Get moves that do not revisit
        Set<MovementCommand> noRevisitMovements = movementCommands.stream().filter(c -> !pointTypeMap.containsKey(getPointAfterMove(c, position))).collect(Collectors.toSet());
        Optional<MovementCommand> nextCommand = noRevisitMovements.stream().findFirst();
        if (nextCommand.isPresent()) return nextCommand.get();

        //Get backtrack move
        nextCommand = movementCommands.stream().filter(c -> !c.equals(previousCommand.opposite())).findFirst();
        return nextCommand.orElseGet(previousCommand::opposite);
    }

    private static Optional<MovementResponse> move(MovementCommand movementCommand) {
        System.out.printf("Moving %s%n", movementCommand.name());
        pointDirectionMap.get(position).remove(movementCommand);
        movementHistory.push(movementCommand);
        IntCodeComputer.addInput(movementCommand.toInputValue());
        Optional<Long> outputValue = IntCodeComputer.getNextOutputValue() ;
        return outputValue.map(MovementResponse::fromOutputValue);
    }

    private static Point getPointAfterMove(MovementCommand previousCommand, Point point) {
        Point newPoint = new Point(point);
        switch (previousCommand) {
            case NORTH: newPoint.translate(0, -1); break;
            case EAST: newPoint.translate(1, 0); break;
            case SOUTH: newPoint.translate(0, 1); break;
            case WEST: newPoint.translate(-1, 0); break;
            default: throw new IllegalArgumentException();
        }
        return newPoint;
    }

    //GRID
    //----------------------------------------------------------------------------------------------------------------------------------------

    private static class Grid {

        private JFrame frame;
        private Grid.TestPane testPane;

        public Grid(Map<Point, PointType> pointTypeMap) {
            EventQueue.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame = new JFrame("Maze");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                testPane = new Grid.TestPane(pointTypeMap);
                frame.add(testPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }

        public void setPointTypeMap(Map<Point, PointType> pointTypeMap) {
            EventQueue.invokeLater(() -> {
                testPane.pointTypeMap = pointTypeMap;
            });
        }

        public void repaint() {
            EventQueue.invokeLater(() -> {
                frame.repaint();
            });
        }

        public static class TestPane extends JPanel {

            private Map<Point, PointType> pointTypeMap;

            private BufferedImage wall;
            private BufferedImage dot;
            private BufferedImage droid;
            private BufferedImage oxygen;
            private BufferedImage goal;

            public TestPane(Map<Point, PointType> pointTypeMap) {
                this.pointTypeMap = pointTypeMap;
                readImages();
            }

            private void readImages() {
                try {
                    wall = ImageIO.read(getClass().getResource("wall.png"));
                    dot = ImageIO.read(getClass().getResource("dot.png"));
                    droid = ImageIO.read(getClass().getResource("droid.png"));
                    oxygen = ImageIO.read(getClass().getResource("oxygen.png"));
                    goal = ImageIO.read(getClass().getResource("goal.png"));
                }
                catch (IOException e) {
                    throw new RuntimeException();
                }
            }

            private PointType[][] getGrid() {
                int minX = pointTypeMap.keySet().stream().map(p -> p.x).min(Comparator.comparing(x -> x)).orElseThrow();
                int maxX = pointTypeMap.keySet().stream().map(p -> p.x).max(Comparator.comparing(x -> x)).orElseThrow();
                int minY = pointTypeMap.keySet().stream().map(p -> p.y).min(Comparator.comparing(y -> y)).orElseThrow();
                int maxY = pointTypeMap.keySet().stream().map(p -> p.y).max(Comparator.comparing(y -> y)).orElseThrow();
                PointType[][] grid = new PointType[maxY - minY + 1][maxX - minX + 1];
                for (PointType[] row : grid) {
                    Arrays.fill(row, PointType.UNEXPLORED);
                }
                pointTypeMap.forEach((key, value) -> {
                    grid[key.y - minY][key.x - minX] = value;
                });
                return grid;
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 800);
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                PointType[][] grid = getGrid();

                int stateHeight = grid.length;
                int stateWidth = grid[0].length;
                int stateSize = Math.max(stateHeight, stateWidth);

                int blockSize = Math.min(getWidth() - 4, getHeight() - 4) / stateSize;
                for (int i = 0; i < grid.length; i++) {
                    PointType[] row = grid[i];
                    for (int j = 0; j < row.length; j++) {
                        PointType pointType = row[j];
                        int x = j * blockSize;
                        int y = i * blockSize;

                        switch (pointType) {
                            case UNEXPLORED:
                                continue;
                            case WALL: {
                                g2d.drawImage(wall, x, y, blockSize, blockSize, null);
                            }
                            break;
                            case EMPTY: {
                                g2d.drawImage(dot, x, y, blockSize, blockSize, null);
                            }
                            break;
                            case DROID: {
                                g2d.drawImage(droid, x, y, blockSize, blockSize, null);
                            }
                            break;
                            case OXYGEN: {
                                g2d.drawImage(oxygen, x, y, blockSize, blockSize, null);
                            }
                            break;
                            case GOAL: {
                                g2d.drawImage(goal, x, y, blockSize, blockSize, null);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
