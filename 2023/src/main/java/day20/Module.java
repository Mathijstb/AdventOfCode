package day20;

import java.util.List;

public abstract class Module {

    private final String name;

    private final List<String> sources;

    private final List<String> destinations;

    public Module(String name, List<String> sources, List<String> destinations) {
        this.name = name;
        this.sources = sources;
        this.destinations = destinations;
    }

    public abstract List<Pulse> processInput(Pulse pulse);

    public abstract void reset();

    public String getName() {
        return name;
    }

    public List<String> getSources() {
        return sources;
    }

    public List<String> getDestinations() {
        return destinations;
    }
}
