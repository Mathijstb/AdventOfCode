package day20;

import java.util.List;
import java.util.Map;

public record ProcessResult(Map<PulseType, Integer> numberOfPulsesSent, List<Pulse> pulseHistory, Map<String, Module> modules) {
}
