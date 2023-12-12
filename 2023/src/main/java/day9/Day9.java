package day9;

import fileUtils.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day9 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input9.csv");
        var sequences = lines.stream().map(line ->
            new Sequence(Arrays.stream(line.split(" ")).map(Long::parseLong).toList())).toList();
        var sequenceListMap = findSequenceLists(sequences);

        var extrapolations1 = sequenceListMap.values().stream().map(Day9::findForwardExtrapolatedNumber).toList();
        System.out.println("Extrapolations 1: " + extrapolations1);
        System.out.println("Sum 1: " + extrapolations1.stream().reduce(0L, Long::sum));
        System.out.println();

        var extrapolations2 = sequenceListMap.values().stream().map(Day9::findBackwardExtrapolatedNumber).toList();
        System.out.println("Extrapolations 2: " + extrapolations2);
        System.out.println("Sum 2: " + extrapolations2.stream().reduce(0L, Long::sum));
    }

    private static Map<Sequence, List<Sequence>> findSequenceLists(List<Sequence> startSequences) {
        return startSequences.stream().map(startSequence -> {
            var currentSequence = new Sequence(new ArrayList<>(startSequence.numbers()));
            var sequenceList = new ArrayList<Sequence>();
            sequenceList.add(currentSequence);

            while (true) {
                var numbers = currentSequence.numbers();
                var nextSequence = new Sequence(new ArrayList<>(IntStream.range(1, numbers.size())
                       .mapToObj(i -> numbers.get(i) - numbers.get(i-1))
                       .toList()));
                sequenceList.add(nextSequence);
                if (nextSequence.numbers().stream().allMatch(n -> n == 0)) break;
                currentSequence = nextSequence;
            }
            return sequenceList;
        }).collect(Collectors.toMap(list -> list.get(0), list -> list));
    }

    private static long findForwardExtrapolatedNumber(List<Sequence> sequences) {
        for (int i = 0; i < sequences.size(); i++) {
            int index = sequences.size() - i - 1;
            var sequence = sequences.get(index);
            if (index == sequences.size() - 1) {
                sequence.numbers().add(0L);
            }
            else {
                var previous = sequences.get(index + 1).getLast();
                sequence.numbers().add(sequence.getLast() + previous);
            }
        }
        return sequences.get(0).getLast();
    }

    private static long findBackwardExtrapolatedNumber(List<Sequence> sequences) {
        for (int i = 0; i < sequences.size(); i++) {
            int index = sequences.size() - i - 1;
            var sequence = sequences.get(index);
            if (index == sequences.size() - 1) {
                sequence.numbers().add(0, 0L);
            }
            else {
                var previous = sequences.get(index + 1).getFirst();
                sequence.numbers().add(0,sequence.getFirst() - previous);
            }
        }
        return sequences.get(0).getFirst();
    }
}
