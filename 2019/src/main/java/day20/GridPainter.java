package day20;

import drawUtils.DrawGrid;
import drawUtils.Images;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GridPainter {

    private static Map<StateType, Consumer<DrawGrid.DrawParameters>> getPaintMap(Map<Point, State> pointStateMap) {
        Map<StateType, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        Set<Teleport> teleports = pointStateMap.values().stream().filter(s -> s.getType() == StateType.TELEPORT).map(State::getTeleport).collect(Collectors.toSet());
        Map<Teleport, Color> teleportColors = new HashMap<>();
        teleports.forEach(teleport -> {
            Random rand = new Random();
            teleportColors.put(teleport, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        });
        paintMap.put(StateType.OPEN, dp -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(StateType.WALL, dp -> dp.getG2d().drawImage(Images.getImage("wall.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(StateType.START, dp -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(StateType.FINISH, dp -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(StateType.TELEPORT, dp -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(StateType.TELEPORT_OUTEREXT, dp -> {
            Teleport teleport = pointStateMap.get(dp.getGridPoint()).getTeleport();
            if (teleport.label.equals("AA")) {
                dp.getG2d().drawImage(Images.getImage("start.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null);
            }
            else if (teleport.label.equals("ZZ")) {
                dp.getG2d().drawImage(Images.getImage("finish.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null);
            }
            else {
                Color color = teleportColors.get(teleport);
                dp.getG2d().setColor(color);
                dp.getG2d().fillRect(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
            }
        });
        paintMap.put(StateType.TELEPORT_INNEREXT, dp -> {
            Teleport teleport = pointStateMap.get(dp.getGridPoint()).getTeleport();
            if (teleport.label.equals("AA")) {
                dp.getG2d().drawImage(Images.getImage("start.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null);
            }
            else if (teleport.label.equals("ZZ")) {
                dp.getG2d().drawImage(Images.getImage("finish.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null);
            }
            else {
                Color color = teleportColors.get(teleport);
                dp.getG2d().setColor(color);
                dp.getG2d().fillOval(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
            }
        });
        paintMap.put(StateType.POSITION, dp -> {
            dp.getG2d().setColor(Color.BLUE);
            dp.getG2d().fillRect(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        return paintMap;
    }

    private Map<Point, StateType> drawMap;
    private DrawGrid<StateType> drawGrid;
    private static Map<StateType, Consumer<DrawGrid.DrawParameters>> paintMap;

    public GridPainter(Map<Point, State> pointStateMap) {
        if (paintMap == null) {
            paintMap = getPaintMap(pointStateMap);
        }
        drawMap = new HashMap<>();
        pointStateMap.forEach((key, value) -> drawMap.put(key, value.getType()));
        drawGrid = new DrawGrid<>("Portal maze", StateType.class, drawMap, StateType.EMPTY, paintMap);
        drawGrid.setLocation(0,0);
        drawGrid.setSize(400, 400);
    }

    public void draw(List<Point> shortestRoute, int speedInMs) {
        for (Point position: shortestRoute) {
            drawMap.put(position, StateType.POSITION);
            drawGrid.repaint();
            try {
                Thread.sleep(speedInMs);
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Interrupted");
            }
        }
    }
}
