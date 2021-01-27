import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day8 {

    private static final int width = 25;
    private static final int height = 6;

    @Data
    private static class Layer {
        int[][] grid = new int[height][width];
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input8.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<Integer> digits = readDigits(line);
        List<Layer> layers = getLayers(digits);
        //findLayerWithFewestZeroDigits(layers);
        findImage(layers);
    }

    private static void findImage(List<Layer> layers) {
        Layer image = new Layer();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                image.grid[i][j] = findPixel(layers, i, j);
            }
        }
        for (int i = 0; i < height; i++) {
            int[] row = image.grid[i];
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < width; j++) {
                sb.append(row[j] == 1 ? "#" : " ");
            }
            System.out.println(sb.toString());
        }
    }

    private static int findPixel(List<Layer> layers, int y, int x) {
        for (Layer layer: layers) {
            int pixel = layer.grid[y][x];
            if (pixel == 1 || pixel == 0) {
                return pixel;
            }
        }
        return 2;
    }

    private static void findLayerWithFewestZeroDigits(List<Layer> layers) {
        Layer minLayer = layers.stream().min(Comparator.comparing(layer -> getNumberOfDigits(layer, 0)))
                .orElseThrow(() -> new IllegalStateException("Minimum expected"));
        long numberOfOneDigits = getNumberOfDigits(minLayer, 1);
        long numberOfTwoDigits = getNumberOfDigits(minLayer, 2);
        System.out.printf("Number of 1 digits: %s, number of 2 digits: %s, product: %s%n",
                          numberOfOneDigits, numberOfTwoDigits, numberOfOneDigits * numberOfTwoDigits);
    }

    private static long getNumberOfDigits(Layer layer, int number) {
        return Arrays.stream(layer.grid).map(row -> Arrays.stream(row).filter(digit -> digit == number).count()).reduce(0L, Long::sum);
    }

    private static List<Layer> getLayers(List<Integer> digits) {
        List<Layer> layers = new ArrayList<>();
        int layerSize = width * height;
        int numberOfLayers = digits.size() / layerSize;
        for (int i = 0; i < numberOfLayers; i++) {
            Layer layer = new Layer();
            List<Integer> layerDigits = digits.subList(i * layerSize, (i+1) * layerSize);
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    layer.grid[j][k] = layerDigits.get(j * width + k);
                }
            }
            layers.add(layer);
        }
        return layers;
    }

    private static List<Integer> readDigits(String line) {
        return Arrays.stream(line.split("")).map(Integer::parseInt).collect(Collectors.toList());
    }

}
