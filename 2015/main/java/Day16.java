import fileUtils.FileReader;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day16 {

    private static Map<String, Integer> sueCompoundMap = new HashMap<>();

    @Value
    private static class Aunt {
        int number;
        Map<String, Integer> compoundMap = new HashMap<>();
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input16b.csv");
        initializeCompoundMap(lines);
        List<String> lines2 = FileReader.getFileReader().readFile("input16.csv");
        List<Aunt> aunts = getAunts(lines2);
        determineAuntSue(aunts);
    }

    private static void determineAuntSue(List<Aunt> aunts) {
        List<Aunt> result = aunts.stream().filter(aunt ->  aunt.compoundMap.entrySet().stream()
                .map(entry -> {
                    String compoundName = entry.getKey();
                    if (!sueCompoundMap.containsKey(compoundName)) return true;
                    switch (compoundName) {
                        case "cats" :
                        case "trees" : return sueCompoundMap.get(compoundName) < entry.getValue();
                        case "pomeranians" :
                        case "goldfish" : return sueCompoundMap.get(compoundName) > entry.getValue();
                        default: return sueCompoundMap.get(compoundName).equals(entry.getValue());
                    }
                    //sueCompoundMap.get(entry.getKey()).equals(entry.getValue());
                })
                .reduce(true, Boolean::logicalAnd)).collect(Collectors.toList());
        Aunt aunt = result.stream().findFirst().orElseThrow();
        System.out.println("Found aunt: " + aunt);
    }

    private static void initializeCompoundMap(List<String> lines) {
        lines.forEach(line -> {
            String[] nameAndAmount = line.split(": ");
            sueCompoundMap.put(nameAndAmount[0], Integer.parseInt(nameAndAmount[1]));
        });
    }

    private static List<Aunt> getAunts(List<String> lines) {
        return lines.stream().map(line -> {

            String[] firstAndOthers = line.split(", ");
            String firstPart = firstAndOthers[0];
            String[] nameAndFirstCompoundAndValue = firstPart.split(": ");
            String nameAndNumber = nameAndFirstCompoundAndValue[0];
            Aunt aunt = new Aunt(Integer.parseInt(nameAndNumber.split(" ")[1]));

            String firstCompoundName = nameAndFirstCompoundAndValue[1];
            int firstCompoundValue = Integer.parseInt(nameAndFirstCompoundAndValue[2]);
            aunt.compoundMap.put(firstCompoundName, firstCompoundValue);
            for (int i = 1; i < firstAndOthers.length; i++) {
                String[] compoundNameAndValue = firstAndOthers[i].split(": ");
                aunt.compoundMap.put(compoundNameAndValue[0], Integer.parseInt(compoundNameAndValue[1]));
            }
            return aunt;
        }).collect(Collectors.toList());
    }
}
