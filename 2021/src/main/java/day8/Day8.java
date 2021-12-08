package day8;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day8 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input8.csv");
        List<Display> displays = readDisplays(input);
        printNumberOfEasyDigits(displays);
    }

    private static List<Display> readDisplays(List<String> input) {
        List<Display> displays = new ArrayList<>();
        input.forEach(line -> {
            String[] signalsAndDigits = line.split(" \\| ");
            //String digits = signalsAndDigits[1];
            List<Signal> signals = Arrays.stream(signalsAndDigits[0].split(" ")).map(signal ->
                new Signal(signal.chars().mapToObj(c -> Segment.of((char) c)).collect(Collectors.toList()))).collect(Collectors.toList());
            List<Digit> digits = Arrays.stream(signalsAndDigits[1].split(" ")).map(digit ->
                new Digit(digit.chars().mapToObj(c -> Segment.of((char) c)).collect(Collectors.toList()))).collect(Collectors.toList());
            displays.add(new Display(signals, digits));
        });
        return displays;
    }

    private static void printNumberOfEasyDigits(List<Display> displays) {
        int number = displays.stream().map(d -> d.getNumberOfDisplayValues(1) + d.getNumberOfDisplayValues(4) + d.getNumberOfDisplayValues(7) + d.getNumberOfDisplayValues(8))
                .mapToInt(v -> v).sum();
        System.out.println("Number of easy digits: " + number);
    }

}
