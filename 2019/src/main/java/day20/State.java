package day20;

import lombok.Value;

@Value
public class State {
    StateType type;
    Teleport teleport;

    public static State fromValue(Character character) {
        switch (character) {
            case '.': return new State(StateType.OPEN, null);
            case '#': return new State(StateType.WALL, null);
            default: return new State(StateType.EMPTY, null);
        }
    }

    public Teleport getTeleport() {
        return teleport;
    }
}
