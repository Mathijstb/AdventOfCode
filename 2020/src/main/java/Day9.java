import fileUtils.FileReader;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day9 {

    private static final long invalidNumber = 1721308972;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input9.csv");
        List<Long> numbers = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        findFirstInvalidNumber(numbers);
        List<Long> contiguousNumbers = findContiguousNumbersThatSumUpToInvalidNumber(numbers);
        if (contiguousNumbers != null) {
            System.out.println("Contiguous numbers: " + contiguousNumbers.toString());
            long min = Collections.min(contiguousNumbers);
            long max = Collections.max(contiguousNumbers);
            System.out.printf("Smallest number: %d, largest number: %d, sum: %d", min, max, min + max);
        }
    }

    private static void findFirstInvalidNumber(List<Long> numbers) {
        for (int i = 25; i < numbers.size(); i++) {
            long number = numbers.get(i);
            List<Long> previousNumbers = numbers.subList(i - 25, i);
            if (!numberSumsUpTwoAnyTwoNumbers(previousNumbers, number)) {
                System.out.printf("Number %d, index %d does not sum up%n", number, i);
                break;
            }
        }
    }

    private static boolean numberSumsUpTwoAnyTwoNumbers(List<Long> previousNumbers, Long number) {
        for (int i = 0; i < previousNumbers.size(); i++) {
            long firstNumber = previousNumbers.get(i);
            if (firstNumber >= number) continue;

            for (int j = i + 1; j < previousNumbers.size(); j++) {
                long secondNumber = previousNumbers.get(j);
                if (firstNumber + secondNumber == number) return true;
            }
        }
        return false;
    }

    private static List<Long> findContiguousNumbersThatSumUpToInvalidNumber(List<Long> numbers) {
        for (int i = 0; i < numbers.size(); i++) {
            long sum = 0;
            for (int j = i; j < numbers.size(); j++) {
                sum += numbers.get(j);
                if (sum > invalidNumber) {
                    break;
                }
                if (sum == invalidNumber) {
                    System.out.printf("sum found! index %d to %d %n", i, j);
                    return numbers.subList(i, j+1);
                }
            }
        }

        return null;
    }
}
