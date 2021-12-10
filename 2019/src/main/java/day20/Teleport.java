package day20;

import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Teleport {
    String label;
    List<Point> gates = new ArrayList<>();
    Map<Point, GateType> gateTypeMap = new HashMap<>();

    public Teleport(String label) {
        this.label = label;
    }

    public GateType getGateType(Point gate) {
        assert gates.contains(gate);
        return gateTypeMap.get(gate);
    }

    public Point getOtherGate(Point gate) {
        assert gates.contains(gate);
        return gates.stream().filter(g -> !g.equals(gate)).findFirst().orElseThrow();
    }
}
