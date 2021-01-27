import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day3 {

    private enum Space {
        OPEN('.'),
        TREE('#');

        private final char character;

        Space(char character) {
            this.character = character;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        List<List<Space>> parsedLines = lines.stream().map(line -> {
            ArrayList<Space> spaces = new ArrayList<>();
            for (int i = 0; i < line.length(); i++) {
                char character = line.charAt(i);
                spaces.add(Arrays.stream(Space.values()).filter(s -> s.character == character).findFirst().orElseThrow());
            }
            return spaces;
        }).collect(Collectors.toList());

        long multiplication = countTrees(parsedLines, 1, 1) *
                             countTrees(parsedLines, 3, 1) *
                             countTrees(parsedLines, 5, 1) *
                             countTrees(parsedLines, 7, 1) *
                             countTrees(parsedLines, 1, 2);
        System.out.println("multiplication: " + multiplication);
    }

    private static long countTrees(List<List<Space>> parsedLines, int dx, int dy) {
        int gridWidth = parsedLines.get(0).size();
        int gridHeight = parsedLines.size();

        int x = 0;
        int y = 0;
        int count = 0;
        while (y < gridHeight - dy) {
            x += dx;
            y += dy;
            if (parsedLines.get(y).get(x % gridWidth) == Space.TREE) {
                count += 1;
            }
        }
        System.out.printf("number of trees for slope(%s, %s): %s%n", dx, dy, count);
        return count;
    }

}
