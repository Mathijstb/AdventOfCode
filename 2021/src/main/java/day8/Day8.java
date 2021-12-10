package day8;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day8 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input8.csv");
        List<Display> displays = readDisplays(input);
        printNumberOfEasyDigits(displays);

        printSumOfOutput(displays);
    }

    private static List<Display> readDisplays(List<String> input) {
        List<Display> displays = new ArrayList<>();
        input.forEach(line -> {
            String[] signalsAndDigits = line.split(" \\| ");
            List<Signal> signals = Arrays.stream(signalsAndDigits[0].split(" ")).map(signal ->
                new Signal(signal.chars().mapToObj(c -> Segment.of((char) c)).collect(Collectors.toSet()))).collect(Collectors.toList());
            List<Digit> digits = Arrays.stream(signalsAndDigits[1].split(" ")).map(digit ->
                new Digit(digit.chars().mapToObj(c -> Segment.of((char) c)).collect(Collectors.toSet()))).collect(Collectors.toList());
            displays.add(new Display(signals, digits));
        });
        return displays;
    }

    private static void printNumberOfEasyDigits(List<Display> displays) {
        int number = displays.stream().map(d -> d.getNumberOfDisplayValues(1) + d.getNumberOfDisplayValues(4) + d.getNumberOfDisplayValues(7) + d.getNumberOfDisplayValues(8))
                .mapToInt(v -> v).sum();
        System.out.println("Number of easy digits: " + number);
    }

    private static Map<Signal, Integer> getSignalToIntegerMap(Display display) {
        // Number of segments -> digits
        // #2 -> 1
        // #3 -> 7
        // #4 -> 4
        // #5 -> 2, 3, 5
        // #6 -> 0, 6, 9
        // #7 -> 8

        List<Signal> signals = display.getSignals();
        Signal signal1 = getSingleSignalWithNumberOfSegments(signals, 2);
        Signal signal4 = getSingleSignalWithNumberOfSegments(signals, 4);
        Signal signal7 = getSingleSignalWithNumberOfSegments(signals, 3);
        Signal signal8 = getSingleSignalWithNumberOfSegments(signals, 7);

        Segment signalSegmentForSegmentA = determineSignalSegmentForSegmentA(signal1, signal7);
        Segment signalSegmentForSegmentG = determineSignalSegmentForSegmentG(signal4, signal7, signals);
        Segment signalSegmentForSegmentE = determineSignalSegmentForSegmentE(signal4, signalSegmentForSegmentA, signalSegmentForSegmentG);
        Segment signalSegmentForSegmentB = determineSignalSegmentForSegmentB(signal7, signalSegmentForSegmentE, signalSegmentForSegmentG, signals);
        Segment signalSegmentForSegmentD = determineSignalSegmentForSegmentD(signal4, signal1, signalSegmentForSegmentB);
        Segment signalSegmentForSegmentF = determineSignalSegmentForSegmentF(signalSegmentForSegmentA, signalSegmentForSegmentB, signalSegmentForSegmentD, signalSegmentForSegmentG, signals);
        Segment signalSegmentForSegmentC = determineSignalSegmentForSegmentC(signal1, signalSegmentForSegmentF);

        Signal signal0 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentB, signalSegmentForSegmentC, signalSegmentForSegmentE, signalSegmentForSegmentF, signalSegmentForSegmentG));
        Signal signal2 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentC, signalSegmentForSegmentD, signalSegmentForSegmentE, signalSegmentForSegmentG));
        Signal signal3 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentC, signalSegmentForSegmentD, signalSegmentForSegmentF, signalSegmentForSegmentG));
        Signal signal5 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentB, signalSegmentForSegmentD, signalSegmentForSegmentF, signalSegmentForSegmentG));
        Signal signal6 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentB, signalSegmentForSegmentD, signalSegmentForSegmentE, signalSegmentForSegmentF, signalSegmentForSegmentG));
        Signal signal9 = new Signal(Set.of(signalSegmentForSegmentA, signalSegmentForSegmentB, signalSegmentForSegmentC, signalSegmentForSegmentD, signalSegmentForSegmentF, signalSegmentForSegmentG));

        Map<Signal, Integer> signalSegmentToDisplaySegmentMap = new HashMap<>();
        signalSegmentToDisplaySegmentMap.put(signal0, 0);
        signalSegmentToDisplaySegmentMap.put(signal1, 1);
        signalSegmentToDisplaySegmentMap.put(signal2, 2);
        signalSegmentToDisplaySegmentMap.put(signal3, 3);
        signalSegmentToDisplaySegmentMap.put(signal4, 4);
        signalSegmentToDisplaySegmentMap.put(signal5, 5);
        signalSegmentToDisplaySegmentMap.put(signal6, 6);
        signalSegmentToDisplaySegmentMap.put(signal7, 7);
        signalSegmentToDisplaySegmentMap.put(signal8, 8);
        signalSegmentToDisplaySegmentMap.put(signal9, 9);
        return signalSegmentToDisplaySegmentMap;
    }

    private static Segment determineSignalSegmentForSegmentA(Signal signal1, Signal signal7) {
        //Segment a = Signal7 - Signal1
        List<Segment> segments = new ArrayList<>(signal7.getSegments());
        segments.removeAll(signal1.getSegments());
        assert segments.size() == 1;
        return segments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentG(Signal signal4, Signal signal7, List<Signal> signals) {
        // -----         -----          |     |
        // |                 |          |     |   ----
        // -----   ----      |    ----  |-----|   ----
        //     |             |                |
        // -----             |                |          _____ -> g
        //Segment g = Signals with 5 segments (2,3,5) - signal7 - signal 4, then filter by the one with only one segment left
        List<Signal> signalsWith5Segments = getSignalsWithNumberOfSegments(signals, 5);
        List<Segment> segmentGSegments = signalsWith5Segments.stream().map(signal -> {
            List<Segment> segments = new ArrayList<>(signal.getSegments());
            segments.removeAll(signal4.getSegments());
            segments.removeAll(signal7.getSegments());
            return segments;
        }).filter(segments -> segments.size() == 1).findFirst().orElseThrow();
        assert segmentGSegments.size() == 1;
        return segmentGSegments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentE(Signal signal4, Segment segmentA, Segment segmentG) {
        //Segment e = All segments - segment a - segment g - signal 4
        List<Segment> allSegments = Arrays.stream(Segment.values()).collect(Collectors.toList());
        allSegments.removeAll(signal4.getSegments());
        allSegments.remove(segmentA);
        allSegments.remove(segmentG);
        assert allSegments.size() == 1;
        return allSegments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentB(Signal signal7, Segment segmentE, Segment segmentG, List<Signal> signals) {
        //Segment b = Signals with 6 segments (0,6,9) - signal 7 - segmentE - segmentG, then filter by the one with only one segment left
        List<Signal> signalsWith6Segments = getSignalsWithNumberOfSegments(signals, 6);
        List<Segment> segmentBSegments = signalsWith6Segments.stream().map(signal -> {
            List<Segment> segments = new ArrayList<>(signal.getSegments());
            segments.removeAll(signal7.getSegments());
            segments.remove(segmentE);
            segments.remove(segmentG);
            return segments;
        }).filter(segments -> segments.size() == 1).findFirst().orElseThrow();
        assert segmentBSegments.size() == 1;
        return segmentBSegments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentD(Signal signal4, Signal signal1, Segment segmentB) {
        //Segment d = Signal 4 - Signal 1 - segment b
        List<Segment> segmentDsegments = new ArrayList<>(signal4.getSegments());
        segmentDsegments.removeAll(signal1.getSegments());
        segmentDsegments.remove(segmentB);
        assert segmentDsegments.size() == 1;
        return segmentDsegments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentF(Segment segmentA, Segment segmentB, Segment segmentD, Segment segmentG, List<Signal> signals) {
        //Segment f = Signals with 5 segments (2,3,5) - signal a - segment b - segment d - segment g, then filter by the one with only one segment left
        List<Signal> signalsWith5Segments = getSignalsWithNumberOfSegments(signals, 5);
        List<Segment> segmentFSegments = signalsWith5Segments.stream().map(signal -> {
            List<Segment> segments = new ArrayList<>(signal.getSegments());
            segments.remove(segmentA);
            segments.remove(segmentB);
            segments.remove(segmentD);
            segments.remove(segmentG);
            return segments;
        }).filter(segments -> segments.size() == 1).findFirst().orElseThrow();
        assert segmentFSegments.size() == 1;
        return segmentFSegments.get(0);
    }

    private static Segment determineSignalSegmentForSegmentC(Signal signal1, Segment segmentF) {
        List<Segment> segmentCsegments = new ArrayList<>(signal1.getSegments());
        segmentCsegments.remove(segmentF);
        assert segmentCsegments.size() == 1;
        return segmentCsegments.get(0);
    }

    private static Signal getSingleSignalWithNumberOfSegments(List<Signal> signals, int numberOfSegments) {
        List<Signal> result = getSignalsWithNumberOfSegments(signals, numberOfSegments);
        assert result.size() == 1;
        return result.get(0);
    }

    private static List<Signal> getSignalsWithNumberOfSegments(List<Signal> signals, int numberOfSegments) {
        return signals.stream().filter(signal -> signal.getSegments().size() == numberOfSegments).collect(Collectors.toList());
    }

    private static void printSumOfOutput(List<Display> displays) {
        List<Integer> sums = displays.stream().map(display -> {
            Map<Signal, Integer> signalToIntegerMap = getSignalToIntegerMap(display);
            List<Integer> output = display.getDigits().stream().map(digit -> {
                Signal signal = new Signal(digit.getLightedSegments());
                return signalToIntegerMap.get(signal);
            }).collect(Collectors.toList());
            assert output.size() == 4;
            return output.get(0) * 1000 + output.get(1) * 100 + output.get(2) * 10 + output.get(3);
        }).collect(Collectors.toList());

        long sum = sums.stream().mapToLong(v -> v).sum();
        System.out.println("Output sum: " + sum);
    }




}
