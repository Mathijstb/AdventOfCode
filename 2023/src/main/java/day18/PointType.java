package day18;


import static day18.Movement.*;

public record PointType(Direction source, Direction target) {

    public Movement getMovement() {
        return switch (source) {
            case L -> switch (target) {
                case L -> H;
                case U -> NE;
                case D -> SE;
                default -> throw new IllegalStateException();
            };
            case R -> switch (target) {
                case R -> H;
                case U -> NW;
                case D -> SW;
                default -> throw new IllegalStateException();
            };
            case U -> switch (target) {
                case U -> V;
                case R -> SE;
                case L -> SW;
                default -> throw new IllegalStateException();
            };
            case D -> switch (target) {
                case D -> V;
                case R -> NE;
                case L -> NW;
                default -> throw new IllegalStateException();
            };
        };
    }
}
