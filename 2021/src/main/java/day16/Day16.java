package day16;

import fileUtils.FileReader;

import java.math.BigInteger;
import java.util.List;

public class Day16 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input16.csv");
        assert input.size() == 1;
        String line = input.get(0);
        BigInteger binary = new BigInteger(line, 16);
        System.out.println(binary.toString(2));
    }
}