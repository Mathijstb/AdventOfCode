package day16;

import java.util.List;

public record Valve(int index, String name, int flowRate, List<String> valves) {
}
