import fileUtils.FileReader;
import intCode.IntCodeComputer;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Day7 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input7.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        findOptimalPhaseSettings(numbers);
    }

    private static void findOptimalPhaseSettings(List<String> numbers) {
        Collection<List<Long>> permutations = Collections2.permutations(Arrays.asList(5L,6L,7L,8L,9L));
        long maxThrusterSignal = 0;
        List<Long> maxPhasesSettingSequence = null;
        for (List<Long> phaseSettingSequence: permutations) {
            long thrusterSignal = getThrusterSignal(numbers, phaseSettingSequence);
            if (thrusterSignal > maxThrusterSignal) {
                maxThrusterSignal = thrusterSignal;
                maxPhasesSettingSequence = phaseSettingSequence;
                System.out.println("Found bigger thruster signal: " + thrusterSignal);
                System.out.println("Phase setting sequence: " + phaseSettingSequence);
                System.out.println();
            }
        }
        System.out.println("Max thruster signal: " + maxThrusterSignal);
        System.out.println("Phase setting sequence: " + maxPhasesSettingSequence);
    }

    private static long getThrusterSignal(List<String> numbers, List<Long> phaseSettingSequence) {
        CountDownLatch latch = new CountDownLatch(5);
        List<LinkedBlockingDeque<Long>> queues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LinkedBlockingDeque<Long> queue = new LinkedBlockingDeque<>();
            queue.add(phaseSettingSequence.get(i));
            queues.add(queue);
        }
        queues.add(queues.get(0));
        List<Thread> threads = new ArrayList<>();
        LinkedList<Long> lastOutputs = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            Long lastOutput = 0L;
            lastOutputs.add(lastOutput);
            threads.add(IntCodeComputer.start(numbers));
        }
        queues.get(0).add(0L);
        for (Thread thread: threads) {
            thread.start();
        }

        try {
            latch.await();
            return lastOutputs.get(4);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException("No input");
        }
    }
}
