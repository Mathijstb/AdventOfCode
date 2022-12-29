package day5;

import fileUtils.FileReader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class Day5 {

    public static void execute() {
        var doorId = FileReader.getFileReader().readFile("input5.csv").stream().findFirst().orElseThrow();

        // part a
        var password = findPassword(doorId);
        System.out.println("Password: " + password);
        System.out.println();

        // part b
        var advancedPassword = findAdvancedPassword(doorId);
        System.out.println("Advanced password: " + advancedPassword);
    }

    private static String hashMD5(String value) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            var hashText =  new BigInteger(m.digest(value.getBytes())).toString(16);
            return String.format("%32s", hashText).replace(" ", "0");
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static String findPassword(String doorId) {
        List<Character> characters = new ArrayList<>();
        int index = 0;
        while (characters.size() < 8) {
            var hashed = hashMD5(doorId + index);
            if (hashed.startsWith("00000")) {
                characters.add(hashed.charAt(5));
                System.out.println("Character found: " + hashed.charAt(5));
            }
            index+=1;
        }
        return characters.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public static String findAdvancedPassword(String doorId) {
        Map<Integer, Character> characters = new HashMap<>();
        int index = 0;
        while (characters.size() < 8) {
            var hashed = hashMD5(doorId + index);
            if (hashed.startsWith("00000")) {
                var positionChar = hashed.charAt(5);
                if (Character.isDigit(positionChar)) {
                    int position = Character.getNumericValue(positionChar);
                    if (position >=0 && position <= 7 && !characters.containsKey(position)) {
                        var character = hashed.charAt(6);
                        System.out.printf("Character found: %s at position: %s%n", character, position);
                        characters.put(position, character);
                    }
                }
            }
            index+=1;
        }
        StringBuilder sb = new StringBuilder();
        characters.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> sb.append(entry.getValue()));
        return sb.toString();
    }

}