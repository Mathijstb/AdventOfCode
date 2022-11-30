package day16;

import java.util.Arrays;

public enum PacketType {
    SUM(0),
    PRODUCT(1),
    MINIMUM(2),
    MAXIMUM(3),
    LITERAL(4),
    GREATER_THAN(5),
    LESS_THAN(6),
    EQUAL_TO(7);

    int id;

    PacketType(int id) {
        this.id = id;
    }

    public static PacketType of(int id) {
        return Arrays.stream(PacketType.values()).filter(t -> t.id == id).findFirst().orElseThrow();
    }
}
