package algorithms;

import java.math.BigInteger;
import java.util.List;

public class LCM {

    public static BigInteger findLeastCommonMultiple(List<Long> numbers) {
        BigInteger result = BigInteger.ONE;
        for (Long number : numbers) {
            result = lcm(result, BigInteger.valueOf(number));
        }
        return result;
    }

    private static BigInteger gcd(BigInteger a, BigInteger b)
    {
        if (a.equals(BigInteger.ZERO)) return b;
        return gcd(b.mod(a), a);
    }

    private static BigInteger lcm(BigInteger a, BigInteger b)
    {
        return a.multiply(b.divide(gcd(a, b)));
    }
}
