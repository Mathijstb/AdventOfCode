import cryptUtils.Crypto;
import fileUtils.FileReader;

import java.util.List;
import java.util.regex.Pattern;

public class Day4 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");
        String line = lines.stream().findFirst().orElseThrow();
        findMd5Hash(line);
    }

    public static void findMd5Hash(String line) {
        long number = 0;
        while(true) {
            String key = line + number;
            String hash = Crypto.hashMD5(key);
            if (Pattern.matches("[0]{6}.*", hash)) {
                System.out.println("Found key!");
                System.out.println("Key: " + key);
                System.out.println("Number: " + number);
                System.out.println("Hash: " + hash);
                break;
            }
            number += 1;
        }
    }
}
