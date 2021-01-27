import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    @Value
    private static class Box {
        int length;
        int width;
        int height;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        List<Box> boxes = findBoxes(lines);
        findTotalWrappingPaperNeeded(boxes);
        findTotalRibbonNeeded(boxes);
    }

    private static List<Box> findBoxes(List<String>lines) {
        return lines.stream().map(line -> {
            String[] dimensions = line.split("x");
            return new Box(Integer.parseInt(dimensions[0]),
                           Integer.parseInt(dimensions[1]),
                           Integer.parseInt(dimensions[2]));
        }).collect(Collectors.toList());
    }

    private static void findTotalWrappingPaperNeeded(List<Box> boxes) {
        long totalWrappingPaper = boxes.stream().map(box -> {
            int surface1 = box.length * box.width;
            int surface2 = box.width * box.height;
            int surface3 = box.height * box.length;
            int smallestSurface = Math.min(Math.min(surface1, surface2), surface3);
            return 2 * surface1 + 2 * surface2 + 2 * surface3 + smallestSurface;
        }).reduce(0, Integer::sum);
        System.out.println("Total wrapping paper needed: " + totalWrappingPaper);
    }

    private static void findTotalRibbonNeeded(List<Box> boxes) {
        long totalRibbon = boxes.stream().map(box -> {
            List<Integer> dimensions = Arrays.asList(box.length, box.width, box.height);
            Collections.sort(dimensions);
            int smallestDimension = dimensions.get(0);
            int middleDimension = dimensions.get(1);
            int wrapNeeded = smallestDimension * 2 + middleDimension * 2;
            int volume = box.length * box.width * box.height;
            return wrapNeeded + volume;
        }).reduce(0, Integer::sum);
        System.out.println("Total ribbon needed: " + totalRibbon);
    }
}
