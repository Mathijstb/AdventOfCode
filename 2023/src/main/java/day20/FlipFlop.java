package day20;

import java.util.Collections;
import java.util.List;

public class FlipFlop extends Module {

    private boolean state = false;

    public FlipFlop(String name, List<String> sources, List<String> destinations) {
        super(name, sources, destinations);
    }

    @Override
    public List<Pulse> processInput(Pulse pulse) {
        return switch (pulse.type()) {
            case HIGH -> Collections.emptyList();
            case LOW -> {
                state = !state;
                yield getDestinations().stream().map(destination -> new Pulse(getName(), destination, state ? PulseType.HIGH : PulseType.LOW)).toList();
            }
        };
    }

    @Override
    public void reset() {
        state = false;
    }
}
