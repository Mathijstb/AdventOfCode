package algorithms;

import java.util.List;
import java.util.stream.IntStream;

public class ChineseRemainder {

    public static long execute(List<Integer> coefficients, List<Integer> remainders) {
        long product = coefficients.stream().reduce(1, (a, b) -> a * b);
        List<Long> partialProducts = coefficients.stream().map(n -> product / n).toList();
        List<Long> inverses = IntStream.range(0, coefficients.size()).mapToObj(i -> {
            var partialProduct = partialProducts.get(i);
            var coefficient = coefficients.get(i);
            return computeInverse(partialProduct, coefficient);
        }).toList();
        var sum = IntStream.range(0, coefficients.size())
                .mapToObj(i -> partialProducts.get(i) * inverses.get(i) * remainders.get(i)).reduce(0L, Long::sum);
        return Math.floorMod(sum, product);
    }

    private static long computeInverse(long a, long b){
        long m = b, t, q;
        long x = 0, y = 1;
        if (b == 1) {
            return 0;
        }

        // Apply extended Euclid Algorithm
        while (a > 1)         {
            // q is quotient
            q = a / b;
            t = b;

            // now proceed same as Euclid's algorithm
            b = a % b;
            a = t;
            t = x;
            x = y - q * x;
            y = t;
        }

        // Make result positive
        if (y < 0) {
            y += m;
        }
        return y;
    }
}
