package algorithms;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Dijkstra {

    public record PathResult<T>(List<T> path, long cost) {
    }

    public static <T> PathResult<T> calculateMinimalPath(T start, Predicate<T> goalCondition,
                                                         Function<T, Long> getCost,
                                                         Function<T, List<T>> getNeighbours) {
        Map<T, Optional<T>> cameFrom = new HashMap<>();
        var costSoFar = new HashMap<T, Long>();
        costSoFar.put(start, 0L);
        var frontier = new PriorityQueue<T>(Comparator.comparing(costSoFar::get));
        frontier.add(start);
        while (!frontier.isEmpty()) {
            var current = frontier.remove();
            if (goalCondition.test(current)) {
                break;
            }
            var neighbours = getNeighbours.apply(current);
            for (T next : neighbours) {
                var nextCost = getCost.apply(next);
                var newCost = costSoFar.get(current) + nextCost;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    cameFrom.put(next, Optional.of(current));
                    costSoFar.put(next, newCost);
                    frontier.remove(next);
                    frontier.add(next);
                }
            }
        }
        var goalNode = cameFrom.keySet().stream().filter(goalCondition).
                min(Comparator.comparingLong(costSoFar::get)).stream()
                .findFirst().orElseThrow();
        return new PathResult<>(getPath(cameFrom, start, goalNode), costSoFar.get(goalNode));
    }

    private static <T> List<T> getPath(Map<T, Optional<T>> cameFrom, T start, T goalNode) {
        List<T> path = new ArrayList<>();
        var current = goalNode;
        while(!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current).orElseThrow();
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }
}
