package day20;

import java.util.List;

public class Broadcaster extends Module {

    public Broadcaster(String name, List<String> sources, List<String> destinations) {
        super(name, sources, destinations);
    }

    @Override
    public List<Pulse> processInput(Pulse pulse) {
        return getDestinations().stream().map(destination -> new Pulse(getName(), destination, pulse.type())).toList();
    }

    @Override
    public void reset() {
        //do nothing
    }

}
