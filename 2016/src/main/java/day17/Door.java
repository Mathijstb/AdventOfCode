package day17;

public enum Door {
    UP("U"),
    DOWN("D"),
    LEFT("L"),
    RIGHT("R");

    final String shortName;

    Door(String shortName) {
        this.shortName = shortName;
    }
}
