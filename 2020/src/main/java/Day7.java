import lombok.Data;
import lombok.Value;

import java.util.*;

public class Day7 {

    @Value
    private static class BagConstraint {
        String name;
        int amount;
    }

    @Data
    private static class Bag {
        boolean canContainGold = false;
        Set<BagConstraint> bagConstraints  = new HashSet<>();
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input7.csv");
        Map<String, Bag> bagMap = findBagMap(lines);
        countNumberOfBagsThatContainGold(bagMap);

        String bagName = "shiny gold";
        Bag goldBag = bagMap.get(bagName);
        int numberOfBagsInside = countNumberOfBags(bagMap, goldBag) - 1;
        System.out.println("number of bags in a " + bagName + " bag: " + numberOfBagsInside);
    }

    private static Map<String, Bag> findBagMap(List<String> lines) {
        Map<String, Bag> bags = new HashMap<>();
        for (String line : lines) {
            int bagsIndex = line.indexOf("bags");
            String bagName = line.substring(0, bagsIndex - 1);
            if (!bags.containsKey(bagName)) {
                bags.put(bagName, new Bag());
            }
            Bag bag = bags.get(bagName);

            Set<BagConstraint> bagConstraints = bag.getBagConstraints();
            int containIndex = line.indexOf("contain");
            if (containIndex > 0 && !line.contains("no other")) {
                bagConstraints.add(findBagConstraint(line, containIndex + 8));
            }
            int nextCommaIndex = line.indexOf(",");
            while(nextCommaIndex > 0) {
                bagConstraints.add(findBagConstraint(line, nextCommaIndex + 2));
                int commaSubIndex = line.substring(nextCommaIndex + 1).indexOf(",");
                if (commaSubIndex > 0) {
                    nextCommaIndex += commaSubIndex + 1;
                }
                else {
                    nextCommaIndex = -1;
                }
            }

        }
        return bags;
    }

    private static BagConstraint findBagConstraint(String line, int amountStartIndex) {
        int amountEndIndex = amountStartIndex + line.substring(amountStartIndex).indexOf(" ");
        int amount = Integer.parseInt(line.substring(amountStartIndex, amountEndIndex));
        int bagNameStartIndex = amountEndIndex + 1;
        int bagNameEndIndex = bagNameStartIndex + line.substring(bagNameStartIndex).indexOf("bag") - 1;
        String bagName = line.substring(bagNameStartIndex, bagNameEndIndex);
        return new BagConstraint(bagName, amount);
    }

    private static void countNumberOfBagsThatContainGold(Map<String, Bag> bagMap) {
        boolean mapUpdated = true;
        while(mapUpdated) {
            mapUpdated = false;
            for (Bag bag : bagMap.values()) {
                if (!bag.canContainGold && bag.bagConstraints.stream().anyMatch(c -> (c.name.equals("shiny gold") || bagMap.get(c.name).canContainGold))) {
                    bag.canContainGold = true;
                    mapUpdated = true;
                }
            }
        }
        System.out.println("number of bags that contain gold: " + bagMap.values().stream().filter(bag -> bag.canContainGold).count());
    }

    private static int countNumberOfBags(Map<String, Bag> bagMap, Bag bag) {
        return 1 + bag.bagConstraints.stream().map(c -> c.amount * countNumberOfBags(bagMap, bagMap.get(c.name))).reduce(0, Integer::sum);
    }
}
