package day16;

import lombok.Value;

import java.util.List;

@Value
public class Packet {

    PacketType type;

    int version;

    int literalValue;

    List<Packet> packets;

    public int getSumOfVersions() {
        return version + packets.stream().map(Packet::getSumOfVersions).mapToInt(v -> v).sum();
    }
}
