import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Day19 {
    
    @Value
    private static class Replacement {
        String strFrom;
        String strTo;
    }
    
    private static final List<Replacement> replacements = new ArrayList<>();
    private static String molecule;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19.csv");
        readReplacements(lines);
        findNumberOfReplacements();
    }

    public static String replace(String s, String in, String out, int position) {
        return s.substring(0, position) + out + s.substring(position + in.length());
    }

    public static void findNumberOfReplacements() {

        int count = 0;

        while(!molecule.equals("e")) {
            for (Replacement replacement : replacements) {
                if (molecule.contains(replacement.strTo)) {
                    molecule = replace(molecule, replacement.strTo, replacement.strFrom, molecule.lastIndexOf(replacement.strTo));
                    count++;
                    break;
                }
            }
        }
        System.out.println(count);
    }


    private static void readReplacements(List<String> lines) {
        lines.subList(0, lines.size() - 2).forEach(line -> {
            String[] fromAndTo = line.split(" => ");
            replacements.add(new Replacement(fromAndTo[0], fromAndTo[1]));
            Collections.shuffle(replacements);
        });
        molecule = lines.get(lines.size() - 1);
    }
}
