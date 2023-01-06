package day16;

import fileUtils.FileReader;

public class Day16 {

    private static final int DISK_SIZE = 272;

    public static void execute() {
        String input = FileReader.getFileReader().readFile("input16.csv").stream().findFirst().orElseThrow();

        //part a
        var data = findData(input, 272);
        var checkSum = findCheckSum(data);
        System.out.println("CheckSum 1: " + checkSum);

        //part b
        var data2 = findData(input, 35651584);
        var checkSum2 = findCheckSum(data2);
        System.out.println("CheckSum 2: " + checkSum2);
    }

    private static String findData(String input, int diskSize) {
        var dataSize = input.length();
        var sb = new StringBuilder(input);
        while (dataSize < diskSize) {
            var reverse = new StringBuilder(sb).reverse();
            var flippedReverse = reverse.toString()
                    .replaceAll("0", "x")
                    .replaceAll("1", "0")
                    .replaceAll("x", "1");
            sb.append("0").append(flippedReverse);
            dataSize = dataSize * 2 + 1;
        }
        return sb.substring(0, diskSize);
    }

    private static String findCheckSum(String input) {
        int checkSumSize = input.length();
        var checkSum = input;
        while (checkSumSize % 2 == 0) {
            var newCheckSum = new StringBuilder();
            for (int i = 0; i < checkSumSize / 2; i++) {
                newCheckSum.append(checkSum.charAt(i * 2) == checkSum.charAt((i * 2) + 1) ? "1" : "0");
            }
            checkSumSize = checkSumSize / 2;
            checkSum = newCheckSum.toString();
        }
        return checkSum;
    }
}