import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.*;

public class Day21 {

    @Data
    @AllArgsConstructor
    private static class Player {
        String name;
        int hitPoints;
        int damage;
        int armor;

        public void printStats() {
            System.out.printf("%s stats: Hitpoints: %s, Damage: %s, Armor: %s%n", name, hitPoints, damage, armor);
        }
    }

    @Value
    private static class Stats {
        int hitPoints;
        int damage;
        int armor;
    }

    private enum ItemType {
        WEAPON, ARMOR, RING
    }

    @Value
    private static class Item {
        String name;
        ItemType type;
        int cost;
        int damage;
        int armor;
    }

    private static final List<Item> allItems = new ArrayList<>();
    private static List<Item> weapons;
    private static List<Item> armors;
    private static List<Item> rings;
    private static Stats bossStats;
    private static final int playerHitPoints = 100;

    public static void execute() {
        bossStats = readBossStats(FileReader.getFileReader().readFile("input21.csv"));
        readAllItems(FileReader.getFileReader().readFile("input21b.csv"));
        determineLeastCostWinningStrategy();
        System.out.println();
        determineMaxCostLosingStrategy();
    }

    private static void determineLeastCostWinningStrategy() {
        List<List<Item>> strategies = determineBuyingStrategies();
        int minCost = 999999;
        List<Item> minStrategy = new ArrayList<>();
        Player minPlayer = null;
        Player minBoss = null;
        for (List<Item> strategy: strategies) {
            int cost = strategy.stream().map(item -> item.cost).reduce(0, Integer::sum);
            int damage = strategy.stream().map(Item::getDamage).reduce(0, Integer::sum);
            int armor = strategy.stream().map(Item::getArmor).reduce(0, Integer::sum);
            Player player = new Player("Player", playerHitPoints, damage, armor);
            Player boss = new Player("Boss", bossStats.hitPoints, bossStats.damage, bossStats.armor);
            Player winningPlayer = determineWinner(player, boss);
            if (winningPlayer == player && cost < minCost) {
                System.out.println("Found better strategy! Cost: " + cost);
                minCost = cost;
                minStrategy = strategy;
                minPlayer = player;
                minBoss = boss;
            }
        }
        System.out.println("Minimal cost winning strategy: ");
        System.out.println(minStrategy.toString());
        System.out.println("Cost: " + minCost);
        assert minPlayer != null;
        minPlayer.printStats();
        minBoss.printStats();
    }

    private static void determineMaxCostLosingStrategy() {
        List<List<Item>> strategies = determineBuyingStrategies();
        int maxCost = -999999;
        List<Item> maxStrategy = new ArrayList<>();
        Player maxPlayer = null;
        Player maxBoss = null;
        for (List<Item> strategy: strategies) {
            int cost = strategy.stream().map(item -> item.cost).reduce(0, Integer::sum);
            int damage = strategy.stream().map(Item::getDamage).reduce(0, Integer::sum);
            int armor = strategy.stream().map(Item::getArmor).reduce(0, Integer::sum);
            Player player = new Player("Player", playerHitPoints, damage, armor);
            Player boss = new Player("Boss", bossStats.hitPoints, bossStats.damage, bossStats.armor);
            Player winningPlayer = determineWinner(player, boss);
            if (winningPlayer == boss && cost > maxCost) {
                System.out.println("Found worse strategy! Cost: " + cost);
                maxCost = cost;
                maxStrategy = strategy;
                maxPlayer = player;
                maxBoss = boss;
            }
        }
        System.out.println("Maximal cost losing strategy: ");
        System.out.println(maxStrategy.toString());
        System.out.println("Cost: " + maxCost);
        assert maxPlayer != null;
        maxPlayer.printStats();
        maxBoss.printStats();
    }

    private static List<List<Item>> determineBuyingStrategies() {
        List<List<Item>> strategies = new ArrayList<>();
        for (Item weapon: weapons) {
            for (Item armor : armors) {
                for (int i = 0; i < rings.size(); i++) {
                    Item ring1 = rings.get(i);
                    for (int j = i + 1; j < rings.size(); j++) {
                        Item ring2 = rings.get(j);
                        strategies.add(List.of(weapon, armor, ring1, ring2));
                    }
                }
            }
        }
        return strategies;
    }

    private static Player determineWinner(Player player, Player boss) {
        while (true) {
            boss.hitPoints -= Math.max(1, player.damage - boss.armor);
            if (boss.hitPoints <= 0) {
                return player;
            }
            player.hitPoints -= Math.max(1, boss.damage - player.armor);
            if (player.hitPoints <= 0) {
                return boss;
            }
        }
    }

    private static Stats readBossStats(List<String> lines) {
        int hitPoints = Integer.parseInt(lines.get(0).split("Hit Points: ")[1]);
        int damage = Integer.parseInt(lines.get(1).split("Damage: ")[1]);
        int armor = Integer.parseInt(lines.get(2).split("Armor: ")[1]);
        return new Stats(hitPoints, damage, armor);
    }

    private static void readAllItems(List<String> lines) {
        weapons = readItems(findItemRange(lines, "Weapons:"), ItemType.WEAPON);
        armors = readItems(findItemRange(lines, "Armor:"), ItemType.ARMOR);
        armors.add(new Item("No armor", ItemType.ARMOR, 0, 0, 0));
        armors.add(new Item("No armor 2", ItemType.ARMOR, 0, 0, 0));
        rings = readItems(findItemRange(lines, "Rings:"), ItemType.RING);
        rings.add(new Item("No ring", ItemType.RING, 0, 0, 0));
        allItems.addAll(weapons);
        allItems.addAll(armors);
        allItems.addAll(rings);
    }

    private static List<Item> readItems(List<String> lines, ItemType itemType) {
        List<Item> items = new ArrayList<>();
        for (String line: lines) {
          String[] parts = line.split("\\s{2,}");
          items.add(new Item(parts[0], itemType, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
        }
        return items;
    }

    private static List<String> findItemRange(List<String> lines, String substring) {
        int startIndex = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(substring)) {
                for (int j = i + 1; j < lines.size(); j++) {
                    if (j == lines.size() - 1 || lines.get(j+1).isEmpty()) {
                        return lines.subList(i + 1, j+1);
                    }
                }
            }
        }
        throw new NoSuchElementException("no line found");
    }
}
