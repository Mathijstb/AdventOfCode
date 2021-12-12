package networks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Network<T extends Network.Node> {

    public interface Node {
        String getName();
    }

    private final Map<String, T> nodes = new HashMap<>();

    private final Map<String, Set<T>> connections = new HashMap<>();

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
    }

    public T getNode(String name) {
        return nodes.get(name);
    }

    public void addNode(T node) {
        nodes.put(node.getName(), node);
        connections.put(node.getName(), new HashSet<>());
    }

    public void addConnection(String name, T node) {
        connections.get(name).add(node);
    }


    public Set<T> getConnections(String name) {
        return connections.get(name);
    }
}
