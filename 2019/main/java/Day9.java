import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Day9 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input9.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = readNumbers(line);
        for (int i = 0; i < 1000; i++) {
            numbers.add(String.valueOf(0));
        }
        LinkedBlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
        Day5.executeProgram(numbers, new LinkedBlockingDeque<>(Lists.newArrayList(2L)), outputs);
        System.out.println(outputs);
    }

    private static List<String> readNumbers(String line) {
        return Arrays.stream(line.split(",")).collect(Collectors.toList());
    }

}
