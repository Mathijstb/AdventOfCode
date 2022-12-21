package day19;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record State(List<Integer> robots, List<Integer> resources) {

    public State copy() {
        return new State(new ArrayList<>(robots), new ArrayList<>(resources));
    }

    public int getRobotAmount(RobotType type) {
        return robots.get(type.index);
    }

    public int getResourceAmount(ResourceType type) {
        return resources.get(type.index);
    }

    public boolean isHigherState(State other) {
        return IntStream.range(0, robots.size())
                .allMatch(i -> robots.get(i) >= other.robots.get(i) && resources.get(i) >= other.resources.get(i))
                && IntStream.range(0, robots.size())
                .anyMatch(i -> robots.get(i) > other.robots.get(i) || resources.get(i) > other.resources.get(i));
    }

    public State addRobots(List<Integer> addedRobots) {
        var newRobots = IntStream.range(0, robots.size()).mapToObj(i -> robots.get(i) + addedRobots.get(i)).toList();
        return new State(newRobots, new ArrayList<>(resources));
    }

    public State addResources(List<Integer> addedResources) {
        var newResources = IntStream.range(0, resources.size()).mapToObj(i -> resources.get(i) + addedResources.get(i)).toList();
        return new State(new ArrayList<>(robots), newResources);
    }

    public boolean canBuildRobot(RobotType type, BluePrint bluePrint) {
        return bluePrint.costs().get(type).entrySet().stream()
                .allMatch(entry-> getResourceAmount(entry.getKey()) >= entry.getValue());
    }

    public int timeNeededToBuildRobot(RobotType type, BluePrint bluePrint) {
        return bluePrint.costs().get(type).entrySet().stream()
                .map(entry -> {
                    var resourceType = entry.getKey();
                    var cost = entry.getValue();
                    var inStock =  getResourceAmount(resourceType);
                    var needed = cost - inStock;
                    var robotAmount = getRobotAmount(RobotType.harvestsResourceType(resourceType));
                    return robotAmount > 0 ? (int) Math.ceil((double)needed / robotAmount) : 999;
                }).reduce(0, Integer::max);
    }

    public State buildRobot(RobotType type, BluePrint bluePrint) {
        var newRobots = new ArrayList<>(robots);
        newRobots.set(type.index, newRobots.get(type.index) + 1);
        var newResources = new ArrayList<>(resources);
        bluePrint.costs().get(type).forEach((resourceType, cost) -> {
            var resourceIndex = resourceType.index;
            newResources.set(resourceIndex, newResources.get(resourceIndex) - cost);
        });
        return new State(newRobots, newResources);
    }

}
