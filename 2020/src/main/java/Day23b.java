import fileUtils.FileReader;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class Day23b {

    @Data
    private static class CupGame {
        Integer[] circle;
        int currentCup;

        public CupGame(Integer[] circle) {
            this.circle = circle;
            currentCup = circle[0];
        }

        public void playGame(int numberOfSteps) {
            for (int i = 0; i < numberOfSteps; i++) {
                if ((i % 1000) == 0) System.out.printf("-- move %s --%n", i + 1);
//                System.out.println("cups: " + circle.toString());
//                System.out.println("Current cup: " + currentCup);
                Integer[] movedCups = moveCups();
                //System.out.println();
            }
            System.out.println("-- final --");
            System.out.println("cups: " + Arrays.toString(circle));
            int cup1index = Arrays.binarySearch(circle, 1);
            int nextCup1 = circle[(cup1index + 1) % circle.length];
            int nextCup2 = circle[(cup1index + 2) % circle.length];
            System.out.printf("next cup 1: %s, next cup 2: %s%n", nextCup1, nextCup2);
            long multiplication = (long) nextCup1 * nextCup2;
            System.out.println("multiplication: " + multiplication);
        }

        Integer[] moveCups() {
            //pickup the cups
            int size = circle.length;
            int currentCupIndex = ArrayUtils.indexOf(circle, currentCup);
            int startIndex = (currentCupIndex + 1 % size);
            int endIndex= (startIndex + 3) % size;
            Integer[] movedCups;
            if (endIndex > startIndex) {
                movedCups = ArrayUtils.subarray(circle, startIndex, endIndex);
                for (int i = startIndex; i < endIndex; i++) {
                    ArrayUtils.remove(circle, i);
                }
            }
            else {
                Integer[] sublist1 = ArrayUtils.subarray(circle, startIndex, size);
                for (int i = startIndex; i < size; i++) {
                    ArrayUtils.remove(circle, i);
                }
                Integer[] sublist2 = ArrayUtils.subarray(circle, 0, endIndex);
                for (int i = 0; i < endIndex; i++) {
                    ArrayUtils.remove(circle, i);
                }
                movedCups = ArrayUtils.addAll(sublist1, sublist2);
            }
            //System.out.println("pick up: " + movedCups.toString());

            //determine destination
            int destination = currentCup;
            do {
                destination -= 1;
                if (destination == 0) destination = size;
            } while (ArrayUtils.contains(movedCups, destination));
            //System.out.println("destination: " + destination);

            //insert
            int destinationIndex = ArrayUtils.indexOf(circle, destination);
            circle = ArrayUtils.insert(destinationIndex + 1, circle, movedCups);

            //select next current cup
            currentCupIndex = ArrayUtils.indexOf(circle, currentCup);
            currentCup = circle[(currentCupIndex + 1) % size];

            return movedCups;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input23.csv");
        CupGame cupGame = getCups(lines);
        cupGame.playGame(10000000);
    }

    private static CupGame getCups(List<String> lines) {
        String line = lines.stream().findFirst().orElseThrow();
        Integer[] circle = new Integer[1000000];
        for (int i = 0; i < line.length(); i++) {
            circle[i] = Integer.parseInt(String.valueOf(line.charAt(i)));
        }
        for (int i = line.length(); i < 1000000; i++) {
            circle[i] = i;
        }
        return new CupGame(circle);
    }

    private static void executeGame(CupGame cups) {

    }
}
