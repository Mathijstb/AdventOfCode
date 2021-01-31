package drawUtils;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class Grid<T> {

    private final T[][] grid;

    public Grid(Class<T> c, Map<Point, T> pointTypeMap, T defaultValue) {
        int minX = pointTypeMap.keySet().stream().map(p -> p.x).min(Comparator.comparing(x -> x)).orElseThrow();
        int maxX = pointTypeMap.keySet().stream().map(p -> p.x).max(Comparator.comparing(x -> x)).orElseThrow();
        int minY = pointTypeMap.keySet().stream().map(p -> p.y).min(Comparator.comparing(y -> y)).orElseThrow();
        int maxY = pointTypeMap.keySet().stream().map(p -> p.y).max(Comparator.comparing(y -> y)).orElseThrow();

        int height = maxY - minY + 1;
        int width = maxX - minX + 1;

        @SuppressWarnings("unchecked")
        final T[][] grid = (T[][]) Array.newInstance(c, height, width);
        this.grid = grid;
        fill(defaultValue);
        pointTypeMap.forEach((key, value) -> set(key.y - minY, key.x - minX, value));
    }

    public T get(int y, int x) {
        return grid[y][x];
    }

    public T[] getRow(int y) {
        return grid[y];
    }

    public int getHeight() {
        return grid.length;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public void set(int y, int x, T value) {
        grid[y][x] = value;
    }

    public void fill(T pointType) {
        for (T[] row : grid) {
            Arrays.fill(row, pointType);
        }
    }
}