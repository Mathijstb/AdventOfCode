import fileUtils.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        List<Long> weights = readWeights(lines);
        determineTotalFuelNeeded(weights);
    }

    private static List<Long> readWeights(List<String> lines) {
        return lines.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private static void determineTotalFuelNeeded(List<Long> weights) {
        long fuelNeeded = weights.stream().map(Day1::determineFuelNeeded).reduce(0L, Long::sum);
        System.out.println("Total fuel needed = " + fuelNeeded);
    }

    private static long determineFuelNeeded(Long weight) {
        long fuelNeeded = Math.max(0, Math.floorDiv(weight, 3) - 2);
        System.out.println("Extra fuel needed = " + fuelNeeded);
        if (fuelNeeded <= 0) {
            return 0;
        }
        else {
            return fuelNeeded + determineFuelNeeded(fuelNeeded);
        }
    }

}
