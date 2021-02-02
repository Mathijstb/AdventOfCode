package day18;

import lombok.Value;

@Value
public class State {
    StateType type;
    Character character;

    public static State fromValue(Character character) {
        switch (character) {
            case '.': return new State(StateType.EMPTY, null);
            case '#': return new State(StateType.WALL, null);
            case '@': return new State(StateType.POSITION, '@');
            default: return Character.isUpperCase(character) ? new State(StateType.DOOR, character) : new State(StateType.KEY, character);
        }
    }
}
