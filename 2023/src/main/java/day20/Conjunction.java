package day20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conjunction extends Module {

    public final Map<String, PulseType> lastPulseMap = new HashMap<>();

    public Conjunction(String name, List<String> sources, List<String> destinations) {
        super(name, sources, destinations);
        sources.forEach(source -> lastPulseMap.put(source, PulseType.LOW));
    }

    @Override
    public List<Pulse> processInput(Pulse pulse) {
        lastPulseMap.put(pulse.source(), pulse.type());
        PulseType sendType =  lastPulseMap.values().stream().allMatch(type -> type.equals(PulseType.HIGH)) ? PulseType.LOW : PulseType.HIGH;
        return getDestinations().stream().map(destination -> new Pulse(getName(), destination, sendType)).toList();
    }

    @Override
    public void reset() {
        lastPulseMap.keySet().forEach(key -> lastPulseMap.put(key, PulseType.LOW));
    }
}
