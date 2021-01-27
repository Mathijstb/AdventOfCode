import com.google.common.collect.Collections2;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
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

    @Data
    @AllArgsConstructor
    private static class ExecuteProgramTask implements Runnable {

        Integer taskNumber;
        List<String> numbers;
        LinkedBlockingDeque<Long> inputQueue;
        LinkedBlockingDeque<Long> outputQueue;
        CountDownLatch latch;
        LinkedList<Long> lastOutputs;

        @Override
        public void run() {
            long lastOutput = Day5.executeProgram(numbers, inputQueue, outputQueue);
            //System.out.println("Task " + taskNumber + "  " + lastOutput);
            lastOutputs.set(taskNumber, lastOutput);
            latch.countDown();
        }
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
            threads.add(new Thread(new ExecuteProgramTask(i, new ArrayList<>(numbers), queues.get(i), queues.get(i + 1), latch, lastOutputs)));
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
