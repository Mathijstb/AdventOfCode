import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.awt.*;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day12 {

    @Data
    @AllArgsConstructor
    private static class Moon {
        int x;
        int y;
        int z;

        int dx;
        int dy;
        int dz;

        private Moon copy() {
            return new Moon(x, y, z, dx, dy, dz);
        }


        @Override
        public String toString() {
            return String.format("pos=<x= %s, y= %s, z= %s>, vel=<x= %s, y= %s, z= %s>", x, y, z, dx, dy, dz);
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input12.csv");
        List<Moon> moons = getMoons(lines);
        long periodX = getPeriod(moons, Moon -> new Point(Moon.getX(), Moon.getDx()));
        long periodY = getPeriod(moons, Moon -> new Point(Moon.getY(), Moon.getDy()));
        long periodZ = getPeriod(moons, Moon -> new Point(Moon.getZ(), Moon.getDz()));
        findLeastCommonMultiple(List.of(periodX, periodY, periodZ));
//        int numberOfSteps = 100;
//        moveMoons(moons, numberOfSteps);
//        int totalEnergy = calculateEnergy(moons);
//        System.out.printf("Total energy after %s steps: %s", numberOfSteps, totalEnergy);
    }

    private static BigInteger findLeastCommonMultiple(List<Long> numbers) {
        BigInteger result = BigInteger.ONE;
        for (Long number : numbers) {
            result = lcm(result, BigInteger.valueOf(number));
        }
        System.out.println("LCM: " + result);
        return result;
    }

    private static BigInteger gcd(BigInteger a, BigInteger b)
    {
        if (a.equals(BigInteger.ZERO)) return b;
        return gcd(b.mod(a), a);
    }

    private static BigInteger lcm(BigInteger a, BigInteger b)
    {
        return a.multiply(b.divide(gcd(a, b)));
    }

    @Value
    private static class Coords {
        Point moon1;
        Point moon2;
        Point moon3;
        Point moon4;
    }

    private static Coords getCoords(List<Moon> moons, Function<Moon, Point> getPoint) {
        return new Coords(getPoint.apply(moons.get(0)),
                          getPoint.apply(moons.get(1)),
                          getPoint.apply(moons.get(2)),
                          getPoint.apply(moons.get(3)));
    }

    private static long getPeriod(List<Moon> originalMoons, Function<Moon, Point> getPoint) {
        List<Moon> moons = originalMoons.stream().map(Moon::copy).collect(Collectors.toList());
        Coords startCoords = getCoords(moons, getPoint);
        long period = 0;
        do {
            updateVelocities(moons);
            applyVelocities(moons);
            period += 1;
            //if (period == 12165972) break;
        } while (!getCoords(moons, getPoint).equals(startCoords));
        return period;
    }

    public static int calculateEnergy(List<Moon> moons) {
        return moons.stream().map(moon -> {
            int potentialEnergy = Math.abs(moon.x) + Math.abs(moon.y) + Math.abs(moon.z);
            int kineticEnergy = Math.abs(moon.dx) + Math.abs(moon.dy) + Math.abs(moon.dz);
            return  potentialEnergy * kineticEnergy;
        }).reduce(0, Integer::sum);
    }

    private static void moveMoons(List<Moon> moons, int numberOfSteps) {
        System.out.printf("After %s steps:%n", 0);
        moons.forEach(System.out::println);
        for (int i = 0; i < numberOfSteps; i++) {
            updateVelocities(moons);
            applyVelocities(moons);
            System.out.println();
            System.out.printf("After %s steps:%n", i+1);
            moons.forEach(System.out::println);
        }
    }

    private static void updateVelocities(List<Moon> moons) {
        moons.forEach(moon -> {
            moons.stream().filter(otherMoon -> otherMoon != moon).forEach(otherMoon -> {
                moon.dx += Integer.compare(otherMoon.x, moon.x);
                moon.dy += Integer.compare(otherMoon.y, moon.y);
                moon.dz += Integer.compare(otherMoon.z, moon.z);
            });
        });
    }

    private static void applyVelocities(List<Moon> moons) {
        moons.forEach(moon -> {
            moon.x += moon.dx;
            moon.y += moon.dy;
            moon.z += moon.dz;
        });
    }

    private static List<Moon> getMoons(List<String> lines) {
        return lines.stream().map(line ->{
            String[] parts = line.split("x=")[1].split(", y=");
            int x = Integer.parseInt(parts[0]);
            String[] parts2 = parts[1].split(", z=");
            int y = Integer.parseInt(parts2[0]);
            int z = Integer.parseInt(parts2[1].split(">")[0]);
            return new Moon(x, y, z, 0, 0, 0);
        }).collect(Collectors.toList());
    }

}
