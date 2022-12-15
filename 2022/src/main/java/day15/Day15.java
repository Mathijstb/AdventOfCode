package day15;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day15 {
    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input15.csv");
        var sensors = readSensors(lines);

        //part a
        int numberOfScannedAreas = findNumberOfScannedAreasAtRow(sensors, 2_000_000);
        System.out.println("Number of scanned areas: " + numberOfScannedAreas);

        //part b
        findBeaconFrequency(sensors);
    }

    private static List<Sensor> readSensors(List<String> lines) {
        return lines.stream().map(line -> {
            var sensorLock = line.split(": closest beacon is at x=");
            var sensorLocation = sensorLock[0].split("Sensor at x=")[1].split(", y=");
            var beaconLocation = sensorLock[1].split(", y=");
            return new Sensor(Integer.parseInt(sensorLocation[0]), Integer.parseInt(sensorLocation[1]),
                              new Beacon(Integer.parseInt(beaconLocation[0]), Integer.parseInt(beaconLocation[1])));
        }).toList();
    }

    private static int findNumberOfScannedAreasAtRow(List<Sensor> sensors, int row) {
        var beacons = sensors.stream().map(Sensor::beacon).toList();
        var minX = sensors.stream().map(sensor -> sensor.x() - sensor.getDistanceToBeacon())
                .min(Comparator.comparingInt(v -> v)).stream().findFirst().orElseThrow();
        var maxX = sensors.stream().map(sensor -> sensor.x() + sensor.getDistanceToBeacon())
                .max(Comparator.comparingInt(v -> v)).stream().findFirst().orElseThrow();
        var pointsInRange = IntStream.range(minX, maxX + 1)
                .mapToObj(x -> new Point(x, row))
                .filter(p -> sensors.stream().anyMatch(sensor -> sensor.isInRange(p)))
                .filter(p -> beacons.stream().noneMatch(b -> new Point(b.x(), b.y()).equals(p)))
                .toList();
        return pointsInRange.size();
    }

    private static void findBeaconFrequency(List<Sensor> sensors) {
        var minX = 0;
        var maxX = 4_000_000;
        var minY = 0;
        var maxY = 4_000_000;
        boolean found = false;
        Point beacon = null;
        for (Sensor sensor : sensors) {
            var points = sensor.getPointsJustOutsideBoundary(minX, maxX, minY, maxY);
            for (Point point : points) {
                if (sensors.stream().noneMatch(sensor1 -> sensor1.isInRange(point))) {
                    found = true;
                    beacon = point;
                    break;
                }
            }
            if (found) {
                break;
            }
        };
        if (!found) {
            throw new IllegalStateException("Beacon should have been found");
        }
        long frequency = 4_000_000L * beacon.x + beacon.y;
        System.out.println("Found! " + beacon);
        System.out.println("Frequency: " + frequency);

    }

}
