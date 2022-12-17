package day16;

import java.util.Set;

public record State(int time, Valve valve, Set<Valve> valvesOpened) {
}
