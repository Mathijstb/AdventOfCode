package day19;

public record MachinePart(int x, int m, int a, int s) {

    public int getProperty(RangeType rangeType) {
        return switch (rangeType) {
            case X -> x;
            case M -> m;
            case A -> a;
            case S -> s;
        };
    }
}
