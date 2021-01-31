package intCode;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class IntCodeComputer {

    public static void addInput(long number) {
        ExecuteProgramTask.addInput(number);
    }

    public static int getOutputSize() {
        return ExecuteProgramTask.outputQueue.size();
    }

    public static Optional<Long> getNextOutputValue() {
        return ExecuteProgramTask.getNextOutputValue();
    }

    public static Thread start(List<String> numbers) {
        Thread thread = new Thread(new IntCodeComputer.ExecuteProgramTask(numbers));
        thread.start();
        return thread;
    }

    @AllArgsConstructor
    private static class ExecuteProgramTask implements Runnable {

        List<String> numbers;
        private static LinkedBlockingDeque<Long> inputQueue = new LinkedBlockingDeque<>();
        private static LinkedBlockingDeque<Long> outputQueue = new LinkedBlockingDeque<>();

        private static void addInput(long number) {
            inputQueue.add(number);
        }

        private static Optional<Long> getNextOutputValue() {
            try {
                Long output = outputQueue.takeFirst();
                return output == 99L ? Optional.empty() : Optional.of(output);
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Interrupted");
            }
        }

        @Override
        public void run() {
            executeProgram(numbers, inputQueue, outputQueue);
            outputQueue.add(99L);
        }
    }

    private static long executeProgram(List<String> numbers, BlockingDeque<Long> inputQueue, BlockingDeque<Long> outputQueue) {
        int index = 0;
        while (true) {
            String number = numbers.get(index);

            int opCode = getOpCode(number);
            int[] paramModes = getParamModes(number);

            Optional<Integer> newIndex = executeOperation(numbers, inputQueue, outputQueue, opCode, paramModes, index);
            if (newIndex.isEmpty()) break;
            index = newIndex.get();
        }
        return lastOutput.get();
    }

    private static final ThreadLocal<Long> base = ThreadLocal.withInitial(() -> 0L);
    private static final ThreadLocal<Long> lastOutput = new ThreadLocal<>();

    private static int getOpCode(String number) {
        return number.length() == 1 ? Integer.parseInt(number) : Integer.parseInt(number.substring(number.length() - 2));
    }

    private static int[] getParamModes(String number) {
        int[] paramModes = new int[3];
        for (int i = 0; i < number.length() - 2; i++) {
            paramModes[i] = Character.getNumericValue(number.charAt(number.length() - 3 - i));
        }
        return paramModes;
    }


    private static Optional<Integer> executeOperation(List<String> numbers, BlockingDeque<Long> inputs, BlockingDeque<Long> outputs,
                                                     int opCode, int[] paramModes, int index) {
        if (opCode == 99) {
            return Optional.empty();
        }
        else if (opCode == 1 || opCode == 2) {
            long operand1 = getValue(numbers,index + 1, paramModes[0]);
            long operand2 = getValue(numbers,index + 2, paramModes[1]);
            long result = opCode == 1 ? operand1 + operand2 : operand1 * operand2;
            int destination = getIndex(numbers,index + 3, paramModes[2]);
            numbers.set(destination, String.valueOf(result));
            return Optional.of(index + 4);
        }
        else if (opCode == 3) {
            int destinationIndex = getIndex(numbers,index + 1, paramModes[0]);
            long input;
            try {
                input = inputs.takeFirst();
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("No input");
            }
            numbers.set(destinationIndex, String.valueOf(input));
            return Optional.of(index + 2);
        }
        else if (opCode == 4) {
            int sourceIndex = getIndex(numbers,index + 1, paramModes[0]);
            lastOutput.set(Long.parseLong(numbers.get(sourceIndex)));
            outputs.add(Long.parseLong(numbers.get(sourceIndex)));
            return Optional.of(index + 2);
        }
        else if (opCode == 5) {
            long value = getValue(numbers,index + 1, paramModes[0]);
            long value2 = getValue(numbers,index + 2, paramModes[1]);
            return value != 0 ? Optional.of((int) value2) : Optional.of(index + 3);
        }
        else if (opCode == 6) {
            long value = getValue(numbers,index + 1, paramModes[0]);
            long value2 = getValue(numbers,index + 2, paramModes[1]);
            return value == 0 ? Optional.of((int) value2) : Optional.of(index + 3);
        }
        else if (opCode == 7) {
            long value = getValue(numbers,index + 1, paramModes[0]);
            long value2 = getValue(numbers,index + 2, paramModes[1]);
            int destinationIndex = getIndex(numbers,index + 3, paramModes[2]);
            numbers.set(destinationIndex, value < value2 ? String.valueOf(1) : String.valueOf(0));
            return Optional.of(index + 4);
        }
        else if (opCode == 8) {
            long value = getValue(numbers,index + 1, paramModes[0]);
            long value2 = getValue(numbers,index + 2, paramModes[1]);
            int destinationIndex = getIndex(numbers,index + 3, paramModes[2]);
            numbers.set(destinationIndex, value == value2 ? String.valueOf(1) : String.valueOf(0));
            return Optional.of(index + 4);
        }
        else if (opCode == 9) {
            long diff = getValue(numbers,index + 1, paramModes[0]);
            base.set(base.get() + diff);
            return Optional.of(index + 2);
        }
        else
            throw new IllegalStateException("Invalid opCode: " + opCode);
    }

    private static long getValue(List<String> numbers, int index, int paramMode) {
        return Long.parseLong(numbers.get(getIndex(numbers, index, paramMode)));
    }

    private static int getIndex(List<String> numbers, int index, int paramMode) {
        int newIndex;
        switch (paramMode) {
            case 0: newIndex = Integer.parseInt(numbers.get(index)); break;
            case 1: newIndex = index; break;
            case 2: newIndex = base.get().intValue() + Integer.parseInt(numbers.get(index)); break;
            default: throw new IllegalArgumentException("Invalid paramMode");
        }
        //Add some memory
        if (newIndex > (numbers.size() - 100)) {
            for (int i = 0; i < 1000; i++) {
                numbers.add(String.valueOf(0));
            }
        }
        return newIndex;
    }
}
