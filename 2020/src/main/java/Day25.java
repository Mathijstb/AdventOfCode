import java.util.List;
import java.util.stream.Collectors;

public class Day25 {

    private static long cardPublicKey;
    private static long doorPublicKey;
    private static long cardLoopSize;
    private static long doorLoopSize;
    private static long cardEncryptionKey;
    private static long doorEncryptionKey;
    private static final int subjectNumber = 7;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input25.csv");
        cardPublicKey = Long.parseLong(lines.get(0));
        doorPublicKey = Long.parseLong(lines.get(1));
        cardLoopSize = determineLoopSize(cardPublicKey);
        doorLoopSize = determineLoopSize(doorPublicKey);
        cardEncryptionKey = getEncryptionKey(doorPublicKey, cardLoopSize);
        doorEncryptionKey = getEncryptionKey(cardPublicKey, doorLoopSize);
        System.out.println("Card encryption key: " + cardEncryptionKey);
        System.out.println("Door encryption key: " + doorEncryptionKey);
    }

    private static long determineLoopSize(long publicKey) {
        long numberOfLoops = 1;
        long number = 1;
        while (true) {
            number = (number * subjectNumber) % 20201227;
            if (number == publicKey) {
                return numberOfLoops;
            }
            numberOfLoops += 1;
        }
    }

    private static long getEncryptionKey(long publicKey, long loopSize) {
        long number = 1;
        for (int i = 0; i < loopSize; i++) {
            number = (number * publicKey) % 20201227;
        }
        return number;
    }
}
