package day19;

public enum ResourceType {
    ORE(0),
    CLAY(1),
    OBSIDIAN(2),
    GEODE(3);

    final int index;

    ResourceType(int index) {
        this.index = index;
    }
}
