import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");
        String[] parts = lines.stream().findFirst().orElseThrow().split("-");
        IntStream range = IntStream.rangeClosed(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        findNumberThatMatch(range);
    }

    private static void findNumberThatMatch(IntStream range) {
        long amount = range.filter(integer -> {
            String s = String.valueOf(integer);
            //boolean containsDoubleChar = Pattern.matches(".*(.)\\1.*", s);
            boolean containsIsolatedDouble = Pattern.matches("(^(\\d)\\2(?!\\2)\\d*)|(.*(\\d)(?!\\4)(\\d)\\5(?!\\5)\\d*)", s);
            boolean increasing = Pattern.matches("(?=\\d{6}$)1*2*3*4*5*6*7*8*9*", s);
            return containsIsolatedDouble && increasing;
        }).count();
        System.out.println("Matching amount: " + amount);
    }

}
