package networks;

import java.util.*;

public class Graph<T> {

    private final List<T> nodes = new ArrayList<>();

    private final Map<T, Map<T, Long>> edges = new HashMap<>();

    public void addNode(T node) {
        nodes.add(node);
    }

    public void addDirectedEdge(T startNode, T endNode, long weight) {
        edges.getOrDefault(startNode, new HashMap<>()).put(endNode, weight);
    }

    public Set<T> getNeighbours(T node) {
        return edges.getOrDefault(node, Collections.emptyMap()).keySet();
    }

    public Long getWeight(T startNode, T endNode) {
        return edges.get(startNode).get(endNode);
    }
}
