package day19;

import java.util.Map;

public record BluePrint(int id, Map<RobotType, Map<ResourceType, Integer>> costs) {
}
