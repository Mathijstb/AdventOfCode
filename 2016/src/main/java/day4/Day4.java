package day4;

import fileUtils.FileReader;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input4.csv");

        //part a
        var rooms = readRooms(lists);
        var realRooms = determineRealRooms(rooms);

        //part b
        var decryptedRooms = decrypt(realRooms);
        var northpoleObjectStorage = decryptedRooms.stream().filter(room -> room.name().contains("northpole")).findFirst().orElseThrow();
        System.out.println("North pole object storage: " + northpoleObjectStorage);
    }

    private static List<Room> readRooms(List<String> lists) {
        var pattern = Pattern.compile("\\d+");
        return lists.stream().map(list -> {
            var matcher = pattern.matcher(list);
            if (!matcher.find()) {
               throw new IllegalArgumentException("Can not find id");
            }
            int id = Integer.parseInt(matcher.group());
            var name = list.substring(0, matcher.start() - 1);
            var checkSum = list.substring(matcher.end()).replaceAll("[\\[\\]]", "");
            return new Room(name, id, checkSum);
        }).toList();
    }

    private static List<Room> determineRealRooms(List<Room> rooms) {
        List<Room> realRooms = rooms.stream().filter(Day4::isRealRoom).toList();
        System.out.println("Real rooms: " + realRooms.stream().map(Room::id).toList());
        System.out.println("Sum of ID's: " + realRooms.stream().map(Room::id).reduce(0,  Integer::sum));
        return realRooms;
    }

    private static boolean isRealRoom(Room room) {
        var groups = room.name().replace("-", "").chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        var sortedChars = groups.entrySet().stream()
                .sorted(Map.Entry.<Character, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).toList();
        var checkSum = room.checksum();
        return IntStream.range(0, checkSum.length()).allMatch(i -> sortedChars.get(i).getKey().equals(checkSum.charAt(i)));
    }

    private static List<Room> decrypt(List<Room> rooms) {
        return rooms.stream().map(Day4::decrypt).toList();
    }

    private static Room decrypt(Room room) {
        var name = room.name();
        var id = room.id();
        var decryptedName = name.chars().map(c -> c == '-' ? ' ' : 'a' + ((c - 'a' + id) % 26))
                .mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining());
        return new Room(decryptedName, id, null);
    }
}