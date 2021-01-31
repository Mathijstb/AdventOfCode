import fileUtils.FileReader;
import intCode.IntCodeComputer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day5 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input5.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = readNumbers(line);
        IntCodeComputer.start(numbers);
        IntCodeComputer.addInput(1L);
        System.out.println("Last output: " + IntCodeComputer.getNextOutputValue());
    }

    private static List<String> readNumbers(String line) {
        return Arrays.stream(line.split(",")).collect(Collectors.toList());
    }
}
