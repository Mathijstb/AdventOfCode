package day2;

import java.util.Arrays;

public enum Color {
    RED("red"),
    BLUE("blue"),
    GREEN("green");

    public final String color;

    Color(String color) {
        this.color = color;
    }
    public static Color of(String color) {
        return Arrays.stream(Color.values()).filter(c -> c.color.equals(color)).findFirst().orElseThrow();
    }
}
