import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Day23 {

//    next cup 1: 720852, next cup 2: 397022
//    multiplication: 286194102744

    @Data
    private static class CupGame {
        List<Integer> circle;

        public CupGame(List<Integer> circle) {
            this.circle = circle;
        }

        public void playGame(int numberOfSteps) {
            int currentCupIndex = 0;
            for (int i = 0; i < numberOfSteps; i++) {
                if ((i % 1000) == 0) System.out.printf("-- move %s --%n", i + 1);
//                System.out.println("cups: " + circle.toString());
//                System.out.println("Current cup: " + currentCup);
                currentCupIndex = moveCups(currentCupIndex);
                //System.out.println();
            }
            System.out.println("-- final --");
            System.out.println("cups: " + circle.toString());
            int cup1index = circle.indexOf(1);
            int nextCup1 = circle.get((cup1index + 1) % circle.size());
            int nextCup2 = circle.get((cup1index + 2) % circle.size());
            System.out.printf("next cup 1: %s, next cup 2: %s%n", nextCup1, nextCup2);
            long multiplication = (long) nextCup1 * nextCup2;
            System.out.println("multiplication: " + multiplication);
        }

        int moveCups(int currentCupIndex) {
            //pickup the cups
            int size = circle.size();
            int startIndex = (currentCupIndex + 1 % size);
            int endIndex= (startIndex + 3) % size;
            List<Integer> movedCups;
            if (endIndex > startIndex) {
                List<Integer> sublist = circle.subList(startIndex, endIndex);
                movedCups = new ArrayList<>(sublist);
                sublist.clear();
            }
            else {
                List<Integer> sublist1 = circle.subList(startIndex, size);
                movedCups = new ArrayList<>(sublist1);
                sublist1.clear();
                List<Integer> sublist2 = circle.subList(0, endIndex);
                movedCups.addAll(sublist2);
                currentCupIndex -= sublist2.size();
                sublist2.clear();
            }
            //System.out.println("pick up: " + movedCups.toString());

            //determine destination
            int destination = circle.get(currentCupIndex);
            do {
                destination -= 1;
                if (destination == 0) destination = size;
            } while (movedCups.contains(destination));
            //System.out.println("destination: " + destination);

            //insert
            int destinationIndex = circle.indexOf(destination);
            if (destinationIndex < currentCupIndex) currentCupIndex += 3;
            circle.addAll(destinationIndex + 1, movedCups);

            return (currentCupIndex + 1) % size;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input23.csv");
        CupGame cupGame = getCups(lines);
        cupGame.playGame(10000000);
    }

    private static CupGame getCups(List<String> lines) {
        String line = lines.stream().findFirst().orElseThrow();
        List<Integer> circle = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            circle.add(Integer.parseInt(String.valueOf(line.charAt(i))));
        }
        for (int i = circle.size() + 1; i <= 1000000; i++) {
            circle.add(i);
        }
        return new CupGame(circle);
    }
}
