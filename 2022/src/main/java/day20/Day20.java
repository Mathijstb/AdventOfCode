package day20;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.IntStream;

public class Day20 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input20.csv");
        List<Item> items = readItems(lines);

        //part a
        var result = moveNumbers(items, items);
        findCoordinates(result);

        //part b
        var encryptedItems = items.stream().map(item -> new Item(item.index(), item.value() * 811589153L)).toList();
        var sortedItems = new LinkedList<>(encryptedItems);
        for (int i = 0; i < 10; i++) {
            sortedItems = moveNumbers(sortedItems, encryptedItems);
        }
        findCoordinates(sortedItems);
    }

    private static List<Item> readItems(List<String> lines) {
        return IntStream.range(0, lines.size()).mapToObj(index -> new Item(index, Long.parseLong(lines.get(index)))).toList();
    }

    private static LinkedList<Item> moveNumbers(List<Item> items, List<Item> itemsOrder) {
        var result = new LinkedList<>(items);
        itemsOrder.forEach(item -> {
            int index = result.indexOf(item);
            result.remove(index);
            var newIndex = Math.floorMod(index + item.value(), result.size());
            result.add(newIndex, item);
        });
        return result;
    }

    private static void findCoordinates(List<Item> items) {
        var item0 = items.stream().filter(item -> item.value() == 0).findFirst().orElseThrow();
        var index0 = items.indexOf(item0);
        int index1000 = Math.floorMod(index0 + 1000, items.size());
        int index2000 = Math.floorMod(index0 + 2000, items.size());
        int index3000 = Math.floorMod(index0 + 3000, items.size());
        long result = items.get(index1000).value() + items.get(index2000).value() + items.get(index3000).value();
        System.out.println("Result: " + result);
    }
}