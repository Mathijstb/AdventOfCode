package day22;

import com.google.common.collect.Lists;
import fileUtils.FileReader;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day22 {

    private enum Type {
        REVERSE,
        CUT,
        DEAL
    }

    @Value
    private static class Instruction {
        Type type;
        Integer value;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input22.csv");
        List<Instruction> instructions = readInstructions(lines);

        long position = 9;
        long number = findNumberThatEndsInPosition(instructions, position);
        System.out.printf("Number that ends in position %s: %s", position, number);
        //List<Long> newCards = executeInstructions(instructions);
        //System.out.println("Position of card 2019: " + newCards.indexOf(2019));
        //System.out.println("Card on position 2020: " + newCards.get(2020));
    }

    private static long stackSize = 119315717514047L;
    //private static long stackSize = 10;

    private static long findNumberThatEndsInPosition(List<Instruction> instructions, long index) {
        long previousIndex = index;
        List<Instruction> reverseInstructions = new ArrayList<>(instructions);
        Collections.reverse(reverseInstructions);
        for (int i = 0; i < stackSize; i++) {
            for (Instruction instruction: reverseInstructions) {
                previousIndex = getPreviousIndex(instruction, previousIndex);
            }
        }
        return previousIndex;
    }

    private static long getPreviousIndex(Instruction instruction, long index) {
        switch (instruction.type) {
            case REVERSE: return stackSize - 1 - index;
            case DEAL: {
                int value = instruction.value;
                return (ModUtils.modInverse(value, stackSize) * index) % stackSize;
            }
            case CUT: {
                int value = instruction.value;
                return Math.floorMod(index + value + stackSize, stackSize);
            }
            default: throw new IllegalStateException();
        }
    }

    private static List<Long> executeInstructions(List<Instruction> instructions) {
        List<Long> cards = new ArrayList<>();
        for (long i = 0; i < stackSize; i++) {
            cards.add(i);
        }
        for (Instruction instruction: instructions) {
            switch (instruction.type) {
                case REVERSE: cards = Lists.reverse(cards); break;
                case CUT: {
                    int value = instruction.value;
                    int absValue = Math.abs(value);
                    List<Long> newCards;
                    if (absValue == value) {
                        newCards = new ArrayList<>(cards.subList(value, cards.size()));
                        newCards.addAll(cards.subList(0, value));
                    }
                    else {
                        newCards = new ArrayList<>(cards.subList(cards.size() - absValue , cards.size()));
                        newCards.addAll(cards.subList(0, cards.size() - absValue));
                    }
                    cards = newCards;
                } break;
                case DEAL: {
                    int value = instruction.value;
                    List<Long> newCards = new ArrayList<>(cards);
                    int index = 0;
                    for (int i = 0; i < cards.size(); i++) {
                        long number = cards.get(i);
                        newCards.set(index, number);
                        index = (index + value) % cards.size();
                    }
                    cards = newCards;
                }
            }
        }
        return cards;
    }

    private static List<Instruction> readInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            if (line.equals("deal into new stack")) {
                return new Instruction(Type.REVERSE, null);
            }
            else if (line.contains("cut")) {
                int value = Integer.parseInt(line.split("cut ")[1]);
                return new Instruction(Type.CUT, value);
            }
            else if (line.contains("deal with increment")) {
                int value = Integer.parseInt(line.split("deal with increment ")[1]);
                return new Instruction(Type.DEAL, value);
            }
            else
                throw new IllegalStateException("can not read line");
        }).collect(Collectors.toList());
    }

}
