package day19;

public enum RobotType {
    ORE(0),
    CLAY(1),
    OBSIDIAN(2),
    GEODE(3);

    final int index;

    RobotType(int index) {
        this.index = index;
    }
}
