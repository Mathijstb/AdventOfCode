package day22;

import fileUtils.FileReader;
import lombok.Value;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.math.BigInteger.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

//112905223072132 too high
public class Test {

    private static final String ERR_OP = "Unsupported shuffle operation '%s'";

    private static final String DEAL_INTO = "deal into new stack";
    private static final String CUT = "cut";
    private static final String DEAL_W_INC = "deal with increment";

    private static String[] input;
    private static final BigInteger nCards = BigInteger.valueOf(119315717514047L);
    private static final BigInteger nShuffles = BigInteger.valueOf(101741582076661L);

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input22.csv");
        input = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            input[i] = lines.get(i);
        }
        System.out.println(part2());
    }


    /** Represents any linear function that can be written f(x)=kx+m */
    @Value
    public static class LinFunc {
        BigInteger k, m;
        BigInteger apply(BigInteger x) { return x.multiply(k).add(m); }
    }

    /* The identity function: f(x)=x */
    private static final LinFunc ID = new LinFunc(ONE, ZERO);

    /** Aggregate two functions f(x) and g(x) to create a new function h(x)=g(f(x)) */
    private static LinFunc agg(LinFunc f, LinFunc g) {
        // Let f(x)=k*x+m and g(x)=j*x+n, then h(x) = g(f(x)) = Ax+B = j*(k*x+m)+n = j*k*x + (j*m+n) => A=j*k, B=j*m+n
        return new LinFunc(g.k.multiply(f.k), g.k.multiply(f.m).add(g.m));
    }

    public static String part1() { return positionOf(valueOf(2019)).toString(); }
    public static String part2() { return cardAt(valueOf(2020)).toString(); }

    private static BigInteger positionOf(BigInteger in) {
        LinFunc shuffle = stream(input)
                .filter(s -> !"".equalsIgnoreCase(s))
                .map(s -> {
                    if (s.startsWith(DEAL_INTO))        return new LinFunc(ONE.negate(), ONE.negate());
                    else if (s.startsWith(CUT))         return new LinFunc(ONE, argOf(s).mod(nCards).negate());
                    else if (s.startsWith(DEAL_W_INC))  return new LinFunc(argOf(s), ZERO);
                    throw new RuntimeException(String.format(ERR_OP, s));
                })
                .reduce(ID, Test::agg); // Create one func of all shuffle operations, i.e. like: f(x)=f1((f2(f3(x)))
        return executeTimes(shuffle.k, shuffle.m, nShuffles).apply(in).mod(nCards);
    }

    private static BigInteger cardAt(BigInteger in) {
        LinFunc shuffle = reverse(stream(input)
                .filter(s -> !"".equalsIgnoreCase(s))
                .map(s -> {
                    if (s.startsWith(DEAL_INTO))        return new LinFunc(ONE.negate(), ONE.negate().subtract(nCards));
                    else if (s.startsWith(CUT))         return new LinFunc(ONE, argOf(s).mod(nCards));
                    else if (s.startsWith(DEAL_W_INC))  {
                        BigInteger z = argOf(s).modInverse(nCards);
                        return new LinFunc(z.mod(nCards), ZERO);
                    }
                    throw new RuntimeException(String.format(ERR_OP, s));
                }))
                .reduce(ID, Test::agg); // Create one func of all shuffle operations, i.e. like: f(x)=f1((f2(f3(x)))
        return executeTimes(shuffle.k, shuffle.m, nShuffles).apply(in).mod(nCards);
    }

    /** Strips everything out of a string except for a number and then creates a BigInteger out of it */
    private static BigInteger argOf(String s) { return new BigInteger(s.replaceAll("[^-?0-9]+", "")); }

    public static <T> Stream<T> reverse(Stream<T> stream) {
        Iterable<T> iterable = () -> stream.collect(toCollection(LinkedList::new)).descendingIterator();
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static LinFunc executeTimes(BigInteger k, BigInteger m, BigInteger nShuffles) {
        if (nShuffles.equals(ZERO)) {
            return ID;
        } else if (nShuffles.mod(TWO).equals(ZERO)) {
            return executeTimes(k.multiply(k).mod(nCards), k.multiply(m).add(m).mod(nCards), nShuffles.divide(TWO));
        } else {
            LinFunc cd = executeTimes(k, m, nShuffles.subtract(ONE));
            return new LinFunc(k.multiply(cd.k).mod(nCards), k.multiply(cd.m).add(m).mod(nCards));
        }
    }
}
