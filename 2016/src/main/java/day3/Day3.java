package day3;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day3 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input3.csv");
        List<Triangle> triangles = readTrianglesRowWise(input);
        printNumberOfValidTriangles(triangles);

        List<Triangle> triangles2 = readTrianglesColumnWise(input);
        printNumberOfValidTriangles(triangles2);
    }

    private static List<Triangle> readTrianglesRowWise(List<String> input) {
        return input.stream().map(line -> {
            String[] parts = line.strip().split("[\\s]{2,}");
            return new Triangle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }).collect(Collectors.toList());
    }

    private static List<Triangle> readTrianglesColumnWise(List<String> input) {
        List<Triangle> triangles = new ArrayList<>();
        IntStream.range(0, input.size() / 3).forEach(i -> {
            String[] parts1 = input.get(i * 3).strip().split("[\\s]{2,}");
            String[] parts2 = input.get(i * 3 + 1).strip().split("[\\s]{2,}");
            String[] parts3 = input.get(i * 3 + 2).strip().split("[\\s]{2,}");
            triangles.add(new Triangle(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]), Integer.parseInt(parts3[0])));
            triangles.add(new Triangle(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]), Integer.parseInt(parts3[1])));
            triangles.add(new Triangle(Integer.parseInt(parts1[2]), Integer.parseInt(parts2[2]), Integer.parseInt(parts3[2])));
        });
        return triangles;
    }


    private static void printNumberOfValidTriangles(List<Triangle> triangles) {
        int numberOfValidTriangles = (int) triangles.stream().filter(triangle -> {
            int side1 = triangle.getSide1();
            int side2 = triangle.getSide2();
            int side3 = triangle.getSide3();
            return ((side1 + side2 > side3) && (side1 + side3 > side2) && (side2 + side3 > side1));
        }).count();
        System.out.println("Number of valid triangles: " + numberOfValidTriangles);
    }
}