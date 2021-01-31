import fileUtils.FileReader;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day5 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input5.csv");
        Set<Integer> seatNumbers = lines.stream().map(Day5::getSeatNumber).collect(Collectors.toSet());

        //System.out.println("Max seat number: " + Collections.max(seatNumbers));
        OptionalInt optFreeSeat = IntStream.range(0, 901).filter(i -> i >= 8 && i <= 127 * 8 && !seatNumbers.contains(i)).findFirst();
        int freeSeat = optFreeSeat.orElseThrow();
        System.out.println("free seat: " + freeSeat);
    }

    public static int getSeatNumber(String line) {
        int frontRowIndex = 0;
        int backRowIndex = 127;
        for (int i = 0; i < 7; i++) {
            int numberOfRowsLeft = (1 + backRowIndex - frontRowIndex);
            char character = line.charAt(i);
            if (character == 'F') {
                backRowIndex -= numberOfRowsLeft / 2;
            }
            else if (character == 'B') {
                frontRowIndex += numberOfRowsLeft / 2;
            }
            else {
                throw new IllegalArgumentException("invalid character");
            }
        }

        int leftSeatIndex = 0;
        int rightSeatIndex = 7;
        for (int i = 7; i < 10; i++) {
            int numberOfSeatsLeft = (1 + rightSeatIndex - leftSeatIndex);
            char character = line.charAt(i);
            if (character == 'L') {
                rightSeatIndex -= numberOfSeatsLeft / 2;
            }
            else if (character == 'R') {
                leftSeatIndex += numberOfSeatsLeft / 2;
            }
            else {
                throw new IllegalArgumentException("invalid character");
            }
        }

        return frontRowIndex * 8 + leftSeatIndex;
    }
}
