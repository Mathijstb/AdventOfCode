package day11;

import fileUtils.FileReader;
import java.util.List;

public class Day11 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input11.csv");

        //part a
        // 8 packages on 1st floor
        // 2 packages on 2nd floor
        // To get everything to 2nd floor: 2 * 6 + 1 = 13
        // -> 10 packages on 2nd floor
        // To get everything to 4th floor: 13 + 2 * (8 * 2 + 1) = 47

        //part b
        // to get 4 extra packages to 4th floor: 47 + 3 * (2 * 4)  = 71
    }
}