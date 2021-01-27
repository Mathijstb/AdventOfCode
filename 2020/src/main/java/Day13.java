import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day13 {

    @Value
    private static class Bus {
        long number;
        long startTime;
    }

    private static long earliestTime;

    private static List<Long> startTimes;
    private static List<Long> requirements;

    public static void execute() {

        List<String> lines = FileReader.getFileReader().readFile("input13.csv");
        earliestTime = Integer.parseInt(lines.get(0));
        List<Bus> buses = Arrays.stream(lines.get(1).split("[,]"))
                .filter(s -> !s.equals("x"))
                .map(s -> new Bus(Long.parseLong(s), Long.parseLong(s))).collect(Collectors.toList());
        startTimes = buses.stream().map(Bus::getStartTime).sorted().collect(Collectors.toList());
        requirements = Arrays.stream(lines.get(1).split("[,]"))
                .map(s -> s.equals("x") ? null : Long.parseLong(s)).collect(Collectors.toList());
        findEarliestSchedule();
        //findEarliestBus(buses);
    }

    private static void findEarliestSchedule() {
        for (long time = 739; time < Long.MAX_VALUE; time+=787) {
            if (    (time % 17) == 0 &&
                    (time + 11) % 37 == 0 &&
                    (time + 17) % 439 == 0 &&
                    (time + 19) % 29 == 0 &&
                    (time + 30) % 13 == 0 &&
                    (time + 40) % 23 == 0 &&
                    (time + 48) % 787 == 0 &&
                    (time + 58) % 41 == 0 &&
                    (time + 67) % 19 == 0) {
                System.out.println("time: " + time); // 803_025_030_761_664
                break;

            }
        }
    }
    //y=x0*17, y=x1*37 - 11, y=x2*439 - 17, y=x3*29 - 19, y=x4*13 - 30
    //y=48218426 + 104101387*z0, y=z1*23 - 40, y=z2*787 - 48, y=z3*41 - 58, y=z4*19 - 67
    //y=803025030761664 + 1467900241541773 n

    private static void findEarliestBus(List<Bus> buses) {
        for (long i = earliestTime; i < earliestTime + 1000; i++) {
            long time = i;
            findBusThatStartsAtTime(buses, time).ifPresent(bus -> {
                long waitingTime = time - earliestTime;
                System.out.printf("Bus found! Bus number: %s%n", bus.number);
                System.out.printf(
                        "Time: %s, Waiting time: %s, multiplication: %s%n",
                        time, waitingTime, waitingTime * bus.number);

            });
        }
    }

    private static Optional<Bus> findBusThatStartsAtTime(List<Bus> buses, long time) {
        return buses.stream().filter(bus -> (time % bus.startTime) == 0).findFirst();
    }
}
