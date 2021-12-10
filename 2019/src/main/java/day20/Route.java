package day20;

import lombok.Value;

import java.awt.*;
import java.util.List;

@Value
public class Route {
    Point fromGate;
    Point toGate;
    Teleport fromTeleport;
    Teleport toTeleport;
    List<Point> moves;

    public GateType getFromGateType() {
        return fromTeleport.getGateType(fromGate);
    }

    public GateType getToGateType() {
        return toTeleport.getGateType(toGate);
    }
}
