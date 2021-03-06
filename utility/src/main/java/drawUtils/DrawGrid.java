package drawUtils;

import lombok.Value;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;

public class DrawGrid<T> {

    private JFrame frame;
    private GridPanel<T> gridPanel;

    public DrawGrid(String title, Class<T> c, Map<Point, T> pointTypeMap, T defaultValue, Map<T, Consumer<DrawParameters>> paintMap) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }

            frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gridPanel = new GridPanel<>(c, pointTypeMap, defaultValue, paintMap);
            frame.add(gridPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void setPointTypeMap(Map<Point, T> pointTypeMap) {
        EventQueue.invokeLater(() -> {
            gridPanel.pointTypeMap = pointTypeMap;
        });
    }

    public void repaint() {
        EventQueue.invokeLater(() -> {
            frame.repaint();
        });
    }

    @Value
    public static class DrawParameters {
        Graphics2D g2d;
        Point point;
        int blockSize;
    }

    public static class GridPanel<T> extends JPanel {

        private final Class<T> c;
        private Map<Point, T> pointTypeMap;
        private final T defaultValue;
        private final Map<T, Consumer<DrawParameters>> paintMap;

        public GridPanel(Class<T> c, Map<Point, T> pointTypeMap, T defaultValue, Map<T, Consumer<DrawParameters>> paintMap) {
            this.c = c;
            this.pointTypeMap = pointTypeMap;
            this.defaultValue = defaultValue;
            this.paintMap = paintMap;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 800);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Grid<T> grid = new Grid<>(c, pointTypeMap, defaultValue);
            int stateSize = Math.max(grid.getHeight(), grid.getWidth());

            int blockSize = Math.min(getWidth() - 4, getHeight() - 4) / stateSize;
            for (int i = 0; i < grid.getHeight(); i++) {
                T[] row = grid.getRow(i);
                for (int j = 0; j < row.length; j++) {
                    T pointType = row[j];
                    int x = j * blockSize;
                    int y = i * blockSize;
                    //g2d.drawLine(0, 10, 1, 50);
                    if (paintMap.containsKey(pointType)) {
                        paintMap.get(pointType).accept(new DrawParameters(g2d, new Point(x, y), blockSize));
                    }

                    // function: (Point, blockSize) ->

                    // biConsumer: (g2d, Point) -> g2d.drawImage(
                    // - Point,
                    // - blockSize,
                    // - image
                    // - g2d
                    // - function
//                    switch (pointType) {
//                        case UNEXPLORED:
//                            continue;
//                        case WALL: {
//                            g2d.drawImage(wall, x, y, blockSize, blockSize, null);
//                        }
//                        break;
//                        case EMPTY: {
//                            g2d.drawImage(dot, x, y, blockSize, blockSize, null);
//                        }
//                        break;
//                        case DROID: {
//                            g2d.drawImage(droid, x, y, blockSize, blockSize, null);
//                        }
//                        break;
//                        case OXYGEN: {
//                            g2d.drawImage(oxygen, x, y, blockSize, blockSize, null);
//                        }
//                        break;
//                        case GOAL: {
//                            g2d.drawImage(goal, x, y, blockSize, blockSize, null);
//                        }
//                        break;
                    }
                }
            }

    }
}
