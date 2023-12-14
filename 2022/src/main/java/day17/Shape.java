package day17;

import grids.FiniteGrid;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

public class Shape {

    private final ShapeType type;

    private final FiniteGrid<Boolean> grid;

    public Shape(ShapeType type, FiniteGrid<Boolean> grid) {
        this.type = type;
        this.grid = grid;
    }

    public int getHeight() {
        return grid.getHeight();
    }

    public int getWidth() {
        return grid.getWidth();
    }

    public boolean getValue(Point point) {
        return grid.getValue(point);
    }

    public Set<Point> getRockPoints() {
        return grid.getAllPoints().stream().filter(grid::getValue).collect(Collectors.toSet());
    }

    public void draw() {
        System.out.println("Type: " + type);
        grid.draw(b -> b ? "#" : ".");
        System.out.println();
    }


}
