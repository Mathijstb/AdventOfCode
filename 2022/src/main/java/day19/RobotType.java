package day19;

public enum RobotType {
    ORE(0),
    CLAY(1),
    OBSIDIAN(2),
    GEODE(3);

    final int index;

    public static RobotType harvestsResourceType(ResourceType resourceType) {
        return switch (resourceType) {
            case ORE -> ORE;
            case CLAY -> CLAY;
            case OBSIDIAN -> OBSIDIAN;
            case GEODE -> GEODE;
        };
    }

    RobotType(int index) {
        this.index = index;
    }
}
