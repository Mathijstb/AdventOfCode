package day1;

import lombok.Value;

import java.util.Arrays;

@Value
public class Instruction {

    public enum Turn {
        L("L"),
        R("R");

        public final String turn;

        Turn(String turn) {
            this.turn = turn;
        }

        public static Turn of(String turn) {
            return Arrays.stream(Turn.values()).filter(t -> t.turn.equals(turn)).findFirst().orElseThrow();
        }
    }

    Turn turn;
    int numberOfSteps;
}
