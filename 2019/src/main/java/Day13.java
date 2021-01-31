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

public class Day13 {

    private static Grid grid;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input13.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        try {
            executeProgram(numbers);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }
    }

    private static void executeProgram(List<String> numbers) throws InterruptedException {
        Thread thread = IntCodeComputer.start(numbers);
        Thread.sleep(1000);
        intializePoints();
        grid = new Grid(pointMap);
        playGame();
        Thread.sleep(2000);
        System.exit(0);
    }

    private static void playGame() throws InterruptedException {
        while (true) {
            Point ballPosition = getPosition(TileType.BALL);
            Point paddlePosition = getPosition(TileType.PADDLE);
            Long input;
            if (ballPosition.x < paddlePosition.x) {
                input = -1L;
            }
            else if (ballPosition.x > paddlePosition.x) {
                input = 1L;
            }
            else {
                input = 0L;
            }
            Thread.sleep(10);
            IntCodeComputer.addInput(input);
            Thread.sleep(10);
            if (!updateGrid()) {
                grid.repaint();
                break;
            }
            grid.repaint();
        }
    }

    private static Point getPosition(TileType tileType) {
        return pointMap.entrySet().stream().filter(entry -> entry.getValue().equals(tileType)).findFirst().orElseThrow().getKey();
    }

    public static boolean updateGrid() {
        while (true) {
            Optional<Long> optX = IntCodeComputer.getNextOutputValue();
            if (optX.isEmpty()) return false;
            Optional<Long> optY = IntCodeComputer.getNextOutputValue();
            if (optY.isEmpty()) return false;
            Optional<Long> optZ = IntCodeComputer.getNextOutputValue();
            if (optZ.isEmpty()) return false;
            int x = optX.get().intValue();
            int y = optY.get().intValue();
            int z = optZ.get().intValue();
            if (x == -1 && y == 0) {
                grid.setScore(z);
            } else {
                pointMap.put(new Point(x, y), TileType.fromValue(z));
            }
            if (IntCodeComputer.getNextOutputValue().isEmpty()) return true;
        }
    }

    public static void determineNumberOfBlocks() {
        long numberOfBlocks = pointMap.values().stream().filter(t -> t == TileType.BLOCK).count();
        System.out.println("Number of blocks: " + numberOfBlocks);
    }

    enum TileType {
        EMPTY,
        WALL,
        BLOCK,
        PADDLE,
        BALL;

        public char toDrawChar() {
            switch (this) {
                case EMPTY: return ' ';
                case WALL: return '#';
                case BLOCK: return 'B';
                case PADDLE: return '-';
                case BALL: return 'o';
                default: throw new IllegalArgumentException();
            }
        }

        public static TileType fromValue(int tileId) {
            switch (tileId) {
                case 0: return EMPTY;
                case 1: return WALL;
                case 2: return BLOCK;
                case 3: return PADDLE;
                case 4: return BALL;
                default: throw new IllegalArgumentException("Invalid color");
            }
        }

    }

    private static Map<Point, TileType> pointMap = new HashMap<>();

    public static void intializePoints() {
        while(IntCodeComputer.getOutputSize() >= 3) {
            int x = IntCodeComputer.getNextOutputValue().orElseThrow().intValue();
            int y = IntCodeComputer.getNextOutputValue().orElseThrow().intValue();
            int tileId =IntCodeComputer.getNextOutputValue().orElseThrow().intValue();
            pointMap.put(new Point(x, y), TileType.fromValue(tileId));
        }
    }

    private static void printGrid(TileType[][] grid){
        for (TileType[] row : grid) {
            StringBuilder sb = new StringBuilder();
            for (TileType tileType : row) {
                sb.append(tileType.toDrawChar());
            }
            System.out.println(sb.toString());
        }
    }

    public static class Grid {

        private JFrame frame;
        private TestPane testPane;

        public Grid(Map<Point, TileType> pointMap) {
            EventQueue.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame = new JFrame("Brick breaker");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                testPane = new TestPane(pointMap);
                frame.add(testPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }

        public void repaint() {
            EventQueue.invokeLater(() -> {
                frame.repaint();
            });
        }

        public void setScore(long score) {
            EventQueue.invokeLater(() -> {
                testPane.score = score;
            });
        }

        public static class TestPane extends JPanel {

            private Map<Point, TileType> pointMap;
            private long score = 0;

            private BufferedImage wall;
            private BufferedImage brick;
            private BufferedImage paddle;
            private BufferedImage ball;

            public TestPane(Map<Point, TileType> pointMap) {
                this.pointMap = pointMap;
                readImages();
            }

            private void readImages() {
                try {
                    wall = ImageIO.read(getClass().getResource("wall.png"));
                    brick = ImageIO.read(getClass().getResource("brick.png"));
                    paddle = ImageIO.read(getClass().getResource("paddle.png"));
                    ball = ImageIO.read(getClass().getResource("ball.png"));
                }
                catch (IOException e) {
                    throw new RuntimeException();
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }

            private TileType[][] getGameState() {
                int minX = pointMap.keySet().stream().map(p -> p.x).min(Comparator.comparing(x -> x)).orElseThrow();
                int maxX = pointMap.keySet().stream().map(p -> p.x).max(Comparator.comparing(x -> x)).orElseThrow();
                int minY = pointMap.keySet().stream().map(p -> p.y).min(Comparator.comparing(y -> y)).orElseThrow();
                int maxY = pointMap.keySet().stream().map(p -> p.y).max(Comparator.comparing(y -> y)).orElseThrow();
                TileType[][] grid = new TileType[maxY - minY + 1][maxX - minX + 1];
                for (TileType[] row : grid) {
                    Arrays.fill(row, TileType.EMPTY);
                }
                pointMap.forEach((key, value) -> {
                    grid[key.y - minY][key.x - minX] = value;
                });
                return grid;
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                TileType[][] gameState = getGameState();
                int stateHeight = gameState.length;
                int stateWidth = gameState[0].length;
                int stateSize = Math.max(stateHeight, stateWidth);

                int blockSize = Math.max(getWidth() - 4, getHeight() - 4) / stateSize;
                for (int i = 0; i < gameState.length; i++) {
                    TileType[] row = gameState[i];
                    for (int j = 0; j < row.length; j++) {
                        TileType tileType = row[j];
                        int x = j * blockSize;
                        int y = i * blockSize;
                        switch (tileType) {
                            case EMPTY: continue;
                            case WALL: {
                                g2d.setColor(Color.black);
                                g2d.drawImage(wall, x, y, blockSize, blockSize, null);
                            } break;
                            case BLOCK: {
                                g2d.setColor(Color.orange);
                                g2d.drawImage(brick, x, y, blockSize, blockSize, null);
                            } break;
                            case BALL: {
                                g2d.setColor(Color.blue);
                                g2d.drawImage(ball, x, y, blockSize, blockSize, null);
                            } break;
                            case PADDLE: {
                                g2d.setColor(Color.pink);
                                g2d.drawImage(paddle, x, y + blockSize / 4, blockSize, blockSize / 2, null);
                            } break;
                        }
                    }
                }
                g2d.drawString("Score: " + String.valueOf(score), getWidth() / 2 - 20, 20);
                g2d.dispose();
            }

        }
    }
}
