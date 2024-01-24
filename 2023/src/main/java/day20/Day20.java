package day20;

import algorithms.LCM;
import fileUtils.FileReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day20 {

    private static final String BROADCASTER = "broadcaster";

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input20.csv");
        var modules = parseModules(lines);
        var numberOfPulsesSent = pressButton(modules, 1000).numberOfPulsesSent();
        var numberOfLowPulses = numberOfPulsesSent.get(PulseType.LOW);
        var numberOfHighPulses = numberOfPulsesSent.get(PulseType.HIGH);
        System.out.println("Number of low pulses: " + numberOfLowPulses);
        System.out.println("Number of high pulses: " + numberOfHighPulses);
        System.out.println("Multiplication: " + numberOfLowPulses * numberOfHighPulses);

        var timesPressed = findLowestNumberOfButtonPressToOutputLow(modules);
        System.out.println("Times pressed: " + timesPressed);
    }

    private static ProcessResult pressButton(Map<String, Module> modules, Integer numberOfTimes) {
        Map<PulseType, Integer> numberOfPulsesSent = new HashMap<>(Map.of(PulseType.LOW, 0, PulseType.HIGH, 0));
        ProcessResult result = new ProcessResult(new HashMap<>(), Collections.emptyList(), Collections.emptyMap());
        for (int i = 0; i < numberOfTimes; i++) {
           result = pressButton(modules);
           var nextNumberSent = result.numberOfPulsesSent();
           numberOfPulsesSent.put(PulseType.LOW, numberOfPulsesSent.get(PulseType.LOW) + nextNumberSent.get(PulseType.LOW));
           numberOfPulsesSent.put(PulseType.HIGH, numberOfPulsesSent.get(PulseType.HIGH) + nextNumberSent.get(PulseType.HIGH));
        }
        return new ProcessResult(numberOfPulsesSent, result.pulseHistory(), modules);
    }

    private static long findLowestNumberOfButtonPressToOutputLow(Map<String, Module> modules) {
        var output = (Output) modules.entrySet().stream().filter(entry -> entry.getValue() instanceof Output).findFirst().orElseThrow().getValue();
        var sources = output.getSources().stream()
                .map(source -> modules.get(source).getSources())
                .flatMap(List::stream)
                .map(modules::get).toList();
        var lowestNumbers = sources.stream().map(source -> findLowestNumberOfButtonPress(modules, source)).toList();
        return LCM.findLeastCommonMultiple(lowestNumbers).longValue();
    }

    private static long findLowestNumberOfButtonPress(Map<String, Module> modules, Module module) {
        modules.values().forEach(Module::reset);
        var result = pressButton(modules);
        var timesPressed = 1L;
        while (result.pulseHistory().stream().noneMatch(pulse -> pulse.source().equals(module.getName()) && pulse.type().equals(PulseType.HIGH))) {
            result = pressButton(modules);
            timesPressed += 1;
        }
        return timesPressed;
    }

    private static ProcessResult pressButton(Map<String, Module> modules) {
        Map<PulseType, Integer> numberOfPulsesSent = new HashMap<>();
        var pulseHistory = new ArrayList<Pulse>();
        var pulses = new ArrayList<>(List.of(new Pulse("button", BROADCASTER, PulseType.LOW)));
        while (!pulses.isEmpty()) {
            var currentPulse = pulses.removeFirst();
            pulseHistory.add(currentPulse);
            numberOfPulsesSent.put(currentPulse.type(), numberOfPulsesSent.getOrDefault(currentPulse.type(), 0) + 1);
            var destination = modules.get(currentPulse.destination());
            pulses.addAll(destination.processInput(currentPulse));
        }
        return new ProcessResult(numberOfPulsesSent, pulseHistory, modules);
    }

    private static Map<String, Module> parseModules(List<String> lines) {
        var destinationMap = new HashMap<String, List<String>>();
        lines.forEach(line -> {
            var sourceAndTargets = line.replace("%", "").replace("&", "").split(" -> ");
            destinationMap.put(sourceAndTargets[0], Arrays.stream(sourceAndTargets[1].split(", ")).toList());
        });
        var modules = lines.stream().map(line -> {
            var sourceAndTargets = line.split(" -> ");
            var targets = Arrays.stream(sourceAndTargets[1].split(", ")).toList();
            var source = sourceAndTargets[0];
            if (source.equals(BROADCASTER)) {
                return new Broadcaster(BROADCASTER, List.of("button"), targets);
            }
            else {
                var name = source.substring(1);
                var sources = destinationMap.entrySet().stream()
                        .filter(entry -> entry.getValue().contains(name))
                        .map(Map.Entry::getKey).toList();
                return switch (source.substring(0, 1)) {
                    case "%" -> new FlipFlop(name, sources, targets);
                    case "&" -> new Conjunction(name, sources, targets);
                    default -> throw new IllegalStateException("Unexpected value: " + source.charAt(0));
                };
            }
        }).toList();
        Map<String, Module> moduleMap = modules.stream().collect(Collectors.toMap(Module::getName, Function.identity()));
        var destinationSet = new HashSet<String>();
        destinationMap.values().forEach(destinationSet::addAll);
        var output = destinationSet.stream().filter(d -> !moduleMap.containsKey(d)).toList().get(0);
        var sources = destinationMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(output))
                .map(Map.Entry::getKey).toList();
        moduleMap.put(output, new Output(output, sources, Collections.emptyList()));
        return moduleMap;
    }

}
