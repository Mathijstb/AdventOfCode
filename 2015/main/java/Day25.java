import fileUtils.FileReader;

import java.util.List;

public class Day25 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input25.csv");
        String line = lines.stream().findFirst().orElseThrow();
        String[] parts = line.split("row ")[1].split(", column ");
        int row = Integer.parseInt(parts[0]);
        int column = Integer.parseInt(parts[1].split("\\.")[0]);
        long fullDiagonals = row + column - 1 - 1;
        long index = (fullDiagonals * (fullDiagonals + 1)) / 2 + column - 1;
        findCode(index);
    }

    public static void findCode(long index) {
        long code = 20151125;
        for (int i = 0; i < index; i++) {
            code = Math.floorMod(code *= 252533, 33554393);
        }
        System.out.println("Code: " + code);
    }

}
