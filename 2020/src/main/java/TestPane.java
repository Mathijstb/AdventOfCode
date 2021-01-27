import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static java.lang.Math.round;

public class TestPane extends JPanel {

    public static JFrame frame = new JFrame("Testing");

    public static void Test() {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TestPane());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static final int CELL_SIZE = 5;
    private static final int START_X = 700;
    private static final int START_Y = 700;

    private static Point shipCoordinate = new Point(START_X, START_Y);
    private static Point waypointCoordinate = new Point(START_X, START_Y);

    public static void setShipCoordinate(Point shipCoordinate) {
        TestPane.shipCoordinate = shipCoordinate;
    }

    public static void setWaypointCoordinate(Point waypointCoordinate) {
        TestPane.waypointCoordinate = waypointCoordinate;
    }

    public TestPane() {
        setBackground(Color.BLUE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000 * CELL_SIZE, 1000 * CELL_SIZE);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int ship_paint_x = START_X + (int) round(shipCoordinate.x * 0.003);
        int ship_paint_y = START_Y + (int) round(shipCoordinate.y * 0.003);
        int waypoint_paint_x = ship_paint_x + waypointCoordinate.x;
        int waypoint_paint_y = ship_paint_y + waypointCoordinate.y;
        paintShip(g2d, ship_paint_x, ship_paint_y);
        paintWaypoint(g2d, waypoint_paint_x, waypoint_paint_y);
        g2d.drawLine(ship_paint_x + CELL_SIZE /2, ship_paint_y + CELL_SIZE /2, waypoint_paint_x + CELL_SIZE /2, waypoint_paint_y + CELL_SIZE /2);
        g2d.dispose();
    }

    protected void paintShip(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.RED);
        g2d.fillOval(x, y, CELL_SIZE, CELL_SIZE);
    }

    protected void paintWaypoint(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(x, y, CELL_SIZE, CELL_SIZE);
    }
}

