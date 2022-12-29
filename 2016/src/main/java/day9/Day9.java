package day9;

import fileUtils.FileReader;
import java.util.List;

public class Day9 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input9.csv");

        //part a
        var decompressed = decompress(lists);
        decompressed.forEach(s -> System.out.printf("Decompressed: %s%n length: %s%n", s, s.length()));

        //part b
        System.out.println();
        var decompressedLength = findDecompressedLength(lists.get(0));
        System.out.printf("Fully decompressed length: %s", decompressedLength);
    }

    private static long findDecompressedLength(String value) {
        long result = 0;
        int index = 0;
        var markerStartIndex = value.indexOf('(');
        result += Math.max(0, markerStartIndex);
        while (markerStartIndex >= 0) {
            var markerStopIndex = value.indexOf(')', markerStartIndex);
            var marker = value.substring(markerStartIndex + 1, markerStopIndex);
            var params = marker.split("x");
            int numberOfCharacters = Integer.parseInt(params[0]);
            int repeatAmount = Integer.parseInt(params[1]);
            int dataStartIndex = markerStopIndex + 1;
            int dataStopIndex = dataStartIndex + numberOfCharacters;
            var subValue = value.substring(dataStartIndex, dataStopIndex);
            result += repeatAmount * findDecompressedLength(subValue);
            index = dataStopIndex;
            markerStartIndex = value.indexOf('(', index);
            if (markerStartIndex >= 0) {
                result += markerStartIndex - dataStopIndex;
            }
        }
        result += value.length() - index;
        return result;
    }

    private static List<String> decompress(List<String> lists) {
        return lists.stream().map(Day9::decompress).toList();
    }

    private static String decompress(String value) {
        if (!value.contains("(")) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        var markerStartIndex = value.indexOf('(', index);
        sb.append(value, 0, markerStartIndex);
        while (markerStartIndex >= 0) {
            var markerStopIndex = value.indexOf(')', markerStartIndex);
            var marker = value.substring(markerStartIndex + 1, markerStopIndex);
            var params = marker.split("x");
            int numberOfCharacters = Integer.parseInt(params[0]);
            int repeatAmount = Integer.parseInt(params[1]);
            int dataStartIndex = markerStopIndex + 1;
            int dataStopIndex = dataStartIndex + numberOfCharacters;
            var subValue = value.substring(dataStartIndex, dataStopIndex);
            sb.append(subValue.repeat(Math.max(0, repeatAmount)));
            index = dataStopIndex;
            markerStartIndex = value.indexOf('(', index);
            if (markerStartIndex >= 0) {
                sb.append(value, dataStopIndex, markerStartIndex);
            }
        }
        sb.append(value.substring(index));
        return sb.toString();
    }

}