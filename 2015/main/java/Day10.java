import java.util.List;

public class Day10 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        String line = lines.stream().findFirst().orElseThrow();
        executeGame(line);
    }

    private static void executeGame(String line) {
        for (int i = 0; i < 50; i++) {
            line = getNextString(line);
            System.out.printf("result after %s steps: %s...%n ...", i+1, line.substring(0, Math.min(100, line.length())));
            System.out.println("length: " + line.length());
            System.out.println();
        }

    }

    private static String getNextString(String line) {
        String result = "";
        char previousChar = line.charAt(0);
        char nextChar = line.charAt(1);
        int count = 1;
        for (int i = 1; i < line.length() ; i++) {
            nextChar = line.charAt(i);
            if (nextChar == previousChar) {
                count++;
            }
            else {
                result = addCharacterToString(result, previousChar, count);
                count = 1;
            }
            previousChar = nextChar;
        }
        if (count > 0) {
            result = addCharacterToString(result, nextChar, count);
        }
        return result;
    }

    private static String addCharacterToString(String result, char character, int count) {
        return result + count + character;
    }
}
