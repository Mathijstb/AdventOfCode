package day17;

import com.google.common.collect.Sets;

import java.util.Set;

public enum State {
    NEWLINE,
    SCAFFOLD,
    EMPTY,
    CROSSING,
    LEFT,
    RIGHT,
    OFF,
    UP,
    DOWN,
    OTHER;

    public int otherValue;

    public static Set<State> gridStates = Sets.newHashSet(NEWLINE, SCAFFOLD, EMPTY, CROSSING, LEFT, RIGHT, UP, DOWN);

    public static State fromValue(int value) {
        switch (value) {
            case 10: return NEWLINE;
            case 35: return SCAFFOLD;
            case 46: return EMPTY;
            case 60: return LEFT;
            case 62: return RIGHT;
            case 88: return OFF;
            case 94: return UP;
            case 118: return DOWN;
            default: {
                State state = OTHER;
                state.otherValue = value;
                return state;
            }
        }
    }

    public static State fromTextValue(int value) {
        if (value == 10) {
            return NEWLINE;
        }
        State state = OTHER;
        state.otherValue = value;
        return state;
    }

    public Character toCharacter() {
        switch (this) {
            case NEWLINE: throw new IllegalArgumentException("Cannot draw newline");
            case EMPTY: return ' ';
            case SCAFFOLD: return '#';
            case CROSSING: return 'O';
            case LEFT: return '<';
            case RIGHT: return '>';
            case UP: return '^';
            case DOWN: return  'v';
            default: throw new IllegalArgumentException("Invalid state");
        }
    }
}
