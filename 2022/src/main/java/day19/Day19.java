package day19;

import com.google.common.math.IntMath;
import fileUtils.FileReader;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Day19 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19.csv");
        List<BluePrint> bluePrints = readBluePrints(lines);

        //part a
        var maxAmounts = determineMaxNumberOfGeodes(bluePrints, 24);
        var qualityLevel = maxAmounts.entrySet().stream().map(entry -> entry.getKey().id() * entry.getValue()).reduce(0,  Integer::sum);
        System.out.println("Quality level: " + qualityLevel);

        //part b
        bluePrints = bluePrints.subList(0, 3);
        var maxAmounts2 = determineMaxNumberOfGeodes(bluePrints, 32);
        var multiplication = maxAmounts2.values().stream().mapToLong(Integer::longValue).reduce(1L, (a, b) -> a * b);
        System.out.println("Multiplication: " + multiplication);
    }


    private static List<BluePrint> readBluePrints(List<String> lines) {
        return lines.stream().map(line -> {
            Map<RobotType, Map<ResourceType, Integer>> costs = new HashMap<>();
            Arrays.stream(RobotType.values()).forEach(robotType -> costs.put(robotType, new HashMap<>()));
            var bluePrintIdAndOre =line.split(": Each ore robot costs ");
            int id = Integer.parseInt(bluePrintIdAndOre[0].split("Blueprint ")[1]);
            var oreAndClay = bluePrintIdAndOre[1].split(" ore. Each clay robot costs ");
            var oreRobotOreCost = Integer.parseInt(oreAndClay[0]);
            var clayAndObsidian = oreAndClay[1].split(" ore. Each obsidian robot costs ");
            var clayRobotOreCost = Integer.parseInt(clayAndObsidian[0]);
            var obsidianAndGeode = clayAndObsidian[1].split(" clay. Each geode robot costs ");
            var obsidianRobotCosts = obsidianAndGeode[0].split(" ore and ");
            var obsidianRobotOreCost = Integer.parseInt(obsidianRobotCosts[0]);
            var obsidianRobotClayCost = Integer.parseInt(obsidianRobotCosts[1]);
            var geodeRobotCosts = obsidianAndGeode[1].split(" obsidian.")[0].split(" ore and ");
            var geodeRobotOreCost = Integer.parseInt(geodeRobotCosts[0]);
            var geodeRobotObsidianCost = Integer.parseInt(geodeRobotCosts[1]);

            costs.get(RobotType.ORE).put(ResourceType.ORE, oreRobotOreCost);
            costs.get(RobotType.CLAY).put(ResourceType.ORE, clayRobotOreCost);
            costs.get(RobotType.OBSIDIAN).put(ResourceType.ORE, obsidianRobotOreCost);
            costs.get(RobotType.OBSIDIAN).put(ResourceType.CLAY, obsidianRobotClayCost);
            costs.get(RobotType.GEODE).put(ResourceType.ORE, geodeRobotOreCost);
            costs.get(RobotType.GEODE).put(ResourceType.OBSIDIAN, geodeRobotObsidianCost);
            return new BluePrint(id, costs);
        }).collect(Collectors.toList());
    }


    private static final int ORE = 0;
    private static final int CLAY = 1;
    private static final int OBSIDIAN = 2;
    private static final int GEODE = 3;
    private static int oreRobotOreCost;
    private static int clayRobotOreCost;
    private static int obsidianRobotOreCost;
    private static int obsidianRobotClayCost;
    private static int geodeRobotOreCost;
    private static int geodeRobotObsidianCost;

    private static int maxOreNeeded;
    private static int maxClayNeeded;
    private static int maxObsidianNeeded;

    private static Map<BluePrint, Integer> determineMaxNumberOfGeodes(List<BluePrint> bluePrints, int numberOfMinutes) {
        Map<BluePrint, Integer> maxAmounts = new HashMap<>();
        for (BluePrint bluePrint : bluePrints) {
            oreRobotOreCost = bluePrint.costs().get(RobotType.ORE).get(ResourceType.ORE);
            clayRobotOreCost = bluePrint.costs().get(RobotType.CLAY).get(ResourceType.ORE);
            obsidianRobotOreCost = bluePrint.costs().get(RobotType.OBSIDIAN).get(ResourceType.ORE);
            obsidianRobotClayCost = bluePrint.costs().get(RobotType.OBSIDIAN).get(ResourceType.CLAY);
            geodeRobotOreCost = bluePrint.costs().get(RobotType.GEODE).get(ResourceType.ORE);
            geodeRobotObsidianCost = bluePrint.costs().get(RobotType.GEODE).get(ResourceType.OBSIDIAN);
            maxOreNeeded = bluePrint.costs().values().stream().filter(map -> map.containsKey(ResourceType.ORE)).map(map -> map.get(ResourceType.ORE)).reduce(0, Integer::max);
            maxClayNeeded = bluePrint.costs().values().stream().filter(map -> map.containsKey(ResourceType.CLAY)).map(map -> map.get(ResourceType.CLAY)).reduce(0, Integer::max);
            maxObsidianNeeded = bluePrint.costs().values().stream().filter(map -> map.containsKey(ResourceType.OBSIDIAN)).map(map -> map.get(ResourceType.OBSIDIAN)).reduce(0, Integer::max);
            var maxGeodeResources = determineMax(new ArrayList<>(List.of(1, 0, 0, 0)), new ArrayList<>(List.of(0, 0, 0, 0)), numberOfMinutes);
            maxAmounts.put(bluePrint, maxGeodeResources);
        }
        return maxAmounts;
    }

    private static int determineMax(List<Integer> robots, List<Integer> resources, int timeLeft) {
        List<Integer> maxAmounts = new ArrayList<>();
        if (canBuildGeodeRobot(robots, resources, timeLeft)) {
            maxAmounts.add(determineMaxWhenBuildingGeode(new ArrayList<>(robots), new ArrayList<>(resources), timeLeft));
        }
        if (canBuildObsidianRobot(robots, resources, timeLeft)) {
            maxAmounts.add(determineMaxWhenBuildingObsidian(new ArrayList<>(robots), new ArrayList<>(resources), timeLeft));
        }
        if (canBuildClayRobot(robots, resources, timeLeft)) {
            maxAmounts.add(determineMaxWhenBuildingClay(new ArrayList<>(robots), new ArrayList<>(resources), timeLeft));
        }
        if (canBuildOreRobot(robots, resources, timeLeft)) {
            maxAmounts.add(determineMaxWhenBuildingOre(new ArrayList<>(robots), new ArrayList<>(resources), timeLeft));
        }
        harvest(robots, resources, timeLeft);
        maxAmounts.add(resources.get(GEODE));

        return maxAmounts.stream().reduce(0, Integer::max);
    }

    private static int determineMaxWhenBuildingGeode(List<Integer> robots, List<Integer> resources, int timeLeft) {
        var harvestTimeGeodeRobot = getHarvestTimeGeodeRobot(robots, resources);
        harvest(robots, resources, harvestTimeGeodeRobot + 1);
        robots.set(GEODE, robots.get(GEODE) + 1);
        resources.set(ORE, resources.get(ORE) - geodeRobotOreCost);
        resources.set(OBSIDIAN, resources.get(OBSIDIAN) - geodeRobotObsidianCost);
        return determineMax(robots, resources, timeLeft - harvestTimeGeodeRobot - 1);
    }

    private static int determineMaxWhenBuildingObsidian(List<Integer> robots, List<Integer> resources, int timeLeft) {
        var harvestTimeObsidianRobot = getHarvestTimeObsidianRobot(robots, resources);
        harvest(robots, resources, harvestTimeObsidianRobot + 1);
        robots.set(OBSIDIAN, robots.get(OBSIDIAN) + 1);
        resources.set(ORE, resources.get(ORE) - obsidianRobotOreCost);
        resources.set(CLAY, resources.get(CLAY) - obsidianRobotClayCost);
        return determineMax(robots, resources, timeLeft - harvestTimeObsidianRobot - 1);
    }

    private static int determineMaxWhenBuildingClay(List<Integer> robots, List<Integer> resources, int timeLeft) {
        var harvestTimeClayRobot = getHarvestTimeClayRobot(robots, resources);
        harvest(robots, resources, harvestTimeClayRobot + 1);
        robots.set(CLAY, robots.get(CLAY) + 1);
        resources.set(ORE, resources.get(ORE) - clayRobotOreCost);
        return determineMax(robots, resources, timeLeft - harvestTimeClayRobot - 1);
    }

    private static int determineMaxWhenBuildingOre(List<Integer> robots, List<Integer> resources, int timeLeft) {
        var harvestTimeOreRobot = getHarvestTimeOreRobot(robots, resources);
        harvest(robots, resources, harvestTimeOreRobot + 1);
        robots.set(ORE, robots.get(ORE) + 1);
        resources.set(ORE, resources.get(ORE) - oreRobotOreCost);
        return determineMax(robots, resources, timeLeft - harvestTimeOreRobot - 1);
    }

    private static void harvest(List<Integer> robots, List<Integer> resources, int time) {
        resources.set(ORE, resources.get(ORE) + robots.get(ORE) * time);
        resources.set(CLAY, resources.get(CLAY) + robots.get(CLAY) * time);
        resources.set(OBSIDIAN, resources.get(OBSIDIAN) + robots.get(OBSIDIAN) * time);
        resources.set(GEODE, resources.get(GEODE) + robots.get(GEODE) * time);
    }

    private static int getHarvestTimeOreRobot(List<Integer> robots, List<Integer> resources) {
        if (resources.get(ORE) >= oreRobotOreCost) {
            return 0;
        }
        if (robots.get(ORE) > 0) {
            return IntMath.divide(oreRobotOreCost - resources.get(ORE), robots.get(ORE), RoundingMode.CEILING);
        }
        else {
            return 99;
        }
    }

    private static boolean canBuildOreRobot(List<Integer> robots, List<Integer> resources, int timeLeft) {
        return (resources.get(ORE) < maxOreNeeded) && getHarvestTimeOreRobot(robots, resources) + 1 < timeLeft;
    }


    private static int getHarvestTimeClayRobot(List<Integer> robots, List<Integer> resources) {
        if (resources.get(ORE) >= clayRobotOreCost) {
            return 0;
        }
        if (robots.get(ORE) > 0) {
            return IntMath.divide(clayRobotOreCost - resources.get(ORE), robots.get(ORE), RoundingMode.CEILING);
        }
        else {
            return 99;
        }
    }
    private static boolean canBuildClayRobot(List<Integer> robots, List<Integer> resources, int timeLeft) {
        return (resources.get(CLAY) < maxClayNeeded) && getHarvestTimeClayRobot(robots, resources) + 1 < timeLeft;
    }

    private static int getHarvestTimeObsidianRobot(List<Integer> robots, List<Integer> resources) {
        if (robots.get(ORE) > 0 && robots.get(CLAY) > 0) {
            int harvestTimeNeededOre = IntMath.divide(Math.max(0, obsidianRobotOreCost - resources.get(ORE)), robots.get(ORE), RoundingMode.CEILING);
            int harvestTimeNeededClay = IntMath.divide(Math.max(0, obsidianRobotClayCost - resources.get(CLAY)), robots.get(CLAY), RoundingMode.CEILING);
            return Math.max(harvestTimeNeededOre, harvestTimeNeededClay);
        }
        else {
            return 99;
        }
    }

    private static boolean canBuildObsidianRobot(List<Integer> robots, List<Integer> resources, int timeLeft) {
        return (resources.get(OBSIDIAN) < maxObsidianNeeded) && getHarvestTimeObsidianRobot(robots, resources) + 1 < timeLeft;
    }

    private static int getHarvestTimeGeodeRobot(List<Integer> robots, List<Integer> resources) {
        if (robots.get(ORE) > 0 && robots.get(OBSIDIAN) > 0) {
            int harvestTimeNeededOre = IntMath.divide(Math.max(0, geodeRobotOreCost - resources.get(ORE)), robots.get(ORE), RoundingMode.CEILING);
            int harvestTimeNeededObsidian = IntMath.divide(Math.max(0, geodeRobotObsidianCost - resources.get(OBSIDIAN)), robots.get(OBSIDIAN), RoundingMode.CEILING);
            return Math.max(harvestTimeNeededOre, harvestTimeNeededObsidian);
        }
        else {
            return 99;
        }
    }

    private static boolean canBuildGeodeRobot(List<Integer> robots, List<Integer> resources, int timeLeft) {
        return getHarvestTimeGeodeRobot(robots, resources) + 1 < timeLeft;
    }



}