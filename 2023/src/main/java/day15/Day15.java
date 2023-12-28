package day15;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.IntStream;

public class Day15 {

    public static void execute() {
        var strings = Arrays.stream(FileReader.getFileReader().readFile("input15.csv").stream()
                .findFirst().orElseThrow()
                .split(",")).toList();
        var hashSum = findHashSum(strings);
        System.out.println("Sum of hashes: " + hashSum);

        //part 2
        var instructions = parseInstructions(strings);
        var boxNumberToLensesMap = executeInstructions(instructions);
        var focussingPower = calculateFocussingPower(boxNumberToLensesMap);
        System.out.println("Focussing power: " + focussingPower);
    }

    private static List<Instruction> parseInstructions(List<String> strings) {
        return strings.stream().map(s -> s.contains("=")
                    ? new Instruction(s.split("=")[0], Operation.INSERT, Optional.of(Integer.parseInt(s.split("=")[1])))
                    : new Instruction(s.split("-")[0], Operation.REMOVE, Optional.empty()))
                .toList();
    }

    private static Map<Integer, List<Lens>> executeInstructions(List<Instruction> instructions) {
         Map<Integer, List<Lens>> boxNumberToLensesMap = new HashMap<>();
         instructions.forEach(instruction -> {
             var boxNumber = getHash(instruction.label());
             var lensList = boxNumberToLensesMap.getOrDefault(boxNumber, new ArrayList<>());
             var oldLensOpt = lensList.stream().filter(lenses -> lenses.label().equals(instruction.label())).findFirst();
             switch (instruction.operation()) {
                 case INSERT -> {
                    var newLens = new Lens(instruction.label(), instruction.focalLength().orElseThrow());
                    oldLensOpt.ifPresentOrElse(
                            oldLens -> {
                                var index = lensList.indexOf(oldLens);
                                lensList.add(index, newLens);
                                lensList.remove(oldLens);
                            },
                            () -> lensList.add(newLens)
                    );
                 }
                 case REMOVE -> oldLensOpt.ifPresent(lensList::remove);
             }
             boxNumberToLensesMap.put(boxNumber, lensList);
         });
         return boxNumberToLensesMap;
    }

    private static int calculateFocussingPower(Map<Integer, List<Lens>> boxNumberToLensesMap) {
        return boxNumberToLensesMap.entrySet().stream().map(entry -> {
            var boxNumber = entry.getKey();
            var lensesList = entry.getValue();

            return IntStream.range(0, lensesList.size())
                    .mapToObj(i -> (boxNumber + 1) * (i + 1) * lensesList.get(i).focalLength())
                    .reduce(0, Integer::sum);
        }).reduce(0, Integer::sum);
    }

    private static int findHashSum(List<String> strings) {
        return strings.stream().map(Day15::getHash).reduce(0, Integer::sum);
    }

    private static int getHash(String string) {
        return string.chars().reduce(0, (a,b) -> ((a + b) * 17) % 256);
    }
}
