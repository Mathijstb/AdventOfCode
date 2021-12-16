package day16;

import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class Packet {

    PacketType type;

    int version;

    long literalValue;

    List<Packet> packets;

    public long getValue() {
        if (type.equals(PacketType.LITERAL)) return literalValue;

        Stream<Long> valueStream = packets.stream().map(Packet::getValue);
        switch (type) {
            case GREATER_THAN: {
                List<Long> values = valueStream.collect(Collectors.toList());
                assert values.size() == 2;
                return values.get(0) > values.get(1) ? 1 : 0;
            }
            case LESS_THAN: {
                List<Long> values = valueStream.collect(Collectors.toList());
                assert values.size() == 2;
                return values.get(0) < values.get(1) ? 1 : 0;
            }
            case EQUAL_TO: {
                List<Long> values = valueStream.collect(Collectors.toList());
                assert values.size() == 2;
                return Objects.equals(values.get(0), values.get(1)) ? 1 : 0;
            }
            case SUM: return valueStream.mapToLong(v -> v).sum();
            case PRODUCT: return valueStream.reduce(1L, (p1, p2) -> p1 * p2);
            case MINIMUM: return valueStream.mapToLong(v -> v).min().orElseThrow();
            case MAXIMUM: return valueStream.mapToLong(v -> v).max().orElseThrow();
            default: throw new IllegalArgumentException("Invalid packet type");
        }
    }

    public int getSumOfVersions() {
        return version + packets.stream().map(Packet::getSumOfVersions).mapToInt(v -> v).sum();
    }
}
