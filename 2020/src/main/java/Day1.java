import fileUtils.FileReader;

import java.util.List;
import java.util.stream.Collectors;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        List<Integer> integers = lines.stream().map(Integer::parseInt).collect(Collectors.toList());
        for (int i = 0; i < integers.size(); i++) {
            for (int j = i; j < integers.size(); j++) {
                for (int k = j; k < integers.size(); k++) {
                    int val1 = integers.get(i);
                    int val2 = integers.get(j);
                    int val3 = integers.get(k);
                    if (val1 + val2 + val3 == 2020) {
                        System.out.printf("%s * %s * %s = %s%n", val1, val2, val3, val1 * val2 * val3);
                    }
                }
            }
        }

    }
}
