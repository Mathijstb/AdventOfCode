import fileUtils.FileReader;

import java.util.List;

public class Day20 {

    private static long numberOfPresentsTreshold;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input20.csv");
        numberOfPresentsTreshold = Long.parseLong(lines.stream().findFirst().orElseThrow());
        determineLowestHouseNumber();
    }


    public static void determineLowestHouseNumber() {
        long houseNumber = 1;
        long numberOfPresents;
        while(true) {
            numberOfPresents = getNumberOfPresents2(houseNumber);
            if (numberOfPresents >= numberOfPresentsTreshold) {
                break;
            }
            if (houseNumber % 10_000 == 0)
                System.out.println("HouseNumber: " + houseNumber + " , number of presents: " + numberOfPresents);
            houseNumber++;
        }
        System.out.printf("House number %s got %s presents!%n", houseNumber, numberOfPresents);
    }

    // 637560 too low
    private static long getNumberOfPresents2(long houseNumber) {
        long numberOfPresents = 0;
        long maxD = (long) Math.sqrt(houseNumber);
        for (long i = 1; i <= maxD; i++) {
            if ( i <= 0) continue;
            if (houseNumber % i == 0) {
                if (i * 50 >= houseNumber) numberOfPresents += i * 11;
                long d = (houseNumber / i);
                if (d != i && d * 50 >= houseNumber) numberOfPresents += d * 11;
            }
        }
        return numberOfPresents;
    }

    private static long getNumberOfPresents(long houseNumber) {
        long numberOfPresents = 0;
        long maxD = (long) Math.sqrt(houseNumber);
        for (long i = 1; i <= maxD; i++) {
            if (houseNumber % i == 0) {
                numberOfPresents += i * 10;
                long d = (houseNumber / i);
                if (d != i) numberOfPresents += d * 10;
            }
        }
        return numberOfPresents;
    }
}
