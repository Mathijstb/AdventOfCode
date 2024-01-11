package day17;

import java.util.Optional;

public class PointState {
    public PointState(int heat) {
        this.heat = heat;
    }

    public int heat;

    public Optional<String> pathCharacter = Optional.empty();

    public String getDrawCharacter() {
        return pathCharacter.orElseGet(() -> String.valueOf(heat));
    }

}
