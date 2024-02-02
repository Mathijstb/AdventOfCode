package day20;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Output extends Module {

    public Optional<PulseType> lastPulseType = Optional.empty();

    public Output(String name, List<String> sources, List<String> destinations) {
        super(name, sources, destinations);
    }

    @Override
    public List<Pulse> processInput(Pulse pulse) {
        lastPulseType = Optional.of(pulse.type());
        return Collections.emptyList();
    }

    @Override
    public void reset() {
        lastPulseType = Optional.empty();
    }

}
