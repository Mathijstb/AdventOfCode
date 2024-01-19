package day19;

import java.util.Arrays;

public enum RangeType {
    X("x"),
    M("m"),
    A("a"),
    S("s");

    public final String type;

    RangeType(String type) {
        this.type = type;
    }

    public static RangeType of(String type) {
        return Arrays.stream(RangeType.values()).filter(v -> v.type.equals(type)).findFirst().orElseThrow();
    }
}
