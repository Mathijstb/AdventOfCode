import fileUtils.FileReader;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {

    private enum SeatState {
        FLOOR('.'),
        EMPTY('L'),
        OCCUPIED('#');

        private final char character;

        SeatState(char character) {
            this.character = character;
        }
    }

    private static Point maxSeat;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input11.csv");
        Map<Point, SeatState> stateMap = getMap(lines);

        int maxX = Collections.max(stateMap.keySet().stream().map(p -> p.x).collect(Collectors.toList()));
        int maxY = Collections.max(stateMap.keySet().stream().map(p -> p.y).collect(Collectors.toList()));
        maxSeat = new Point(maxX, maxY);

        changeStates(stateMap);
    }

    private static Map<Point, SeatState> getMap(List<String> lines) {
        Map<Point, SeatState> stateMap = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char character = line.charAt(x);
                SeatState seatState = Arrays.stream(SeatState.values()).filter(s -> s.character == character).findFirst().orElseThrow();
                stateMap.put(new Point(x, y), seatState);
            }
        }
        return stateMap;
    }

    private static void changeStates(Map<Point, SeatState> stateMap) {
        while (true) {
            int numberOfOccupiedChanges = changeOccupiedState2(stateMap);
            int numberOfEmptyChanges = changeEmptyState2(stateMap);
            if (numberOfOccupiedChanges == 0 && numberOfEmptyChanges == 0)
                break;
        }

        System.out.println("number of total seats: " + (long) stateMap.values().size());
        System.out.println("number of occupied seats: " + stateMap.values().stream().filter(seatState -> seatState == SeatState.OCCUPIED).count());
    }

    private static int changeOccupiedState(Map<Point, SeatState> stateMap) {
        List<Point> seatsToOccupied = new ArrayList<>();
        for (Point seat: stateMap.keySet()) {
            if (stateMap.get(seat) == SeatState.EMPTY) {
                List<Point> adjacentSeats = getAdjacentSeats(seat);
                if (adjacentSeats.stream().noneMatch(s -> stateMap.get(s) == SeatState.OCCUPIED)) {
                    seatsToOccupied.add(seat);
                }
            }
        }
        for (Point seat: seatsToOccupied) {
            stateMap.put(seat, SeatState.OCCUPIED);
        }
        return seatsToOccupied.size();
    }

    private static int changeEmptyState(Map<Point, SeatState> stateMap) {
        List<Point> seatsToEmpty = new ArrayList<>();
        for (Point seat: stateMap.keySet()) {
            if (stateMap.get(seat) == SeatState.OCCUPIED) {
                List<Point> adjacentSeats = getAdjacentSeats(seat);
                if (adjacentSeats.stream().filter(s -> stateMap.get(s) == SeatState.OCCUPIED).count() >= 4) {
                    seatsToEmpty.add(seat);
                }
            }
        }
        for (Point seat: seatsToEmpty) {
            stateMap.put(seat, SeatState.EMPTY);
        }
        return seatsToEmpty.size();
    }

    private static int changeOccupiedState2(Map<Point, SeatState> stateMap) {
        List<Point> seatsToOccupied = new ArrayList<>();
        for (Point seat: stateMap.keySet()) {
            if (stateMap.get(seat) == SeatState.EMPTY) {
                List<Point> adjacentSeats = getVisibleSeats(stateMap, seat);
                if (adjacentSeats.stream().noneMatch(s -> stateMap.get(s) == SeatState.OCCUPIED)) {
                    seatsToOccupied.add(seat);
                }
            }
        }
        for (Point seat: seatsToOccupied) {
            stateMap.put(seat, SeatState.OCCUPIED);
        }
        return seatsToOccupied.size();
    }

    private static int changeEmptyState2(Map<Point, SeatState> stateMap) {
        List<Point> seatsToEmpty = new ArrayList<>();
        for (Point seat: stateMap.keySet()) {
            if (stateMap.get(seat) == SeatState.OCCUPIED) {
                List<Point> adjacentSeats = getVisibleSeats(stateMap, seat);
                if (adjacentSeats.stream().filter(s -> stateMap.get(s) == SeatState.OCCUPIED).count() >= 5) {
                    seatsToEmpty.add(seat);
                }
            }
        }
        for (Point seat: seatsToEmpty) {
            stateMap.put(seat, SeatState.EMPTY);
        }
        return seatsToEmpty.size();
    }

    private static List<Point> getAdjacentSeats(Point seat) {
        int x = seat.x;
        int y = seat.y;
        return Stream.of(new Point(x - 1, y - 1),
                         new Point(x - 1, y),
                         new Point(x - 1, y + 1),
                         new Point(x, y - 1),
                         new Point(x, y + 1),
                         new Point(x + 1, y - 1),
                         new Point(x + 1, y),
                         new Point(x + 1, y + 1))
                .filter(p -> p.x >= 0 && p.x <= maxSeat.x && p.y >=0 && p.y <= maxSeat.y)
                .collect(Collectors.toList());
    }

    private static List<Point> getVisibleSeats(Map<Point, SeatState> stateMap, Point seat) {
        int x = seat.x;
        int y = seat.y;
        List<Point> visibleSeats = new ArrayList<>();
        for (int diffX = -1; diffX <= 1; diffX++) {
            for (int diffY = -1; diffY <= 1; diffY++) {
                if (diffX == 0 && diffY == 0) continue;
                getFirstVisibleSeat(stateMap, seat, diffX, diffY).ifPresent(visibleSeats::add);
            }
        }
        return visibleSeats;
    }

    private static Optional<Point> getFirstVisibleSeat(Map<Point, SeatState> stateMap, Point seat, int diffX, int diffY) {
        int x = seat.x;
        int y = seat.y;

        for (int i = x + diffX, j = y + diffY; i >= 0 && i <= maxSeat.x; i += diffX, j += diffY) {
            Point visibleSeat = new Point(i, j);
            if (stateMap.get(visibleSeat) != SeatState.FLOOR) {
                return Optional.of(visibleSeat);
            }
        }
        return Optional.empty();
    }
}
