package day21;

import com.google.common.collect.Lists;
import fileUtils.FileReader;
import java.util.List;
import java.util.Optional;

public class Day21 {

    private static final String PASSWORD = "abcdefgh";
    private static final String SCRAMBLED = "fbgdceah";

    public static void execute() {
        //cdhfgaeb not ok
        List<String> lists = FileReader.getFileReader().readFile("input21.csv");
        var instructions = readInstructions(lists);

        //part a
        var scrambled = executeInstructions(instructions);
        System.out.println("Scrambled password: " + scrambled);

        //part b
        System.out.println();
        var password = executeInstructionsReversed(instructions);
        System.out.println("Password: " + password);
    }

    private static List<Instruction> readInstructions(List<String> lists) {
        return lists.stream().map(list -> {
            if (list.startsWith("swap position ")) {
                var positions = list.split("swap position ")[1].split(" with position ");
                return new Instruction(InstructionType.SWAP_POSITION, Optional.of(positions[0]), Optional.of(positions[1]));
            } else if (list.startsWith("swap letter ")) {
                var letters = list.split("swap letter ")[1].split(" with letter ");
                return new Instruction(InstructionType.SWAP_LETTER, Optional.of(letters[0]), Optional.of(letters[1]));
            } else if (list.startsWith("rotate left ")) {
                var step = list.split("rotate left ")[1].split(" step")[0];
                return new Instruction(InstructionType.ROTATE_LEFT, Optional.of(step), Optional.empty());
            } else if (list.startsWith("rotate right ")) {
                var step = list.split("rotate right ")[1].split(" step")[0];
                return new Instruction(InstructionType.ROTATE_RIGHT, Optional.of(step), Optional.empty());
            } else if (list.startsWith("rotate based on position of letter ")) {
                var letter = list.split("rotate based on position of letter ")[1];
                return new Instruction(InstructionType.ROTATE_X, Optional.of(letter), Optional.empty());
            } else if (list.startsWith("reverse positions ")) {
                var positions = list.split("reverse positions ")[1].split(" through ");
                return new Instruction(InstructionType.REVERSE_RANGE, Optional.of(positions[0]), Optional.of(positions[1]));
            } else if (list.startsWith("move position ")) {
                var positions = list.split("move position ")[1].split(" to position ");
                return new Instruction(InstructionType.MOVE_POSITION, Optional.of(positions[0]), Optional.of(positions[1]));
            } else {
                throw new IllegalArgumentException("Can not parse line");
            }
        }).toList();
    }

    private static String executeInstructions(List<Instruction> instructions) {
        System.out.println("Password: " + PASSWORD);
        String scrambled = PASSWORD;
        for(Instruction instruction : instructions) {
            scrambled = executeInstruction(scrambled, instruction);
        }
        return scrambled;
    }

    private static String executeInstruction(String word, Instruction instruction) {
        return switch (instruction.type()) {
            case SWAP_POSITION -> swapPosition(word, Integer.parseInt(instruction.param1().orElseThrow()), Integer.parseInt(instruction.param2().orElseThrow()));
            case SWAP_LETTER -> swapLetter(word, instruction.param1().orElseThrow().charAt(0), instruction.param2().orElseThrow().charAt(0));
            case ROTATE_LEFT -> rotateLeft(word, Integer.parseInt(instruction.param1().orElseThrow()));
            case ROTATE_RIGHT -> rotateRight(word, Integer.parseInt(instruction.param1().orElseThrow()));
            case ROTATE_X -> rotateBasedOnLetter(word, instruction.param1().orElseThrow().charAt(0));
            case REVERSE_RANGE -> reverseRange(word, Integer.parseInt(instruction.param1().orElseThrow()), Integer.parseInt(instruction.param2().orElseThrow()));
            case MOVE_POSITION -> movePosition(word, Integer.parseInt(instruction.param1().orElseThrow()), Integer.parseInt(instruction.param2().orElseThrow()));
        };
    }

    private static String executeInstructionsReversed(List<Instruction> instructions) {
        System.out.println("Scrambled password: " + SCRAMBLED);
        var scrambled = SCRAMBLED;
        var instructionsReversed = Lists.reverse(instructions);
        for(Instruction instruction : instructionsReversed) {
            scrambled = executeInstructionReversed(scrambled, instruction);
        }
        return scrambled;
    }

    private static String executeInstructionReversed(String word, Instruction instruction) {
        return switch (instruction.type()) {
            case SWAP_POSITION -> swapPosition(word, Integer.parseInt(instruction.param1().orElseThrow()), Integer.parseInt(instruction.param2().orElseThrow()));
            case SWAP_LETTER -> swapLetter(word, instruction.param1().orElseThrow().charAt(0), instruction.param2().orElseThrow().charAt(0));
            case ROTATE_LEFT -> rotateRight(word, Integer.parseInt(instruction.param1().orElseThrow()));
            case ROTATE_RIGHT -> rotateLeft(word, Integer.parseInt(instruction.param1().orElseThrow()));
            case ROTATE_X -> rotateBasedOnLetterReversed(word, instruction.param1().orElseThrow().charAt(0));
            case REVERSE_RANGE -> reverseRange(word, Integer.parseInt(instruction.param1().orElseThrow()), Integer.parseInt(instruction.param2().orElseThrow()));
            case MOVE_POSITION -> movePosition(word, Integer.parseInt(instruction.param2().orElseThrow()), Integer.parseInt(instruction.param1().orElseThrow()));
        };
    }

    private static String swapPosition(String word, int index1, int index2) {
        var char1 = word.charAt(index1);
        var sb = new StringBuilder(word);
        sb.setCharAt(index1, word.charAt(index2));
        sb.setCharAt(index2, char1);
        return sb.toString();
    }

    private static String swapLetter(String word, char letter1, char letter2) {
        int index1 = word.indexOf(letter1);
        int index2 = word.indexOf(letter2);
        var sb = new StringBuilder(word);
        sb.setCharAt(index1, letter2);
        sb.setCharAt(index2, letter1);
        return sb.toString();
    }

    private static String rotateLeft(String word, int amount) {
        var moddedAmount = amount % word.length();
        return word.substring(moddedAmount) + word.substring(0, moddedAmount);
    }

    private static String rotateRight(String word, int amount) {
        var moddedAmount = amount % word.length();
        return word.substring(word.length() - moddedAmount) + word.substring(0, word.length() - moddedAmount);
    }

    private static String rotateBasedOnLetter(String word, char letter) {
        int index = word.indexOf(letter);
        int amount = (index >= 4 ? index + 2 : index + 1) % word.length();
        return rotateRight(word, amount);
    }

    private static String rotateBasedOnLetterReversed(String word, char letter) {
        int index = word.indexOf(letter);
        int amount = index == 0 ? 1 : (index % 2 == 0) ? (index / 2 + 5) % word.length() : ((index + 1) / 2);
        return rotateLeft(word, amount);
    }

    private static String reverseRange(String word, int minIndex, int maxIndex) {
        var reversedWord = new StringBuilder(word.substring(minIndex, maxIndex + 1)).reverse().toString();
        return word.substring(0, minIndex) + reversedWord + word.substring(maxIndex + 1);
    }

    private static String movePosition(String word, int removeIndex, int insertIndex) {
        var char1 = word.charAt(removeIndex);
        var sb = new StringBuilder(word);
        sb.deleteCharAt(removeIndex);
        sb.insert(insertIndex, char1);
        return sb.toString();
    }
}