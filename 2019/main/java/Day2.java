import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<Integer> numbers = readNumbers(line);
        findParameterConfiguration(numbers);
    }

    public static void findParameterConfiguration(List<Integer> numbers) {
        boolean found = false;
        int theNoun = -1;
        int theVerb = -1;
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99 ; verb++) {
                List<Integer> newNumbers = new ArrayList<>(numbers);
                newNumbers.set(1, noun);
                newNumbers.set(2, verb);
                executeProgram(newNumbers);
                if (newNumbers.get(0) == 19690720) {
                    found = true;
                    theNoun = noun;
                    theVerb = verb;
                    break;
                }
            }
            if (found) break;
        }
        System.out.println("Noun: " + theNoun);
        System.out.println("Verb: " + theVerb);
        System.out.println("Result: " + (100 * theNoun + theVerb));
    }

    public static void executeProgram(List<Integer> numbers) {
        int index = 0;
        while (true) {
            int opCode = numbers.get(index);
            if (opCode == 99) break;
            int operand1 = numbers.get(numbers.get(index + 1));
            int operand2 = numbers.get(numbers.get(index + 2));
            int destinationIndex = numbers.get(index + 3);
            if (opCode == 1) {
                numbers.set(destinationIndex, operand1 + operand2);
            }
            else if (opCode == 2) {
                numbers.set(destinationIndex, operand1 * operand2);
            }
            else
                throw new IllegalStateException("Invalid opCode");
            index += 4;
        }
    }

    private static List<Integer> readNumbers(String line) {
        return Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

}
