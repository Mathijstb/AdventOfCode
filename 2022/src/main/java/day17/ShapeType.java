package day17;

import grids.FiniteGrid;

import java.util.ArrayList;
import java.util.List;

public enum ShapeType {
    HORIZONTAL,
    PLUS,
    DOWN_RIGHT,
    VERTICAL,
    BLOCK;

    public static Shape createShape(ShapeType type) {
        List<String> lines = new ArrayList<>();
        switch (type) {
            case HORIZONTAL -> lines.add("####");
            case PLUS -> {
                lines.add(".#.");
                lines.add("###");
                lines.add(".#.");
            }
            case DOWN_RIGHT -> {
                lines.add("..#");
                lines.add("..#");
                lines.add("###");
            }
            case VERTICAL -> {
                lines.add("#");
                lines.add("#");
                lines.add("#");
                lines.add("#");
            }
            case BLOCK -> {
                lines.add("##");
                lines.add("##");
            }
        }
        FiniteGrid<Boolean> grid = new FiniteGrid<>();
        lines.forEach(line -> grid.addRow(line.chars().mapToObj(c -> c == '#').toList()));
        return new Shape(type, grid);
    }
}
