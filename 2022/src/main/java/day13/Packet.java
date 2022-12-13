package day13;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Packet {

    Optional<Integer> numericalValue;

    List<Packet> packets;

    public Packet(Optional<Integer> numericalValue, List<Packet> packets) {
        this.numericalValue = numericalValue;
        this.packets = packets;
    }

    public boolean isValue() {
        return numericalValue.isPresent();
    }

    public int getValue() {
        return numericalValue.orElseThrow(() -> new IllegalStateException("No value present"));
    }

    @Override
    public String toString() {
        if (numericalValue.isPresent()) {
            return String.valueOf(numericalValue.get());
        }
        else {
            return packets.toString();
        }
    }
}
