package day18;

import java.awt.*;

public record Instruction(Direction direction, int numberOfSteps, String colorCode) {

    public Color getColor() {
        int r = Integer.parseInt(colorCode.substring(0, 2), 16);
        int g = Integer.parseInt(colorCode.substring(4, 6), 16);
        int b = Integer.parseInt(colorCode.substring(4, 6), 16);
        return new Color(r, g, b);
    }
}
