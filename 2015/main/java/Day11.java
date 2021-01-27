import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day11 {

    //cqjxpqrr not right
    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input11.csv");
        String line = lines.stream().findFirst().orElseThrow();
        initializeNextCharacterMap();
        for (int i = 0; i < 2; i++) {
            line = determineNextPassword(line);
        }
    }

    private static String determineNextPassword(String line) {
        String password = line;
        do {
            password = setNextCharacter(password, password.length() - 1);
        } while (!passWordValid(password));
        System.out.println("Password: " + password);
        return password;
    }

    private static boolean passWordValid(String password) {
        return passwordContains3Sequence(password) &&
                !passwordContainsInvalidCharacters(password) &&
                passwordContainsDoubleCharacters(password);
    }

    private static boolean passwordContainsInvalidCharacters(String password) {
        return Pattern.matches(".*[iol]+.*", password);
    }

    private static boolean passwordContainsDoubleCharacters(String password) {
        return Pattern.matches(".*(([a-z])\\2).*(([a-z])\\4).*", password);
    }

    private static boolean passwordContains3Sequence(String password) {
        int maxSequenceLength = 1;
        int sequenceLength = 1;
        char character = password.charAt(0);
        for (int i = 1; i < password.length(); i++) {
            char previousCharacter = password.charAt(i - 1);
            char nextCharacter = password.charAt(i);
            if (nextCharacter != 'a' && nextCharacter == nextCharacterMap.get(previousCharacter)) {
                sequenceLength += 1;
            }
            else {
                maxSequenceLength = Math.max(sequenceLength, maxSequenceLength);
                sequenceLength = 1;
            }
        }
        return maxSequenceLength >= 3;
    }

    private static final Map<Character, Character> nextCharacterMap = new HashMap<>();

    private static void initializeNextCharacterMap() {
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < alphabet.length(); i++) {
            if (i == alphabet.length() - 1) {
                nextCharacterMap.put(alphabet.charAt(i), alphabet.charAt(0));
            } else {
                nextCharacterMap.put(alphabet.charAt(i), alphabet.charAt(i + 1));
            }

        }
    }

    private static String setNextCharacter(String password, int index) {
        char character = password.charAt(index);
        char nextCharacter = nextCharacterMap.get(character);
        StringBuilder sb = new StringBuilder(password);
        sb.setCharAt(index, nextCharacter);
        String result = sb.toString();
        if (nextCharacter == 'a') {
            result = setNextCharacter(result, index -1);
        }
        return result;
    }
}
