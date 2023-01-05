package day14;

import cryptUtils.Crypto;
import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Day14 {

    public static void execute() {
        String salt = FileReader.getFileReader().readFile("input14.csv").stream().findFirst().orElseThrow();

        //part a
        var keyIndices = findKeys(salt, Crypto::hashMD5);
        System.out.println("64th key index: " + keyIndices.get(63));
        System.out.println();

        //part b
        var keyIndices2 = findKeys(salt, Day14::hashMulti);
        System.out.println("64th key index: " + keyIndices2.get(63));
    }

    private static List<Integer> findKeys(String salt, Function<String, String> hashFunction) {
        Pattern triple = Pattern.compile("(.)\\1\\1");
        List<Integer> keyIndices = new ArrayList<>();
        Map<Integer, String> indexToHashedValueMap = new HashMap<>();
        int index = 0;
        while (keyIndices.size() < 64) {
            var hashed = getHashedValue(salt, index, indexToHashedValueMap, hashFunction);
            var matcher = triple.matcher(hashed);
            if (matcher.find()) {
                var foundPattern = matcher.group();
                char repeatingChar = foundPattern.charAt(0);
                Pattern five = Pattern.compile(String.format("(%s)\\1\\1\\1\\1", repeatingChar));
                for (int i = 0; i < 1000; i++) {
                    var hashed2 = getHashedValue(salt, index + i + 1, indexToHashedValueMap, hashFunction);
                    var matcher2 = five.matcher(hashed2);
                    if (matcher2.find()) {
                        System.out.println("Found index: " + index);
                        keyIndices.add(index);
                        break;
                    }
                }
            }
            index += 1;
        }
        return keyIndices;
    }

    private static String getHashedValue(String salt, int index, Map<Integer, String> indexToHashedValueMap, Function<String, String> hashFunction) {
        if (!indexToHashedValueMap.containsKey(index)) {
           var hashedValue = hashFunction.apply(salt + index);
           indexToHashedValueMap.put(index, hashedValue);
        }
        return indexToHashedValueMap.get(index);
    }

    private static String hashMulti(String value) {
        String result = value;
        for (int i = 0; i < 2017; i++) {
           result = Crypto.hashMD5(result);
        }
        return result;
    }
}