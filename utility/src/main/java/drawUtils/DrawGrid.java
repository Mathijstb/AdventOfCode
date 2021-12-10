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
        EventQueue.invokeLater(() -> gridPanel.pointTypeMap = pointTypeMap);
    }

    public void repaint() {
        EventQueue.invokeLater(() -> frame.repaint());
    }

    public void setLocation(int x, int y) {
        EventQueue.invokeLater(() -> frame.setLocation(x, y));
    }

    public void setSize(int width, int height) {
        EventQueue.invokeLater(() -> frame.setSize(width, height));
    }

    @Value
    public static class DrawParameters {
        Graphics2D g2d;
        Point gridPoint;
        Point drawPoint;
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
            g2d.setBackground(Color.WHITE);
            Grid<T> grid = new Grid<>(c, pointTypeMap, defaultValue);
            int stateSize = Math.max(grid.getHeight(), grid.getWidth());

            int blockSize = Math.min(getWidth() - 4, getHeight() - 4) / stateSize;
            for (int i = 0; i < grid.getHeight(); i++) {
                T[] row = grid.getRow(i);
                for (int j = 0; j < row.length; j++) {
                    T pointType = row[j];
                    int x = j * blockSize;
                    int y = i * blockSize;
                    if (paintMap.containsKey(pointType)) {
                        paintMap.get(pointType).accept(new DrawParameters(g2d, new Point(j, i), new Point(x, y), blockSize));
                    }
                }
            }
        }
    }
}
